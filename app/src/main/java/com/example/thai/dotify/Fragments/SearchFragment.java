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

import com.example.thai.dotify.Adapters.PlaylistsAdapter;
import com.example.thai.dotify.Adapters.SearchAlbumAdapter;
import com.example.thai.dotify.Adapters.SearchArtistAdapter;
import com.example.thai.dotify.Adapters.SearchSongAdapter;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.SearchSongResult;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.example.thai.dotify.Utilities.JSONUtilities;
import com.example.thai.dotify.Utilities.SearchArtist;
import com.example.thai.dotify.Utilities.SentToServerRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//Allow user to search for songs, artists, albums
public class SearchFragment extends Fragment implements TextWatcher{

    // Instantiate the Fragment Views
    private EditText searchEditText;
    private RecyclerView songSearchResultRecycler;
    private RecyclerView artistSearchResultRecycler;
    private RecyclerView albumSearchResultRecycler;
    private RecyclerView selectPlaylistList;
    private OnFragmentInteractionListener onFragmentInteractionListener;
    private SearchSongAdapter songSearchResultAdapter;
    private SearchArtistAdapter artistSearchResultAdapter;
    private SearchAlbumAdapter albumSearchResultAdapter;
    private static SentToServerRequest sentToServerRequest;
    private static GetFromServerRequest getFromServerRequest;
    private PlaylistsAdapter currPlaylistAdapter;
    private String currSearchQuery;
    private LinearLayout songQueryLayout;
    private LinearLayout artistQueryLayout;
    private LinearLayout albumQueryLayout;
    private LinearLayout welcomeMessageLayout;
    private AlertDialog currDialogBox;
    private Map<String, ArrayList<SearchSongResult>> songSearchQuery;
    private Map<String, ArrayList<String>> artistSearchQuery;


    private enum RECYCLER_TYPE{
        SEARCH_SONG,
        SEARCH_ARTIST,
        SEARCH_ALBUM
    }

