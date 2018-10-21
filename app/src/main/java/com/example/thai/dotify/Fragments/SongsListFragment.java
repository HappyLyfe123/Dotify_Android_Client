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
import com.example.thai.dotify.Utilities.UserUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

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
    private static SongsAdapter songsListAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;
    private String playlistName;

    /**
     * default constructor
     */
    public SongsListFragment(){ }

    /**
     * Listener to tell the main container to switch fragments
     */
    public interface OnChangeFragmentListener{
        void songClicked(String songID);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener){
        this.onChangeFragmentListener = onChangeFragmentListener;
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
        songsListAdapter = new SongsAdapter(this);

        songListRecycleView.setAdapter(songsListAdapter);
        GetFromServerRequest.getSongsFromPlaylist(playlistName);

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

    //
    public void setPlaylistTitle(String name){
        playlistName = name;
    }

    //Display all of the songs in the playlist
    public static void displaySongsList(JsonArray songsList){
        Gson gson = new Gson();
        for(int x = 0; x < songsList.size(); x++){
            songsListAdapter.insertSongToSongsList(x, gson.fromJson(
                    songsList.get(x), DotifySong.class
            ));
        }
        notifyRecyclerDataInsertedChanged();

    }

    private static void notifyRecyclerDataInsertedChanged() {
        songsListAdapter.notifyItemRangeChanged(0, songsListAdapter.getItemCount());
        songsListAdapter.notifyItemRangeInserted(0, songsListAdapter.getItemCount());
        songsListAdapter.notifyDataSetChanged();
    }

    //OnClickListener
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.song_list_back_image_button:
                getFragmentManager().popBackStackImmediate();
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


    }

}