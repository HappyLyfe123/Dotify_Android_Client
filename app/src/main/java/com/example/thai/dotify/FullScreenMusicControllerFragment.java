package com.example.thai.dotify;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;


public class FullScreenMusicControllerFragment extends Fragment implements View.OnClickListener{

    private TextView songInfoTextView;
    private ImageView songImageView;
    private ImageButton addToPlaylistButton;
    private ImageButton previousSongImageButton;
    private ImageButton nextSongImageButton;
    private ImageButton playPauseImageButton;
    public static SeekBar songSeekBar;
    private static boolean isSonPlaying;

    public static FullScreenMusicControllerFragment newInstance(PlayingMusicController currController){
        FullScreenMusicControllerFragment fragment = new FullScreenMusicControllerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_music_controller, container, false);

        //Initialize view layout
        songInfoTextView = (TextView) view.findViewById(R.id.full_screen_song_info_text_view);
        songImageView = (ImageView) view.findViewById(R.id.full_screen_song_image_view);
        addToPlaylistButton = (ImageButton) view.findViewById(R.id.full_screen_add_to_playlist_button);
        previousSongImageButton = (ImageButton) view.findViewById(R.id.full_screen_previous_track_image_button);
        nextSongImageButton = (ImageButton) view.findViewById(R.id.full_screen_next_track_image_button);
        playPauseImageButton = (ImageButton) view.findViewById(R.id.full_screen_play_pause_image_button);
        songSeekBar = (SeekBar) view.findViewById(R.id.full_screen_song_seekBar);

        //Set listener
        addToPlaylistButton.setOnClickListener(this);
        previousSongImageButton.setOnClickListener(this);
        previousSongImageButton.setOnClickListener(this);
        nextSongImageButton.setOnClickListener(this);
        playPauseImageButton.setOnClickListener(this);



        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(PlayingMusicController.getSongPlayingStatus()){
            playPauseImageButton.setImageResource(R.drawable.big_pause_button_icon);
        }
        else{
            playPauseImageButton.setImageResource(R.drawable.big_play_button_icon);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.full_screen_add_to_playlist_button:

                break;
            case R.id.full_screen_previous_track_image_button:
                break;
            case R.id.full_screen_next_track_image_button:
                break;
            case R.id.full_screen_play_pause_image_button:
                if(PlayingMusicController.getSongPlayingStatus()){
                    playPauseImageButton.setImageResource(R.drawable.big_play_button_icon);
                    PlayingMusicController.setSongPlayingStatus(false);
                }
                else{
                    playPauseImageButton.setImageResource(R.drawable.big_pause_button_icon);
                    PlayingMusicController.setSongPlayingStatus(true);
                }
                break;
        }
    }
}
