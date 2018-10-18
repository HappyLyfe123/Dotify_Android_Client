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
import java.util.List;

/**
 * this object allows the user to move the media player
 */
public class PlayingMusicController {
    private boolean isSongPlaying;
    private int currSongPosition;
    private List<DotifySong> songList;
    private Context activityContext;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean songQueried;

    /**
     * the UDPClient class runs the client-side portion of our application
     */
    public class UDPClient extends AsyncTask<DotifySong, byte[], byte[]>{
        private InetAddress serverAddress;
        private final int SERVER_UDP_PORT = 40000;
        private final int CLIENT_UDP_PORT = 30001;
        private final String TAG = UDPClient.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected byte[] doInBackground(DotifySong... songs) {
            try{
                DatagramSocket sendSocket = new DatagramSocket(CLIENT_UDP_PORT);
                serverAddress = Inet4Address.getByName("www.dotify.online");

                // Retrieve the song to request from the server
                DotifySong requestSong = songs[0];
                // Turn the request into bytes
                byte[] requestBytes = ("x" + requestSong.getSongid()).getBytes();

                // Create the Packet to send to the server
                DatagramPacket sendPacket = new DatagramPacket(
                        requestBytes,
                        requestBytes.length,
                        serverAddress,
                        SERVER_UDP_PORT
                );

                // Send the request to the server
                sendSocket.send(sendPacket);
                // Specify the buffer to retrieve the new port
                byte[] receiveBuffer = new byte[8];
                // Receive the new socket to connect to
                DatagramPacket newPortPacket = new DatagramPacket(receiveBuffer,
                        receiveBuffer.length);
                sendSocket.receive(newPortPacket);
                // Get the reply
                ByteBuffer portByteBuffer = ByteBuffer.wrap(receiveBuffer);
                double fragmentCount = portByteBuffer.getDouble();


                // Start receiving the packets for the music
                byte[] receiveSongBuffer = new byte[20000];
                DatagramPacket receiveSongPacket = new DatagramPacket(receiveSongBuffer, receiveSongBuffer.length);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                for (int i = 0; i < fragmentCount; i++) {
                    byte[] songRequestBytes = ("" + i).getBytes();
                    sendPacket = new DatagramPacket(
                            songRequestBytes,
                            songRequestBytes.length,
                            serverAddress,
                            SERVER_UDP_PORT
                    );
                    sendSocket.send(sendPacket);
                    sendSocket.receive(receiveSongPacket);
                    outputStream.write(receiveSongBuffer);
                    Log.d(TAG, "Iteration Value " + i);
                }

                sendSocket.close();

                return outputStream.toByteArray();

            }catch(Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(byte[] song) {
            super.onPostExecute(song);
            try{
                songQueried = true;
                File tempMP3 = File.createTempFile("temp", "mp3", activityContext.getCacheDir());
                tempMP3.deleteOnExit();
                FileOutputStream fos = new FileOutputStream(tempMP3);
                fos.write(song);
                fos.close();

                mediaPlayer.reset();

                FileInputStream fis = new FileInputStream(tempMP3);
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * constructor with given list of songs
     *
     * @param songList - list of songs to add
     */
    public PlayingMusicController(Context context, List<DotifySong> songList) {
        this.songList = songList;
        activityContext = context;
        songQueried = false;
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
        if (!songQueried) {
            // Retrieve the current song object
            DotifySong currentSong = songList.get(currSongPosition);
            new UDPClient().execute(currentSong);
        } else {
            mediaPlayer.start();
        }
    }

    public void pauseMusic() {
        mediaPlayer.pause();
    }

    public boolean musicIsPlaying(){
        return mediaPlayer.isPlaying();
    }

}
