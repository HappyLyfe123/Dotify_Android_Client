package com.example.thai.dotify;

import android.content.Context;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import static android.support.constraint.Constraints.TAG;

//the fragment object that represents the home page for the profile page
public class PlaylistFragment extends Fragment implements View.OnClickListener  {

    private Button createPlaylistButton;
    private Button playlistCreateButton;
    private Button editPlaylistButton;
    private Button cancelPlaylistButton;
    private RecyclerView playlistListRecycleView;
    private EditText playlistNameEditText;
    private List<String> playlistList = new ArrayList<>();
    private PlaylistsAdapter playlistsAdapter;
    private OnChangeFragmentListener onChangeFragmentListener;
    private String playlistName = "";
    private Context activityContext;
    private TextView createPlaylistErrorMessageTextView;
    private ProgressBar savingProgressBar;
    private DotifyUser user;

    //default constructor
    public PlaylistFragment()  {
    }

    private enum ErrorType{
        CREATE_PLAYLIST_EMPTY_EDIT_TEXT,
        CREATE_PLAYLIST_DUPLICATE_NAME,
        CONNECTION_FAILED,
        SUCCESS,
        DUPLICATE,
    }

    //interface that changes fragment view
    public interface OnChangeFragmentListener{
        void buttonClicked(MainActivityContainer.PlaylistFragmentType fragmentType);
        void setTitle(String title);
    }

    //attaches context object to fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(PlaylistFragment.OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

    /***
     * creates view for the object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize Variables
        user = ((MainActivityContainer) this.getActivity()).getCurrentUser();

        // Initialize views
        View view  = inflater.inflate(R.layout.fragment_playlist, container, false);
        createPlaylistButton = view.findViewById(R.id.create_playlist_button);
        createPlaylistButton.setOnClickListener(this);
        playlistListRecycleView = view.findViewById(R.id.playlist_list_recycle_view);
        editPlaylistButton = (Button) view.findViewById(R.id.edit_playlist_button);
        editPlaylistButton.setOnClickListener(this);
        cancelPlaylistButton = (Button) view.findViewById(R.id.cancel_playlist_button);
        cancelPlaylistButton.setOnClickListener(this);


        //Set up recycler view click adapter
        RecyclerViewClickListener listener = (myView, position) -> {
            onChangeFragmentListener.buttonClicked(MainActivityContainer.PlaylistFragmentType.SONGS_LIST_PAGE);
            onChangeFragmentListener.setTitle(getPlaylistName(position));
        };

        //Display all of the items into the recycler view
        playlistList = new ArrayList<>();
        playlistsAdapter = new PlaylistsAdapter(getActivity(), user, playlistList, listener,
                new PlaylistsAdapter.OnPlaylistDeletedListener() {
                    @Override
                    public void onPlaylistDeleted(int position) {
                        playlistList.remove(position);
                        playlistsAdapter.notifyDataSetChanged();
                    }
                });
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext());
        playlistListRecycleView.setLayoutManager(songLayoutManager);
        playlistListRecycleView.setItemAnimator(new DefaultItemAnimator());
        playlistListRecycleView.setAdapter(playlistsAdapter);
        return view;
    }

    /***
     * invoked when user selects playlist
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPlaylist();
    }

    /***
     * invoked when the button is selected
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.create_playlist_button: //user selects create
                {
                    createPlaylistDialog();
                }
            break;
            case R.id.edit_playlist_button: //user selects edit
                {
                // Update all of the views to have the delete button to appear
                playlistsAdapter.setDeleteIconVisibility(true);
                playlistsAdapter.notifyDataSetChanged();
                // Make the Edit button disappear while making the
                // cancel button appear
                cancelPlaylistButton.setVisibility(View.VISIBLE);
                editPlaylistButton.setVisibility(View.GONE);
            }
            break;
            case R.id.cancel_playlist_button: //user selects cancel
                {
                // Update all of the views to have the delete button to disappear
                playlistsAdapter.setDeleteIconVisibility(false);
                playlistsAdapter.notifyDataSetChanged();
                // Make the Edit button appear while making the cancel button disappear
                cancelPlaylistButton.setVisibility(View.GONE);
                editPlaylistButton.setVisibility(View.VISIBLE);
            }
            break;
        }
    }
    /**
     * Create an AlertDialog object to allow the user to create a playlist
     *
     * @return
     */
    private void createPlaylistDialog() {
        //Create an instance of the Alert Dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        //Set the View of the Alert Dialog
        final View alertDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_create_playlist, null);
        alertDialogBuilder.setView(alertDialogView);

        //Create Alert DialogBox
        AlertDialog currDialogBox = alertDialogBuilder.create();
        currDialogBox.show();

