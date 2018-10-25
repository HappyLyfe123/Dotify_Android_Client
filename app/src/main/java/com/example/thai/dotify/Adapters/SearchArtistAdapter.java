package com.example.thai.dotify.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class SearchArtistAdapter extends RecyclerView.Adapter<SearchArtistAdapter.ItemsViewHolder> {

    private ArrayList<String> searchResultArtists;
    private RecyclerViewClickListener onItemClickedListener;

    //Default constructor
    public SearchArtistAdapter(RecyclerViewClickListener listener){
        searchResultArtists = new ArrayList<>();
        onItemClickedListener = listener;
    }

    //Set the view layout to be display
    @NonNull
    @Override
    public SearchArtistAdapter.ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_recycler_item_layout, parent, false);
        return new SearchArtistAdapter.ItemsViewHolder(itemView, onItemClickedListener);
    }

    //Set the name to display
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        holder.setViewText(searchResultArtists.get(position));
    }

    public void newResult(){
        searchResultArtists = new ArrayList<>();
    }

    public ArrayList<String> getQueryArtistsList(){
        return searchResultArtists;
    }

    /**
     * Update searchResultSong with new result
     *
     * @param newResult new result
     *
     */
    public void updateSearchResult(ArrayList<String> newResult){
        searchResultArtists = newResult;
    }


    public void addSearchResultItem(String artistName){
        searchResultArtists.add(artistName);
    }

    /**
     * Get the artist name in a specific position
     *
     * @param position the position of the artist
     *
     */
    public String getArtistName(int position){
        return searchResultArtists.get(position);
    }

    /**
     * Get the amount of artist in the search result
     *
     */
    @Override
    public int getItemCount() {
        if(searchResultArtists == null){
            return 0;
        }
        else {
            return searchResultArtists.size();
        }
    }


    public class ItemsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{


        private TextView artistNameTextView;
        private RecyclerViewClickListener itemClickedListener;

        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            this.itemClickedListener = listener;

            // Initialize the text from the search result item
            artistNameTextView = (TextView) view.findViewById(R.id.search_result_item_recycler_view);

            //Disable song add to playlist image
            view.findViewById(R.id.search_add_to_play_list_image_view).setVisibility(View.GONE);

            //Set listen for the text view
            artistNameTextView.setOnClickListener(this);

        }

        /**
         * Sets the text for the current item in the recycler view
         * @param artistName
         */
        public void setViewText(String artistName) {
            artistNameTextView.setText(artistName);
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
