package com.example.thai.dotify.Utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.thai.dotify.Fragments.LoginFragment;
import com.example.thai.dotify.Fragments.PlaylistFragment;
import com.example.thai.dotify.Fragments.SearchFragment;
import com.example.thai.dotify.Fragments.SongsListFragment;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.google.gson.JsonArray;
import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.Fragments.ForgetPasswordFragment;
import com.example.thai.dotify.R;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Server.DotifySong;
import com.example.thai.dotify.StartUpContainer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

public class GetFromServerRequest {

    private static Dotify dotify;
    private static DotifyHttpInterface dotifyHttpInterface;
    private static String appKey;
    private static String username;

    private static final int SUCCESS = 0;

    public GetFromServerRequest(String baseURL, String appKey){
        dotify = new Dotify(baseURL);
        this.appKey = appKey;
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        dotifyHttpInterface = dotify.getHttpInterface();
    }
    /**
     * Constructor
     * @param baseURL baseURL inside the strings folder
     * @param appKey appKey inside the strings folder
     * @param username the user's username
     */
    public GetFromServerRequest(String baseURL, String appKey, String username){
        //Start a GET request to get the list of playlists that belongs to the user
        dotify = new Dotify(baseURL);
        this.appKey = appKey;
        this.username = username;
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        dotifyHttpInterface = dotify.getHttpInterface();

    }

    /**
     * Checks to see if the user's credentials match. If they do, allow them access.
     * @param username The username to authenticate
     * @param password The password to authenticatre
     * @return a number that correlates with success
     */
    public void loginDotifyUser(final String username, final String password, final Context activityContext){

        //Create the GET request
        Call<DotifyUser> request = dotifyHttpInterface.getUser(
                appKey,
                username,
                password
        );

        request.enqueue(new Callback<DotifyUser>() {
            @Override
            public void onResponse(Call<DotifyUser> call, retrofit2.Response<DotifyUser> response) {
                if (response.isSuccessful()){
                    int respCode = response.code();
                    if (respCode == Dotify.ACCEPTED) {
                        DotifyUser dotifyUser = response.body();
                        UserUtilities.cacheUser(activityContext, dotifyUser);
                        LoginFragment.loginResponse(LoginFragment.ResponseCode.SUCCESS);
                    }
                }
                else{
                    Log.d(TAG, "loginUser-> onResponse: Invalid Credentials : " + response.code());
                    //User needs to retry to log in
                    LoginFragment.loginResponse(LoginFragment.ResponseCode.FAIL);
                }
            }

            @Override
            public void onFailure(Call<DotifyUser> call, Throwable throwable) {
                Log.w(TAG, "loginUser-> onFailure");
                //Error message that the server is down
                LoginFragment.loginResponse(LoginFragment.ResponseCode.SERVER_ERROR);

            }
        });

    }

    /**
     * Get's the 2 security questions from the given username. It will add to the List<String>
     * listOfSecQuestions the security Questions.
     * @param username The username of the person you want to do the retreival from
     * @return listOfSecQuestions Returns a list of security questions that belong to the user or null if none found
     */
    public static Call<ResponseBody> getSecurityQuestions(final String username){
        //Create the GET request
        Call<ResponseBody> request = dotifyHttpInterface.getResetQuestions(
                appKey,
                username
        );
        return request;
    }

    /**
     * Sends a request to the server to see if the security questions are correct
     * @param username The username of the user that you want to verify
     * @param securityAnswer1 The answer to the first Security Question
     * @param securityAnswer2 THe asnwer to the second Security Question
     * @return securityToken The securitytoken inside of a list
     */
    public static Call<ResponseBody> validateSecurityAnswers(final String username, final String securityAnswer1, final String securityAnswer2){
        //Create the GET request
        Call<ResponseBody> request = dotifyHttpInterface.validateSecAnswers(
                appKey,
                username,
                securityAnswer1,
                securityAnswer2
        );
        return request;
    }

    /**
     * Method that gets the playlists
     * @return The string of playlists
     */
    public static void getUserplaylistsList(){
        Call<List<String>> getPlaylistsList = dotifyHttpInterface.getPlaylistsList(
                appKey,
                username
        );
        getPlaylistsList.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "getPlaylist-> onResponse: Success Code : " + response.code());
                    //gets a list of strings of playlist names
                    List<String> userPlaylist = response.body();
                    PlaylistFragment.displayPlaylistsList(userPlaylist);
                } else {
                    //If unsuccessful, show the response code
                    Log.d(TAG, "getPlaylist-> Unable to retrieve playlist " + response.code());
                }
            }

            //If something is wrong with our request to the server, goes to this method
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d(TAG, "Invalid failure: onFailure");
            }
        });

    }

    /**
     * display the song lists given the playlist
     * @param playListTitle the playlist to get the song from
     * @return songsList the list of dotifysongs
     */
    public static void getSongsFromPlaylist(String playListTitle){

        //Start Get Request
        Call<ResponseBody> getSongsFromPlaylist = dotifyHttpInterface.getSongsFromPlaylist(
                appKey,
                username,
                playListTitle
        );

        getSongsFromPlaylist.enqueue(new Callback<ResponseBody>() {
            /**
             * display a success message
             * @param call - request to server
             * @param response - server's response
             */
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "getPlaylist-> onResponse: Success Code : " + response.code());
                    //gets a list of strings of playlist names
                    ResponseBody mySong = response.body();
                    try {
                        JsonObject currSongList= JSONUtilities.ConvertStringToJSON(mySong.string());
                        SongsListFragment.displaySongsList(currSongList.getAsJsonArray("songs"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    //If unsucessful, show the response code
                    Log.d(TAG, "getPlaylist-> Unable to retreive playlists " + response.code());
                }
            }

            /**
             * If something is wrong with our request to the server, goes to this method
             * @param call - request to server
             * @param t - unnecessary parameter
             */
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Invalid failure: onFailure");
            }
        });
    }

    /**
     * Method to get the search results
     * @param currSearchQuery
     */
    public static void getSearchResult(String currSearchQuery) {
        Call<ResponseBody> querySearch = dotifyHttpInterface.querySong(
                appKey,
                currSearchQuery
        );

        querySearch.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.code() == Dotify.OK) {
                        String serverResponse = response.body().string();
                        JsonObject jsonResponse = JSONUtilities.ConvertStringToJSON(serverResponse);

                        JsonArray songQuery = jsonResponse.getAsJsonArray("songs");
                        JsonArray artistQuery = jsonResponse.getAsJsonArray("artist");
                        //Call the method to display the result
                        SearchFragment.displaySearchResultSong(songQuery);
                        SearchFragment.displaySearchResultArtists(artistQuery);

                    }

                } catch (IOException ex) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * Method that is used in search.
     * @param artistName The artist name to query
     */
    public static void getSongByArtist(String artistName){
        Call<ResponseBody> getSongsByArtist = dotifyHttpInterface.getSongsByArtist(
                appKey,
                artistName
        );
        //Ask anthony to get rid of quotation mark
        getSongsByArtist.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String serverResponse = response.body().string();
                    JsonObject jsonResponse = JSONUtilities.ConvertStringToJSON(serverResponse);
                    System.out.println(jsonResponse);


                } catch (IOException e) {

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
