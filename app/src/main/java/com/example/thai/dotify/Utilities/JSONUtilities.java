package com.example.thai.dotify.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class JSONUtilities {

    public static JsonObject ConvertStringToJSON(String convertString){
        JsonParser parser = new JsonParser();
        return (JsonObject) parser.parse(convertString);
    }

    /**
     * Changes a Json object to a string
     * @param json The json object to convert to a string
     * @return The string conversion
     */
    public static String jsonToString(Gson json, String stringContent){
        Gson gson = new Gson();
        String gsonContent = gson.fromJson(stringContent, String.class);
        return gsonContent;
    }

    /**
     * Converts a string to a Gson object
     * @param string
     * @return
     */
    public static Gson stringToJson(String string){
        Gson gson = new Gson();
        gson.toJson(string);
        return gson;
    }

}
