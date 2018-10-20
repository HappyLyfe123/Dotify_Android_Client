package com.example.thai.dotify.Utilities;

import android.util.Log;

import com.example.thai.dotify.Fragments.SearchFragment;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.google.gson.JsonArray;
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


    public GetFromServerRequest(String baseURL, String appKey, String username) {
        //Start a GET request to get the list of playlists that belongs to the user
        dotify = new Dotify(baseURL);
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        dotifyHttpInterface = dotify.getHttpInterface();
        this.appKey = appKey;
        this.username = username;
    }

    //
    public static List<String> getPlaylist(String playlistName) {
        List<String> playlistList = new ArrayList<>();
        Call<List<String>> getPlaylist = dotifyHttpInterface.getPlaylist(
                appKey,
                username,
                playlistName //Why do we need this to get the list of playlists?
        );

        getPlaylist.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "getPlaylist-> onResponse: Success Code : " + response.code());
                    //gets a list of strings of playlist names
                    List<String> userPlaylist = response.body();

                    //Converts the playlist we got to a list of playlists instead of a list of strings
                    for (int i = 0; i < userPlaylist.size(); i++) {
                        playlistList.add(userPlaylist.get(i));
                    }
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

        return playlistList;
    }

    //
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

    public static void getSongByArtist(String artistName){
        Call<ResponseBody> getSongsByArtist = dotifyHttpInterface.getSongsByArtist(
                appKey,
                artistName
        );
        //Ask anthony to get rid of quotation mark
        System.out.println(artistName);
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

