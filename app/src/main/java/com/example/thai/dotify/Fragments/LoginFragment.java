package com.example.thai.dotify.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;

import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.R;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.StartUpContainer;
import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.example.thai.dotify.Utilities.UserUtilities;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.support.constraint.Constraints.TAG;

/**
 * this object represents the data for the login page
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private static TextView errorMessageTextView;
    private static OnChangeFragmentListener onChangeFragmentListener;
    private Context activityContext;

    public enum ResponseCode{
        SUCCESS,
        FAIL,
        SERVER_ERROR
    }

    /**
     * object's personal interface
     */
    public interface OnChangeFragmentListener {
        void buttonClicked(StartUpContainer.AuthFragmentType fragmentType);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

    /***
     * invoked when context object will provide information about app's environment
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /***
     * creates the view object for the login page
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return a View object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //Initialize all of the views
        usernameEditText = view.findViewById(R.id.user_name_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        errorMessageTextView = view.findViewById(R.id.error_text_view);
        Button signInButton = view.findViewById(R.id.sign_in_button);
        Button signUpButton = view.findViewById(R.id.sign_up_button);
        Button forgetPasswordButton = view.findViewById(R.id.forget_password_button);

        //Set listeners for buttons
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        forgetPasswordButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
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
     * Gets called upon the user clicking an interactive item on screen
     * @param view The current view
     */
    public void onClick(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        switch (view.getId()) {
            case R.id.sign_in_button: //user selects sign in
                //Username or password edit field is empty
                if(username.trim().isEmpty() || password.trim().isEmpty()) {
                    errorMessageTextView.setText(R.string.empty_login_entry);
                }
                else{
                    //Tries the login for the username and password


                    //Start a GET request to get the list of playlists that belongs to the user
                    Dotify dotify = new Dotify(getString(R.string.base_URL));
                    dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
                    DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();

                    //Create the GET request
                    Call<DotifyUser> request = dotifyHttpInterface.getUser(
                            getString(R.string.appKey),
                            username,
                            password
                    );

                    request.enqueue(new Callback<DotifyUser>() {
                        @Override
                        public void onResponse(Call<DotifyUser> call, retrofit2.Response<DotifyUser> response) {
                            if (response.isSuccessful()){
                                int respCode = response.code();
                                if (respCode == Dotify.ACCEPTED) {
                                    DotifyUser dotifyUser = response.body();
                                    UserUtilities.cacheUser(activityContext, dotifyUser);
                                    LoginFragment.loginResponse(LoginFragment.ResponseCode.SUCCESS);
                                }
                            }
                            else{
                                Log.d(TAG, "loginUser-> onResponse: Invalid Credentials : " + response.code());
                                //User needs to retry to log in
                                LoginFragment.loginResponse(LoginFragment.ResponseCode.FAIL);
                            }
                        }

                        @Override
                        public void onFailure(Call<DotifyUser> call, Throwable throwable) {
                            Log.w(TAG, "loginUser-> onFailure");
                            //Error message that the server is down
                            LoginFragment.loginResponse(LoginFragment.ResponseCode.SERVER_ERROR);

                        }
                    });
                }
                break;
            case R.id.sign_up_button: //user selects sign up
                onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.CREATE_ACCOUNT);
                break;
            case R.id.forget_password_button: //user selects forget password
                onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.FORGOT_PASSWORD);
                break;
        }
    }

    /**
     * Checks to see if the credentials match for the user. If it matches, allows the user to be authenticated.
     * Otherwise, give a message that the credentials are incorrect or if the server is currently down.
     */
    public static void loginResponse(ResponseCode codeType){
        if(codeType == ResponseCode.SUCCESS){
            onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.LOGIN);
        }
        else if(codeType == ResponseCode.FAIL){
            errorMessageTextView.setText(R.string.invalid_credential);
        }
        else if(codeType == ResponseCode.SERVER_ERROR){
            errorMessageTextView.setText(R.string.connection_failed);
        }
    }




}
