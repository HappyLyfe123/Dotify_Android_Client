package com.example.thai.dotify.Utilities;

import android.util.Log;
import android.view.View;

import com.example.thai.dotify.Fragments.PlaylistFragment;
import com.example.thai.dotify.R;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.support.constraint.Constraints.TAG;

public class SentToServerRequest {

    private static Dotify dotify;
    private static DotifyHttpInterface dotifyHttpInterface;
    private static String appKey;
    private static String username;

    public SentToServerRequest(String baseURL, String appKey, String username){
        //Start a GET request to get the list of playlists that belongs to the user
        dotify = new Dotify(baseURL);
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        dotifyHttpInterface = dotify.getHttpInterface();
        this.appKey = appKey;
        this.username = username;

    }

    public static int createPlaylist(String playlistName){
        final int[] errorCodeNum = new int[1];

        Call<ResponseBody> addPlaylist = dotifyHttpInterface.createPlaylist(
                appKey,
                username,
                playlistName
        );
        addPlaylist.enqueue(new Callback<ResponseBody>() {
            /***
             * server sends a reply to the client indicating successful action
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "loginUser-> onResponse: Success Code : " + response.code());
                    errorCodeNum[0] = 0;

                } else {
                    //A playlist with the same name already exist
                    errorCodeNum[0] = 1;

                }

            }

            /**
             * server sends reply indicating a failure on server's side
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Invalid failure: onFailure");
                errorCodeNum[0] = 2;

            }
        });


        return errorCodeNum[0];
    }
}
