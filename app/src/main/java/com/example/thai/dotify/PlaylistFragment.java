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
    private RecyclerView playlistListRecycleView;
    private List<Playlist> playlistList = new ArrayList<>();
    private PlaylistsAdapter playlistsAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;
    private String playlistName = "";
    private Context activityContext;
    private TextView errorMessageTextView;
    private String username;


    public PlaylistFragment()  {
    }

    public interface OnChangeFragmentListener{
        void buttonClicked(MainActivityContainer.PlaylistFragmentType fragmentType);
        void setTitle(String title);
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
        SharedPreferences sharedPreferences = activityContext.getSharedPreferences("UserData", MODE_PRIVATE);
//        username = sharedPreferences.getString("username", null);
        username = "PenguinDan";

        //Set up recycler view click adapter
        RecyclerViewClickListener listener = (myView, position) -> {
            onChangeFragmentListener.setTitle(getPlaylistName(position));
            onChangeFragmentListener.buttonClicked(MainActivityContainer.PlaylistFragmentType.SONGS_LIST_PAGE);
        };

        //Get the user playlist list from the server

        //Display all of the items into the recycler view
        playlistsAdapter = new PlaylistsAdapter(playlistList, listener);
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        playlistListRecycleView.setLayoutManager(songLayoutManager);
        playlistListRecycleView.setItemAnimator(new DefaultItemAnimator());
        playlistListRecycleView.setAdapter(playlistsAdapter);
        //test();
        getPlaylist();


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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }
    /***
     * invoked when the button is selected
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_playlist_button:
                //Call the create dialog method
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
        AlertDialog currDialogBox = alertDialogBuilder.create();
        //Show the dialog box
        currDialogBox.show();

        //Initialize Views for this Fragment
        final Button createPlaylist = (Button) alertDialogView.findViewById(R.id.create_button);
        final EditText playlistName = (EditText) alertDialogView.findViewById(R.id.playlist_name_edit_text);
        final TextView errorMessageTextView = (TextView) alertDialogView.findViewById(R.id.create_playlist_error_text_view);

        //Set Listeners
        createPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Can have the play list name edit text empty
                if (playlistName.getText().toString().isEmpty()) {
                    errorMessageTextView.setText(R.string.empty_playlist_name_error);
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }
                // Check whether there is a playlist with the exact same name
                for (int i = 0; i < playlistList.size(); i++) {
                    if (playlistName.getText().toString().equals(playlistList.get(i).getPlaylistName())) {
                        errorMessageTextView.setText(R.string.duplicate_playlist_name_error);
                        errorMessageTextView.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                playlistList.add(new Playlist(playlistName.getText().toString()));
                playlistsAdapter.notifyDataSetChanged();
                currDialogBox.dismiss();
                createPlaylistDotify(playlistName.getText().toString());

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

                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "loginUser-> onResponse: Success Code : " + response.code());
                    //DotifyUser dotifyUser = response.body();
                    //Cache the playlist
                    SharedPreferences userData = activityContext.getSharedPreferences("Playlist", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userData.edit();
                    editor.putString("playlist", playlistName);
                    editor.apply();

                } else {
                    //A playlist with the same name already exist
                    errorMessageTextView = new TextView(activityContext);
                    errorMessageTextView.setText("Playlist exists.");
                    errorMessageTextView.setVisibility(View.VISIBLE);
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
                    playlistList.clear();
                    //Converts the playlist we got to a list of playlists instead of a list of strings
                    for (int i = 0; i < myPlaylist.size(); i++){
                        Playlist playlistToAdd = new Playlist(myPlaylist.get(i));
                        playlistList.add(playlistToAdd);
                    }
                    playlistsAdapter.notifyDataSetChanged();
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


    //Remove the playlist from the user playlist list
    private void removePlayList(){

    }


    /***
     * add random playlist to list
     */
    private void test(){
        Playlist playList = new Playlist("Hello");
        playlistList.add(playList);
        for(int x = 0; x < 10000000; x++){
            playList = new Playlist("A");
            playlistList.add(playList);
        }
    }

    private void test1(String name){

    }
}
