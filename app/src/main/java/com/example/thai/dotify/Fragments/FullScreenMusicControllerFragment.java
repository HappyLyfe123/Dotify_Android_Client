package com.example.thai.dotify.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.MainActivityContainer;
import com.example.thai.dotify.PlayingMusicController;
import com.example.thai.dotify.Adapters.PlaylistsAdapter;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Server.DotifySong;

import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.support.constraint.Constraints.TAG;

/**
 * this object will display the information for a song object at full screen
 */
public class FullScreenMusicControllerFragment extends Fragment implements View.OnClickListener{

    private TextView songTitleTextView;
    private TextView songArtistNameTextView;
    private ImageView songImageView;
    private ImageButton addToPlaylistButton;
    private ImageButton previousSongImageButton;
    private ImageButton nextSongImageButton;
    private ImageButton playPauseImageButton;
    private ImageButton likeSongImageButton;
    private RecyclerView selectPlaylistList;
    public static SeekBar songSeekBar;
    private static PlayingMusicController musicController;
    private DotifyUser user;

    /**
     * create an object of type FullScreenMusicControllerFragment
     * @param currController - object of type PlayingMusicController to attach to new object
     * @return
     */
    public static FullScreenMusicControllerFragment newInstance(PlayingMusicController currController){
        FullScreenMusicControllerFragment fragment = new FullScreenMusicControllerFragment();
        musicController = currController;
        return fragment;
    }

    /**
     * creates view for FullScreenMusicControllerFragment object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return new View object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_music_controller, container, false);

        //Initialize view layout
        songTitleTextView = (TextView) view.findViewById(R.id.full_screen_song_title_text_view);
        songArtistNameTextView = (TextView) view.findViewById(R.id.full_screen_song_artist_text_view);
        songImageView = (ImageView) view.findViewById(R.id.full_screen_song_image_view);
        addToPlaylistButton = (ImageButton) view.findViewById(R.id.full_screen_add_to_playlist_button);
        previousSongImageButton = (ImageButton) view.findViewById(R.id.full_screen_previous_track_image_button);
        nextSongImageButton = (ImageButton) view.findViewById(R.id.full_screen_next_track_image_button);
        playPauseImageButton = (ImageButton) view.findViewById(R.id.full_screen_play_pause_image_button);
        songSeekBar = (SeekBar) view.findViewById(R.id.full_screen_song_seekBar);
        likeSongImageButton = (ImageButton) view.findViewById(R.id.full_screen_like_button_image);

        //Set listener
        addToPlaylistButton.setOnClickListener(this);
        previousSongImageButton.setOnClickListener(this);
        previousSongImageButton.setOnClickListener(this);
        nextSongImageButton.setOnClickListener(this);
        playPauseImageButton.setOnClickListener(this);
        likeSongImageButton.setOnClickListener(this);

        // Initialize Variables
        user = ((MainActivityContainer) this.getActivity()).getCurrentUser();


        return view;
    }

    /**
     * add extra fragments or stuff to object
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the current song's information and display it to the screen
        setMusicPlayerButtonImage();
        setSongInfo();
    }


    /**
     * Sets the music controller for the current this fragment
     * @param playingMusicController A PlayingMusicController object that contains a list of songs
     *                               and the currently playing song
     */
    public void setMusicController(PlayingMusicController playingMusicController){

    }



    /**
     * invoked when user wants to do something with the song
     * @param v - View object displaying the FullScreenMusicControllerFragment object
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.full_screen_add_to_playlist_button:
                getSelectedPlaylist();
                break;
            case R.id.full_screen_previous_track_image_button:
                break;
            case R.id.full_screen_next_track_image_button:
                break;
            case R.id.full_screen_play_pause_image_button:{
                if (musicController.isSongPlaying()) {
                    musicController.pauseMusic();
                    musicController.setSongStatus(false);
                } else {
                    musicController.playMusic();
                    musicController.setSongStatus(true);
                }
                setMusicPlayerButtonImage();
            }
            break;
            case R.id.full_screen_like_button_image:
                likeSongImageButton.setImageResource(R.drawable.already_like_song_icon);
                break;
        }
    }

    public void setSongInfo(){
        if(musicController.getSongCount() > 0){
            songTitleTextView.setText(musicController.getCurrSongTitle());
            songArtistNameTextView.setText(musicController.getCurrSongArtist());
        }
    }

    /**
     * Change the button image depend on if a song is playing or not
     */
    public void setMusicPlayerButtonImage(){
        if(musicController.isSongPlaying()){
            //Pause the music and show the play button
            playPauseImageButton.setImageResource(R.drawable.big_pause_button_icon);
        }
        else{
            //Play the music and show the pause button
            playPauseImageButton.setImageResource(R.drawable.big_play_button_icon);
        }
    }

    /**
     * Get the playlist name that the user selected
     */
    private void getSelectedPlaylist(){
        final int[] selectPosition = {};
        //Create an instance of the Alert Dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        //Set the View of the Alert Dialog
        final View alertDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_select_playlist, null);
        alertDialogBuilder.setView(alertDialogView);
        AlertDialog currDialogBox = alertDialogBuilder.create();

        //Initialize view
        selectPlaylistList = alertDialogView.findViewById(R.id.select_playlist_playlist_list);
        Button cancelButton = alertDialogView.findViewById(R.id.select_playlist_cancel_button);
        // Initialize the recycler view listener
        RecyclerViewClickListener playlistItemClickListener = (listView, position) -> {
            // Create a music controller object
            currDialogBox.dismiss();
        };

        //Cancel to close the select playlist view
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currDialogBox.dismiss();
            }
        });
        //Display all of the items into the recycler view
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        selectPlaylistList.setLayoutManager(songLayoutManager);
        selectPlaylistList.setItemAnimator(new DefaultItemAnimator());

        //Create Alert DialogBox
        currDialogBox.show();
    }

    /**
     *  Add the song to the playlist that the user seleted
     * @param playlistName name of the playlist the user selected
     */
    private void addSongToPlaylist(String playlistName){
        System.out.println(user.getUsername());
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        Call<ResponseBody> addSongToPlaylist = dotifyHttpInterface.addSongToPlaylist(
                getString(R.string.appKey),
                user.getUsername(),
                playlistName,
                musicController.getCurrentSongGUID()
        );
        addSongToPlaylist.enqueue(new Callback<ResponseBody>() {
            /**
             * server sends a reply to the client indicating successful action
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "loginUser-> onResponse: Success Code : " + response.code());
                } else {
                    //A playlist with the same name already exist
                }

            }

            /**
             * server sends reply indicating a failure on server's side
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG,"Invalid failure: onFailure");
            }
        });

    }


}