package com.example.thai.dotify;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MiniMusicControllerFragment extends Fragment {

    private ImageButton playPauseMusicButton;
    private TextView songTitleInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mini_music_controller, container, false);

        // Initialize view layout
        playPauseMusicButton = (ImageButton) view.findViewById(R.id.mini_music_player_play_pause_image_button);
        songTitleInfo = (TextView) view.findViewById(R.id.mini_music_player_song_info_text_view);

        return view;
    }
}
