package com.example.thai.dotify;

public class Song {

    private int numOfTimeSongPlayed;
    private boolean isSongLiked;
    private String songName, artistName, albumName, songID;

    public Song(String songName, String artistName, String albumName, boolean isSongLiked,
                String songID, int numOfTimeSongPlayed){
        this.songName = songName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.isSongLiked = isSongLiked;
        this.songID = songID;
        this.numOfTimeSongPlayed = numOfTimeSongPlayed;

    }

    //Return the song name
    public String getSongName(){
        return songName;

    }

    //Return artist name
    public String getArtistName(){
        return artistName;
    }

    //Return album name
    public String getAlbumName(){
        return albumName;
    }

    //Return weather or not the song is liked by the user
    public boolean isSongLiked() {
        return isSongLiked;
    }

    //Return if number of time a song played
    public int getNumOfTimeSongPlayed() {
        return numOfTimeSongPlayed;
    }

    //Set the song name
    public void setSongName(String title){
        songName = title;
    }

    //Set artist name
    public void setArtistName(String name){
        artistName = name;
    }

    //Set album name
    public void setAlbumName(String name){
        albumName = name;
    }

    //Set weather the song is like or not
    public void setIsSongLike(boolean likeStatus){
        isSongLiked = likeStatus;
    }

    //Increase the number of time a song played by one
    public void increaseNumOfSongPlayed(){
        numOfTimeSongPlayed++;
    }

    //Increase the number of time a song played the specific number
    public void increaseNumOfSongPlayed(int numToIncrease){
        numOfTimeSongPlayed += numToIncrease;
    }







}
