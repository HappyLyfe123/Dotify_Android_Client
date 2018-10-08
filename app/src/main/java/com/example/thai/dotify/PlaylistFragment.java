package com.example.thai.dotify;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import okhttp3.logging.HttpLoggingInterceptor;

public class PlaylistFragment extends Fragment implements View.OnClickListener  {

    private Button createPlaylistButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_playlist, container, false);
        createPlaylistButton = view.findViewById(R.id.create_playlist_button);
        createPlaylistButton.setOnClickListener(this);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
}
