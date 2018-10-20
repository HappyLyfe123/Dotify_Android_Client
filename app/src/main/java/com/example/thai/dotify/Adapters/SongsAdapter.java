package com.example.thai.dotify.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.DotifySong;

import java.util.List;

//object that manipulates song data
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ItemsViewHolder> {

    private List<DotifySong> songsList;
    private RecyclerViewClickListener mListener;


    /**
     * constructor with given list of songs
     * @param songList - list of songs
     * @param listener
     */
    public SongsAdapter(List<DotifySong> songList, RecyclerViewClickListener listener){
        this.songsList = songList;
        mListener = listener;
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


        return new ItemsViewHolder(itemView, mListener);
    }

    /**
     * binds song data to a MyViewHolder object
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        DotifySong song = songsList.get(position);
        holder.songTitle.setText(song.getSong());
        holder.artistName.setText(song.getArtist());
        holder.albumName.setText(song.getAlbum());
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
        public TextView songTitle, artistName, albumName;
        private RecyclerViewClickListener mListener;

        /***
         * constructor w/ given objects
         * @param view - View object displaying song information
         * @param listener
         */
        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            songTitle = (TextView) view.findViewById(R.id.song_info_song_title_text_view);
            artistName = (TextView) view.findViewById(R.id.song_info_artist_name_text_view);
            albumName = (TextView) view.findViewById(R.id.song_info_album_name_text_view);
            mListener = listener;
            view.setOnClickListener(this);
        }

        /***
         * invoked when a song is selected
         * @param v
         */
        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, getAdapterPosition());
        }
    }

}