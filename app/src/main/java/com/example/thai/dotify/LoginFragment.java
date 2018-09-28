package com.example.thai.dotify;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView errorMessageTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        errorMessageTextView = getActivity().findViewById(R.id.error_text_view);
        Button signInButton = getActivity().findViewById(R.id.sign_in_button);
        Button signUpButton = getActivity().findViewById(R.id.sign_up_button);

        //Set listeners for buttons
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    /**
     * Gets called upon the user clicking an interactive item on screen
     *
     * @param view The current view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                if(isValidCredential(usernameEditText.getText().toString(), passwordEditText.getText().toString()) == 1){
                    errorMessageTextView.setText(R.string.empty_login_entry);
                }
                break;
            case R.id.sign_up_button:
                break;
        }
    }


    /**
     * Gets called upon the user clicking an interactive item on screen
     *
     * @param username The user username
     * @param password The user password
     */
    private int isValidCredential(String username, String password){
        int flagType = 0;

        if(username.trim().isEmpty() || password.trim().isEmpty()){
            flagType = 1;
        }

        return flagType;
    }
}
