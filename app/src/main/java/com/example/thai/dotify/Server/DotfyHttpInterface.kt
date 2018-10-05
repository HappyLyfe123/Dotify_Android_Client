package com.example.thai.dotify.Server

import com.example.thai.dotify.DotifyUser
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
                   @Field("question1") securityQuestion1: String,
                   @Field("question2") securityQuestion2: String,
                   @Field("answer1") securityAnswer1: String,
                   @Field("answer2") securityAnswer2: String?)
            : retrofit2.Call<DotifyUser>
}