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
    private boolean successfulLogin;

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

        //Determines if the user is logged in
        successfulLogin = false;

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
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        switch (view.getId()) {
            case R.id.sign_in_button:
                //Username or password edit field is empty
                if(username.trim().isEmpty() || password.trim().isEmpty()) {
                    errorMessageTextView.setText(R.string.empty_login_entry);
                }
                else{
                    //Tries the login for the username and password
                    loginDotifyUser(username, password);
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
     * Checks to see if the credentials match for the user
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
                if (response.isSuccessful()){
                    int respCode = response.code();
                    if (respCode == Dotify.ACCEPTED) {
                        Log.d(TAG, "loginUser-> onResponse: Success Code : " + response.code());
                        DotifyUser dotifyUser = response.body();
                        onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.LOGIN);
                        //Cache the user
                        //SharedPreferences pref = activityContext.getSharedPreferences();
                    }
                }
                else{
                    Log.d(TAG, "loginUser-> onResponse: Invalid Credentials : " + response.code());
                    errorMessageTextView.setText(R.string.invalid_credential);
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
