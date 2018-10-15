package com.example.thai.dotify;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivityContainer extends AppCompatActivity
        implements PlaylistFragment.OnChangeFragmentListener{

    private TextView mTextMessage;
    private SearchFragment searchFragment;
    private PlaylistFragment playlistFragment;
    private ProfileInfoFragment profileInfoFragment;
    private ForYouFragment forYouFragment;
    private CreatePlaylistFragment createPlaylistFragment;
    private MiniMusicControllerFragment miniMusicControllerFragment;
    private SongsListFragment songListScreenFragment;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout miniMusicControllerLayout;
    private FrameLayout mainDisplayLayout;
    private SearchView searchView;
    private static boolean isMusicPlaying;
    private PlayingMusicController musicController;
    private DotifyUser user;

    //list of pages
    public enum PlaylistFragmentType{
        SEARCH,
        PLAYLISTS,
        FOR_YOU,
        PROFILE,
        CREATE_PLAYLIST,
        SONGS_LIST_PAGE,
        FULL_SCREEN_MUSIC,
        BACK_BUTTON

    }

    /***
     * invoked at the very beginning
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);

        // Initialize Main Activity Variables
        user = UserUtilities.getCachedUserInfo(this);

        //Initialize view layout
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        miniMusicControllerLayout = (FrameLayout) findViewById(R.id.mini_music_player_controller_frame);
        mainDisplayLayout = (FrameLayout) findViewById(R.id.main_display_frame);

        //Instantiate fragments
        searchFragment = new SearchFragment();
        playlistFragment = new PlaylistFragment();
        playlistFragment.setOnChangeFragmentListener(this);
        profileInfoFragment = new ProfileInfoFragment();
        profileInfoFragment.setOnUserImageUploadedListener((dotifyUser) ->
            // Updates the current user object
            user = dotifyUser
        );
        forYouFragment = new ForYouFragment();
        songListScreenFragment = new SongsListFragment();
        // Set the song clicked listener for songListScreenFragment
        songListScreenFragment.setOnChangeFragmentListener((fragmentType, musicController) -> {
            // Set the current music controller object
            this.musicController = musicController;
            startFragment(PlaylistFragmentType.FULL_SCREEN_MUSIC, true,
                    true);
        });
        createPlaylistFragment = createPlaylistFragment.newInstance();
        miniMusicControllerFragment = miniMusicControllerFragment.newInstance();

        isMusicPlaying = false;
        //create bottom navigation bar
//        UserUtilities.getCachedUserInfo(activityContext);
        createMiniMusicControllerView();
        createBottomNavigationView();
    }

    /***
     * invoked when button is selected
     * @param fragmentType
     */
    @Override
    public void buttonClicked(MainActivityContainer.PlaylistFragmentType fragmentType) {
        switch (fragmentType){
            case SONGS_LIST_PAGE:
                startFragment(PlaylistFragmentType.SONGS_LIST_PAGE, true, true);
                break;
            case BACK_BUTTON:
                startFragment(PlaylistFragmentType.BACK_BUTTON, false, false);
        }
    }

    /***
     * assigns playlist title to user-provided playlist title
     * @param title
     */
    @Override
    public void setTitle(String title) {
        SongsListFragment.setPlayListTitle(title);
    }

    /***
     * display playlist, search, and profile fragments via icons
     */
    private BottomNavigationView.OnNavigationItemSelectedListener NavItemListen =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.playlists:
                            startFragment(PlaylistFragmentType.PLAYLISTS, true, false);
                            break;
                        case R.id.search:
                            startFragment(PlaylistFragmentType.SEARCH,true, false);
                            break;
                        case R.id.for_you:
                            startFragment(PlaylistFragmentType.FOR_YOU,true, false) ;
                            break;
                        case R.id.profile:
                            //go to user account
                            startFragment(PlaylistFragmentType.PROFILE,true, false);
                            break;
                    }
                    return true;
                }
            };

    /**
     * Starts a fragment for the user
     *
     * @param fragmentType The id of the fragment that is going to start
     * @return True if the fragment has started correctly
     */
    private boolean startFragment(PlaylistFragmentType fragmentType, boolean setTransition, boolean addToBackStack){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (fragmentType) {
            case SEARCH:
                fragmentTransaction.replace(mainDisplayLayout.getId(), searchFragment);
                break;
            case PLAYLISTS:
                fragmentTransaction.replace(mainDisplayLayout.getId(), playlistFragment);
                break;
            case FOR_YOU:
                fragmentTransaction.replace(mainDisplayLayout.getId(), forYouFragment);
                break;
            case PROFILE:
                fragmentTransaction.replace(mainDisplayLayout.getId(), profileInfoFragment);
                break;
            case CREATE_PLAYLIST:
                fragmentTransaction.replace(mainDisplayLayout.getId(),createPlaylistFragment);
                break;
            case FULL_SCREEN_MUSIC:
                // Initialize a FulLScreenMusicControllerFragment
                FullScreenMusicControllerFragment fullScreenMusicControllerFragment = new FullScreenMusicControllerFragment();
                // Set the current music controller for the FullScreenMusicControllerFragment
                fullScreenMusicControllerFragment.setMusicController(musicController);
                // set the fragment to be displayed
                fragmentTransaction.replace(R.id.main_display_frame, fullScreenMusicControllerFragment);
                break;
            case SONGS_LIST_PAGE:
                fragmentTransaction.replace(R.id.main_display_frame, songListScreenFragment);
                break;
            case BACK_BUTTON:
                getFragmentManager().popBackStackImmediate();
        }
        if(setTransition){
            fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        if(addToBackStack){
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
        return true;
    }

    /***
     * invoked when back button is pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startFragment(PlaylistFragmentType.BACK_BUTTON, false,false);
    }

    /**
     * Creates the bottom navigation for the activity container. Sets the home screen as default.
     */
    private void createBottomNavigationView() {
        //Disables automatic shifting from the bottom navigation
        BottomNavigationBarShiftHelp.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.playlists);
        bottomNavigationView.setOnNavigationItemSelectedListener(NavItemListen);
        startFragment(PlaylistFragmentType.PLAYLISTS, false, false);

    }

    /**
     * Create mini music controller view
     */
    private void createMiniMusicControllerView(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(miniMusicControllerLayout.getId(), miniMusicControllerFragment);
        transaction.commit();
    }

    /**
     * Create full screen music player
     */
    public void fullMusicScreenClicked(View view) {
        miniMusicControllerLayout.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        startFragment(PlaylistFragmentType.FULL_SCREEN_MUSIC, false, true);
    }

    /**
     * Create a mini screen of music player
     */
    public void miniMusicScreenClicked(View view){
        bottomNavigationView.setVisibility(View.VISIBLE);
        miniMusicControllerLayout.setVisibility(View.VISIBLE);
        miniMusicControllerFragment.changeMusicPlayerButtonImage();
        startFragment(PlaylistFragmentType.BACK_BUTTON, false, false);
    }



    /**
     * Returns the current user object
     */
    public DotifyUser getCurrentUser(){
        return user;
    }
}
