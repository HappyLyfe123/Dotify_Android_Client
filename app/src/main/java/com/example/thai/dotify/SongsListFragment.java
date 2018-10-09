package com.example.thai.dotify;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SongsListFragment extends Fragment{

    private ImageButton backButton;
    private TextView titleTextView;
    private RecyclerView songListRecycleView;
    private List<Song> songsList = new ArrayList<>();
    private SongsAdapter songsAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;
    private static String playListTitle;

    public interface OnChangeFragmentListener{
        void buttonClicked(MainActivityContainer.PlaylistFragmentType fragmentType);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     *
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(SongsListFragment.OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        test();
    }

    public static void setPlayListTitle(String title){
        playListTitle = title;
    }

    //Set playlist name as title in song list view
    private void setFragmentTitle(){
        titleTextView.setText(playListTitle);
    }

    private void test(){
        Song song = new Song("Hi", "Hello", "Sam", true, "0001", 10);
        songsList.add(song);

        for(int x =0; x < 40; x++){
            song = new Song("Hi", "Hello", "Sam", true, "0001", 10);
            songsList.add(song);
        }
    }
}
