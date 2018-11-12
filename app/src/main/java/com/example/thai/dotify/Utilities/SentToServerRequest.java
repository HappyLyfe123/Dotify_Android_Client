package com.example.thai.dotify.Utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.Fragments.CreateAccountFragment;
import com.example.thai.dotify.Fragments.LoginFragment;
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


    public SentToServerRequest(String baseURL, String appKey){
        dotify = new Dotify(baseURL);
        this.appKey = appKey;
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        dotifyHttpInterface = dotify.getHttpInterface();
    }

    public SentToServerRequest(String baseURL, String appKey, String username){
        //Start a GET request to get the list of playlists that belongs to the user
        dotify = new Dotify(baseURL);
        this.appKey = appKey;
        this.username = username;
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        dotifyHttpInterface = dotify.getHttpInterface();
    }

    /**
     * Creates a dotify user from the federated identities
     * @param username The user's chosen username
     * @param password The user's chosen password
     * @param secQuestion1 The user's chosen security question 1
     * @param secQuestion2 The user's chosen security quesiton 2
     * @param secAnswer1 The user's chosen security answer 1
     * @param secAnswer2 The user's chosen security answer 2
     * @return Call<DotifyUser> Returns the request to allow the user to continue to create their account
     */
    public Call<DotifyUser> createDotifyUser(final String username, final String password, final String secQuestion1, final String secQuestion2,
                                  final String secAnswer1, final String secAnswer2, final Context activityContext) {
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
        return request;
    }

    /**
     * Put request to reset the password
     * @param newPassword The new password to reset to
     */
    public Call<DotifyUser> resetPassword(final String securityToken, final String username,
                                                 final String newPassword) {
        //Create the PUT Request
        Call<DotifyUser> request = dotifyHttpInterface.updatePassword(appKey, securityToken,
                username, newPassword);

        return request;
    }

    /**
     * Method to create a playlist
     * @param playlistName The name of the playlist that is being created
     * @return An integer detailing what error code to use
     */
    public Call<ResponseBody> createPlaylist(String playlistName){
        Call<ResponseBody> createPlaylist = dotifyHttpInterface.createPlaylist(
                appKey,
                username,
                playlistName
        );
        return createPlaylist;
    }

    /**
     * Method to create a playlist
     * @param playlistName The name of the playlist that is being created
     * @return An integer detailing what error code to use
     */
    public Call<ResponseBody> deletePlaylist(String playlistName) {

        Call<ResponseBody> deletePlaylist = dotifyHttpInterface.deletePlaylist(
                appKey,
                playlistName,
                username
        );

        return deletePlaylist;

    }

    public Call<ResponseBody> addSongToPlaylist(String playlistName, String songID){
        Call<ResponseBody> addSongToPlaylist = dotifyHttpInterface.addSongToPlaylist(
                appKey,
                username,
                playlistName,
                songID
        );
        return addSongToPlaylist;
    }

    public Call<ResponseBody> deleteSongFromPlaylist(String playlistName, String songID){
        Call<ResponseBody> deleteSong = dotifyHttpInterface.deleteSongFromPlaylist(
                appKey,
                username,
                playlistName,
                songID
        );
        return deleteSong;
    }
}