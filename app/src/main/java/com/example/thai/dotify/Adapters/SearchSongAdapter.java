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
import com.example.thai.dotify.SearchSongResult;

import java.util.ArrayList;

public class SearchSongAdapter extends RecyclerView.Adapter<SearchSongAdapter.ItemsViewHolder>{

    private ArrayList<SearchSongResult> searchResultsSongs;
    private RecyclerViewClickListener onItemClickedListener;
    private ArrayList<String> songNameList = new ArrayList<>();

    public SearchSongAdapter(RecyclerViewClickListener listener){
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
        holder.setViewText(getSongName(position));
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
    public ArrayList<SearchSongResult> getQuerySongsList(){
        return searchResultsSongs;
    }

    public void addSongToList(String songName){
        songNameList.add(songName);
    }

    /**
     * Update old search result to the new search result
     *
     * @param newSearchResult new result
     *
     */
    public void updateSearchResult(ArrayList<SearchSongResult> newSearchResult){
        searchResultsSongs = newSearchResult;
    }

    /**
     * Add new song into result list
     *
     * @param songInfo new result
     *
     */
    public void insertSearchResultItem(SearchSongResult songInfo){
        searchResultsSongs.add(songInfo);
    }

    /**
     * Get song name
     *
     * @param position of the song
     *
     */
    public String getSongName(int position){
        return songNameList.get(position);
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
     * Get song artist
     * @return
     */
    public String getArtist(int position){
        return searchResultsSongs.get(position).getArtist();
    }

    /**
     * Get song album
     */
    public String getAlbum(int position){
        return searchResultsSongs.get(position).getAlbum();
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
