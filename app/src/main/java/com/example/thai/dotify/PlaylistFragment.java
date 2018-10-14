package com.example.thai.dotify;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import okhttp3.logging.HttpLoggingInterceptor;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class PlaylistFragment extends Fragment implements View.OnClickListener  {

    private Button createPlaylistButton;
    private Button playlistCreateButton;
    private RecyclerView playlistListRecycleView;
    private EditText playlistNameEditText;
    private List<Playlist> playlistList = new ArrayList<>();
    private PlaylistsAdapter playlistsAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;
    private String playlistName = "";
    private Context activityContext;
    private TextView createPlaylistErrorMessageTextView;
    private ProgressBar savingProgressBar;
    private String username;

    public PlaylistFragment()  {
    }

    private enum ErrorType{
        CREATE_PLAYLIST_EMPTY_EDIT_TEXT,
        CREATE_PLAYLIST_DUPLICATE_NAME,
        CONNECTION_FAILED,
        SUCCESS,
        DUPLICATE,
    }

    public interface OnChangeFragmentListener{
        void buttonClicked(MainActivityContainer.PlaylistFragmentType fragmentType);
        void setTitle(String title);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     *
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
        createPlaylistButton = view.findViewById(R.id.create_playlist_button);
        createPlaylistButton.setOnClickListener(this);
        playlistListRecycleView = view.findViewById(R.id.playlist_list_recycle_view);
        DotifyUser dotifyUser = UserUtilities.getCachedUserInfo(activityContext);
        username = dotifyUser.getUsername();

        //Set up recycler view click adapter
        RecyclerViewClickListener listener = (myView, position) -> {
            onChangeFragmentListener.buttonClicked(MainActivityContainer.PlaylistFragmentType.SONGS_LIST_PAGE);
            onChangeFragmentListener.setTitle(getPlaylistName(position));
        };

        //Display all of the items into the recycler view
        playlistList = new ArrayList<>();
        playlistsAdapter = new PlaylistsAdapter(playlistList, listener);
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        playlistListRecycleView.setLayoutManager(songLayoutManager);
        playlistListRecycleView.setItemAnimator(new DefaultItemAnimator());
        playlistListRecycleView.setAdapter(playlistsAdapter);
        return view;
    }

    /***
     * invoked when user selects playlist
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPlaylist();
    }

    /***
     * invoked when the button is selected
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_playlist_button:
                createPlaylistDialog();
                break;
        }
    }
    /**
     * Create an AlertDialog object to allow the user to create
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
                //Can have the play list name edit text empty
                if (playlistNameEditText.getText().toString().isEmpty()) {
                    displayErrorMessage(ErrorType.CREATE_PLAYLIST_EMPTY_EDIT_TEXT, createPlaylistErrorMessageTextView);
                }
                else {
                    createPlaylistDotify(playlistNameEditText.getText().toString());
                    addToPlaylistList(playlistNameEditText.getText().toString());
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

    /**
     * Checks to see if play list can be created or not
     */
    private boolean createPlaylistDotify(final String playlistName){
        boolean playlistCreated = false;
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        Call<ResponseBody> addPlaylist = dotifyHttpInterface.createPlaylist(
                getString(R.string.appKey),
                username,
                playlistName
        );
        addPlaylist.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                savingProgressBar.setVisibility(View.GONE);
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "loginUser-> onResponse: Success Code : " + response.code());
                    //Cache the playlist
                    SharedPreferences userData = activityContext.getSharedPreferences("Playlist", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userData.edit();
                    editor.putString("playlist", playlistName);
                    editor.apply();

                    //addPlayListR(playlistName);
                    playlistList.add(new Playlist(playlistName));
                    playlistsAdapter.notifyDataSetChanged();
                } else {
                    //A playlist with the same name already exist
                    displayErrorMessage(ErrorType.CREATE_PLAYLIST_DUPLICATE_NAME, createPlaylistErrorMessageTextView);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Invalid failure: onFailure");
            }
        });

        return playlistCreated;
    }

    /**
     * This method sends a get request to the server to get a list of playlists
     * The response body is a list of strings that consists of the names of the playlists that
     * the user has created
     */
    private void getPlaylist(){
        //Start a GET request to get the list of playlists that belongs to the user
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));

        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);

        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        Call<List<String>> getPlaylist = dotifyHttpInterface.getPlaylist(
                getString(R.string.appKey),
                username,
                playlistName //Why do we need this to get the list of playlists?
        );

        getPlaylist.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "getPlaylist-> onResponse: Success Code : " + response.code());
                    //gets a list of strings of playlist names
                    List<String> myPlaylist = response.body();

                    //Converts the playlist we got to a list of playlists instead of a list of strings
                    for (int i = 0; i < myPlaylist.size(); i++){
                        Playlist playlistToAdd = new Playlist(myPlaylist.get(i));
                        playlistList.add(playlistToAdd);
                    }
                    playlistsAdapter.notifyItemRangeInserted(0, myPlaylist.size());
                    playlistsAdapter.notifyItemRangeChanged(0, myPlaylist.size());
                } else {
                    //If unsucessful, show the response code
                    Log.d(TAG, "getPlaylist-> Unable to retreive playlists " + response.code());
                }
            }
            //If something is wrong with our request to the server, goes to this method
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d(TAG, "Invalid failure: onFailure");
            }
        });
    }


    /***
     * get name of the playlist at some position in the playlist
     * @param position - position in list
     * @return playlist name at position
     */
    private String getPlaylistName(int position){
        return playlistList.get(position).getPlaylistName();
    }

    //Add a playlist to the playlist list
    private void addToPlaylistList(String playlistName){
        playlistList.add(new Playlist(playlistName));
        playlistsAdapter.notifyItemInserted(playlistList.size() - 1);
        playlistsAdapter.notifyItemRangeChanged(playlistList.size() - 1, playlistList.size());
    }

    private void removePlayList(){

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
}
