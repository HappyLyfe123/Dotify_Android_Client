package com.example.thai.dotify.Server

import com.example.thai.dotify.DotifyUser
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

    @GET("users")
    fun getUser(@Header("appKey") appKey: String,
                 @Header("username") username: String,
                 @Header("password") password: String)
            : Call<DotifyUser>
}