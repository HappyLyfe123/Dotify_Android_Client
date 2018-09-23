package com.example.thai.dotify;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class create_account_fragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText securityQuestion1EditText;
    private EditText securityQuestion2EditText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    /**
     * Initializes the main components of the fragment
     *
     * @param savedInstanceState The saved instance of the fragment
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialize all of the views
        usernameEditText = getActivity().findViewById(R.id.user_name_edit_text);
        passwordEditText = getActivity().findViewById(R.id.password_edit_text);
        confirmPasswordEditText = getActivity().findViewById(R.id.confirm_password_edit_text);
        securityQuestion1EditText = getActivity().findViewById(R.id.security_question_1_edit_text);
        securityQuestion2EditText = getActivity().findViewById(R.id.security_question_2_edit_text);
    }


}
