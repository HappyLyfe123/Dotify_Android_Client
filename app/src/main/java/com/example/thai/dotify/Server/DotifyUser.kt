package com.example.thai.dotify

import java.io.Serializable
import java.util.*

data class DotifyUser(val username: String,
                      val password: String,
                      val question1: String,
                      val question2: String,
                      val answer1: String,
                      val answer2 : String) : Serializable {
   // val token: String?
//    var name: String?

    init{
 //       token = null
 //       name = null
    }

    /**
     * Turns the user's information into a map
     */
    fun toMap() : HashMap<String,String?> {
        //Initialize the HashMap
        val userInfoMap : HashMap<String, String?> = HashMap<String,String?>()
        userInfoMap.put("username", username)
        userInfoMap.put("password", password)
        return userInfoMap
    }
}
