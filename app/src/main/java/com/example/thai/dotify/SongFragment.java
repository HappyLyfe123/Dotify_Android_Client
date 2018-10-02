package com.example.thai.dotify;

/*import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;*/
import org.json.JSONObject;



public class SongFragment
{
    prviate static int id = 100;
    private string title;
    private string artist;
    private string album;
    private byte[] image;
    private byte[] music;
    private int songId;

    public SongFragment(string song, string Artist, string Alb, byte[] pic, byte[] mus)
    {
        title = song;
        artist = Artist;
        album = Alb;
        image = pic;
        music = mus;
        songId = id + 1;
        //JSONObject obj1 = toJSON();
        string SongJSON = Stringify(this);

    }

    public int getID() {
        return songId;
    }
    public string getTitle() {
        return title;
    }
    public string getAlbum() {return album; }
    public string getArtist() {
        return artist;
    }
    public byte[] getImage() {
        return image;
    }
    public byte[] getMusic() {
        return music;
    }
    public void setID(int id) {
        songid = id;
    }
    public void setTitle(string songTitle) {
        title = songTitle;
    }
    public void setArtist(string artist1) {
        artist = artist1;
    }
    public void setAlbum(string alb) {
        album = alb;
    }
    public void setImage(byte[] img) {
        image = img;
    }
    public void setMusic(byte[] song) {
        music = song;
    }

    public static string Stringify(object obj)
    {
        return JsonConvert.SerializeObject(obj);
    }

}