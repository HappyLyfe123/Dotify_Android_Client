package com.example.thai.dotify.Server

import com.example.thai.dotify.DotifyUser
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface DotifyHttpInterface {
    /**
     * Creates a dotify User  once they have created an account with us or it is their
     * first time signing in to this application
     */
    @FormUrlEncoded
    @POST("users")
    fun createUser(@Field("username") username: String,
                   @Field("password") password: String,
                   @Field("securityQuestion1") securityQuestion1: String,
                   @Field("securityQuestion2") securityQuestion2: String,
                   @Field("securityAnswer1") securityAnswer1: String,
                   @Field("securityAnswer2") securityAnswer2: String?)
            : retrofit2.Call<DotifyUser>

    /**
     * Get the username and password for authentication
     */
    @GET("users")
    fun getUser(@Header("appKey") appKey: String,
                @Header("username") username: String,
                @Header("password") password: String)
            : Call<DotifyUser>

    /**
     * Get the user's reset questions
     */
    @GET("users/reset?")
    fun getResetQuestions(@Header("appKey") appKey: String,
                          @Header("username") username: String)
            : Call<ResponseBody>

    /**
     * Get the token if the user answered the security questions correctly
     */
    @GET("users/reset-check")
    fun validateSecAnswers(@Header("appKey") appKey: String,
                           @Header("username") username: String,
                           @Header("securityAnswer1") securityAnswer1: String,
                           @Header("securityAnswer2") securityAnswer2: String)
            : Call<ResponseBody>


    @FormUrlEncoded
    @PUT("users")
    fun updatePassword(@Header("appKey") appKey:String,
                       @Field("token") token:String,
                       @Field("username") username:String,
                       @Field("password") password:String)
            : Call<DotifyUser>


    @PUT("playlist")
    fun createPlaylist(
            @Header("appKey") appKey: String,
            @Query("username") username: String,
            @Query("playlist") playlist: String)
            : Call<ResponseBody>

    @GET("playlist?")
    fun getPlaylist(
            @Header("appkey") appKey: String,
            @Query("username") username: String,
            @Query("playlistName") playlistName: String)
            : Call<List<String>>

    @GET("playlistpage?")
    fun getSongList(
            @Header("appkey") appKey: String,
            @Query("username") username: String,
            @Query("playlist") playlistName: String)
            :Call<ResponseBody>

    @DELETE("playlist")
    fun deletePlaylist(
            @Header("appkey") appKey : String,
            @Query("playlist") playlistName : String,
            @Query("username") username : String
    ) : Call<ResponseBody>

    @FormUrlEncoded
    @PUT("users/image")
    fun saveUserProfileImage(
            @Header("appkey") appKey : String,
            @Header("username") username : String,
            @Field("image") imageByteArray : String
    ) : Call<ResponseBody>

    @PUT("playlistpage?")
    fun addSongToPlaylist(
            @Header("appkey") appKey: String,
            @Query("username") username: String,
            @Query("playlist") playlistName: String,
            @Query("songid") songID : String
    ) : Call<ResponseBody>

    @GET("search")
    fun querySong(
            @Header("appkey") appKey: String,
            @Query("search") query: String
    ) : Call<ResponseBody>

}