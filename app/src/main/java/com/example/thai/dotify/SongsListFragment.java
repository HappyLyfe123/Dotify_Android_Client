package com.example.thai.dotify;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class SongsListFragment extends Fragment implements View.OnClickListener {

    private ImageButton backButton;
    private TextView titleTextView;
    private RecyclerView songListRecycleView;

    private static String playListTitle;

    public SongsListFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /***
     * create View object of fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return new View object of type SongsListFragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        backButton = (ImageButton) view.findViewById(R.id.song_list_back_image_button);
        backButton.setOnClickListener(this);
        titleTextView = (TextView) view.findViewById(R.id.song_list_title_text_view);
        return view;
    }

    /***
     * invoked when a View object is created
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFragmentTitle();
    }

    public static void setPlayListTitle(String title){
        playListTitle = title;
    }

    /***
     * invoked when button is selected
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.song_list_back_image_button:

                break;
        }
    }

    private void setFragmentTitle(){
        titleTextView.setText(playListTitle);
    }
}
