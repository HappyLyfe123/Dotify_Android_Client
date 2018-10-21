package com.example.thai.dotify.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.thai.dotify.Adapters.SearchArtistAdapter;
import com.example.thai.dotify.Adapters.SearchSongAdapter;
import com.example.thai.dotify.MainActivityContainer;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.SearchResultSongs;
import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;



//Allow user to search for songs, artists, albums
public class SearchFragment extends Fragment implements TextWatcher{

    // Instantiate the Fragment Views
    private EditText searchEditText;
    private RecyclerView songSearchResultRecycler;
    private RecyclerView artistSearchResultRecycler;
    private RecyclerView selectPlaylistList;
    private OnChangeFragmentListener onChangeFragmentListener;
    private static SearchSongAdapter songSearchResultAdapter;
    private static SearchArtistAdapter artistSearchResultAdapter;
    private static String currSearchQuery;
    private static LinearLayout songQueryLayout;
    private static LinearLayout artistQueryLayout;
    private static LinearLayout welcomeMessageLayout;
    private Date textChangedTimer;
    private static Map<String, ArrayList<SearchResultSongs>> songSearchQuery;
    private static Map<String, ArrayList<String>> artistSearchQuery;



    /**
     * Listener for the Fragment to tell the main activity to change fragments
     */
    public interface OnChangeFragmentListener{
        void onSongResultClicked(String song);
        void onArtistResultClicked(String artistName);
    }

