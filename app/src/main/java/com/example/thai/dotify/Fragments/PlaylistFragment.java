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

import com.example.thai.dotify.Adapters.SentToServerRequest;
import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.MainActivityContainer;
import com.example.thai.dotify.Adapters.PlaylistsAdapter;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Utilities.GetFromServerRequest;

import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private static PlaylistsAdapter playlistsAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;
    private String playlistName;
    private TextView createPlaylistErrorMessageTextView;
    private ProgressBar savingProgressBar;

    //default constructor
    public PlaylistFragment(){}

    private enum ErrorType{
        CREATE_PLAYLIST_EMPTY_EDIT_TEXT,
        CREATE_PLAYLIST_DUPLICATE_NAME,
        CONNECTION_FAILED,
        SUCCESS,
        DUPLICATE,
    }

    //interface that changes fragment view
    public interface OnChangeFragmentListener{
        void playlistClicked(String playlistName);
    }

    //attaches context object to fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(PlaylistFragment.OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
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
        GetFromServerRequest.getUserplaylistsList();

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
        AlertDialog currDialogBox = alertDialogBuilder.create();
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
                    SentToServerRequest.createPlaylist(playlistNameEditText.getText().toString());

                    currDialogBox.dismiss();
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

    //Display error message for create playlist AlertDialog view
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

    public static void displayPlaylistsList(List<String> playlistsList){
        for(String playListName : playlistsList){
            playlistsAdapter.insertPlaylistToPlaylistsList(playListName);
        }
        updateInsertPlaylistsList();
    }

    private static void updateInsertPlaylistsList(){
        playlistsAdapter.notifyItemRangeChanged(0, playlistsAdapter.getItemCount());
        playlistsAdapter.notifyItemRangeInserted(0, playlistsAdapter.getItemCount());
        playlistsAdapter.notifyDataSetChanged();
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
        onChangeFragmentListener.playlistClicked(playlistsAdapter.getPlaylistName(position));
    }
}
