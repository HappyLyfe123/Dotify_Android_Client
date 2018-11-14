package com.example.thai.dotify.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Utilities.JSONUtilities;
import com.example.thai.dotify.Utilities.SentToServerRequest;
import com.example.thai.dotify.Adapters.PlaylistsAdapter;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.support.constraint.Constraints.TAG;

//the fragment object that represents the home page for the profile page
public class PlaylistFragment extends Fragment implements View.OnClickListener,
    RecyclerViewClickListener{

    private Button createPlaylistButton;
    private Button playlistCreateButton;
    private Button editPlaylistButton;
    private Button cancelPlaylistButton;
    private RecyclerView playlistListRecycleView;
    private EditText playlistNameEditText;
    private PlaylistsAdapter playlistsAdapter;
    private OnFragmentInteractionListener onFragmentInteractionListener;
    private TextView createPlaylistErrorMessageTextView;
    private ProgressBar savingProgressBar;
    private AlertDialog currDialogBox;
    private static SentToServerRequest sentToServerRequest;
    private static GetFromServerRequest getFromServerRequest;


    //default constructor
    public static PlaylistFragment newInstance(SentToServerRequest sentRequest, GetFromServerRequest getRequest) {
        Bundle args = new Bundle();
        sentToServerRequest = sentRequest;
        getFromServerRequest = getRequest;
        PlaylistFragment fragment = new PlaylistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private enum ErrorType{
        CREATE_PLAYLIST_EMPTY_EDIT_TEXT,
        CREATE_PLAYLIST_DUPLICATE_NAME,
        CONNECTION_FAILED,
        SUCCESS,
        DUPLICATE_PLAYLIST_NAME,
    }

    //interface that changes fragment view
    public interface OnFragmentInteractionListener{
        void playlistClicked(String playlistName);
    }

    //attaches context object to fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     * @param onFragmentInteractionListener The listener for communication
     */
    public void setOnFragmentInteractionListener(OnFragmentInteractionListener onFragmentInteractionListener) {
        this.onFragmentInteractionListener = onFragmentInteractionListener;
    }

    /***
     * creates view for the object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_playlist, container, false);

        // Initialize views
        createPlaylistButton = (Button) view.findViewById(R.id.create_playlist_button);
        playlistListRecycleView = (RecyclerView) view.findViewById(R.id.playlist_list_recycle_view);
        editPlaylistButton = (Button) view.findViewById(R.id.edit_playlist_button);
        cancelPlaylistButton = (Button) view.findViewById(R.id.cancel_playlist_button);

        // Set listener
        cancelPlaylistButton.setOnClickListener(this);
        createPlaylistButton.setOnClickListener(this);
        editPlaylistButton.setOnClickListener(this);
        playlistsAdapter = new PlaylistsAdapter(this);

        //Display all of the items into the recycler view
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        playlistListRecycleView.setLayoutManager(songLayoutManager);
        playlistListRecycleView.setItemAnimator(new DefaultItemAnimator());
        playlistListRecycleView.setAdapter(playlistsAdapter);

        //Get the user playlists list from the server
        displayPlaylistsList();

        return view;
    }

    /***
     * invoked when user selects playlist
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Create an AlertDialog object to allow the user to create a playlist
     *
     * @return
     */
    private void createPlaylistDialog() {
        //Create an instance of the Alert Dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        //Set the View of the Alert Dialog
        final View alertDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_create_playlist, null);
        alertDialogBuilder.setView(alertDialogView);

        //Create Alert DialogBox
        currDialogBox = alertDialogBuilder.create();
        currDialogBox.show();

        //Initialize Views for this Fragment
        final Button cancelButton = (Button) alertDialogView.findViewById(R.id.create_playlist_cancel_button);
        playlistCreateButton = (Button) alertDialogView.findViewById(R.id.create_playlist_create_button);
        playlistNameEditText = (EditText) alertDialogView.findViewById(R.id.playlist_name_edit_text);
        createPlaylistErrorMessageTextView = (TextView) alertDialogView.findViewById(R.id.create_playlist_error_text_view);
        savingProgressBar = (ProgressBar) alertDialogView.findViewById(R.id.create_playlist_progress_bar);

        //Set Listeners
        playlistCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingProgressBar.setVisibility(View.VISIBLE);
                //Can have the play list name edit text empty or only spaces
                if (playlistNameEditText.getText().toString().trim().isEmpty()) {
                    displayErrorMessage(ErrorType.CREATE_PLAYLIST_EMPTY_EDIT_TEXT, createPlaylistErrorMessageTextView);
                }
                else {
                    createPlaylist(playlistNameEditText.getText().toString());
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currDialogBox.dismiss();
            }
        });
    }

    /***
     * Get the playlist from the server and display it
     * @param
     */
    private void displayPlaylistsList(){
        Call<ResponseBody> getPlaylistsList = getFromServerRequest.getUserplaylistsList();
        getPlaylistsList.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    try {
                        Log.d(TAG, "getPlaylist-> onResponse: Success Code : " + response.code());
                        //gets a list of strings of playlist names
                        String serverResponse = response.body().string();
                        JsonObject jsonResponse = JSONUtilities.ConvertStringToJSON(serverResponse);
                        JsonArray playlists = jsonResponse.getAsJsonArray("playlists");

                        for (JsonElement playlistTitle : playlists) {
                            playlistsAdapter.insertPlaylistToPlaylistsList(playlistTitle.getAsString());
                        }

                        updateInsertPlaylistsList();
                    }catch(IOException ex) {
                        Log.d(TAG, "getPlaylist -> IO Error\nError Message:");
                    }

                } else {
                    //If unsuccessful, show the response code
                    Log.d(TAG, "getPlaylist-> Unable to retrieve playlist " + response.code());
                }
            }

            //If something is wrong with our request to the server, goes to this method
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Invalid failure: onFailure");
            }
        });
    }

    /***
     * Create a playlist
     * @param
     */
    private void createPlaylist(String playlistName){

        Call<ResponseBody> createPlaylist = sentToServerRequest.createPlaylist(playlistName);

        createPlaylist.enqueue(new Callback<ResponseBody>() {
            /***
             * server sends a reply to the client indicating successful action
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "loginUser-> onResponse: Success Code : " + response.code());
                    currDialogBox.dismiss();
                    playlistsAdapter.insertPlaylistToPlaylistsList(playlistName);
                    updateInsertPlaylistsList();
                } else {
                    //A playlist with the same name already exist
                    displayErrorMessage(ErrorType.DUPLICATE_PLAYLIST_NAME, createPlaylistErrorMessageTextView);
                }
            }
            /**
             * server sends reply indicating a failure on server's side
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Invalid failure: onFailure");
                displayErrorMessage(ErrorType.CONNECTION_FAILED, createPlaylistErrorMessageTextView);
            }
        });
    }

    /***
     * Delete the playlist from the server
     * @param playlistName the name of the playlist to be deleted
     */
    private void deletePlaylist(String playlistName){

        Call<ResponseBody> deletePlaylist = sentToServerRequest.deletePlaylist(playlistName);

        /**
         * send a reply to user after the playlist is deleted
         */
        deletePlaylist.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d("PlaylistsAdapter",
                        "MyViewHolder -> onClick -> onResponse: Reponse Code = " + response.code());
                if (response.code() == Dotify.OK){

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    /***
     * Update current playlist list display
     * @param
     */
    private void updateInsertPlaylistsList(){
        playlistsAdapter.notifyItemRangeChanged(0, playlistsAdapter.getItemCount());
        playlistsAdapter.notifyItemRangeInserted(0, playlistsAdapter.getItemCount());
        playlistsAdapter.notifyDataSetChanged();
    }


    /***
     * Get the current playlist adapter
     */
    public PlaylistsAdapter getPlaylistsAdapter(){
        return playlistsAdapter;
    }

    /**
     * Display error message for create playlist AlertDialog view
     * @param type the type of error to display
     * @param displayTextView the text view to display the error in
     */
    private void displayErrorMessage(ErrorType type, TextView displayTextView){
        //Disable progress bar
        savingProgressBar.setVisibility(View.GONE);
        displayTextView.setVisibility(View.VISIBLE);
        switch (type){
            case CREATE_PLAYLIST_EMPTY_EDIT_TEXT:
                displayTextView.setText(R.string.empty_playlist_name_error);
                break;
            case CREATE_PLAYLIST_DUPLICATE_NAME:
                displayTextView.setText(R.string.duplicate_playlist_name_error);
                break;
            case CONNECTION_FAILED:
                displayTextView.setText(R.string.connection_failed);
                break;
        }
    }

    /***
     * invoked when the button is selected
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.create_playlist_button: //user selects create
                createPlaylistDialog();
                break;
            case R.id.edit_playlist_button: //user selects edit

                // Update all of the views to have the delete button to appear
                playlistsAdapter.setDeleteIconVisibility(true);
                playlistsAdapter.notifyDataSetChanged();
                // Make the Edit button disappear while making the
                // cancel button appear
                cancelPlaylistButton.setVisibility(View.VISIBLE);
                editPlaylistButton.setVisibility(View.GONE);
                break;
            case R.id.cancel_playlist_button: //user selects cancel

                // Update all of the views to have the delete button to disappear
                playlistsAdapter.setDeleteIconVisibility(false);
                playlistsAdapter.notifyDataSetChanged();
                // Make the Edit button appear while making the cancel button disappear
                cancelPlaylistButton.setVisibility(View.GONE);
                editPlaylistButton.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    public void onItemClick(View v, int position) {
        if(v.getId() == R.id.playlist_list_name_text_view) {
            //Change fragment to display all of the songs in the playlist
            onFragmentInteractionListener.playlistClicked(playlistsAdapter.getPlaylistName(position));
        }
        else if(v.getId() == R.id.playlist_item_delete_icon){
            //Call the delete playlist method
            deletePlaylist(playlistsAdapter.getPlaylistName(position));
            //Tell the adapter to delete the playlist of the display screen
            playlistsAdapter.deletePlaylist(position);
            playlistsAdapter.notifyDataSetChanged();
        }
    }
}
