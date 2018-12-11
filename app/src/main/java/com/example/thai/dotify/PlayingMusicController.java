package com.example.thai.dotify;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifySong;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * this object allows the user to move the media player
 */
public class PlayingMusicController {
    private int currSongPosition;
    private List<DotifySong> currSongList;
    private HashMap<String, MediaPlayer> cacheSongs;
    private static boolean isSongPlaying;

    /**
     * constructor with given list of songs
     *
     * @param
     */
    public PlayingMusicController() {
        currSongList = new ArrayList<>();
        isSongPlaying = false;
    }

    /**
     * Clear all song from the current song list
     */
    public void clearSongList(){
        currSongList.clear();
    }

    /**
     * assigns song being played in a playlist to the DotifySong object
     *
     * @param position - song at specified position
     */
    public void setCurrSongPosition(int position) {

        currSongPosition = position;
    }

    /**
     * Add the a new song to the song list
     * @param newSong
     */
    public void addSongToList(DotifySong newSong, boolean clearSongsList){
        //Clear all of the song from the current song list before adding new song
        if(clearSongsList){
            clearSongList();
        }
        currSongList.add(newSong);
    }

    /**
     * Replace old song list with new song list
     * @param newSongList new song list
     */
    public void setNewSongList(ArrayList<DotifySong> newSongList){
        currSongList = new ArrayList<>(newSongList);
    }

    /**
     * Get the current GUID of the song playing
     * @return GUID
     */
    public String getSongGUID(){
        return currSongList.get(currSongPosition).getGuid();
    }

    /**
     * Retrieves the name of the current song
     * @return song title
     */
    public String getCurrSongTitle() {
        if(currSongList.isEmpty()){
            return "";
        }else {
            return currSongList.get(currSongPosition).getSongTitle();
        }
    }

    /**
     * Get the artist of the song
     * @return artist name
     */
    public String getCurrSongArtist(){
        if(currSongList.isEmpty()){
            return "";
        }else{
            return currSongList.get(currSongPosition).getArtist();
        }

    }

    /**
     * Get the current song album name
     * @return album name
     */
    public String getCurrSongAlbum(){
        return currSongList.get(currSongPosition).getAlbum();
    }

    /**
     * Retrieves the id of the current song
     * @return
     */
    public String getCurrentSongGUID() {
        return currSongList.get(currSongPosition).getGuid();
    }

    /**
     * Get the current song
     * @return current song
     */
    public DotifySong getCurrentSong(){
        return currSongList.get(currSongPosition);
    }

    /**
     * The user want to play the next song in the list
     */
    public void playNextSong(){
        //Check to make sure that the current song is not the last song in the list
        if(currSongPosition >= currSongList.size() - 1){
            //Set the next song to the first song in the list
            currSongPosition = 0;
        }
        else{
            currSongPosition++;
        }
    }

    /**
     * The user want to play previous song in the list
     */
    public void playPreviousSong(){
        //Check to make sure that the song play currently is not the first song in the list
        if(currSongPosition <= 0){
            //Set the next song to be the last on in the list
            currSongPosition = currSongList.size() - 1;
        }
        else{
            currSongPosition--;
        }
    }

    public int getSongCount(){
        return currSongList.size();
    }

    //Pause Music
    public void pauseMusic() {

    }

    //Play music
    public void playMusic(){

    }

    public void setSongStatus(boolean status){
        isSongPlaying = status;
    }

    /**
     * Check the status of the song
     * @return true if song is playing / false is song not playing
     */
    public boolean isSongPlaying(){
        return isSongPlaying;
    }



}