    /**
     * Sets the Listener object from the main activity
     *
     * @param onChangeFragmentListener Set the listener for this fragment
     *
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        songSearchResultRecycler = (RecyclerView) view.findViewById(R.id.song_result_recycler_view);
        artistSearchResultRecycler = (RecyclerView) view.findViewById(R.id.artist_result_recycler_view);
        songQueryLayout = (LinearLayout) view.findViewById(R.id.search_song_result_layout);
        artistQueryLayout = (LinearLayout) view.findViewById(R.id.search_artist_result_layout);
        welcomeMessageLayout = (LinearLayout) view.findViewById(R.id.search_welcome_layout);

        searchEditText.addTextChangedListener(this);
        songSearchQuery = new HashMap<>();
        artistSearchQuery = new HashMap<>();

        //Initialize view layout
        RecyclerView.LayoutManager songSearchLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager artistSearchLayoutManager = new LinearLayoutManager(getContext());

        // The Recycler view that controls the song search results

        songSearchResultRecycler.setLayoutManager(songSearchLayoutManager);
        songSearchResultRecycler.setItemAnimator(new DefaultItemAnimator());
        // The recycler view that controls the artist search results

        artistSearchResultRecycler.setLayoutManager(artistSearchLayoutManager);
        artistSearchResultRecycler.setItemAnimator(new DefaultItemAnimator());
        // Initialize the linear layouts




        // Create the adapters to interact with each recycler view item
        songSearchResultAdapter = new SearchSongAdapter(new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                System.out.println(v.getId());
                if(v.getId() == R.id.search_result_item_recycler_view) {
                    onChangeFragmentListener.onSongResultClicked(songSearchResultAdapter.getSongID(position));
                }
                else if(v.getId() == R.id.search_add_to_play_list_image_view){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    //Set the View of the Alert Dialog
                    final View alertDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_select_playlist, null);
                    alertDialogBuilder.setView(alertDialogView);
                    AlertDialog currDialogBox = alertDialogBuilder.create();

                    //Initialize view
                    selectPlaylistList = alertDialogView.findViewById(R.id.select_playlist_playlist_list);
                    Button cancelButton = alertDialogView.findViewById(R.id.select_playlist_cancel_button);
                    // Initialize the recycler view listener
                    RecyclerViewClickListener playlistItemClickListener = (listView, tempPosition) -> {
                        // Create a music controller object
                        currDialogBox.dismiss();
                    };

                    //Cancel to close the select playlist view
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currDialogBox.dismiss();
                        }
                    });
                    //PlaylistsAdapter playlistsAdapter = new PlaylistsAdapter(playlistsList, playlistItemClickListener);

                    //Display all of the items into the recycler view
                    RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
                    selectPlaylistList.setLayoutManager(songLayoutManager);
                    selectPlaylistList.setItemAnimator(new DefaultItemAnimator());
                    //selectPlaylistList.setAdapter(playlistsAdapter);

                    //Create Alert DialogBox
                    currDialogBox.show();
                }
            }
        });

        artistSearchResultAdapter = new SearchArtistAdapter(new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                GetFromServerRequest.getSongByArtist(artistSearchResultAdapter.getArtistName(position));
            }
        });

        // Set the Adapters for the recycle views
        songSearchResultRecycler.setAdapter(songSearchResultAdapter);
        artistSearchResultRecycler.setAdapter(artistSearchResultAdapter);
        return view;
    }


    private static void changeQueryLayoutStates() {
        if (songSearchResultAdapter.getItemCount() != 0) {
            songQueryLayout.setVisibility(View.VISIBLE);
            welcomeMessageLayout.setVisibility(View.GONE);
            cacheSongQuery(currSearchQuery, songSearchResultAdapter.getQuerySongsList());
        }
        else{
            songQueryLayout.setVisibility(View.GONE);
        }

        if (artistSearchResultAdapter.getItemCount() != 0) {
            artistQueryLayout.setVisibility(View.VISIBLE);
            welcomeMessageLayout.setVisibility(View.GONE);
            cacheArtistQuery(currSearchQuery, artistSearchResultAdapter.getQueryArtistsList());
        }
        else{
            artistQueryLayout.setVisibility(View.GONE);
        }
    }

    private static void notifyRecyclerDataInsertedChanged() {
        songSearchResultAdapter.notifyItemRangeChanged(0, songSearchResultAdapter.getItemCount());
        songSearchResultAdapter.notifyItemRangeInserted(0, songSearchResultAdapter.getItemCount());
        songSearchResultAdapter.notifyDataSetChanged();
        artistSearchResultAdapter.notifyItemRangeChanged(0, artistSearchResultAdapter.getItemCount());
        artistSearchResultAdapter.notifyItemRangeInserted(0, artistSearchResultAdapter.getItemCount());
        artistSearchResultAdapter.notifyDataSetChanged();
    }

    private void notifyRecyclerDataRemovedChanged() {
        songSearchResultAdapter.notifyItemRangeRemoved(0, 0);
        songSearchResultAdapter.notifyDataSetChanged();
        artistSearchResultAdapter.notifyItemRangeRemoved(0, 0);
        artistSearchResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence currSearchQuery, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence currSearchQuery, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable currCharsSequence) {
        currSearchQuery = currCharsSequence.toString();

        if(currSearchQuery.isEmpty()){
            welcomeMessageLayout.setVisibility(View.VISIBLE);
            songQueryLayout.setVisibility(View.GONE);
            artistQueryLayout.setVisibility(View.GONE);
        }
        else if(isSongQueryCached(currSearchQuery) || isArtistQueryCached(currSearchQuery)){
            songSearchResultAdapter.updateSearchResult(songSearchQuery.get(currSearchQuery));
            artistSearchResultAdapter.updateSearchResult(artistSearchQuery.get(currSearchQuery));
            //Update adapter view
            notifyRecyclerDataInsertedChanged();
            //Update layout view
            changeQueryLayoutStates();
        }
        else {
            Gson gson = new Gson();
            textChangedTimer = new Date();
            Timer scheduledRequest = new Timer();
            scheduledRequest.schedule(new TimerTask() {
                Date currTime = new Date();

                @Override
                public void run() {
                    songSearchResultAdapter.newResult();
                    artistSearchResultAdapter.newResult();
                    if (currTime.after(textChangedTimer)) {
                        GetFromServerRequest.getSearchResult(currSearchQuery);
                    }
                }
            }, 500);
        }
    }

    /**
     * Display songs query result
     * @param querySongResult - the songs query result
     */
    public static void displaySearchResultSong(JsonArray querySongResult){
        Gson gson = new Gson();
        //Add result into adapter
        for(JsonElement songInfo : querySongResult){
            songSearchResultAdapter.insertSearchResultItem(gson.fromJson(
                    songInfo, SearchResultSongs.class));
        }
        //Update adapter view
        notifyRecyclerDataInsertedChanged();
        //Update layout view
        changeQueryLayoutStates();
    }

    /**
     * Display artist query result
     * @param queryArtistResult - the artist query result
     */
    public static void displaySearchResultArtists(JsonArray queryArtistResult){
        //Add the result into the adapter list
        for (JsonElement artistName : queryArtistResult) {
            artistSearchResultAdapter.addSearchResultItem(artistName.toString());
        }
        //Update adapter view
        notifyRecyclerDataInsertedChanged();
        //Update layout view
        changeQueryLayoutStates();
    }

    /**
     * Checks whether a specific search query's result has been cached for songs
     * and returns a list of DotifySong objects if it has been cached and null otherwise
     */
    public boolean isSongQueryCached(String key) {
        return songSearchQuery.containsKey(key);
    }

    /**
     * Checks whether a specific search query's result has been cached for artists
     * and returns a list of DotifySong objects if it has been cached and null otherwise
     */
    private boolean isArtistQueryCached(String key) {
        return artistSearchQuery.containsKey(key);
    }

    /**
     * Cache song query result with the given string
     * @param key - the user search query
     */
    private static void cacheSongQuery(String key, ArrayList<SearchResultSongs> results) {
        songSearchQuery.put(key, results);
    }

    /**
     * Cache artist query result with the given string
     * @param key - the user search query
     */
    private static void cacheArtistQuery(String key, ArrayList<String> results) {
        artistSearchQuery.put(key, results);
    }
}