        //Initialize Views for this Fragment
        final Button cancelButton = (Button) alertDialogView.findViewById(R.id.create_playlist_cancel_button);
        playlistCreateButton = (Button) alertDialogView.findViewById(R.id.create_playlist_create_button);
        playlistNameEditText = (EditText) alertDialogView.findViewById(R.id.playlist_name_edit_text);
        createPlaylistErrorMessageTextView = (TextView) alertDialogView.findViewById(R.id.create_playlist_error_text_view);
        savingProgressBar = (ProgressBar) alertDialogView.findViewById(R.id.create_playlist_progress_bar);

        //Set Listeners
        playlistCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingProgressBar.setVisibility(View.VISIBLE);
                //Can have the play list name edit text empty
                if (playlistNameEditText.getText().toString().isEmpty()) {
                    displayErrorMessage(ErrorType.CREATE_PLAYLIST_EMPTY_EDIT_TEXT, createPlaylistErrorMessageTextView);
                }
                else {
                    createPlaylistDotify(playlistNameEditText.getText().toString());
                    currDialogBox.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currDialogBox.dismiss();
            }
        });
    }

    /**
     * Checks to see if play list can be created or not
     */
    private boolean createPlaylistDotify(final String playlistName){
        boolean playlistCreated = false;
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        Call<ResponseBody> addPlaylist = dotifyHttpInterface.createPlaylist(
                getString(R.string.appKey),
                user.getUsername(),
                playlistName
        );
        addPlaylist.enqueue(new Callback<ResponseBody>() {
            /***
             * server sends a reply to the client indicating successful action
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                savingProgressBar.setVisibility(View.GONE);
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "loginUser-> onResponse: Success Code : " + response.code());
                    addToPlaylistList(playlistName);
                } else {
                    //A playlist with the same name already exist
                    displayErrorMessage(ErrorType.CREATE_PLAYLIST_DUPLICATE_NAME, createPlaylistErrorMessageTextView);
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

        return playlistCreated;
    }

    /**
     * This method sends a get request to the server to get a list of playlist
     * The response body is a list of strings that consists of the names of the playlist that
     * the user has created
     */
    private void getPlaylist(){
        //Start a GET request to get the list of playlists that belongs to the user
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));

        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);

        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        Call<List<String>> getPlaylist = dotifyHttpInterface.getPlaylist(
                getString(R.string.appKey),
                user.getUsername(),
                playlistName //Why do we need this to get the list of playlists?
        );

        getPlaylist.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                int respCode = response.code();
                if (respCode == Dotify.OK) {
                    Log.d(TAG, "getPlaylist-> onResponse: Success Code : " + response.code());
                    //gets a list of strings of playlist names
                    List<String> userPlaylist = response.body();

                    //Converts the playlist we got to a list of playlists instead of a list of strings
                    for (int i = 0; i < userPlaylist.size(); i++){
                        playlistList.add(userPlaylist.get(i));
                    }
                    playlistsAdapter.notifyItemRangeInserted(0, userPlaylist.size());
                    playlistsAdapter.notifyItemRangeChanged(0, userPlaylist.size());
                } else {
                    //If unsuccessful, show the response code
                    Log.d(TAG, "getPlaylist-> Unable to retrieve playlist " + response.code());
                }
            }
            //If something is wrong with our request to the server, goes to this method
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d(TAG, "Invalid failure: onFailure");
            }
        });
    }


    /**
     * get name of the playlist at some position in the playlist
     * @param position - position in list
     * @return playlist name at position
     */
    private String getPlaylistName(int position){
        return playlistList.get(position);
    }

    //Add a playlist to the playlist list
    private void addToPlaylistList(String playlistName){
        playlistList.add(playlistName);
        playlistsAdapter.notifyItemInserted(playlistList.size() - 1);
        playlistsAdapter.notifyItemRangeChanged(playlistList.size() - 1, playlistList.size());
    }

    private void removePlayList(){

    }

    //Display error message for create playlist AlertDialog view
    private void displayErrorMessage(ErrorType type, TextView displayTextView){
        //Disable progress bar
        savingProgressBar.setVisibility(View.GONE);
        displayTextView.setVisibility(View.VISIBLE);
        switch (type){
            case CREATE_PLAYLIST_EMPTY_EDIT_TEXT:
                displayTextView.setText(R.string.empty_playlist_name_error);
                break;
            case CREATE_PLAYLIST_DUPLICATE_NAME:
                displayTextView.setText(R.string.duplicate_playlist_name_error);
                break;
            case CONNECTION_FAILED:
                displayTextView.setText(R.string.connection_failed);
                break;
        }
    }
}
