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

public class SearchAlbumSongAdapter extends RecyclerView.Adapter<SearchAlbumSongAdapter.ItemsViewHolder> {
    private ArrayList<DotifySong> songsList;
    private RecyclerViewClickListener onItemClickedListener;
    private String artistName;

    public SearchAlbumSongAdapter(RecyclerViewClickListener listener){
        this.songsList = new ArrayList<>();
        onItemClickedListener = listener;
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_recycler_item_layout, parent, false);
        return new SearchAlbumSongAdapter.ItemsViewHolder(itemView, onItemClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        holder.setViewText(getSongName(position), artistName);
    }

    /**
     * Add new song into the list of song in the album
     * @param song
     */
    public void newSong(DotifySong song){
        songsList.add(song);
    }

    /**
     * Set the artist name
     * @param name
     */
    public void setArtistName(String name){
        artistName = name;
    }

    /**
     * Get the song name
     * @param position the position of where the song located
     * @return song name
     */
    public String getSongName(int position){
        return songsList.get(position).getSongTitle();
    }

    /**
     * Get the song GUID
     * @param position the position of where the song is located
     * @return GUID of the song
     */
    public String getSongGUID(int position){
        return songsList.get(position).getGuid();
    }

    /**
     * Get the album artist name
     * @return artist name
     */
    public String getArtistName(){
        return artistName;
    }

    public DotifySong getSong(int position){
        return songsList.get(position);
    }

    /**
     * Get how many song is in the album
     * @return the number of songs in the album
     */
    @Override
    public int getItemCount() {
        return songsList.size();
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
