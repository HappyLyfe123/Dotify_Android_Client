package com.example.thai.dotify;

/*import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;*/

import org.json.JSONException;
import org.json.JSONObject;



public class SongFragment
{
    static int id = 100;
    private String title;
    private String artist;
    private String album;
    private byte[] image;
    private byte[] music;
    private int songId;

    public SongFragment(String song, String Artist, String Alb, byte[] pic, byte[] mus)
    {
        title = song;
        artist = Artist;
        album = Alb;
        image = pic;
        music = mus;
        songId = id + 1;
        //JSONObject obj1 = toJSON();
        String SongJSON = Stringify(this);

    }

    public int getID() {
        return songId;
    }
    public String getTitle() {
        return title;
    }
    public String getAlbum() {return album; }
    public String getArtist() {
        return artist;
    }
    public byte[] getImage() {
        return image;
    }
    public byte[] getMusic() {
        return music;
    }
    public void setID(int id) {
        songId = id;
    }
    public void setTitle(String songTitle) {
        title = songTitle;
    }
    public void setArtist(String artist1) {
        artist = artist1;
    }
    public void setAlbum(String alb) {
        album = alb;
    }
    public void setImage(byte[] img) {
        image = img;
    }
    public void setMusic(byte[] song) {
        music = song;
    }

    public static String Stringify(Object obj) throws JSONException
    {
        SongFragment song = (SongFragment) obj;
        JSONObject jsonOBJ = new JSONObject();
        jsonOBJ.put(song.title,song.songId);
        jsonOBJ.put(song.getArtist(),song.getID());
        jsonOBJ.put(song.getAlbum(),song.getID());
        return jsonOBJ.toString();
    }

}