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
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifySong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//object that manipulates song data
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ItemsViewHolder> {

    private HashMap<Integer ,DotifySong> songsList;
    private RecyclerViewClickListener itemsClickListener;
    private boolean deleteIconIsVisible = false;


    /**
     * constructor with given list of songs
     * @param listener
     */
    public SongsAdapter(RecyclerViewClickListener listener){
        this.songsList = new HashMap<>();
        itemsClickListener = listener;
    }

    /**
     * display the list of songs
     * @param parent
     * @param viewType
     * @return MyViewHolder object
     */
    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_info_layout, parent, false);

        return new ItemsViewHolder(itemView, itemsClickListener);
    }

    /**
     * binds song data to a MyViewHolder object
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        DotifySong song = songsList.get(position);
        holder.setSongTitle(song.getSongTitle());
        holder.setArtistName(song.getArtist());
        holder.setAlbumName(song.getAlbum());

        // Check whether the boolean for whether the delete icon should be visible is
        // set to true
        if (deleteIconIsVisible) {
            holder.setDeleteIconVisible();
        } else {
            holder.setDeleteIconGone();
        }
    }


    /**
     * Sets whether the delete icons for each item in the views should be visible or not
     * @param isVisible Whether the items in each recycler view should be visible
     */
    public void setDeleteIconVisibility(boolean isVisible) {
        deleteIconIsVisible = isVisible;
    }

    /**
     * constructor with given list of songs
     * @param newSong new song to insert into the playlist
     */
    public void insertSongToSongsList(int songID, DotifySong newSong){
        songsList.put(songID, newSong);
    }

    public void deleteSongFromList(int position){
        songsList.remove(position);
    }


    /**
     * Get the song id at the selected position
     * @param position the position of the song in the map
     * @return the song id
     */
    public String getSongGUID(int position){
        return songsList.get(position).getSongGUID();
    }

    /**
     * get size of list of songs
     * @return songsList.size()
     */
    @Override
    public int getItemCount() {
        return songsList.size();
    }


    /**
     * A View object for each item in a corresponding RecyclerView
     */
    public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView songTitleTextView, artistNameTextView, albumNameTextView;
        private ImageView deleteIcon;
        private RecyclerViewClickListener itemsClickListener;

        /***
         * constructor w/ given objects
         * @param view - View object displaying song information
         * @param listener
         */
        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            itemsClickListener = listener;
            songTitleTextView = (TextView) view.findViewById(R.id.song_info_song_title_text_view);
            artistNameTextView = (TextView) view.findViewById(R.id.song_info_artist_name_text_view);
            albumNameTextView = (TextView) view.findViewById(R.id.song_info_album_name_text_view);
            deleteIcon = (ImageView) view.findViewById(R.id.song_info_song_delete_icon);

            deleteIcon.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        /**
         *  Set song title
         *  @param songName
         */
        public void setSongTitle(String songName){
            songTitleTextView.setText(songName);
        }

        /**
         * Set artist name
         * @param artistName
         */
        public void setArtistName(String artistName){
            artistNameTextView.setText(artistName);
        }

        /**
         * Set album name
         * @param albumName
         */
        public void setAlbumName(String albumName){
            albumNameTextView.setText(albumName);
        }

        /**
         * Sets the delete icon corresponding with the current recycler view item to be visible
         */
        public void setDeleteIconVisible(){
            deleteIcon.setVisibility(View.VISIBLE);
        }

        /**
         * Sets the delete icon corresponding with the current recycler view item to disappear
         */
        public void setDeleteIconGone(){
            deleteIcon.setVisibility(View.GONE);
        }

        /***
         * invoked when a song is selected
         * @param v
         */
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.song_info_song_delete_icon){
                itemsClickListener.onItemClick(v, getAdapterPosition());
            }
            else {
                itemsClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

}
