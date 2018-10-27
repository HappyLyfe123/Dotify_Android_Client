package com.example.thai.dotify.Fragments;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.MainActivityContainer;
import com.example.thai.dotify.PlayingMusicController;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Server.DotifySong;
import com.example.thai.dotify.Adapters.SongsAdapter;
import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.example.thai.dotify.Utilities.JSONUtilities;
import com.example.thai.dotify.Utilities.SentToServerRequest;
import com.example.thai.dotify.Utilities.UserUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

/**
 * the SongsListFragment object allows the user to do various functions with a list of songs
 */
public class SongsListFragment extends Fragment implements View.OnClickListener,
    RecyclerViewClickListener{

    private ImageButton backButton;
    private Button editSongButton;
    private Button cancelDeleteSongButton;
    private TextView titleTextView;
    private RecyclerView songListRecycleView;
    private SongsAdapter songsListAdapter;
    private OnFragmentInteractionListener onFragmentInteractionListener;
    private String playlistName;
    private static SentToServerRequest sentToServerRequest;
    private static GetFromServerRequest getFromServerRequest;

    /**
     * Default constructor
     */
    public static SongsListFragment newInstance(SentToServerRequest sentRequest, GetFromServerRequest getRequest) {
        sentToServerRequest = sentRequest;
        getFromServerRequest = getRequest;
        SongsListFragment fragment = new SongsListFragment();
        return fragment;
    }

    /**
     * Listener to tell the main container to switch fragments
     */
    public interface OnFragmentInteractionListener{
        void songClicked(String songID);
        void backButtonPressed();
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnFragmentInteractionListener(OnFragmentInteractionListener onChangeFragmentListener){
        this.onFragmentInteractionListener = onChangeFragmentListener;
    }

    /**
     * add a Bundle object to the SongsListFragment object
     * @param savedInstanceState - Bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * add information about app's environment to the SongsListFragment object
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /***
     * create View object of fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return new View object of type SongsListFragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        backButton = (ImageButton) view.findViewById(R.id.song_list_back_image_button);
        titleTextView = (TextView) view.findViewById(R.id.song_list_title_text_view);
        songListRecycleView = (RecyclerView) view.findViewById(R.id.song_list_recycle_view);
        editSongButton = (Button) view.findViewById(R.id.song_list_edit_song_button);
        cancelDeleteSongButton = (Button) view.findViewById(R.id.song_list_cancel_song_button);

        //Display all of the items into the recycler view
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        songListRecycleView.setLayoutManager(songLayoutManager);
        songListRecycleView.setItemAnimator(new DefaultItemAnimator());

        //Set listener
        backButton.setOnClickListener(this);
        editSongButton.setOnClickListener(this);
        cancelDeleteSongButton.setOnClickListener(this);

        //Set adapter
        songsListAdapter = new SongsAdapter(this);
        displaySongsList();
        songListRecycleView.setAdapter(songsListAdapter);

        return view;
    }

    /**
     * add extra features when the activity object has been created
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setFragmentTitle();
    }

    /**
     * Set the title for the text view to title of current fragment
     */
    private void setFragmentTitle(){
        titleTextView.setText(playlistName);
    }

    /**
     * Set the title for the text view to title of current fragment
     */
    public void setPlaylistTitle(String name){
        playlistName = name;
    }

    /**
     * Display all of the song in the selected playlist
     */
    private void displaySongsList(){

        Call<ResponseBody> getSongsFromPlaylist = getFromServerRequest.getSongsFromPlaylist(playlistName);

        getSongsFromPlaylist.enqueue(new Callback<ResponseBody>() {
            /**
             * display a success message
             * @param call - request to server
             * @param response - server's response
             */
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                int respCode = response.code();

                if (respCode == Dotify.OK) {
                    Gson gson = new Gson();
                    Log.d(TAG, "getPlaylist-> onResponse: Success Code : " + response.code());
                    //gets a list of strings of playlist names
                    ResponseBody mySong = response.body();
                    try {
                        JsonObject currSongList= JSONUtilities.ConvertStringToJSON(mySong.string());
                        JsonArray songsList = currSongList.getAsJsonArray("songs");
                        for(int x = 0; x < songsList.size(); x++){
                            songsListAdapter.insertSongToSongsList(x, gson.fromJson(
                                    songsList.get(x), DotifySong.class
                            ));
                        }
                        notifyRecyclerDataInsertedChanged(0, songsList.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    //If unsucessful, show the response code
                    Log.d(TAG, "getPlaylist-> Unable to retreive playlists " + response.code());
                }
            }

            /**
             * If something is wrong with our request to the server, goes to this method
             * @param call - request to server
             * @param t - unnecessary parameter
             */
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Invalid failure: onFailure");
            }
        });

    }

    /**
     * Delete selected song from playlist
     * @param songID the song ID that is to be deleted
     */
    private void deleteSongFromPlaylist(String songID, int position){
        Call<ResponseBody> deleteSong = sentToServerRequest.deleteSongFromPlaylist(playlistName, songID);
        deleteSong.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int respCode = response.code();
                if(respCode == Dotify.OK){
                    songsListAdapter.deleteSongFromList(position);
                    songsListAdapter.notifyItemRemoved(position);
                }
                else{

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    private void notifyRecyclerDataInsertedChanged(int startPosition, int endPosition) {
        songsListAdapter.notifyItemRangeChanged(startPosition, endPosition);
        songsListAdapter.notifyItemRangeInserted(startPosition, endPosition);
        songsListAdapter.notifyDataSetChanged();
    }


    //OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.song_list_back_image_button:
                onFragmentInteractionListener.backButtonPressed();
                break;
            case R.id.song_list_edit_song_button:
                // Update all of the views to have the delete button to appear
                songsListAdapter.setDeleteIconVisibility(true);
                songsListAdapter.notifyDataSetChanged();
                // Make the Edit button disappear while making the
                // cancel button appear
                cancelDeleteSongButton.setVisibility(View.VISIBLE);
                editSongButton.setVisibility(View.GONE);
                break;
            case R.id.song_list_cancel_song_button:
                // Update all of the views to have the delete button to disappear
                songsListAdapter.setDeleteIconVisibility(false);
                songsListAdapter.notifyDataSetChanged();
                // Make the Edit button appear while making the cancel button disappear
                cancelDeleteSongButton.setVisibility(View.GONE);
                editSongButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    //RecyclerViewClickListener
    @Override
    public void onItemClick(View v, int position) {
        //The delete icon is selected
        if(v.getId() == R.id.song_info_song_delete_icon){
            deleteSongFromPlaylist(songsListAdapter.getSongID(position), position);
        }
        //The user picked a song
        else{

        }

    }

}