package com.example.thai.dotify.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.thai.dotify.Adapters.PlaylistsAdapter;
import com.example.thai.dotify.Adapters.SearchAlbumAdapter;
import com.example.thai.dotify.Adapters.SearchArtistSongAdapter;
import com.example.thai.dotify.R;
import com.example.thai.dotify.RecyclerViewClickListener;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.SearchArtistSongResult;
import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.example.thai.dotify.Utilities.SentToServerRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SongsByArtistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SongsByArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongsByArtistFragment extends Fragment {

    private static SentToServerRequest sentToServerRequest;
    private static GetFromServerRequest getFromServerRequest;
    private TextView titleTextView;
    private RecyclerView songListRecycleView;
    private RecyclerView albumListRecyclerView;
    private ImageButton backButton;
    private String artistName;
    private AlertDialog currDialogBox;
    private RecyclerView selectPlaylistList;
    private PlaylistsAdapter currPlaylistAdapter;
    private SearchArtistSongAdapter songsListAdapter;
    private SearchAlbumAdapter albumSearchResultAdapter;
    private OnFragmentInteractionListener onFragmentInteractionListener;
    private static JsonElement artistInfo;




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void backButtonPressed();
        PlaylistsAdapter getPlaylistAdapter();
    }

    /**
     * Sets the Listener object from the main activity
     *
     * @param onFragmentInteractionListener Set the listener for this fragment
     *
     */
    public void setOnFragmentInteractionListener(OnFragmentInteractionListener onFragmentInteractionListener) {
        this.onFragmentInteractionListener = onFragmentInteractionListener;
    }

    public SongsByArtistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SongsByArtistFragment.
     */
    public static SongsByArtistFragment newInstance(SentToServerRequest sentRequest, GetFromServerRequest getRequest,
                                                    String artistName, JsonElement currArtistInfo) {
        SongsByArtistFragment fragment = new SongsByArtistFragment();
        Bundle args = new Bundle();
        args.putString("artistName", artistName);
        sentToServerRequest = sentRequest;
        getFromServerRequest = getRequest;
        artistInfo = currArtistInfo;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistName = getArguments().getString("artistName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        //Disable the edit song button
        view.findViewById(R.id.song_list_edit_song_button).setVisibility(View.GONE);
        backButton = view.findViewById(R.id.song_list_back_image_button);
        titleTextView = (TextView) view.findViewById(R.id.song_list_title_text_view);
        songListRecycleView = (RecyclerView) view.findViewById(R.id.song_list_recycle_view);
        albumListRecyclerView = (RecyclerView) view.findViewById(R.id.album_list_recycler_view);

        //Enable album list recycler view
        albumListRecyclerView.setVisibility(View.VISIBLE);

        //Display all of the items into the recycler view
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager albumLayoutManager = new LinearLayoutManager(getContext());

        // Recycler view that controls displaying song
        songListRecycleView.setLayoutManager(songLayoutManager);
        songListRecycleView.setItemAnimator(new DefaultItemAnimator());
        // Recycler view that controls displaying album
        albumListRecyclerView.setLayoutManager(albumLayoutManager);
        albumListRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Set adapter
        songsListAdapter = new SearchArtistSongAdapter(new RecyclerViewClickListener() {
            @Override
            public void onItemClick(View v, int songPosition) {
                if(v.getId() == R.id.search_result_item_recycler_view) {

                }
                else if(v.getId() == R.id.search_add_to_play_list_image_view){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    //Set the View of the Alert Dialog
                    final View alertDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_select_playlist, null);
                    alertDialogBuilder.setView(alertDialogView);
                    currDialogBox = alertDialogBuilder.create();

                    //Initialize view
                    selectPlaylistList = alertDialogView.findViewById(R.id.select_playlist_playlist_list);
                    Button cancelButton = alertDialogView.findViewById(R.id.select_playlist_cancel_button);

                    //Cancel to close the select playlist view
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currDialogBox.dismiss();
                        }
                    });

                    //Create a click listener for the recycler view
                    currPlaylistAdapter = new PlaylistsAdapter(new RecyclerViewClickListener() {
                        @Override
                        public void onItemClick(View v, int playlistPosition) {
                            addSongToPlaylist(songPosition, playlistPosition);
                        }
                    });

                    //Get the list of playlist from playlist fragment
                    currPlaylistAdapter.replacePlaylistList(onFragmentInteractionListener.getPlaylistAdapter().getPlaylistListName());

                    //Display all of the items into the recycler view
                    RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
                    selectPlaylistList.setLayoutManager(songLayoutManager);
                    selectPlaylistList.setItemAnimator(new DefaultItemAnimator());
                    selectPlaylistList.setAdapter(currPlaylistAdapter);

                    //Create Alert DialogBox
                    currDialogBox.show();
                }

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFragmentInteractionListener.backButtonPressed();
            }
        });



        setFragmentTitle();
        displaySongs();
        songListRecycleView.setAdapter(songsListAdapter);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            onFragmentInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentInteractionListener = null;
    }

    /**
     * Display all of the song by the artist
     */
    private void displaySongs(){
        Gson gson = new Gson();
        JsonElement albumInfo = artistInfo.getAsJsonObject().get("albumList");
        songsListAdapter.setArtistName(artistName);
        for(JsonElement currAlbum : albumInfo.getAsJsonArray()){

            for(JsonElement songInfo : currAlbum.getAsJsonObject().get("songList").getAsJsonArray()) {
                songsListAdapter.insertSearchResultItem(gson.fromJson(
                        songInfo, SearchArtistSongResult.class));
            }
        }
    }

    /**
     * Set the title of the page to the artist name
     */
    private void setFragmentTitle(){
        titleTextView.setText(artistName);
    }

    /**
     * Add the song to the select playlist
     */
    private void addSongToPlaylist(int songPosition, int playlistPosition){

        Call<ResponseBody> addSongRequest = sentToServerRequest.addSongToPlaylist(currPlaylistAdapter.getPlaylistName(playlistPosition),
                songsListAdapter.getSongGUID(songPosition));

        addSongRequest.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == Dotify.OK){
                    currDialogBox.dismiss();
                }
                else{
                    System.out.println(response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }



}
