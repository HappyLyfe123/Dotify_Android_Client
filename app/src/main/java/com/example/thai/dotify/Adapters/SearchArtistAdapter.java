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
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Set;

public class SearchArtistAdapter extends RecyclerView.Adapter<SearchArtistAdapter.ItemsViewHolder> {

    private RecyclerViewClickListener onItemClickedListener;
    private ArrayList<JsonElement> searchArtistResult;
    private ArrayList<String> artistNameList;

    //Default constructor
    public SearchArtistAdapter(RecyclerViewClickListener listener){
        artistNameList = new ArrayList<>();
        searchArtistResult = new ArrayList<>();
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
        holder.setViewText(artistNameList.get(position));
    }

    //Clear out all of the previous result
    public void newResult(){
        artistNameList.clear();
        searchArtistResult.clear();
    }

    /**
     * Update the curret search result for artist to a new one
     * @param newResult
     */
    public void setSearchArtistResult(ArrayList<JsonElement> newResult){
        searchArtistResult = newResult;
    }

    /**
     * Update searchResultSong with new result
     *
     * @param newResult new result
     *
     */
    public void setArtistNameList(ArrayList<String> newResult){
        artistNameList = newResult;
    }

    /**
     * Get the search result for artist
     */
    public ArrayList<JsonElement> getQueryArtistResult(){
        return searchArtistResult;
    }

    /**
     * Get the current list of artist name
     * @return Arraylist of artist name
     */
    public ArrayList<String> getQueryArtistsNameList(){
        return artistNameList;
    }

    public void addArtistSearchResult(JsonElement result){
        searchArtistResult.add(result);
    }

    /**
     * Add an artist to the name list
     * @param artistName
     */
    public void addArtist(String artistName){
        artistNameList.add(artistName);
    }

    /**
     * Set the list of artist
     * @param artistName
     */
    public void setArtistNameList(Set<String> artistName){
        artistNameList = new ArrayList<>(artistName);
    }

    /**
     * Set the list of
     */

    /**
     * Get the artist name in a specific position
     *
     * @param position the position of the artist
     *
     */
    public String getArtistName(int position){
        return artistNameList.get(position);
    }

    /**
     * Get the amount of artist in the search result
     *
     */
    @Override
    public int getItemCount() {
        if(artistNameList == null){
            return 0;
        }
        else {
            return artistNameList.size();
        }
    }


    public class ItemsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{


        private TextView artistNameTextView;
        private ImageView nextPageImage;
        private RecyclerViewClickListener itemClickedListener;

        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            this.itemClickedListener = listener;

            // Initialize the text from the search result item
            artistNameTextView = (TextView) view.findViewById(R.id.search_result_item_recycler_view);

            //Set the image
            nextPageImage = (ImageView)view.findViewById(R.id.search_add_to_play_list_image_view);
            nextPageImage.setImageResource(R.drawable.next_page_icon);

            //Set listen for the text view
            artistNameTextView.setOnClickListener(this);
            nextPageImage.setOnClickListener(this);

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
