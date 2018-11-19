package com.example.thai.dotify.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchSongAdapter extends RecyclerView.Adapter<SearchSongAdapter.ItemsViewHolder>{

    private HashMap<String, ArrayList<SearchResult>> cacheSongsList;
    private ArrayList<SearchResult> searchSongsResult;
    private RecyclerViewClickListener onItemClickedListener;

    /**
     * Default Constructor
     * @param listener
     */
    public SearchSongAdapter(RecyclerViewClickListener listener){
        cacheSongsList = new HashMap<>();
        onItemClickedListener = listener;
    }

    /**
     *
     * Set the view layout to be display
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_recycler_item_layout, parent, false);
        return new SearchSongAdapter.ItemsViewHolder(itemView, onItemClickedListener);
    }

    /**
     * Set the text to be display
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        holder.setViewText(getSongName(position), getArtistName(position), getAlbumName(position));
    }

    /**
     * Create a new result list
     *
     */
    public void newResult(){
        searchSongsResult = new ArrayList<>();
    }

    /**
     * Return all of the songs from for the current search
     * @return list of all of the songs
     */
    public ArrayList<SearchResult> getSearchsongsResult(){
        return searchSongsResult;
    }

    /**
     * Load old search result from cache
     *
     * @param currQueryChars current search characters sequence
     *
     */
    public void updateSearchResultFromCache(String currQueryChars){
        searchSongsResult = cacheSongsList.get(currQueryChars);
    }

    /**
     * Add new song into result list
     *
     * @param songInfo new result
     *
     */
    public void addSong(SearchResult songInfo, String songName){
        searchSongsResult.add(songInfo);
        songInfo.setSongTitle(songName);

    }

    /**
     * Cache the song result
     */
    public void cacheSongsResult(String queryChars){
        cacheSongsList.put(queryChars, searchSongsResult);
    }

    /**
     * Get song name
     *
     * @param position of the song
     *
     */
    public String getSongName(int position){
        return searchSongsResult.get(position).getSongTitle();
    }

    /**
     * Get song ID
     *
     * @param position Position of the song
     *
     */
    public String getSongGUID(int position){
        return searchSongsResult.get(position).getGuid();
    }

    /**
     * Get song artist name
     * @return
     */
    public String getArtistName(int position){
        return searchSongsResult.get(position).getArtist();
    }

    /**
     * Get song album
     */
    public String getAlbumName(int position){
        return searchSongsResult.get(position).getAlbum();
    }

    //Get the number of items in the searchResult list
    @Override
    public int getItemCount() {
        if (searchSongsResult == null) {
            return 0;
        } else{
            return searchSongsResult.size();
        }
    }

    /**
     * Deallocate memory space back to the OS
     */
    public void cleanUp(){
        searchSongsResult = null;
        onItemClickedListener = null;
    }

    /**
     * A View object for each item in a corresponding RecyclerView
     */
    public class ItemsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView songNameTextView;
        private TextView artistAlbumInfoTextView;
        private ImageView addToPlaylistView;
        private RecyclerViewClickListener itemClickedListener;

        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            this.itemClickedListener = listener;

            // Initialize the text from the search result item
            songNameTextView = (TextView) view.findViewById(R.id.search_result_item_recycler_view);
            artistAlbumInfoTextView = (TextView) view.findViewById(R.id.artist_album_info_text_view);
            addToPlaylistView = (ImageView) view.findViewById(R.id.search_add_to_play_list_image_view);

            //
            artistAlbumInfoTextView.setVisibility(View.VISIBLE);

            //Set listen for the text view
            songNameTextView.setOnClickListener(this);
            addToPlaylistView.setOnClickListener(this);

        }

        /**
         * Sets the text for the current item in the recycler view
         * @param songName
         */
        public void setViewText(String songName, String artsitName, String albumName) {
            songNameTextView.setText(String.format("%s", songName));
            artistAlbumInfoTextView.setText(String.format("%s \u2022 %s", artsitName, albumName));
        }

        /**
         * Get the item that the user clicked on
         * @param view
         */
        @Override
        public void onClick(View view) {
            itemClickedListener.onItemClick(view, getAdapterPosition());
        }
    }

}
