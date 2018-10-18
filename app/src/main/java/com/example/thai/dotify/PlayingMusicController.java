package com.example.thai.dotify;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifySong;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * this object allows the user to move the media player
 */
public class PlayingMusicController {
    private boolean isSongPlaying;
    private int currSongPosition;
    private MediaPlayer mediaPlayer;
    private List<DotifySong> songList;

    /**
     * the UDPClient class runs the client-side portion of our application
     */
    public class UDPClient extends AsyncTask<DotifySong, Void, Void>{
        private InetAddress serverAddress;
        private DatagramSocket socket;
        private final int SERVER_UDP_PORT = 40000;
        private byte[] songFragmentByteArray;
        private final String TAG = UDPClient.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(DotifySong... songs) {
            try{
                socket = new DatagramSocket();
                serverAddress = Inet4Address.getByName("www.dotify.online");

                // Retrieve the song to request from the server
                DotifySong requestSong = songs[0];
                // Turn the request into bytes
                byte[] requestBytes = requestSong.getSongid().getBytes();

                // Create the Packet to send to the server
                DatagramPacket sendPacket = new DatagramPacket(
                        requestBytes,
                        requestBytes.length,
                        serverAddress,
                        SERVER_UDP_PORT
                );

                // Send the request to the server
                socket.send(sendPacket);

                // The Receive datagram packet
                byte[] receiveBuffer = new byte[16];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length );
                // Retrieve the request from the server
                socket.receive(receivePacket);

                ByteBuffer byteArrayWrapper = ByteBuffer.wrap(receiveBuffer);
                double songFragmentCount = byteArrayWrapper.getDouble();

                Log.d(TAG, "test");
            }catch(Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /**
     * constructor with given list of songs
     *
     * @param songList - list of songs to add
     */
    public PlayingMusicController(Context context, List<DotifySong> songList) {
        this.songList = songList;
        mediaPlayer = new MediaPlayer();
    }

    /**
     * assigns song being played in a playlist to the DotifySong object
     *
     * @param position - song at specified position
     */
    public void setCurrentSong(int position) {
        currSongPosition = position;
    }

    /**
     * Retrieves the name of the current song
     * @return
     */
    public String getCurrSongName() {
        return songList.get(currSongPosition).getSong();
    }

    /**
     * Retrieves the id of the current song
     * @return
     */
    public String getCurrentSongID() {
        return songList.get(currSongPosition).getSongid();
    }

    public void requestCurrentSong() {
        // Retrieve the current song object
        DotifySong currentSong = songList.get(currSongPosition);
        new UDPClient().execute(currentSong);
    }

}
