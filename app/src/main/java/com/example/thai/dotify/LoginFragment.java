package com.example.thai.dotify;

import android.content.SharedPreferences;
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

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.support.constraint.Constraints.TAG;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView errorMessageTextView;
    private OnChangeFragmentListener onChangeFragmentListener;
    private Context activityContext;

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
    public void onAttach(Context context) {
        super.onAttach(context);
            activityContext = context;
    }

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
                    //Cache that the user is logged in
                    loginDotifyUser(usernameEditText.toString(), passwordEditText.toString());
                    onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.LOGIN);
                }
                //Username or password edit field is empty
                if( flagNum == 1){
                    errorMessageTextView.setText(R.string.empty_login_entry);
                }
                //Username and password doesn't match
                else if(flagNum == 2){
                    errorMessageTextView.setText(R.string.invalid_credential);
                }
                break;
            case R.id.sign_up_button:
                onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.CREATE_ACCOUNT);
                break;
            case R.id.forget_password_button:
                onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.FORGOT_PASSWORD);
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

        flagType = 0;

        return flagType;
    }

    /**
     * Checks to see if the credentials matach for the user
     */
    private void loginDotifyUser(final String username, final String password){
        //Start a GET request to login the user
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));
        //Add logging interceptor
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
                if (response.code() == Dotify.ACCEPTED) {
                    Log.d(TAG, "loginUser-> onResponse: Success Code : " + response.code());
                    DotifyUser dotifyUser = response.body();
                    //Cache the user
                    //SharedPreferences pref = activityContext.getSharedPreferences();

                }
                else if (response.code() == Dotify.NON_AUTHORUTATUVE_INFO){
                    Log.d(TAG, "loginUser-> onResponse: Invalid Credentials : " + response.code());
                    //User needs to retry to log in
                }
            }

            @Override
            public void onFailure(Call<DotifyUser> call, Throwable throwable) {
                Log.w(TAG, "loginUser-> onFailure");
            }
        });
    }

}
