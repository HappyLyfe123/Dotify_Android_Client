package com.example.thai.dotify.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.example.thai.dotify.Utilities.JSONUtilities;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicService extends IntentService {

    // Music Requests
    public static final String LOAD_PEER = "lp";
    public static final String START_SONG = "ss";
    public static final String PAUSE_SONG = "ps";
    public static final String CONTINUE_SONG = "cs";
    // Class constants
    private static final String TAG = MusicService.class.getSimpleName();
    private static final int CREATE_PEER_PORT = 40000;
    // Class variables
    private static int streamPort;
    private static final MediaPlayer mediaPlayer = new MediaPlayer();
    private static DatagramSocket clientSocket;
    private static InetAddress serverAddress;
    private static boolean isLoaded;

    public MusicService() {
        super("MusicService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch(intent.getAction()) {
            case LOAD_PEER:{
                Log.d(TAG, "Creating a peer object");
                try {
                    clientSocket = new DatagramSocket(40000);
                    serverAddress = Inet4Address.getByName("www.dotify.online");

                    // Create the request to open a peer in the server
                    byte[] requestBytes = "open".getBytes();

                    // Create the Packet to send to the server
                    DatagramPacket packet = new DatagramPacket(
                            requestBytes,
                            requestBytes.length,
                            serverAddress,
                            CREATE_PEER_PORT
                    );

                    // Send the request to the server
                    clientSocket.send(packet);

                    // Specify the buffer to retrieve the new port
                    byte[] responseBuffer = new byte[8];
                    // Receive the new socket to connect to
                    DatagramPacket newPortPacket = new DatagramPacket(
                            responseBuffer,
                            responseBuffer.length
                    );
                    // Block until we get the reply
                    clientSocket.receive(newPortPacket);

                    // Cast the reply and retrieve the peer's port number
                    ByteBuffer portByteBuffer = ByteBuffer.wrap(responseBuffer);
                    streamPort = portByteBuffer.getInt();

                    Log.d(TAG, "Successfully created and connected to peer");

                }catch(Exception ex) {
                    Log.d(TAG, "onHandleIntent-> Error");
                    ex.printStackTrace();
                }
            }
            break;
            case START_SONG:{
               startSong(intent);
            }
            break;
            case PAUSE_SONG : {
                mediaPlayer.pause();
            }
            break;
            case CONTINUE_SONG: {
                mediaPlayer.start();
            }
        }
    }

    /**
     * Kills the Peer object that's currently living server-side in the case that the application
     * has been abruptly turned off
     *
     * @param rootIntent
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (rootIntent != null) {
            super.onTaskRemoved(rootIntent);
        }
        killPeer();
    }

    /**
     *
     * @return True if there is a song loaded in the MediaPlayer, false otherwise
     */
    public static boolean isSongLoaded() {
        return isLoaded;
    }

    /**
     * Method that runs on a worker thread to kill a peer object
     */
    public static void killPeer() {
        AsyncTask.execute(() -> {
            Log.d(TAG, "killPeer -> Android Application has been Killed");
            try {
                DatagramSocket socket = new DatagramSocket(streamPort);
                InetAddress serverAddress = Inet4Address.getByName("www.dotify.online");
                byte[] killPeerRequest = "close".getBytes();

                // Create the packet to send to the server
                DatagramPacket packet = new DatagramPacket(
                        killPeerRequest,
                        killPeerRequest.length,
                        serverAddress,
                        streamPort
                );

                // Send the request
                socket.send(packet);

                // Close the client socket
                clientSocket.close();
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void startSong(Intent intent){
        Thread t = new Thread();
        t.start();
            try {
                String songGuid = intent.getStringExtra("guid");

                // Create the request to receive a song from the server
                byte[] musicRequestBytes = songGuid.getBytes();

                // Create the Packet to send to the server
                DatagramPacket musicRequestPacket = new DatagramPacket(
                        musicRequestBytes,
                        musicRequestBytes.length,
                        serverAddress,
                        streamPort
                );

                // Send the request to the server
                clientSocket.send(musicRequestPacket);

                // The buffer to receive the music size
                byte[] responseBuffer = new byte[8];

                // Datagram packet for receiving the music size
                DatagramPacket fileSizePacket = new DatagramPacket(
                        responseBuffer,
                        responseBuffer.length
                );

                clientSocket.receive(fileSizePacket);
                ByteBuffer byteBuffer = ByteBuffer.wrap(responseBuffer);
                int fileSize = byteBuffer.getInt();

                // Initialize the memory space to store the file contents
                byte[] musicBuffer = new byte[30000];
                ByteArrayOutputStream musicOS = new ByteArrayOutputStream();
                DatagramPacket musicChunkPacket = new DatagramPacket(
                        musicBuffer,
                        musicBuffer.length
                );

                int burst = 1;
                int burstSize = 10000;

                for (int i = 0; i < fileSize; i += burstSize*burst) {
                    byte[] musicChunkRequest = ("" + i).getBytes();
                    DatagramPacket musicChunkRequestPacket = new DatagramPacket(
                            musicChunkRequest,
                            musicChunkRequest.length,
                            serverAddress,
                            streamPort
                    );
                    clientSocket.send(musicChunkRequestPacket);

                    for (int j = 0; j < burst; j++) {
                        Log.d(TAG, "Inner Iteration: " + i);
                        if (i + j * burstSize > fileSize) {
                            break;
                        }
                        clientSocket.receive(musicChunkPacket);
                        musicOS.write(musicBuffer);
                    }
                    Log.d(TAG, "Outer Iteration: " + i);
                }

                // Create the temporary file to read from
                File tempMP3 = File.createTempFile("temp", "mp3", this.getCacheDir());
                tempMP3.deleteOnExit();
                FileOutputStream fos = new FileOutputStream(tempMP3);
                fos.write(musicOS.toByteArray());
                fos.close();

                mediaPlayer.reset();

                // Read in the file and start playing music
                FileInputStream fis = new FileInputStream(tempMP3);
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
                mediaPlayer.start();

            }catch(Exception ex) {
                ex.printStackTrace();
            }
    }
}
