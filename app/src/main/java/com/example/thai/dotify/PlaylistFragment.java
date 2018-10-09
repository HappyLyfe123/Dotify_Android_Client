package com.example.thai.dotify;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import okhttp3.logging.HttpLoggingInterceptor;

public class PlaylistFragment extends Fragment implements View.OnClickListener  {

    private Button createPlaylistButton;
    private RecyclerView playlistListRecycleView;
    private List<Playlist> playlistList = new ArrayList<>();
    private PlaylistsAdapter playlistsAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;
    private String playlistName = "";

    public interface OnChangeFragmentListener{
        void buttonClicked(MainActivityContainer.PlaylistFragmentType fragmentType);
        void setTitle(String title);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     *
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(PlaylistFragment.OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_playlist, container, false);
        createPlaylistButton = view.findViewById(R.id.create_playlist_button);
        createPlaylistButton.setOnClickListener(this);
        playlistListRecycleView = view.findViewById(R.id.playlist_list_recycle_view);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set up recycler view click adapter
        RecyclerViewClickListener listener = (view, position) -> {
            System.out.println(getPlaylistName(position));
            onChangeFragmentListener.buttonClicked(MainActivityContainer.PlaylistFragmentType.SONGS_LIST_PAGE);
            onChangeFragmentListener.setTitle(getPlaylistName(position));
        };

        //Display all of the items into the recycler view
        playlistsAdapter = new PlaylistsAdapter(playlistList, listener);
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        playlistListRecycleView.setLayoutManager(songLayoutManager);
        playlistListRecycleView.setItemAnimator(new DefaultItemAnimator());
        playlistListRecycleView.setAdapter(playlistsAdapter);

        test();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_playlist_button:
                //case R.id.delete_playlist_button:
                AlertDialog dialogBox = createPlaylistDialog();
                dialogBox.show();
                //dialogBox.cancel();
                break;
        }
    }
    /**
     * Create an AlertDialog object to allow the user to create
     *
     * @return
     */
    private AlertDialog createPlaylistDialog() {
        //Create an instance of the Alert Dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        //Set the View of the Alert Dialog
        final View alertDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_create_playlist, null);
        alertDialogBuilder.setView(alertDialogView);


        //Initialize Views for this Fragment
        final Button createPlaylist = (Button) alertDialogView.findViewById(R.id.create_button);
        final EditText playlistName = (EditText) alertDialogView.findViewById(R.id.playlist_name_edit_text);
        final TextView errorMessageTextView = (TextView) alertDialogView.findViewById(R.id.create_playlist_error_text_view);

        //Set Listeners
        createPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Can have the play list name edit text empty
                if(playlistName.getText().toString().isEmpty()) {
                    errorMessageTextView.setText(R.string.empty_playlist_name_error);
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }
                else if (createPlaylistDotify(playlistName.getText().toString())){

                }
                else{
                    //A playlist with the same name already exist
                    errorMessageTextView.setText(R.string.duplicate_playlist_name_error);
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        return alertDialogBuilder.create();
    }

    /**
     * Checks to see if play list can be created or not
     */
    private boolean createPlaylistDotify(String playlistName){
        boolean playlistCreated = false;
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));

        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();

        return playlistCreated;
    }

    private String getPlaylistName(int position){
        return playlistList.get(position).getPlaylistName();
    }

    private void test(){
        Playlist playList = new Playlist("Hello");
        playlistList.add(playList);
        for(int x = 0; x < 50; x++){
            playList = new Playlist("A");
            playlistList.add(playList);
        }
    }
}
