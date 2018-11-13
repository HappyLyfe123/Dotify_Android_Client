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

public class SearchAlbumAdapter extends RecyclerView.Adapter<SearchAlbumAdapter.ItemsViewHolder>{

    private RecyclerViewClickListener onItemClickedListener;
    private ArrayList<String> albumNameList;
    private ArrayList<JsonElement> searchAlbumResult;

    public SearchAlbumAdapter(RecyclerViewClickListener listener){
        albumNameList = new ArrayList<>();
        searchAlbumResult = new ArrayList<>();
        onItemClickedListener = listener;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_recycler_item_layout, parent, false);
        return new SearchAlbumAdapter.ItemsViewHolder(itemView, onItemClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        holder.setViewText(albumNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return albumNameList.size();
    }

    /**
     * Clear out all of the previous result
     */
    public void newResult(){
        searchAlbumResult.clear();
        albumNameList.clear();
    }

    /**
     * Add album to the list
     * @param name name of the album
     */
    public void addAlbumName(String name){
        albumNameList.add(name);
    }

    public class ItemsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView albumNameTextView;
        private ImageView nextPageImage;
        private RecyclerViewClickListener itemClickedListener;

        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            this.itemClickedListener = listener;

            // Initialize the text from the search result item
            albumNameTextView = (TextView) view.findViewById(R.id.search_result_item_recycler_view);
            nextPageImage = (ImageView) view.findViewById(R.id.search_add_to_play_list_image_view);
            nextPageImage.setImageResource(R.drawable.next_page_icon);

            //Set listen for the text view
            albumNameTextView.setOnClickListener(this);
            nextPageImage.setOnClickListener(this);

        }

        /**
         * Sets the text for the current item in the recycler view
         * @param albumName
         */
        public void setViewText(String albumName) {
            albumNameTextView.setText(albumName);
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
