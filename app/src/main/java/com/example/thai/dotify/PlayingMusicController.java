package com.example.thai.dotify;

public class PlayingMusicController {
    private static boolean isSongPlaying;

    public PlayingMusicController(){

    }

    public static void setSongPlayingStatus(boolean currStatus){
        isSongPlaying = currStatus;
    }

    public static boolean getSongPlayingStatus(){
        return isSongPlaying;
    }

}
