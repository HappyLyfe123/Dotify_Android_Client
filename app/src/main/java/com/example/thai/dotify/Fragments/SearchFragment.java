package com.example.thai.dotify.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.thai.dotify.Adapters.PlaylistsAdapter;
import com.example.thai.dotify.Adapters.SearchArtistAdapter;
import com.example.thai.dotify.Adapters.SearchSongAdapter;
import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.MainActivityContainer;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.SearchResultSongs;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Utilities.JSONUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;


//Allow user to search for songs, artists, albums
public class SearchFragment extends Fragment{

    // Instantiate the Fragment Views
    private EditText searchEditText;
    private RecyclerView songSearchResultRecycler;
    private RecyclerView artistSearchResultRecycler;
    private RecyclerView selectPlaylistList;
    private SearchSongAdapter songSearchResultAdapter;
    private SearchArtistAdapter artistSearchResultAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;
    private Date textChangedTimer;
    private String currSearchQuery;
    private MainActivityContainer mainActivityContainer;
    private LinearLayout songQueryLayout;
    private LinearLayout artistQueryLayout;
    private DotifyUser user;


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

    private TextWatcher textChangedListener;

    {
        textChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currSearchQuery = charSequence.toString();
                if (currSearchQuery.isEmpty()) {
                    notifyRecyclerDataRemovedChanged();
                    changeQueryLayoutStates();
                    return;
                }
                // Check if the item has been searched before
                ArrayList<SearchResultSongs> cachedSongQuery = mainActivityContainer.isSongQueryCached(currSearchQuery);
                ArrayList<String> cachedArtistQuery = mainActivityContainer.isArtistQueryCached(currSearchQuery);

                if(cachedSongQuery != null && cachedArtistQuery != null) {
                    changeQueryLayoutStates();
                } else {
                    textChangedTimer = new Date();
                    Timer scheduledRequest = new Timer();
                    // Have it schedule a query after half a second
                    scheduledRequest.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Date currTime = new Date();
                            if (currTime.after(textChangedTimer)) {
                                final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));
                                dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
                                DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
                                Call<ResponseBody> querySearch = dotifyHttpInterface.querySong(
                                        getActivity().getString(R.string.appKey),
                                        currSearchQuery
                                );

                                querySearch.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        try {
                                            String serverResponse = response.body().string();
                                            JsonObject jsonResponse = JSONUtilities.ConvertStringToJSON(serverResponse);

                                            JsonArray songQuery = jsonResponse.getAsJsonArray("songs");
                                            Gson gson = new Gson();
                                            for(JsonElement songInfo : songQuery){
                                                songSearchResultAdapter.insertSearchResultItem(gson.fromJson(
                                                        songInfo, SearchResultSongs.class
                                                ));
                                            }
                                            JsonArray artistQuery = jsonResponse.getAsJsonArray("artist");
                                            for (JsonElement artistName : artistQuery) {
                                                artistSearchResultAdapter.addSearchResultItem(artistName.toString());

                                            }

                                            // Notify that both the song and artist adapter have been changed
                                            notifyRecyclerDataInsertedChanged();
                                            changeQueryLayoutStates();

                                        }catch(IOException ex) {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        }
                    }, 500);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize the container for this fragment
        mainActivityContainer = (MainActivityContainer) this.getActivity();
        user = ((MainActivityContainer) this.getActivity()).getCurrentUser();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Initialize view layout
        RecyclerView.LayoutManager songSearchLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager artistSearchLayoutManager = new LinearLayoutManager(getContext());
        searchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(textChangedListener);
        // The Recycler view that controls the song search results
        songSearchResultRecycler = (RecyclerView) view.findViewById(R.id.song_result_recycler_view);
        songSearchResultRecycler.setLayoutManager(songSearchLayoutManager);
        songSearchResultRecycler.setItemAnimator(new DefaultItemAnimator());
        // The recycler view that controls the artist search results
        artistSearchResultRecycler = (RecyclerView) view.findViewById(R.id.artist_result_recycler_view);
        artistSearchResultRecycler.setLayoutManager(artistSearchLayoutManager);
        artistSearchResultRecycler.setItemAnimator(new DefaultItemAnimator());
        // Initialize the linear layouts
        songQueryLayout = (LinearLayout) view.findViewById(R.id.search_song_result_layout);
        artistQueryLayout = (LinearLayout) view.findViewById(R.id.search_artist_result_layout);



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
                    List<String> playlistsList = MainActivityContainer.getPlaylistList();
                    selectPlaylistList = alertDialogView.findViewById(R.id.select_playlist_playlist_list);
                    Button cancelButton = alertDialogView.findViewById(R.id.select_playlist_cancel_button);
                    // Initialize the recycler view listener
                    RecyclerViewClickListener playlistItemClickListener = (listView, tempPosition) -> {
                        // Create a music controller object
                        addSongToPlaylist(playlistsList.get(tempPosition), songSearchResultAdapter.getSongID(tempPosition));
                        currDialogBox.dismiss();
                    };

                    //Cancel to close the select playlist view
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currDialogBox.dismiss();
                        }
                    });
                    PlaylistsAdapter playlistsAdapter = new PlaylistsAdapter(playlistsList, playlistItemClickListener);
                    //Display all of the items into the recycler view
                    RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
                    selectPlaylistList.setLayoutManager(songLayoutManager);
                    selectPlaylistList.setItemAnimator(new DefaultItemAnimator());
                    selectPlaylistList.setAdapter(playlistsAdapter);

                    //Create Alert DialogBox
                    currDialogBox.show();
                }

            }
        });

        artistSearchResultAdapter = new SearchArtistAdapter(new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        // Set the Adapters for the recycle views
        songSearchResultRecycler.setAdapter(songSearchResultAdapter);
        artistSearchResultRecycler.setAdapter(artistSearchResultAdapter);
        return view;
    }

    private void changeQueryLayoutStates() {
        if (songSearchResultAdapter.getItemCount() != 0) {
            songQueryLayout.setVisibility(View.VISIBLE);
        } else {
            songQueryLayout.setVisibility(View.GONE);
        }

        if (artistSearchResultAdapter.getItemCount() != 0) {
            artistQueryLayout.setVisibility(View.VISIBLE);
        } else {
            artistQueryLayout.setVisibility(View.GONE);
        }
    }

    private void notifyRecyclerDataInsertedChanged() {
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

    private void addSongToPlaylist(String playlistName, String songID){
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        Call<ResponseBody> addSongToPlaylist = dotifyHttpInterface.addSongToPlaylist(
                getString(R.string.appKey),
                user.getUsername(),
                playlistName,
                songID
        );
        addSongToPlaylist.enqueue(new Callback<ResponseBody>() {
            /**
             * server sends a reply to the client indicating successful action
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "loginUser-> onResponse: Success Code : " + response.code());
                } else {
                    //A playlist with the same name already exist
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
            }
        });

    }

}