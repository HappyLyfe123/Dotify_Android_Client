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
import com.example.thai.dotify.Utilities.SearchArtist;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Set;

public class SearchArtistAdapter extends RecyclerView.Adapter<SearchArtistAdapter.ItemsViewHolder> {

    private RecyclerViewClickListener onItemClickedListener;
    private ArrayList<SearchArtist> artistList;

    //Default constructor
    public SearchArtistAdapter(RecyclerViewClickListener listener){
        artistList = new ArrayList<>();
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
        holder.setViewText(artistList.get(position).getArtistName());
    }

    //Clear out all of the previous result
    public void newResult(){
        artistList.clear();
    }

    /**
     * Update searchResultSong with new result
     *
     * @param newResult new result
     *
     */
    public void setArtistNameList(ArrayList<SearchArtist> newResult){
        artistList = newResult;
    }

    /**
     * Get the current list of artist name
     * @return Arraylist of artist name
     */
    public ArrayList<SearchArtist> getQueryArtistsList(){
        return artistList;
    }


    /**
     * Add an artist to the name list
     * @param artist
     */
    public void addArtist(SearchArtist artist){
        artistList.add(artist);
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
    public SearchArtist getArtist(int position){
        return artistList.get(position);
    }

    /**
     * Get the amount of artist in the search result
     *
     */
    @Override
    public int getItemCount() {
        if(artistList == null){
            return 0;
        }
        else {
            return artistList.size();
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
