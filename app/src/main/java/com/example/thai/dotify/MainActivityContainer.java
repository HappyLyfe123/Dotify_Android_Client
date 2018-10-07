package com.example.thai.dotify;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivityContainer extends AppCompatActivity implements MiniMusicControllerFragment.OnChangeFragmentListener{

    private TextView mTextMessage;
    private SearchFragment searchFragment;
    private PlaylistFragment playlistFragment;
    private ProfileInfoFragment profileInfoFragment;
    private ForYouFragment forYouFragment;
    private CreatePlaylistFragment createPlaylistFragment;
    private MiniMusicControllerFragment miniMusicControllerFragment;
    private FullScreenMusicControllerFragment fullScreenMusicControllerFragment;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout miniMusicControllerLayout;
    private FrameLayout mainDisplayLayout;


    public enum AuthFragmentType{
        FULL_MUSIC_SCREEN,

    }

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//            }
//            return false;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);

        //Initialize view layout
        //mTextMessage = (TextView) findViewById(R.id.message);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        miniMusicControllerLayout = (FrameLayout) findViewById(R.id.mini_music_player_controller_frame);
        mainDisplayLayout = (FrameLayout) findViewById(R.id.main_display_frame);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Instantiate fragments
        searchFragment = new SearchFragment();
        playlistFragment = new PlaylistFragment();
        profileInfoFragment = new ProfileInfoFragment();
        forYouFragment = new ForYouFragment();
        createPlaylistFragment = createPlaylistFragment.newInstance();
        miniMusicControllerFragment = new MiniMusicControllerFragment();
        fullScreenMusicControllerFragment = new FullScreenMusicControllerFragment();

        //create bottom navigation bar
        createMiniMusicControllerView();
        createBottomNavigationView();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener NavItemListen =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    switch (item.getItemId()) {
                        case R.id.playlists:
                            fragmentTransaction.replace(mainDisplayLayout.getId(), playlistFragment);
                            break;
                        case R.id.search:
                            fragmentTransaction.replace(mainDisplayLayout.getId(), searchFragment);
                            break;
                        case R.id.for_you:
                            fragmentTransaction.replace(mainDisplayLayout.getId(), forYouFragment);
                            break;
                        case R.id.profile:
                            //go to user account
                            fragmentTransaction.replace(mainDisplayLayout.getId(), profileInfoFragment);
                            break;
                    }
                    fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();
                    return true;
                }
            };

    /**
     * Starts a fragment for the user
     *
     * @param fragmentId The id of the fragment that is going to start
     * @return True if the fragment has started correctly
     */
    private boolean startFragment(int fragmentId) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (fragmentId) {
            case R.id.search:
                fragmentTransaction.replace(mainDisplayLayout.getId(), searchFragment);
                break;
            case R.id.playlists:
                fragmentTransaction.replace(mainDisplayLayout.getId(), playlistFragment);
                break;
            case R.id.for_you:
                fragmentTransaction.replace(mainDisplayLayout.getId(), forYouFragment);
                break;
            case R.id.profile:
                fragmentTransaction.replace(mainDisplayLayout.getId(), profileInfoFragment);
                break;
            case R.id.create_playlist_button:
                fragmentTransaction.replace(mainDisplayLayout.getId(),createPlaylistFragment);
                break;
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        return true;
    }

    /**
     * Creates the bottom navigation for the activity container. Sets the home screen as default.
     */
    private void createBottomNavigationView() {
        //Disables automatic shifting from the bottom navigation
        BottomNavigationBarShiftHelp.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.playlists);
        bottomNavigationView.setOnNavigationItemSelectedListener(NavItemListen);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(mainDisplayLayout.getId(), playlistFragment);
        //Commit changes transaction
        fragmentTransaction.commit();

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
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_display_frame, fullScreenMusicControllerFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Create a mini screen of music player
     */
    public void miniMusicScreenClicked(View view){
        bottomNavigationView.setVisibility(View.VISIBLE);
        miniMusicControllerLayout.setVisibility(View.VISIBLE);
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void buttonClicked(AuthFragmentType fragmentType) {
        switch (fragmentType){

        }
    }
}
