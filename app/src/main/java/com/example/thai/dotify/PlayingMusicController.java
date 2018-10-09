package com.example.thai.dotify;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PlayingMusicController {
    private static boolean isSongPlaying;
    private static String songName, artistName, albumName, songID;
    private static MediaPlayer mediaPlayer;

    public PlayingMusicController(){
        mediaPlayer = new MediaPlayer();
    }

    public static void getFile(){
        try{
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //mediaPlayer.setDataSource();
            mediaPlayer.prepareAsync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected static AsyncTask<Void, Void, Void> client = new AsyncTask<Void, Void, Void>() {
        private String message = "0001";
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                InetAddress address = InetAddress.getByName("www.dotify.online");

                DatagramSocket datagramSocket = new DatagramSocket();
                DatagramPacket datagramPacket = new DatagramPacket(
                        message.getBytes(),
                        message.length(),
                        address,
                        40000
                );
                datagramSocket.setBroadcast(true);
                datagramSocket.send(datagramPacket);
            } catch(Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
    };

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
