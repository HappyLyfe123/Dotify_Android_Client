package com.example.thai.dotify;

import android.graphics.Color;
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


public class CreateAccountFragment extends Fragment implements View.OnClickListener{

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText securityQuestion1EditText;
    private EditText securityQuestion2EditText;
    private TextView weakPasswordTextView;
    private TextView confirmPasswordErrorTextView;
    private boolean isWeakPasswordEnable;
    private boolean isPasswordMatch;
    private boolean usernameFilled, passwordFilled, confirmedPasswordFilled, securityQuestion1Filled,
        securityQuestion2Filled;
    private Button createAccountButton;



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
        createAccountButton = getActivity().findViewById(R.id.create_account_button);
        Button backButton = getActivity().findViewById(R.id.back_button);

        //
        isWeakPasswordEnable = false;
        isPasswordMatch = false;
        usernameFilled = false;
        passwordFilled = false;
        confirmedPasswordFilled = false;
        securityQuestion1Filled = false;
        securityQuestion2Filled = false;

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
        setTextChange();
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
                    if(!usernameEditText.getText().toString().isEmpty()){
                        usernameFilled = true;
                    }
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(passwordEditText.getText().toString().isEmpty()){
                        weakPasswordTextView.setVisibility(View.INVISIBLE);
                        passwordFilled = false;
                    }
                    else if(passwordGuidelineCheck(passwordEditText.getText().toString()) ){
                        weakPasswordTextView.setVisibility(View.INVISIBLE);
                        passwordFilled = true;
                        isWeakPasswordEnable = false;
                    }
                    else{
                        weakPasswordTextView.setVisibility(View.VISIBLE);
                        isWeakPasswordEnable = true;
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
                        isPasswordMatch = true;
                        confirmedPasswordFilled = true;
                    }
                    else{
                        confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        securityQuestion1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(securityQuestion1EditText.getText().toString().isEmpty()){
                        securityQuestion1Filled = false;
                    }
                    else{
                        securityQuestion1Filled = true;
                    }

                }
            }
        });

        securityQuestion2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(securityQuestion2EditText.getText().toString().isEmpty()){
                    securityQuestion2Filled = false;
                }
                else{
                    securityQuestion2Filled = true;
                }

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
                if(!s.toString().toString().trim().isEmpty()){
                    usernameFilled = true;
                    enableCreateAccountButton();
                }
                else{
                    usernameFilled = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordGuidelineCheck(s.toString())){
                    passwordFilled = true;
                    isWeakPasswordEnable = false;
                    enableCreateAccountButton();
                }
                else{
                    passwordFilled = false;
                    isWeakPasswordEnable = true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordEditText.getText().toString().equals(s.toString())){
                    confirmedPasswordFilled = true;
                    isPasswordMatch = true;
                    enableCreateAccountButton();
                }

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        securityQuestion1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    securityQuestion1Filled = true;
                    enableCreateAccountButton();
                }
                else{
                    securityQuestion1Filled = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        securityQuestion2EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    securityQuestion2Filled = true;
                    enableCreateAccountButton();
                }
                else{
                    securityQuestion2Filled = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }



    /**
     * Check to see if every edit text is filled and correctly formatted
     *
     */
    private void enableCreateAccountButton(){

        /**
         *Enable crate button to be able to click and change the color to black
         */
        if(usernameFilled && passwordFilled && confirmedPasswordFilled && securityQuestion1Filled
                && securityQuestion2Filled && !isWeakPasswordEnable && isPasswordMatch){

            createAccountButton.setTextColor(Color.BLACK);
            createAccountButton.setClickable(true);
        }
        else{
            createAccountButton.setTextColor(getResources().getColor(R.color.createAccount));
            createAccountButton.setClickable(false);

        }
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