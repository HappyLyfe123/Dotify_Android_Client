package com.example.thai.dotify;


import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class CreatePlaylistFragment extends Fragment implements View.OnClickListener {

    private EditText playlistNameText;
    private Button button;
    private OnChangeFragmentListener onChangeFragmentListener;

    public interface OnChangeFragmentListener {
        void buttonClicked(StartUpContainer.AuthFragmentType fragmentType);
    }

    public static CreatePlaylistFragment newInstance() {
        CreatePlaylistFragment fragment = new CreatePlaylistFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_playlist, container, false);
    }
    /**
     * Initializes the main components of the fragment
     *
     * @param savedInstanceState The saved instance of the fragment
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button = getActivity().findViewById(R.id.create_button);
        button.setOnClickListener(this);
        playlistNameText = getActivity().findViewById(R.id.playlist_edit_text);
        button.setClickable(false);
        button.setEnabled(false);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if(playlistNameText.getText().length() == 0) {
            displayToast("Cannot proceed. Please enter a playlist name.");
        }
        else if(playlistNameText.getText().length() > 0) {
            button.setClickable(true);
            button.setEnabled(true);
            Editable playlistText = playlistNameText.getText();
            //add playlist name to list of playlist objects (data structure or text file)
        }
    }
    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     *
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }
    public void displayToast(String message) {
        Toast toast = Toast.makeText(this.getContext(),message,Toast.LENGTH_SHORT);
        toast.show();
    }
}
