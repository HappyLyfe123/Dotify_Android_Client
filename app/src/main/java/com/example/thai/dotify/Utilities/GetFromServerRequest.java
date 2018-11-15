package com.example.thai.dotify.Utilities;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;

import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

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
     * Get's the 2 security questions from the given username. It will add to the List<String>
     * listOfSecQuestions the security Questions.
     * @param username1 The username of the person you want to do the retreival from
     * @return listOfSecQuestions Returns a list of security questions that belong to the user or null if none found
     */
    public Call<ResponseBody> getSecurityQuestions(final String username1){
        username = username1;

        //Create the GET request
        Call<ResponseBody> secQuestions = dotifyHttpInterface.getResetQuestions(
                appKey,
                username
        );
        return secQuestions;
    }

    /**
     * Sends a request to the server to see if the security questions are correct
     * @param username The username of the user that you want to verify
     * @param securityAnswer1 The answer to the first Security Question
     * @param securityAnswer2 THe asnwer to the second Security Question
     * @return securityToken The securitytoken inside of a list
     */
    public Call<ResponseBody> validateSecurityAnswers(final String username, final String securityAnswer1, final String securityAnswer2){
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
    public Call<ResponseBody> getUserplaylistsList(){
        Call<ResponseBody> getPlaylistsList = dotifyHttpInterface.getPlaylistsList(
                appKey,
                username
        );

        return getPlaylistsList;
    }


    /**
     * display the song lists given the playlist
     * @param playListTitle the playlist to get the song from
     * @return songsList the list of dotifysongs
     */
    public Call<ResponseBody> getSongsFromPlaylist(String playListTitle){

        //Start Get Request
        Call<ResponseBody> songsFromPlaylist = dotifyHttpInterface.getSongsFromPlaylist(
                appKey,
                username,
                playListTitle
        );

        return songsFromPlaylist;
    }

    /**
     * Method to get the search results
     * @param currSearchQuery
     */
    public Call<ResponseBody> getSearchResult(String currSearchQuery) {
        Call<ResponseBody> querySearch = dotifyHttpInterface.querySong(
                appKey,
                currSearchQuery
        );

        return querySearch;

    }

    /**
     * Method that is used in search.
     * @param artistName The artist name to query
     */
    public Call<ResponseBody> getSongByArtist(String artistName){
        Call<ResponseBody> getSongsByArtist = dotifyHttpInterface.getSongsByArtist(
                appKey,
                artistName
        );

        return getSongsByArtist;
    }

    /**
     * Method that initializes the peer
     * @return The method to be enqueued
     */
    public Call<ResponseBody> initializePeer() {
        Call<ResponseBody> initializePeerRequest = dotifyHttpInterface.initializePeer(
                appKey
        );

        return initializePeerRequest;
    }

}
