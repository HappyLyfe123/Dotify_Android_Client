package com.example.thai.dotify;

/**
 * main interface for the main activity fragment
 */
public interface OnChangeFragmentListener {

    // List of fragments to be used throughout the project
    enum FragmentType{
        SEARCH,
        PLAYLISTS,
        FOR_YOU,
        PROFILE,
        CREATE_PLAYLIST,
        SONGS_LIST_PAGE,
        FULL_SCREEN_MUSIC,
        BACK_BUTTON
    }

    void buttonClicked(MainActivityContainer.PlaylistFragmentType fragmentType);
}
