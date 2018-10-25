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
import com.example.thai.dotify.SearchResultSongs;

import java.util.ArrayList;
import java.util.List;

public class SearchSongAdapter extends RecyclerView.Adapter<SearchSongAdapter.ItemsViewHolder>{

    private ArrayList<SearchResultSongs> searchResultsSongs;
    private RecyclerViewClickListener onItemClickedListener;

    public SearchSongAdapter(RecyclerViewClickListener listener){
        this.searchResultsSongs = new ArrayList<>();
        onItemClickedListener = listener;
    }

    //Set the view layout to be display
    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_recycler_item_layout, parent, false);
        return new SearchSongAdapter.ItemsViewHolder(itemView, onItemClickedListener);
    }

    //Set the text to be display
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        holder.setViewText(((SearchResultSongs) searchResultsSongs.get(position)).getSong_info());
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
    public ArrayList<SearchResultSongs> getQuerySongsList(){
        return searchResultsSongs;
    }

    /**
     * Update old search result to the new search result
     *
     * @param newSearchResult new result
     *
     */
    public void updateSearchResult(ArrayList<SearchResultSongs> newSearchResult){
        searchResultsSongs = newSearchResult;
    }

    /**
     * Add new song into result list
     *
     * @param songInfo new result
     *
     */
    public void insertSearchResultItem(SearchResultSongs songInfo){
        searchResultsSongs.add(songInfo);
    }

    /**
     * Get song name
     *
     * @param position of the song
     *
     */
    public String getSongName(int position){
        return searchResultsSongs.get(position).getSong_info();
    }

    /**
     * Get song ID
     *
     * @param position Position of the song
     *
     */
    public String getSongID(int position){
        return searchResultsSongs.get(position).getSongId();
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
        private ImageView addToPlaylistView;
        private RecyclerViewClickListener itemClickedListener;

        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            this.itemClickedListener = listener;

            // Initialize the text from the search result item
            songNameTextView = (TextView) view.findViewById(R.id.search_result_item_recycler_view);
            addToPlaylistView = (ImageView) view.findViewById(R.id.search_add_to_play_list_image_view);

            //Set listen for the text view
            songNameTextView.setOnClickListener(this);
            addToPlaylistView.setOnClickListener(this);

        }

        /**
         * Sets the text for the current item in the recycler view
         * @param songName
         */
        public void setViewText(String songName) {
            songNameTextView.setText(songName);
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
