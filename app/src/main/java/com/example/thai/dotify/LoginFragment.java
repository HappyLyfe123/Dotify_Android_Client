package com.example.thai.dotify;

import android.os.Bundle;
import android.app.Fragment;
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
    private OnChangeFragmentListener onChangeFragmentListener;


    public interface OnChangeFragmentListener {
        void buttonClicked(StartUpContainer.AuthFragmentType fragmentType);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     *
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        //Initialize all of the views
        usernameEditText = v.findViewById(R.id.user_name_edit_text);
        passwordEditText = v.findViewById(R.id.password_edit_text);
        errorMessageTextView = v.findViewById(R.id.error_text_view);
        Button signInButton = v.findViewById(R.id.sign_in_button);
        Button signUpButton = v.findViewById(R.id.sign_up_button);

        //Set listeners for buttons
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return v;
    }

    /**
     * Initializes the main components of the fragment
     *
     * @param savedInstanceState The saved instance of the fragment
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * Gets called upon the user clicking an interactive item on screen
     *
     * @param view The current view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                int flagNum = isValidCredential(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                //Username and password is valid
                if(flagNum == 0){

                }
                //Username or password edit field is empty
                if( flagNum == 1){
                    errorMessageTextView.setText(R.string.empty_login_entry);
                }
                //Username and password doesn't match
                else if(flagNum == 2){
                    errorMessageTextView.setText(R.string.invalid_credential);
                }
                onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.LOGIN);
                break;
            case R.id.sign_up_button:
                onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.CREATE_ACCOUNT);
                break;
        }
    }


    /**
     * Gets called upon the user clicking an interactive item on screen
     *
     * @param username The user username
     * @param password The user password
     *
     * @return flagType use for telling which error it has
     */
    private int isValidCredential(String username, String password){
        int flagType = 0;

        //Check to see if username or password is empty
        if(username.trim().isEmpty() || password.trim().isEmpty()){
            flagType = 1;
        }
        else{
            flagType = 2;
        }

        return flagType;
    }

}
