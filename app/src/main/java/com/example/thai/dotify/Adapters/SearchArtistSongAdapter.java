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
import com.example.thai.dotify.Server.SearchArtistSongResult;

import java.util.ArrayList;

public class SearchArtistSongAdapter extends RecyclerView.Adapter<SearchArtistSongAdapter.ItemsViewHolder>{
    private ArrayList<SearchArtistSongResult> searchResultsSongs;
    private RecyclerViewClickListener onItemClickedListener;
    private String artistName;

    public SearchArtistSongAdapter(RecyclerViewClickListener listener){
        this.searchResultsSongs = new ArrayList<>();
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
        return new SearchArtistSongAdapter.ItemsViewHolder(itemView, onItemClickedListener);
    }

    /**
     * Set the text to be display
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        holder.setViewText(getSongName(position), getArtistName());
    }

    /**
     * Create a new result list
     *
     */
    public void newResult(){
        searchResultsSongs = new ArrayList<>();
    }

    /**
     * Get song list
     *
     * @return  Current query songs list
     *
     */
    public ArrayList<SearchArtistSongResult> getQuerySongsList(){
        return searchResultsSongs;
    }

    /**
     * Update old search result to the new search result
     *
     * @param newSearchResult new result
     *
     */
    public void updateSearchResult(ArrayList<SearchArtistSongResult> newSearchResult){
        searchResultsSongs = newSearchResult;
    }

    /**
     * Add new song into result list
     *
     * @param songInfo new result
     *
     */
    public void insertSearchResultItem(SearchArtistSongResult songInfo){
        searchResultsSongs.add(songInfo);
    }

    /**
     * Set the artist name for the current song list
     * @param name the name of the artist
     */
    public void setArtistName(String name){
        this.artistName = name;
    }

    /**
     * Get song name
     *
     * @param position of the song
     *
     */
    public String getSongName(int position){
        return searchResultsSongs.get(position).getSongTitle();
    }

    /**
     * Get song ID
     *
     * @param position Position of the song
     *
     */
    public String getSongGUID(int position){
        return searchResultsSongs.get(position).getGuid();
    }

    /**
     * Get song artist name
     * @return
     */
    public String getArtistName(){
        return artistName;
    }

    /**
     * Get song album
     */
    public String getAlbumName(int position){
        return "";
    }

    //Get the number of items in the searchResult list
    @Override
    public int getItemCount() {
        if (searchResultsSongs == null) {
            return 0;
        } else{
            return searchResultsSongs.size();
        }
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
        public void setViewText(String songName, String artistName) {
            songNameTextView.setText(String.format("%s", songName));
            artistAlbumInfoTextView.setText(String.format("%s", artistName));
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
