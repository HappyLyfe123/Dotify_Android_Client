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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView playlistName;

        public MyViewHolder(View view) {
            super(view);
            playlistName = (TextView) view.findViewById(R.id.playlist_list_name_text_view);
        }
    }

    public PlaylistsAdapter(List<Playlist> playlistList){
        this.playlistList = playlistList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_list_layout, parent, false);


        return new MyViewHolder(itemView);
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
