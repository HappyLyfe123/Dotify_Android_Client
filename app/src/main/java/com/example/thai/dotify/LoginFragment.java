package com.example.thai.dotify;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
               // createDotifyUser(usernameEditText.getText(), passwordEditText.getText(), );
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

    /**
     * Creates an Dotify User from the federated identities
     *
     * @param username The user's chosen username
     */
    private void createDotifyUser(final String username, final String password, final String secQuestion1, final String secQuestion2,
                                  final String secAnswer1, final String secAnswer2) {
        //Create an dotifyUser object to send
        DotifyUser dotifyUser = new DotifyUser(username, password, secQuestion1, secQuestion2, secAnswer1, secAnswer2);

        //Start at POST request to create the user in the Astral Framework
        final Dotify dotify = new Dotify(getString(R.string.base_URL));
        //Intercept the request to add a header item
        dotify.addRequestInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                //Add the app key to the request header
                Request.Builder newRequest = request.newBuilder().header(
                        Dotify.APP_KEY_HEADER, getString(R.string.appKey));
                //Continue the request
                return chain.proceed(newRequest.build());
            }
        });
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface astralHttpInterface = dotify.getHttpInterface();
        //Create the POST request
        Call<DotifyUser> request = astralHttpInterface.createUser(dotifyUser.getUsername(), dotifyUser.getPassword(),
                dotifyUser.getQuestion1(), dotifyUser.getQuestion2(), dotifyUser.getAnswer1(), dotifyUser.getAnswer2());
        //Call the request asynchronously
        request.enqueue(new Callback<DotifyUser>() {
            @Override
            public void onResponse(Call<DotifyUser> call, retrofit2.Response<DotifyUser> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "createDotifyUser-> onClick-> onSuccess-> onResponse: Successful Response Code " + response.code());
                    //Create the DotifyUser account
                    //startMainActivity();
                } else {
                    Log.d(TAG, "createAstralUser-> onClick-> onSuccess-> onResponse: Failed response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DotifyUser> call, Throwable t) {
                //The request has unexpectedly failed
                Log.d(TAG, "createAstralUser-> onClick-> onSuccess-> onResponse: Unexpected request failure");
                t.printStackTrace();
            }
        });
    }

}
