package com.example.thai.dotify.Utilities;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.Fragments.PlaylistFragment;
import com.example.thai.dotify.MainActivityContainer;
import com.example.thai.dotify.R;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.StartUpContainer;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
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

    /**
     * Creates a dotify user from the federated identities
     * @param username The user's chosen username
     * @param password The user's chosen password
     * @param secQuestion1 The user's chosen security question 1
     * @param secQuestion2 The user's chosen security quesiton 2
     * @param secAnswer1 The user's chosen security answer 1
     * @param secAnswer2 The user's chosen security answer 2
     */
    public static int createDotifyUser(final String username, final String password, final String secQuestion1, final String secQuestion2,
                                  final String secAnswer1, final String secAnswer2) {
        //To display an error message if one exists
        final int[] errorCodeNum = new int[1];

        //Create an dotifyUser object to send
        DotifyUser dotifyUser = new DotifyUser(
                username,
                password,
                secQuestion1,
                secQuestion2,
                secAnswer1,
                secAnswer2,
                "",
                ""
        );

        //Start at POST request to create the user
        //Dotify
        //Intercept the request to add a header item
        dotify.addRequestInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                //Add the app key to the request header
                Request.Builder newRequest = request.newBuilder().header(
                        Dotify.APP_KEY_HEADER, appKey);
                //Continue the request
                return chain.proceed(newRequest.build());
            }
        });
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        //Create the POST request
        Call<DotifyUser> request = dotifyHttpInterface.createUser(dotifyUser.getUsername(), dotifyUser.getPassword(),
                dotifyUser.getQuestion1(), dotifyUser.getQuestion2(), dotifyUser.getAnswer1(), dotifyUser.getAnswer2());
        //Call the request asynchronously
        request.enqueue(new Callback<DotifyUser>() {
            @Override
            public void onResponse(Call<DotifyUser> call, retrofit2.Response<DotifyUser> response) {
                if (response.code() == 201) {
                    Log.d(TAG, "createDotifyUser-> onClick-> onSuccess-> onResponse: Successful Response Code " + response.code());
                    errorCodeNum[0] = 0;
                } else {
                    Log.d(TAG, "createDotifyUser-> onClick-> onSuccess-> onResponse: Failed response Code " + response.code());
                    errorCodeNum[0] = 1;
                }
            }

            @Override
            public void onFailure(Call<DotifyUser> call, Throwable t) {
                //The request has unexpectedly failed
                Log.d(TAG, "createDotifyUser-> onClick-> onSuccess-> onResponse: Unexpected request failure");
                //Display error message that server is down
                errorCodeNum[0] = 2;
            }
        });
        return errorCodeNum[0];
    }

    /**
     * Put request to reset the password
     * @param newPassword The new password to reset to
     */
    public static DotifyUser resetPassword(final String securityToken, final String username, final String newPassword) {
        DotifyUser dotifyUser = null;
        //Create the PUT Request
        Call<DotifyUser> request = dotifyHttpInterface.updatePassword(appKey, securityToken,
                username, newPassword);
        //Call the request asynchronously
        request.enqueue(new Callback<DotifyUser>() {
            @Override
            public void onResponse(Call<DotifyUser> call, retrofit2.Response<DotifyUser> response) {
                if (response.code() == 200) {

                } else {
                    Log.d(TAG, "resetPassword-> onClick-> onSuccess-> onResponse: Failed response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DotifyUser> call, Throwable t) {
                //The request has unexpectedly failed
                Log.d(TAG, "resetPassword -> onClick-> onFailure-> onResponse: Unexpected request failure");
                t.printStackTrace();
            }
        });
        return dotifyUser;
    }


    /**
     * Method to create a playlist
     * @param playlistName The name of the playlist that is being created
     * @return An integer detailing what error code to use
     */
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
