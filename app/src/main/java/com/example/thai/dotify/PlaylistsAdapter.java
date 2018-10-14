package com.example.thai.dotify;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.MyViewHolder> {

    private List<Playlist> playlistList;
    private RecyclerViewClickListener mlistener;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView playlistName;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            playlistName = (TextView) view.findViewById(R.id.playlist_list_name_text_view);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, getAdapterPosition());
        }
    }

    public PlaylistsAdapter(List<Playlist> playlistList, RecyclerViewClickListener listener){
        this.playlistList = playlistList;
        mlistener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_list_layout, parent, false);

        return new MyViewHolder(itemView, mlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Playlist playlist = playlistList.get(position);
        holder.playlistName.setText(playlist.getPlaylistName());
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }


}
