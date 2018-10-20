package com.example.thai.dotify.Utilities;

import android.util.Log;
import android.view.View;

import com.example.thai.dotify.Fragments.SearchFragment;
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

    /**
     * Constructor
     * @param baseURL baseURL inside the strings folder
     * @param appKey appKey inside the strings folder
     * @param username the user's username
     */
    public GetFromServerRequest(String baseURL, String appKey, String username){
        //Start a GET request to get the list of playlists that belongs to the user
        dotify = new Dotify(baseURL);
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        dotifyHttpInterface = dotify.getHttpInterface();
        this.appKey = appKey;
        this.username = username;
    }

//        /**
//         * Method to get the list of playlists
//         */
//    public static List<String> getPlaylist(String playlistName){
//            List<String> playlistList = new ArrayList<>();
//        }

    /**
     * Checks to see if the user's credentials match. If they do, allow them access.
     * @param username The username to authenticate
     * @param password The password to authenticatre
     * @return a number that correlates with success
     */
    public static int loginDotifyUser(final String username, final String password){
        final int[] errorCodeNum = new int[1];

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
                        errorCodeNum[0] = 0;
                    }
                }
                else{
                    Log.d(TAG, "loginUser-> onResponse: Invalid Credentials : " + response.code());
                    errorCodeNum[0] = 1;
                    //User needs to retry to log in
                }
            }

            @Override
            public void onFailure(Call<DotifyUser> call, Throwable throwable) {
                Log.w(TAG, "loginUser-> onFailure");
                //Error message that the server is down
                errorCodeNum[0] = 2;
            }
        });
        return errorCodeNum[0];
    }

    /**
     * Get's the 2 security questions from the given username. It will add to the List<String>
     * listOfSecQuestions the security Questions.
     * @param username The username of the person you want to do the retreival from
     * @return listOfSecQuestions Returns a list of security questions that belong to the user or null if none found
     */
    public static List<String> getSecurityQuestions(final String username){
        List<String> listOfSecQuestions = new ArrayList<>();

        //Create the GET request
        Call<ResponseBody> request = dotifyHttpInterface.getResetQuestions(
                appKey,
                username
        );

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    int respCode = response.code();
                    if (respCode == 200) {
                        Log.d(TAG, "getSecurityQuestions-> onResponse: Success Code : " + response.code());
                        ResponseBody obtained = response.body();
                        try{
                            String ob = obtained.string();
                            JsonObject securityQuestions = JSONUtilities.ConvertStringToJSON(ob);
                            String sq1 = securityQuestions.get("securityQuestion1").getAsString();
                            String sq2 = securityQuestions.get("securityQuestion2").getAsString();
                            listOfSecQuestions.add(sq1);
                            listOfSecQuestions.add(sq2);
                            Log.d(TAG, "The security questions are "+listOfSecQuestions.get(0)+listOfSecQuestions.get(1));
                            //Switch stubs here
                        }
                        catch(Exception ex){
                            Log.d(TAG, "This didn't work.");
                        }
                        //now send the user to the security question page
                    } else {
                        Log.d(TAG, "getSecurityQuestions-> onResponse: Invalid Credentials : " + response.code());
                        //If failed, the user needs to reenter their username
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.w(TAG, "resetQuestions-> onFailure");
                throwable.printStackTrace();
            }
        });
        return listOfSecQuestions;
    }

    /**
     * Sends a request to the server to see if the security questions are correct
     * @param username The username of the user that you want to verify
     * @param securityAnswer1 The answer to the first Security Question
     * @param securityAnswer2 THe asnwer to the second Security Question
     * @return securityToken The securitytoken inside of a list
     */
    public static List<String> validateSecurityAnswers(final String username, final String securityAnswer1, final String securityAnswer2){
        final List<String> securityToken = new ArrayList<>();

        //Create the GET request
        Call<ResponseBody> request = dotifyHttpInterface.validateSecAnswers(
                appKey,
                username,
                securityAnswer1,
                securityAnswer2
        );

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    int respCode = response.code();
                    if (respCode == 202) {
                        Log.d(TAG, "validateSecurityAnswers-> onResponse: Success Code : " + response.code());
                        //We get a security token back that we send with the user to reset their password
                        ResponseBody obtained = response.body();
                        try{
                            String ob = obtained.string();
                            JsonObject token = JSONUtilities.ConvertStringToJSON(ob);
                            securityToken.add(token.get("token").getAsString());
                        }
                        catch(Exception exception){
                            Log.d(TAG, "This didn't work.");
                        }
                    }
                }
                else{
                    Log.d(TAG, "validateSecurityAnswers-> onResponse: Invalid Credentials : " + response.code());
                    //Security Answers are incorrect. Ask user to try again
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.w(TAG, "validateSecurityAnswers-> onFailure");
            }
        });
        return securityToken;
    }


    /**
     * Method that gets the playlists
     * @param playlistName
     * @return The string of playlists
     */
    public static List<String> getPlaylistRequest(String playlistName){
        final List<String> playlistList = new ArrayList<>();

        Call<List<String>> getPlaylist = dotifyHttpInterface.getPlaylist(
                appKey,
                username,
                playlistName
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
     * @param artistName
     */
    public static void getSongByArtist(String artistName){
        Call<ResponseBody> getSongsByArtist = dotifyHttpInterface.getSongsByArtist(
                appKey,
                artistName
        );

        getSongsByArtist.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String serverResponse = response.body().string();
                    JsonObject jsonResponse = JSONUtilities.ConvertStringToJSON(serverResponse);


                } catch (IOException e) {

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    /**
     * display the song lists given the playlist
     * @param playListTitle the playlist to get the song from
     * @return songsList the list of dotifysongs
     */
    public static List<DotifySong> getSongList(String playListTitle){
        List<DotifySong> songsList = new ArrayList<>();

        //Start Get Request
        Call<ResponseBody> getSongList = dotifyHttpInterface.getSongList(
                appKey,
                username,
                playListTitle
        );

        getSongList.enqueue(new Callback<ResponseBody>() {
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
                        Gson gson = new Gson();
                        for(int x = 0; x < currSongList.get("songs").getAsJsonArray().size(); x++){
                            songsList.add(gson.fromJson(currSongList.get("songs").getAsJsonArray().get(x), DotifySong.class));
                        }
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
        return songsList;
    }
}
