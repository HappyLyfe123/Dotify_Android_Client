package com.example.thai.dotify.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thai.dotify.MainActivityContainer;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Utilities.JSONUtilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//Allow user to search for songs, artists, albums
public class SearchFragment extends Fragment {

    // Instantiate the Fragment Views
    private EditText searchEditText;
    private RecyclerView songSearchResultRecycler;
    private RecyclerView artistSearchResultRecycler;
    private SearchResultAdapter<String> songSearchResultAdapter;
    private SearchResultAdapter<String> artistSearchResultAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;
    private Date textChangedTimer;
    private String currSearchQuery;
    private MainActivityContainer mainActivityContainer;
    private LinearLayout songQueryLayout;
    private LinearLayout artistQueryLayout;

    /**
     * A View object for each item in a corresponding RecyclerView
     */
    private class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView resultText;
        private RecyclerViewClickListener itemClickedListener;

        ViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            // Initialize the text from the search result item
            resultText = (TextView) view.findViewById(R.id.search_result_item_recycler_view);
            // Set the On Click listener for this item
            this.itemClickedListener = listener;
            view.setOnClickListener(this);
        }

        /**
         * Sets the text for the current item in the recycler view
         * @param text
         */
        public void setViewText(String text) {
            resultText.setText(text);
        }

        @Override
        public void onClick(View view) {
            itemClickedListener.onItemClick(view, getAdapterPosition());
        }
    }

    /**
     * Adapter class for the RecyclerViews being utilized in this Fragment
     */
    private class SearchResultAdapter<T> extends RecyclerView.Adapter<ViewHolder>{

        private ArrayList<T> searchResults;
        private Class<T> classObj;
        private RecyclerViewClickListener onItemClickedListener;

        /**
         * Constructor for SearchResultAdapter
         * @param listener The listener for the item clicks
         */
        SearchResultAdapter(Class<T> classObj, RecyclerViewClickListener listener) {
            // Initialize the items for this class
            this.searchResults = new ArrayList<>();
            // Set the listener for each recycler item
            onItemClickedListener = listener;
            // Set the object types that we are going to be receiving
            this.classObj = classObj;
        }

        /**
         * Initializes each Recycler View Item
         *
         * @param parent The parent ViewGroup calling this
         * @param viewType
         *
         * @return A new ViewHolder object
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_recycler_item_layout, parent, false);

            return new ViewHolder(itemView, onItemClickedListener);
        }

        @Override
        public int getItemCount() {
            return searchResults.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String artist = (String) searchResults.get(position);
            holder.setViewText(artist);
        }

        /**
         * Returns the list of strings currently inside of this adapter
         * @return
         */
        public ArrayList<T> getSearchResults() {
            return searchResults;
        }

        /**
         * Inserts search items inside of the search item of this adapter
         *
         * @param searchItem A item received back from the server
         */
        public void insertSearchItem(T searchItem) {
            searchResults.add(searchItem);
        }

        /**
         * Retrieves an item from the class list in the specified index
         *
         * @param index The position in which the item is to be retrieved from
         *
         * @return The T object from the list
         */
        public T get(int index){
            return searchResults.get(index);
        }

        public void clearAdapterList() {
            searchResults.clear();
        }

        public  void setAdapterList(ArrayList<T> newAdapterList) {
            searchResults = newAdapterList;
        }
    }

    /**
     * Listener for the Fragment to tell the main activity to change fragments
     */
    public interface OnChangeFragmentListener{
        void onSongResultClicked(String song);
        void onArtistResultClicked();
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
                    songSearchResultAdapter.clearAdapterList();
                    artistSearchResultAdapter.clearAdapterList();
                    notifyRecyclerDataRemovedChanged();
                    changeQueryLayoutStates();
                    return;
                }
                // Check if the item has been searched before
                ArrayList<String> cachedSongQuery = mainActivityContainer.isSongQueryCached(currSearchQuery);
                ArrayList<String> cachedArtistQuery = mainActivityContainer.isArtistQueryCached(currSearchQuery);

                if(cachedSongQuery != null && cachedArtistQuery != null) {
                    songSearchResultAdapter.setAdapterList(cachedSongQuery);
                    artistSearchResultAdapter.setAdapterList(cachedArtistQuery);
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
                                            songSearchResultAdapter.clearAdapterList();
                                            for (JsonElement songTitle : songQuery) {
                                                songSearchResultAdapter.insertSearchItem(songTitle.getAsString());
                                            }
                                            mainActivityContainer.cacheSongQuery(currSearchQuery, songSearchResultAdapter.getSearchResults());

                                            artistSearchResultAdapter.clearAdapterList();
                                            JsonArray artistQuery = jsonResponse.getAsJsonArray("artist");
                                            for (JsonElement artistTitle : artistQuery) {
                                                artistSearchResultAdapter.insertSearchItem(artistTitle.getAsString());
                                            }
                                            mainActivityContainer.cacheArtistQuery(currSearchQuery, artistSearchResultAdapter.getSearchResults());

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
        songSearchResultAdapter = new SearchResultAdapter<>(String.class, new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                onChangeFragmentListener.onSongResultClicked(songSearchResultAdapter.get(position));
            }
        });
        artistSearchResultAdapter = new SearchResultAdapter<>(String.class, new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        // Set the Adapters for the recyle views
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

    /**
     * Sets the Listener object from the main activity
     *
     * @param onChangeFragmentListener Set the listener for this fragment
     *
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }
}
