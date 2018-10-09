package com.example.thai.dotify;

public class PlayingMusicController {
    private static boolean isSongPlaying;
    private static String songName, artistName, albumName, songID;

    public PlayingMusicController(){

    }

    //Set the current playing song name
    public static void setSongName(String name){
        songName = name;
    }

    //Set the current song artist name
    public static void setArtistName(String name){
        artistName = name;
    }

    //Set the current song album name
    public static void setAlbumName(String name){
        albumName = name;
    }

    //Set the song id number
    public static void setSongID(String id){
        songID = id;
    }

    //Set weather the song is playing or not
    public static void setSongPlayingStatus(boolean currStatus){
        isSongPlaying = currStatus;
    }

    //Return the song name
    public static String getSongName(){
        return songName;
    }

    //Return the artist name
    public static String getArtistName(){
        return artistName;
    }

    //Return the album name
    public static String getAlbumName(){
        return albumName;
    }

    //Return the song id
    public static String getSongID(){
        return songID;
    }

    //Return if the song is playing or not
    public static boolean getSongPlayingStatus(){
        return isSongPlaying;
    }

}
