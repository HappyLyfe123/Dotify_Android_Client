package com.example.thai.dotify.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.thai.dotify.PlayingMusicController;
import com.example.thai.dotify.R;
import com.example.thai.dotify.StartUpContainer;

/**
 * this object allows the user to play a song
 */
public class MiniMusicControllerFragment extends Fragment implements View.OnClickListener{

    private ImageButton playPauseMusicButton;
    private static PlayingMusicController musicController;
    private TextView songInfo;
    private ConstraintLayout currLayout;
    private boolean songStatus;
    private OnFragmentInteractionListener onFragmentInteractionListener;

    /**
     * object's own interface
     */
    public interface OnFragmentInteractionListener {
        void pauseSong();
        void playSong();
    }

    /***
     * instantiate new object
     * @return new MiniMusicControllerFragment object
     */
    public static MiniMusicControllerFragment newInstance(PlayingMusicController controller){
        MiniMusicControllerFragment fragment = new MiniMusicControllerFragment();
        musicController = controller;
        return fragment;
    }

    /***
     * create View object of fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return new View object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mini_music_controller, container, false);

        // Initialize view layout
        playPauseMusicButton = (ImageButton) view.findViewById(R.id.mini_music_player_play_pause_image_button);
        playPauseMusicButton.setOnClickListener(this);
        songInfo = (TextView) view.findViewById(R.id.mini_music_player_song_info_text_view);
        setSongInfo();
        setMusicPlayerButtonImage();

        return view;
    }

    /**
     * add extra features to fragment after it's created
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * add information about app's environment
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void setSongInfo(){
        if(musicController.getSongCount() > 0){
            songInfo.setText(String.format("%s \u2022 %s", musicController.getCurrSongTitle(),
                    musicController.getCurrSongArtist()));
        }

    }

    /***
     * invoked when one of the buttons on the page is selected
     * @param v - View object displaying the buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mini_music_player_play_pause_image_button:
                //Check the state of the music player
                if(musicController.isSongPlaying()){
                    musicController.setSongStatus(false);
                    musicController.pauseMusic();
                }
                else{
                    musicController.setSongStatus(true);
                    musicController.playMusic();
                }
                setMusicPlayerButtonImage();
                break;
        }
    }

    //Change the button image according to the music state
    public void setMusicPlayerButtonImage(){
        if(musicController.isSongPlaying()){
            //Pause the music and show the play button
            playPauseMusicButton.setImageResource(R.drawable.mini_pause_button_icon);
        }
        else{
            //Play the music and show the pause button
            playPauseMusicButton.setImageResource(R.drawable.mini_play_button_icon);
        }
    }
}
