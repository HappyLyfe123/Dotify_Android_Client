package com.example.thai.dotify.Utilities;


import com.google.gson.JsonElement;

//A class object for the specific artist
public class SearchArtist{

    private String artistName;
    private JsonElement artistInfo;

    /**
     * Constructor for Artist
     */
    public SearchArtist(String artistName, JsonElement artistInfo){
        this.artistName = artistName;
        this.artistInfo = artistInfo;
    }

    /**Set the artist name
     *
     * @param name the name of the artist
     */
    public void setArtistName(String name){
        this.artistName = name;
    }

    /**
     * Get artist name
     * @return artist name
     */
    public String getArtistName(){
        return artistName;
    }

    public JsonElement getArtistInfo(){
        return artistInfo;
    }



}
