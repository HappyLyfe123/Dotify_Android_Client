package com.example.thai.dotify.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;


import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * this object manipulates playlist data
 */
public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.ItemsViewHolder> {

    private ArrayList<String> playlistsListName;
    private RecyclerViewClickListener itemClickListener;
    private boolean deleteIconIsVisible = false;

    /**
     * interface for deleting a playlist
     */
    public interface OnPlaylistDeletedListener{
        void onPlaylistDeleted(int position);
    }

    /**
     * constructor w/ given values
     * @param listener
     */
    public PlaylistsAdapter(RecyclerViewClickListener listener){
        this.playlistsListName = new ArrayList<>();
        itemClickListener = listener;
    }


    /**
     * create the view object containing data for file "playlist_list_layout"
     * @param parent
     * @param viewType
     * @return new MyViewHolder object
     */
    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_list_layout, parent, false);

        return new ItemsViewHolder(itemView, itemClickListener);
    }

    /**
     * bind data to the PlaylistsAdapter object
     * @param holder - MyViewHolder object NOT null
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        holder.setViewPlaylistName(playlistsListName.get(position));
        // Check whether the boolean for whether the delete icon should be visible is
        // set to true
        if (deleteIconIsVisible) {
            holder.setDeleteIconVisible();
        } else {
            holder.setDeleteIconGone();
        }
    }

    public String getPlaylistName(int position){
        return playlistsListName.get(position);
    }

    /**
     * get the size of the playlist list
     * @return
     */
    @Override
    public int getItemCount() {
        return playlistsListName.size();
    }

    /**
     * Sets whether the delete icons for each item in the views should be visible or not
     * @param isVisible Whether the items in each recycler view should be visible
     */
    public void setDeleteIconVisibility(boolean isVisible) {
        deleteIconIsVisible = isVisible;
    }

    /**
     * Get all of the names of playlist playlistList
     * @return Name of playlist
     */
    public ArrayList<String> playlistListName(){
        return playlistsListName;
    }

    /**
     * Replace the current playlistsList with a new playlistList
     * @param newPlaylistsList - new playlistList to replace the old one
     */
    public void replacePlaylistList(ArrayList<String> newPlaylistsList){
        playlistsListName = newPlaylistsList;
    }

    /**
     * Add a new playlist to PlaylistsList
     * @param playlistName playlist name to be added
     */
    public void insertPlaylistToPlaylistsList(String playlistName){
        playlistsListName.add(playlistName);
    }

    /**
     * Add a delete playlist to PlaylistsList
     * @param index the  position playlist to be deleted
     */
    public void deletePlaylist(int index){
        playlistsListName.remove(index);
    }

    /**
     * the MyViewHolder object allows user to delete a playlist
     */
    public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView playlistNameTextView;
        private ImageView deleteIcon;
        private RecyclerViewClickListener itemClickedListener;

        /**
         * constructor that allows the user to delete a playlist
         * @param view - View object that will display playlist_list_layout layout file
         * @param listener
         */
        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            itemClickedListener = listener;

            // Initialize the view from the search result item
            playlistNameTextView = (TextView) view.findViewById(R.id.playlist_list_name_text_view);
            deleteIcon = (ImageView) view.findViewById(R.id.playlist_item_delete_icon);

            // Delete icon that handles the event that the user wants to delete a playlist
            playlistNameTextView.setOnClickListener(this);
            deleteIcon.setOnClickListener(this);
        }

        /**
         * invoked when user selects a playlist
         * @param v - View object for type PlaylistsAdapter
         */
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.playlist_list_name_text_view) {
                itemClickedListener.onItemClick(v, getAdapterPosition());
            }
            else if(v.getId() == R.id.playlist_item_delete_icon){
                System.out.println("I'm here");
            }
        }

        public void setViewPlaylistName(String playlistName){
            playlistNameTextView.setText(playlistName);
        }

        /**
         * Sets the delete icon corresponding with the current recycler view item to be visible
         */
        public void setDeleteIconVisible() {
            deleteIcon.setVisibility(View.VISIBLE);
        }

        /**
         * Sets the delete icon corresponding with the current recycler view item to disappear
         */
        public void setDeleteIconGone() {
            deleteIcon.setVisibility(View.GONE);
        }
    }

}
