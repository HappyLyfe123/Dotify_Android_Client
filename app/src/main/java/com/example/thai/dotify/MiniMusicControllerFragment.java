package com.example.thai.dotify;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MiniMusicControllerFragment extends Fragment{

    private ImageButton playPauseMusicButton;
    private TextView songTitleInfo;
    private ConstraintLayout currLayout;
    private OnChangeFragmentListener onChangeFragmentListener;

    public interface OnChangeFragmentListener {
        void buttonClicked(MainActivityContainer.AuthFragmentType fragmentType);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     *
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onChangeFragmentListener = (OnChangeFragmentListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }



}
