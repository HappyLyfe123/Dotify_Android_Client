package com.example.thai.dotify.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.SearchResultSongs;

import java.util.List;

public class SearchResultSongAdapter extends RecyclerView.Adapter<SearchResultSongAdapter.ItemsViewHolder>{

    private List<SearchResultSongs> songsList;
    private RecyclerViewClickListener itemClickListener;

    public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView songTitle;
        private RecyclerViewClickListener itemClickListener;

        /***
         * constructor w/ given objects
         * @param view - View object displaying song information
         * @param listener
         */
        public ItemsViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            songTitle = (TextView) view.findViewById(R.id.song_info_song_title_text_view);

            itemClickListener = listener;
            view.setOnClickListener(this);
        }

        /***
         * invoked when a song is selected
         * @param v
         */
        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    /**
     * constructor with given list of songs
     * @param songList - list of songs
     * @param listener
     */
    public SearchResultSongAdapter(List<SearchResultSongs> songList, RecyclerViewClickListener listener){
        this.songsList = songList;
        itemClickListener = listener;
    }

    /**
     * display the list of songs
     * @param parent
     * @param viewType
     * @return MyViewHolder object
     */
    @NonNull
    @Override
    public SearchResultSongAdapter.ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_recycler_item_layout, parent, false);

        return new SearchResultSongAdapter.ItemsViewHolder(itemView, itemClickListener);
    }

    /**
     * binds song data to a MyViewHolder object
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull SearchResultSongAdapter.ItemsViewHolder holder, int position) {
        SearchResultSongs song = songsList.get(position);
        holder.songTitle.setText(song.getSong_info());
    }

    /**
     * get size of list of songs
     * @return songsList.size()
     */
    @Override
    public int getItemCount() {
        return songsList.size();
    }
}
