package com.example.thai.dotify;


import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifySong;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
 * this object allows the user to move the media player
 */
public class PlayingMusicController {
    private boolean isSongPlaying;
    private DotifySong currentSong;
    private MediaPlayer mediaPlayer;
    private List<DotifySong> songList;

    /**
     * constructor with given list of songs
     * @param songList - list of songs to add
     */
    public PlayingMusicController(List<DotifySong> songList){
        this.songList = songList;
        mediaPlayer = new MediaPlayer();
    }

    /**
     * checks if song is playing or not
     * @return True if song is playing; false otherwise
     */
    public boolean getSongPlayingStatus(){
        return isSongPlaying;
    }

    /**
     * assigns song being played in a playlist to the Song object
     * @param position - Song at specified position
     */
    public void setCurrentSong(int position){
        currentSong = songList.get(position);
    }

    /**
     * play music
     */
    protected static AsyncTask<Void, Void, Void> client = new AsyncTask<Void, Void, Void>() {
        private String message = "0001";

        /**
         * server does stuff in backend while song is playing
         * @param voids
         * @return
         */
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
}
