package com.example.thai.dotify;

import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class create_account_fragment extends Fragment implements View.OnClickListener{

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText securityQuestion1EditText;
    private EditText securityQuestion2EditText;
    private TextView weakPasswordTextView;
    private TextView confirmPasswordErrorTextView;
    private boolean isWeakPasswordEnable;
    private boolean isPasswordMatch;


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
        Button createAccountButton = getActivity().findViewById(R.id.create_account_button);
        Button backButton = getActivity().findViewById(R.id.back_button);

        //
        isWeakPasswordEnable = false;
        isPasswordMatch = false;

        //Initialize all of the views
        usernameEditText = getActivity().findViewById(R.id.user_name_edit_text);
        passwordEditText = getActivity().findViewById(R.id.password_edit_text);
        confirmPasswordEditText = getActivity().findViewById(R.id.confirm_password_edit_text);
        securityQuestion1EditText = getActivity().findViewById(R.id.security_question_1_edit_text);
        securityQuestion2EditText = getActivity().findViewById(R.id.security_question_2_edit_text);
        weakPasswordTextView = getView().findViewById(R.id.weak_password_error);
        confirmPasswordErrorTextView = getActivity().findViewById(R.id.confirm_password_error);

        createAccountButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        setTextEditFocusListener();
    }

    /**
     * Gets called upon the user clicking an interactive item on screen
     *
     * @param view The current view
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.create_account_button:
                break;
            case R.id.back_button:
                break;
        }
    }

    /**
     * Create focus listener for edit text
     *
     */
    private void setTextEditFocusListener(){

        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(passwordGuidelineCheck(passwordEditText.getText().toString())|| passwordEditText.getText().toString().isEmpty()){
                        weakPasswordTextView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        weakPasswordTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
                        confirmPasswordErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        weakPasswordTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        securityQuestion1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                }
            }
        });

        securityQuestion2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

    }

    /**
     * Create text change listener for edit text
     *
     */
    private void setTextChange(){
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        securityQuestion1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        securityQuestion2EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * Checks the user's password to make sure it is strong enough to prevent dictionary attacks
     *
     * @param password The user's desired password
     *
     * @return True if the password is strong enough and false otherwise
     */
    private boolean passwordGuidelineCheck(String password) {
        /**
         * Passwords must contain at least one a-z character, one A-Z character
         * one 0-9 character,and be 8 characters long minimum
         */
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
