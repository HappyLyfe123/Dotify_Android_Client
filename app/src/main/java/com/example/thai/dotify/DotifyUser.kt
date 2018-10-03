package com.example.thai.dotify

import java.io.Serializable
import java.util.*

data class DotifyUser(val username: String,
                      val password: String,
                      val securityQuestion1: String,
                      val securityAnswer1: String,
                      val securityQuestion2: String,
                      val securityAnswer2: String?) : Serializable {
   // val token: String?
//    var name: String?

    init{
 //       token = null
 //       name = null
    }

    /**
     * Turns the user's information into a map
     */
    fun toMap() : Map<String,String?> {
        //Initialize the HashMap
        val userInfoMap : HashMap<String, String?> = HashMap<String,String?>()
        userInfoMap.put("username", username)
        userInfoMap.put("password", password)
        userInfoMap.put("question1", securityQuestion1)
        userInfoMap.put("answer1", securityAnswer1)
        userInfoMap.put("question1", securityQuestion2)
        userInfoMap.put("answer1", securityAnswer2)
        return userInfoMap.toMap()
    }
}
