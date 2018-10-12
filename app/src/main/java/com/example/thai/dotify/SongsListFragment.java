package com.example.thai.dotify;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class SongsListFragment extends Fragment{

    private ImageButton backButton;
    private TextView titleTextView;
    private RecyclerView songListRecycleView;
    private List<Song> songsList = new ArrayList<>();
    private SongsAdapter songsAdapter;
    private final String LOCATION_PLAYLIST = "playlist";
    private final String LOCATION_ARTIST = "artist";

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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        titleTextView = (TextView) view.findViewById(R.id.song_list_title_text_view);
        songListRecycleView = (RecyclerView) view.findViewById(R.id.song_list_recycle_view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerViewClickListener listener = (view, position) -> {


        };
        //Display all of the items into the recycler view
        songsAdapter = new SongsAdapter(songsList, listener);
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        songListRecycleView.setLayoutManager(songLayoutManager);
        songListRecycleView.setItemAnimator(new DefaultItemAnimator());
        songListRecycleView.setAdapter(songsAdapter);
        setFragmentTitle();
    }

    //Set the title for the current fragment to playlist name
    public static void setPlayListTitle(String title){
        playListTitle = title;
    }

    //Set the title for the current fragment to playlist name
    private void setFragmentTitle(){
        titleTextView.setText(playListTitle);
    }

    /***
     * create View object of fragment
     * @param name the name of the item that
     * @param location the location of where to get it from
     *
     */
    private void getMusicList(String name, String location){
        switch (location.toLowerCase()){
            //Get the songs from playlist
            case LOCATION_PLAYLIST:
                break;
            //Get the songs from a specific artist
            case LOCATION_ARTIST:
                break;
        }
    }

}
