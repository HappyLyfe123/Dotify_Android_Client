package com.example.thai.dotify;

public class Playlist {

    private String playlistName;

    public Playlist(String playlistName){
        this.playlistName = playlistName;
    }

    public void setPlaylistName(String name){
        playlistName = name;
    }

    public String getPlaylistName(){
        return playlistName;
    }
}
