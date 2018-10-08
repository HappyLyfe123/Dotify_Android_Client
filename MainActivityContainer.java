package com.example.thai.dotify;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;


public class MainActivityContainer extends AppCompatActivity implements SearchView.OnQueryTextListener  {

    private TextView mTextMessage;
    SearchFragment searchFragment;
    PlaylistFragment playlistFragment;
    ProfileInfoFragment profileInfoFragment;
    ForYouFragment forYouFragment;
    CreatePlaylistFragment createPlaylistFragment;
    ListView list;
    SearchView searchView;
    ListViewAdapter adapter;

    ArrayList<SongFragment> arraylist = new ArrayList<>();



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

        //mTextMessage = (TextView) findViewById(R.id.message);
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //create bottom navigation bar
        createBottomNavigationView();

        //Instantiate fragments
        searchFragment = SearchFragment.newInstance();
        playlistFragment = PlaylistFragment.newInstance();
        profileInfoFragment = ProfileInfoFragment.newInstance();
        forYouFragment = forYouFragment.newInstance();
        createPlaylistFragment = CreatePlaylistFragment.newInstance();

        list = findViewById(R.id.listView);

        arraylist.add(new SongFragment("Payphone","Maroon 5","Overexposed",new byte[]{125,100}, new byte[]{111,123}));
        arraylist.add(new SongFragment("Hall of Fame","The Script","#3",new byte[]{125,13}, new byte[]{11,15}));
        arraylist.add(new SongFragment("Breaking the Habit","Linkin Park","Meteora",new byte[]{10,11},new byte[]{16,100}));

        adapter = new ListViewAdapter(this, arraylist);
        list.setAdapter(adapter);
        searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener NavItemListen =
            new BottomNavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    switch (item.getItemId()) {
                        case R.id.playlists:
                            fragmentTransaction.replace(R.id.main_display_frame, playlistFragment);
                            break;
                        case R.id.search:
                            fragmentTransaction.replace(R.id.main_display_frame, searchFragment);
                            break;
                        case R.id.for_you:
                            fragmentTransaction.replace(R.id.main_display_frame, forYouFragment);
                            break;
                        case R.id.profile:
                            //go to user account
                            fragmentTransaction.replace(R.id.main_display_frame, profileInfoFragment);
                            break;
                    }
                    //fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();
                    return true;
                }
            };

    /**
     * Starts a fragment for the user
     * @param fragmentId The id of the fragment that is going to start
     * @return True if the fragment has started correctly
     */
    private boolean startFragment(int fragmentId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentId) {
            case R.id.search:
                fragmentTransaction.replace(R.id.main_display_frame, searchFragment);
                break;
            case R.id.playlists:
                fragmentTransaction.replace(R.id.main_display_frame, playlistFragment);
                break;
            case R.id.for_you:
                fragmentTransaction.replace(R.id.main_display_frame, forYouFragment);
                break;
            case R.id.profile:
                fragmentTransaction.replace(R.id.main_display_frame, profileInfoFragment);
                break;
            case R.id.create_playlist_button:
                fragmentTransaction.replace(R.id.main_display_frame,createPlaylistFragment);
                break;
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        return true;
    }

    /**
     * Creates the bottom navigation for the activity container. Sets the home screen as default.
     */
    public void createBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        //Disables automatic shifting from the bottom navigation
        BottomNavigationBarShiftHelp.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.playlists);
        bottomNavigationView.setOnNavigationItemSelectedListener(NavItemListen);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_display_frame, PlaylistFragment.newInstance());
        //Commit changes transaction
        transaction.commit();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }
}
