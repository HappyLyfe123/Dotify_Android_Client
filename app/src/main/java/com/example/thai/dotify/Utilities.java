package com.example.thai.dotify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Utilities {

    public static JsonObject ConvertStringToJSON(String convertString){
        JsonParser parser = new JsonParser();
        return (JsonObject) parser.parse(convertString);
    }


    /***
     * creates view for the object
     * @param songID the song id to be check
     * @return true if there's already a song with the same id in the playlist
     */
    public static boolean checkPlaylistForDuplicateSong(String songID){
        return true;
    }
}
