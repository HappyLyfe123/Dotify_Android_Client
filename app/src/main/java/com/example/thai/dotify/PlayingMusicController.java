package com.example.thai.dotify;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class PlayingMusicController {
    private boolean isSongPlaying;
    private  Song currentSong;
    private MediaPlayer mediaPlayer;
    private List<Song> songList;

    public PlayingMusicController(List<Song> songList){
        this.songList = songList;
        mediaPlayer = new MediaPlayer();
    }

    //Return if the song is playing or not
    public boolean getSongPlayingStatus(){
        return isSongPlaying;
    }

    public void setCurrentSong(int position){
        currentSong = songList.get(position);
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
}
