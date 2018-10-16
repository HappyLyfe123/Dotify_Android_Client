package com.example.thai.dotify;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.app.Fragment;
import android.view.ViewGroup;

/**this object will display the fragment_create_playlist file**/
public class CreatePlaylistFragment extends Fragment{

    /**
     * creates CreatePlaylistFragment object
     * @return object of type CreatePlaylistFragment
     */
    public static CreatePlaylistFragment newInstance() {
        CreatePlaylistFragment fragment = new CreatePlaylistFragment();
        return fragment;
    }

    /**
     * creates the layout for this object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return new View object for CreatePlaylistFragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_playlist, container, false);
    }
    /**
     * Initializes the main components of the fragment
     * @param savedInstanceState The saved instance of the fragment
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * detaches fragment from main activity when user is done create a playlist
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }


}
