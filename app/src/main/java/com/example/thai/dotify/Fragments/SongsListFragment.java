package com.example.thai.dotify.Fragments;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.thai.dotify.MainActivityContainer;
import com.example.thai.dotify.PlayingMusicController;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.Server.DotifySong;
import com.example.thai.dotify.Adapters.SongsAdapter;
import com.example.thai.dotify.Utilities.JSONUtilities;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

/**
 * the SongsListFragment object allows the user to do various functions with a list of songs
 */
public class SongsListFragment extends Fragment{

    private ImageButton backButton;
    private TextView titleTextView;
    private RecyclerView songListRecycleView;
    private ArrayList<DotifySong> songsList = new ArrayList<>();
    private SongsAdapter songsAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;


    private static String playListTitle;
    private String username;
    private Context activityContext;

    /**
     * default constructor
     */
    public SongsListFragment(){

    }

    /**
     * Listener to tell the main container to switch fragments
     */
    public interface OnChangeFragmentListener{
        void songClicked(MainActivityContainer.PlaylistFragmentType fragmentType,
                         PlayingMusicController playingMusicController);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener){
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

    /**
     * add a Bundle object to the SongsListFragment object
     * @param savedInstanceState - Bundle object
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * add information about app's environment to the SongsListFragment object
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
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
        backButton.setOnClickListener((backButtonView) -> {
            getFragmentManager().popBackStackImmediate();
        });
        titleTextView = (TextView) view.findViewById(R.id.song_list_title_text_view);
        songListRecycleView = (RecyclerView) view.findViewById(R.id.song_list_recycle_view);
        SharedPreferences sharedPreferences = activityContext.getSharedPreferences("UserData", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

        songsList = new ArrayList<>();
        // Initialize the recycler view listener
        RecyclerViewClickListener songItemClickListener = (listView, position) -> {
            // Create a music controller object
            PlayingMusicController musicController = new PlayingMusicController(getContext(), songsList);
            // Set the current song to be played for the controller
            musicController.setCurrentSong(position);
            // Tell the main activity
            onChangeFragmentListener.songClicked(
                    MainActivityContainer.PlaylistFragmentType.FULL_SCREEN_MUSIC,
                    musicController);
        };
        //Display all of the items into the recycler view
        songsAdapter = new SongsAdapter(songsList, songItemClickListener);
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        songListRecycleView.setLayoutManager(songLayoutManager);
        songListRecycleView.setItemAnimator(new DefaultItemAnimator());
        songListRecycleView.setAdapter(songsAdapter);
        getSongList();

        return view;
    }

    /**
     * add extra features when the activity object has been created
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setFragmentTitle();
    }

    /**
     * Set the title for the current fragment to playlist name
     * @param title - fragment title
     */
    public static void setPlayListTitle(String title){
        playListTitle = title;
    }

    /**
     * Set the title for the text view to title of current fragment
     */
    private void setFragmentTitle(){
        titleTextView.setText(playListTitle);
    }

    /**
     * display the song list
     */
    private void getSongList(){
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));

        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);

        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        Call<ResponseBody> getSongList = dotifyHttpInterface.getSongList(
                getString(R.string.appKey),
                username,
                playListTitle
        );

        getSongList.enqueue(new Callback<ResponseBody>() {
            /**
             * display a success message
             * @param call - request to server
             * @param response - server's response
             */
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "getPlaylist-> onResponse: Success Code : " + response.code());
                    //gets a list of strings of playlist names
                    ResponseBody mySong = response.body();
                    try {
                        JsonObject currSongList= JSONUtilities.ConvertStringToJSON(mySong.string());
                        Gson gson = new Gson();
                        for(int x = 0; x < currSongList.get("songs").getAsJsonArray().size(); x++){
                            songsList.add(gson.fromJson(currSongList.get("songs").getAsJsonArray().get(x), DotifySong.class));
                        }
                        songsAdapter.notifyItemRangeInserted(0, songsList.size());
                        songsAdapter.notifyItemRangeChanged(0, songsList.size());
                        songsAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    //If unsucessful, show the response code
                    Log.d(TAG, "getPlaylist-> Unable to retreive playlists " + response.code());
                }
            }


            /**
             * If something is wrong with our request to the server, goes to this method
             * @param call - request to server
             * @param t - unnecessary parameter
             */
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Invalid failure: onFailure");
            }
        });
    }

}