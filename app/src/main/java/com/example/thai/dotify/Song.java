package com.example.thai.dotify;

/**
 * the Song object stores information about a song
 */
public class Song {

    private int numOfTimeSongPlayed;
    private boolean isSongLiked;
    private String songName, artistName, albumName, songID;

    /**
     * constructor w/ given values
     * @param songName - name of song
     * @param artistName - song artist
     * @param albumName - album containing the song
     * @param isSongLiked - user likes the song (T or F)
     * @param songID - identifier of song
     * @param numOfTimeSongPlayed - times user played the song
     */
    public Song(String songName, String artistName, String albumName, boolean isSongLiked,
                String songID, int numOfTimeSongPlayed){
        this.songName = songName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.isSongLiked = isSongLiked;
        this.songID = songID;
        this.numOfTimeSongPlayed = numOfTimeSongPlayed;

    }

    /**
     * get the name of the song
     * @return songName
     */
    public String getSongName(){
        return songName;

    }

    /**
     * get the song's artist
     * @return artistName
     */
    public String getArtistName(){
        return artistName;
    }

    /**
     * get the album containing the song
     * @return albumName
     */
    public String getAlbumName(){
        return albumName;
    }

    /**
     * check if user likes the song
     * @return isSongLiked
     */
    public boolean isSongLiked() {
        return isSongLiked;
    }

    /**
     * get the number of times the user played the song
     * @return
     */
    public int getNumOfTimeSongPlayed() {
        return numOfTimeSongPlayed;
    }

    /**
     * Set the song name
     * @param title - song title/name
     */
    public void setSongName(String title){
        songName = title;
    }

    /**
     * set the song artist
     * @param name - artist
     */
    public void setArtistName(String name){
        artistName = name;
    }

    /**
     * set the album containing the song
     * @param name - album name
     */
    public void setAlbumName(String name){
        albumName = name;
    }

    /**
     * set a default boolean value for the user liking the song
     * @param likeStatus - whether user likes song or not
     */
    public void setIsSongLike(boolean likeStatus){
        isSongLiked = likeStatus;
    }

    /**
     * increment the number of times user plays the song
     */
    public void increaseNumOfSongPlayed(){
        numOfTimeSongPlayed++;
    }

    /**
     * Increase the number of time a song played the specific number
     * @param numToIncrease - number of times user wants to play the song
     */
    public void increaseNumOfSongPlayed(int numToIncrease){
        numOfTimeSongPlayed += numToIncrease;
    }







}
