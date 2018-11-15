package com.example.thai.dotify.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.example.thai.dotify.Utilities.JSONUtilities;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicService extends IntentService {

    // Music Requests
    public static final String LOAD_PEER = "lp";
    // Class constants
    private static final String TAG = MusicService.class.getSimpleName();
    private static final String BASE_URL = "https://www.dotify.online/";
    private static final String APP_KEY = "DotifyOnline-327";
    // Class variables
    private GetFromServerRequest getFromServerRequest = new GetFromServerRequest(BASE_URL, APP_KEY);
    private static int streamPort;

    public MusicService() {
        super("MusicService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch(intent.getAction()) {
            case LOAD_PEER:{
                Log.d(TAG, "Creating a peer object");
                Call<ResponseBody> initializePeer = getFromServerRequest.initializePeer();
                initializePeer.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()) {
                            try {
                                Log.d(TAG, "onHandleIntent->onResponse: Response is successful");
                                JsonObject json = JSONUtilities.ConvertStringToJSON(response.body().string());
                                streamPort = json.getAsJsonPrimitive("port").getAsInt();
                            }catch(IOException ex) {
                                Log.d(TAG, "onHandleIntent->onResponse: IOException");
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG, "onHandleIntent->onFailure: " + t.getMessage());
                    }
                });
                Log.d(TAG, "Sent");
            }
            break;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (rootIntent != null) {
            super.onTaskRemoved(rootIntent);
        }
        killPeer();
    }

    // Runs the method to kill a peer object
    public static void killPeer() {
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
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
