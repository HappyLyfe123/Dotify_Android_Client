package com.example.thai.dotify;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Utilities {

    public static JsonObject ConvertStringToJSON(String convertString){
        JsonParser parser = new JsonParser();
        return (JsonObject) parser.parse(convertString);
    }


}
