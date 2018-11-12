package com.example.thai.dotify.Utilities;

import com.example.thai.dotify.Server.SearchArtistSongResult;

import java.util.ArrayList;

//A class object for the specific artist
public class Artist {

    private String artistName;
    private ArrayList<String> albumList;
    private ArrayList<SearchArtistSongResult> songList;

    /**
     * Constructor for Artist
     */
    public Artist(String artistName, ArrayList<String> albumList, ArrayList<SearchArtistSongResult> songList){
        this.artistName = artistName;
        this.albumList = new ArrayList<>(albumList);
        this.songList = new ArrayList<>(songList);
    }

    /**Set the artist name
     *
     * @param name the name of the artist
     */
    public void setArtistName(String name){
        this.artistName = name;
    }

    //Set current albumList to new album list
    public void setAlbumList(ArrayList<String> albumList){
        //Check to make sure the array list is not null
        if(this.albumList == null){
            this.albumList = new ArrayList<>(albumList);
        }else {
            this.albumList = albumList;
        }
    }

    /**
     * Set the current songList to new songList
     */
    public void setSongList(ArrayList<SearchArtistSongResult> songList){
        //Check to make sure the array list is not null
        if(this.songList == null){
            this.songList = new ArrayList<>(songList);
        }else {
            this.songList = songList;
        }
    }

    /**
     * Get artist name
     * @return artist name
     */
    public String getArtistName(){
        return artistName;
    }

    /**
     * Get album name at the at given index
     * @param index
     * @return name of the album
     */
    public String getAlbumName(int index){
        if(index < 0 || index > albumList.size()){
            return "Invalid Index";
        }else {
            return albumList.get(index);
        }
    }

    /**
     * Get SearchArtistSongResult o in at the given index
     * @param index
     * @return SearchArtistSongResult object of a song
     */
    public SearchArtistSongResult getSong(int index){
        if(index < 0 || index > songList.size()){
            return null;
        }
        else {
            return songList.get(index);
        }
    }

}
