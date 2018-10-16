package com.example.thai.dotify;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


//Allow user to search for songs, artists, albums
public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView searchResult;

    /**
     * create the View object to display the SearchFragment object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Initialize view layout
        searchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        searchResult = (RecyclerView) view.findViewById(R.id.search_result_view);
        return view;
    }

    /**
     * add the search listener
     */
    private void setSearchListiner(){
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
