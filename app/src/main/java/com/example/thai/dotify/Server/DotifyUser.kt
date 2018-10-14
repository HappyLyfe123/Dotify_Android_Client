package com.example.thai.dotify

import java.io.Serializable
import java.util.*

data class DotifyUser(val username: String,
                      val password: String?,
                      val question1: String?,
                      val question2: String?,
                      val answer1: String?,
                      val answer2 : String?) : Serializable {

    private var userImage : ByteArray?

    init{
        userImage = null
    }

    /**
     * Sets the user's image
     */
    fun setUserImage(imageBytes :ByteArray){
        userImage = imageBytes
    }
}
