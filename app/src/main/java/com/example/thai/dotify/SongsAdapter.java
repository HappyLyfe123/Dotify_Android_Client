package com.example.thai.dotify;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder> {

    private List<Song> songList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView songTitle, artistName, albumName;

        public MyViewHolder(View view) {
            super(view);
            songTitle = (TextView) view.findViewById(R.id.song_info_song_title_text_view);
            artistName = (TextView) view.findViewById(R.id.song_info_artist_name_text_view);
            albumName = (TextView) view.findViewById(R.id.song_info_album_name_text_view);
        }
    }

    public SongsAdapter(List<Song> songList){
        this.songList = songList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_info_layout, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.songTitle.setText(song.getSongName());
        holder.artistName.setText(song.getArtistName());
        holder.albumName.setText(song.getAlbumName());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


}