    /**
     * Default constructor
     */
    public static SearchFragment newInstance(SentToServerRequest sentRequest, GetFromServerRequest getRequest) {

        Bundle args = new Bundle();
        sentToServerRequest = sentRequest;
        getFromServerRequest = getRequest;
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Listener for the Fragment to tell the main activity to change fragments
     */
    public interface OnFragmentInteractionListener{
        void onSongClicked(String songGUID);
        void onArtistResultClicked(String artistName, JsonElement currArtistInfo);

        PlaylistsAdapter getPlaylistAdapter();
    }

    /**
     * Sets the Listener object from the main activity
     *
     * @param onFragmentInteractionListener Set the listener for this fragment
     *
     */
    public void setOnFragmentInteractionListener(OnFragmentInteractionListener onFragmentInteractionListener) {
        this.onFragmentInteractionListener = onFragmentInteractionListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        songSearchResultRecycler = (RecyclerView) view.findViewById(R.id.song_result_recycler_view);
        artistSearchResultRecycler = (RecyclerView) view.findViewById(R.id.artist_result_recycler_view);
        albumSearchResultRecycler = (RecyclerView) view.findViewById(R.id.album_result_recycler_view);
        songQueryLayout = (LinearLayout) view.findViewById(R.id.search_song_result_layout);
        artistQueryLayout = (LinearLayout) view.findViewById(R.id.search_artist_result_layout);
        albumQueryLayout = (LinearLayout) view.findViewById(R.id.search_album_result_layout);
        welcomeMessageLayout = (LinearLayout) view.findViewById(R.id.search_welcome_layout);

        searchEditText.addTextChangedListener(this);
        songSearchQuery = new HashMap<>();
        artistSearchQuery = new HashMap<>();

        //Initialize view layout
        RecyclerView.LayoutManager songSearchLayoutManager = new LinearLayoutManager(getContext()){
            //Disable vertical scroll
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        RecyclerView.LayoutManager artistSearchLayoutManager = new LinearLayoutManager(getContext()){
            //Disable vertical scroll
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        RecyclerView.LayoutManager albumSearchLayoutManager = new LinearLayoutManager(getContext()){
            //Disable vertical scroll
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        // The Recycler view that controls the song search results
        songSearchResultRecycler.setLayoutManager(songSearchLayoutManager);
        songSearchResultRecycler.setItemAnimator(new DefaultItemAnimator());

        // The recycler view that controls the artist search results
        artistSearchResultRecycler.setLayoutManager(artistSearchLayoutManager);
        artistSearchResultRecycler.setItemAnimator(new DefaultItemAnimator());

        // The recycler view that controls the album search results
        albumSearchResultRecycler.setLayoutManager(albumSearchLayoutManager);
        albumSearchResultRecycler.setItemAnimator(new DefaultItemAnimator());


        /**
         * Create the adapters to interact with each recycler view item
         */
        songSearchResultAdapter = new SearchSongAdapter(new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View v, int songPosition) {
                //The user want to play the selected song
                if(v.getId() == R.id.search_result_item_recycler_view) {
                    onFragmentInteractionListener.onSongClicked(songSearchResultAdapter.getSongGUID(songPosition));
                }
                //The user want to add the song to a playlist
                else if(v.getId() == R.id.search_add_to_play_list_image_view){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    //Set the View of the Alert Dialog
                    final View alertDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_select_playlist, null);
                    alertDialogBuilder.setView(alertDialogView);
                    currDialogBox = alertDialogBuilder.create();

                    //Initialize view
                    selectPlaylistList = alertDialogView.findViewById(R.id.select_playlist_playlist_list);
                    Button cancelButton = alertDialogView.findViewById(R.id.select_playlist_cancel_button);

                    //Cancel to close the select playlist view
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currDialogBox.dismiss();
                        }
                    });

                    //Create a click listener for the recycler view
                    currPlaylistAdapter = new PlaylistsAdapter(new RecyclerViewClickListener() {
                        @Override
                        public void onItemClick(View v, int playlistPosition) {
                            addSongToPlaylist(songPosition, playlistPosition);
                        }
                    });

                    //Get the list of playlist from playlist fragment
                    currPlaylistAdapter.replacePlaylistList(onFragmentInteractionListener.getPlaylistAdapter().getPlaylistListName());

                    //Display all of the items into the recycler view
                    RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
                    selectPlaylistList.setLayoutManager(songLayoutManager);
                    selectPlaylistList.setItemAnimator(new DefaultItemAnimator());
                    selectPlaylistList.setAdapter(currPlaylistAdapter);

                    //Create Alert DialogBox
                    currDialogBox.show();
                }
            }
        });

        artistSearchResultAdapter = new SearchArtistAdapter(new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String artistName = artistSearchResultAdapter.getArtist(position).getArtistName();
                onFragmentInteractionListener.onArtistResultClicked(artistName,
                        artistSearchResultAdapter.getArtist(position).getArtistInfo());
            }
        });

        albumSearchResultAdapter = new SearchAlbumAdapter(new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        // Set the Adapters for the recycle views
        songSearchResultRecycler.setAdapter(songSearchResultAdapter);
        artistSearchResultRecycler.setAdapter(artistSearchResultAdapter);
        albumSearchResultRecycler.setAdapter(albumSearchResultAdapter);

        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence currSearchQuery, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        currSearchQuery = s.toString();
        //Check to see if the search box is empty
        if(currSearchQuery.isEmpty()){
            welcomeMessageLayout.setVisibility(View.VISIBLE);
            songQueryLayout.setVisibility(View.GONE);
            artistQueryLayout.setVisibility(View.GONE);
            albumQueryLayout.setVisibility(View.GONE);
        }
        else if(isSongQueryCached(currSearchQuery) || isArtistQueryCached(currSearchQuery)){
            //Get the previous result from cache

            //Update adapter view
            notifyRecyclerDataInsertedChanged(RECYCLER_TYPE.SEARCH_SONG);
            notifyRecyclerDataInsertedChanged(RECYCLER_TYPE.SEARCH_ARTIST);
            //Update layout view
            changeQueryLayoutStates(RECYCLER_TYPE.SEARCH_SONG);
            changeQueryLayoutStates(RECYCLER_TYPE.SEARCH_ARTIST);

        }
        else {
            sendSearchToServer();
        }
    }

    /**
     *
     * @param currCharsSequence
     */
    @Override
    public void afterTextChanged(Editable currCharsSequence) {

    }

    /**
     * Sent the query to the server
     */
    private void sendSearchToServer(){
        Call<ResponseBody> querySearch = getFromServerRequest.getSearchResult(currSearchQuery);
        if(querySearch.isExecuted()){
            querySearch.cancel();
        }
        querySearch.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if(response.code() == Dotify.OK) {
                        songSearchResultAdapter.newResult();
                        artistSearchResultAdapter.newResult();

                        String serverResponse = response.body().string();
                        JsonObject jsonResponse = JSONUtilities.ConvertStringToJSON(serverResponse);

                        //Call the display methods for each category
                        displaySearchResultSong(jsonResponse.getAsJsonArray("song"));
                        displaySearchResultArtists(jsonResponse.getAsJsonArray("artist"));
                        displaySearchResultAlbums(jsonResponse.getAsJsonArray("album"));

                    }

                } catch (IOException ex) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * Display songs query result
     * @param querySongResult - the songs query result
     */
    private void displaySearchResultSong(JsonArray querySongResult){
        Gson gson = new Gson();

        for(JsonElement songInfo : querySongResult){
            songInfo.getAsJsonObject().entrySet().forEach(entry->{
                songSearchResultAdapter.addSongToList(entry.getKey());
                songSearchResultAdapter.insertSearchResultItem(gson.fromJson(
                        entry.getValue(), SearchSongResult.class));
            });


        }
        //Add result into adapter
        //Update adapter view
        notifyRecyclerDataInsertedChanged(RECYCLER_TYPE.SEARCH_SONG);
        //Update layout view
        changeQueryLayoutStates(RECYCLER_TYPE.SEARCH_SONG);
    }

    /**
     * Display artist query result
     * @param queryArtistResult - the artist query result
     */
    private void displaySearchResultArtists(JsonArray queryArtistResult){
        //Add the result into the adapter list
        for(JsonElement artistInfo : queryArtistResult){
             artistInfo.getAsJsonObject().entrySet().forEach(entry -> {
                 artistSearchResultAdapter.addArtist(
                         new SearchArtist(entry.getKey(), entry.getValue()));
             });
        }

        //Update adapter view
        notifyRecyclerDataInsertedChanged(RECYCLER_TYPE.SEARCH_ARTIST);
        //Update layout view
        changeQueryLayoutStates(RECYCLER_TYPE.SEARCH_ARTIST);
    }

    /**
     * Display album query result
     */
    private void displaySearchResultAlbums(JsonArray queryAlbumResult){
        for(JsonElement albumInfo : queryAlbumResult){
            albumInfo.getAsJsonObject().entrySet().forEach(entry -> {
                albumSearchResultAdapter.addAlbumName(entry.getKey());
            });
        }
        //Update adapter view
        notifyRecyclerDataInsertedChanged(RECYCLER_TYPE.SEARCH_ALBUM);
        //Update layout view
        changeQueryLayoutStates(RECYCLER_TYPE.SEARCH_ALBUM);
    }

    /**
     * Notify the adapter to change the layout when new item is added in
     */
    private void notifyRecyclerDataInsertedChanged(RECYCLER_TYPE type) {

        if(type == RECYCLER_TYPE.SEARCH_SONG) {
            songSearchResultAdapter.notifyItemRangeInserted(0, songSearchResultAdapter.getItemCount());
            songSearchResultAdapter.notifyItemRangeChanged(0, songSearchResultAdapter.getItemCount());
            songSearchResultAdapter.notifyDataSetChanged();
        }
        else if(type == RECYCLER_TYPE.SEARCH_ARTIST) {
            artistSearchResultAdapter.notifyItemRangeInserted(0, artistSearchResultAdapter.getItemCount());
            artistSearchResultAdapter.notifyItemRangeChanged(0, artistSearchResultAdapter.getItemCount());
            artistSearchResultAdapter.notifyDataSetChanged();
        }
        else if(type == RECYCLER_TYPE.SEARCH_ALBUM){
            albumSearchResultAdapter.notifyItemRangeInserted(0, artistSearchResultAdapter.getItemCount());
            albumSearchResultAdapter.notifyItemRangeChanged(0, artistSearchResultAdapter.getItemCount());
            albumSearchResultAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Change the layout of the search fragment according to what the user typed in the
     * search text box
     */
    private void changeQueryLayoutStates(RECYCLER_TYPE type) {
        if(type == RECYCLER_TYPE.SEARCH_SONG) {
            if (songSearchResultAdapter.getItemCount() != 0) {
                songQueryLayout.setVisibility(View.VISIBLE);
                welcomeMessageLayout.setVisibility(View.GONE);
                cacheSongQuery(currSearchQuery, songSearchResultAdapter.getQuerySongsList());
            } else {
                songQueryLayout.setVisibility(View.GONE);
            }
        }
        if(type == RECYCLER_TYPE.SEARCH_ARTIST) {
            if (artistSearchResultAdapter.getItemCount() != 0) {
                artistQueryLayout.setVisibility(View.VISIBLE);
                welcomeMessageLayout.setVisibility(View.GONE);
            } else {
                artistQueryLayout.setVisibility(View.GONE);
            }
        }
        if(type == RECYCLER_TYPE.SEARCH_ALBUM){
            if(albumSearchResultAdapter.getItemCount() != 0){
                albumQueryLayout.setVisibility(View.VISIBLE);
                welcomeMessageLayout.setVisibility(View.GONE);
            } else{
                albumQueryLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Add the song to the select playlist
     */
    private void addSongToPlaylist(int songPosition, int playlistPosition){

        Call<ResponseBody> addSongRequest = sentToServerRequest.addSongToPlaylist(currPlaylistAdapter.getPlaylistName(playlistPosition),
                songSearchResultAdapter.getSongGUID(songPosition));

        addSongRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == Dotify.OK){
                    currDialogBox.dismiss();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * Checks whether a specific search query's result has been cached for songs
     * and returns a list of DotifySong objects if it has been cached and null otherwise
     */
    private boolean isSongQueryCached(String key) {
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
    private void cacheSongQuery(String key, ArrayList<SearchSongResult> results) {
        songSearchQuery.put(key, results);
    }

    /**
     * Cache artist query result with the given string
     * @param key - the user search query
     */
    private void cacheArtistQuery(String key, ArrayList<String> results) {
        artistSearchQuery.put(key, results);
    }
}