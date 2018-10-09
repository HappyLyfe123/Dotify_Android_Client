package com.example.thai.dotify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;

import com.example.thai.dotify.Server.DotifyHttpInterface;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.support.constraint.Constraints.TAG;


public class CreateAccountFragment extends Fragment{

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText securityQuestion1AnswerEditText;
    private EditText securityQuestion2AnswerEditText;
    private TextView weakPasswordTextView;
    private TextView confirmPasswordErrorTextView;
    private TextView usernameTakenTextView;
    private Spinner securityQuestion1Spinner;
    private Spinner securityQuestion2Spinner;
    private boolean isWeakPasswordEnable;
    private boolean isPasswordMatch;
    private boolean usernameFilled, passwordFilled, confirmedPasswordFilled, securityQuestion1Filled,
        securityQuestion2Filled;
    private CreateAccountListener fragmentController;

    public interface CreateAccountListener {
        void enableCreateButton(boolean enableCreateAccountButton);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     *
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(CreateAccountListener onChangeFragmentListener) {
        this.fragmentController = onChangeFragmentListener;
    }

    /***
     * instantiates CreateAccountFragment object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);
        //Initialize variable
        isWeakPasswordEnable = false;
        isPasswordMatch = false;
        usernameFilled = false;
        passwordFilled = false;
        confirmedPasswordFilled = false;
        securityQuestion1Filled = false;
        securityQuestion2Filled = false;

        //Initialize all of the views
        usernameEditText = (EditText) view.findViewById(R.id.user_name_edit_text);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.confirm_password_edit_text);
        securityQuestion1AnswerEditText = (EditText) view.findViewById(R.id.security_question_1_answer_edit_text);
        securityQuestion2AnswerEditText = (EditText) view.findViewById(R.id.security_question_2_answer_edit_text);
        weakPasswordTextView = (TextView) view.findViewById(R.id.weak_password_error);
        confirmPasswordErrorTextView = (TextView) view.findViewById(R.id.confirm_password_error);
        usernameTakenTextView = (TextView) view.findViewById(R.id.user_name_error_text_view);
        securityQuestion1Spinner = (Spinner) view.findViewById(R.id.security_question_1_spinner);
        securityQuestion2Spinner = (Spinner) view.findViewById(R.id.security_question_2_spinner);

        setTextEditFocusListener();
        setTextChange();


        // Inflate the layout for this fragment
        return view;
    }

    /***
     * invoked when context object wants to attach to fragment
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            fragmentController = (CreateAccountListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    /**
     * Initializes the main components of the fragment
     *
     * @param savedInstanceState The saved instance of the fragment
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(null);
        populateSpinner();
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

        securityQuestion1AnswerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(securityQuestion1AnswerEditText.getText().toString().isEmpty()){
                        securityQuestion1Filled = false;
                    }
                    else{
                        securityQuestion1Filled = true;
                    }

                }
            }
        });

        securityQuestion2AnswerEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(securityQuestion2AnswerEditText.getText().toString().isEmpty()){
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
                }
                else{
                    usernameFilled = false;
                }
                enableCreateAccountButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isPasswordMatch = false;

                //Check if the password and confirm password match
                if(confirmedPasswordFilled){
                    if(passwordEditText.getText().toString().equals(s.toString())){
                        isPasswordMatch = true;
                    }
                }

                //Check if the user entered a password that follow the guide line
                if(passwordGuidelineCheck(s.toString())){
                    passwordFilled = true;
                    isWeakPasswordEnable = false;
                }
                else{
                    passwordFilled = false;
                    isWeakPasswordEnable = true;
                }
                enableCreateAccountButton();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isPasswordMatch = false;

                //Check if password and confirm password match
                if(passwordEditText.getText().toString().equals(s.toString())){
                    confirmedPasswordFilled = true;
                    isPasswordMatch = true;
                    confirmPasswordErrorTextView.setVisibility(View.INVISIBLE);

                }
                else if(s.toString().trim().isEmpty()){
                    confirmedPasswordFilled = false;
                }
                enableCreateAccountButton();

            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        securityQuestion1AnswerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //CHeck to determine if the security question is entered
                if(!s.toString().trim().isEmpty()){
                    securityQuestion1Filled = true;
                }
                else{
                    securityQuestion1Filled = false;

                }
                enableCreateAccountButton();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        securityQuestion2AnswerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //CHeck to determine if the security question is entered
                if(!s.toString().trim().isEmpty()){
                    securityQuestion2Filled = true;
                }
                else{
                    securityQuestion2Filled = false;
                }
                enableCreateAccountButton();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }



    /**
     * Add the item to the spinner
     *
     */
    private void populateSpinner(){

    }


    /**
     * Enable create account button
     *
     */
    private void enableCreateAccountButton(){

        /**
         *Enable crate button to be able to click and change the color to black
         */
        if(usernameFilled && passwordFilled && confirmedPasswordFilled && securityQuestion1Filled
                && securityQuestion2Filled && !isWeakPasswordEnable && isPasswordMatch){
            fragmentController.enableCreateButton(true);
        }
        else {
            fragmentController.enableCreateButton(false);
        }
    }

    /**
     * Sent the user information to the server
     *
     */
    public boolean createAccount(){
        boolean isAccountCreated = true;
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String securityQuestion1 = securityQuestion1Spinner.getSelectedItem().toString();
        String securityQuestion2 = securityQuestion2Spinner.getSelectedItem().toString();
        String securityAnswer1 = securityQuestion1AnswerEditText.getText().toString();
        String securityAnswer2 = securityQuestion2AnswerEditText.getText().toString();
        createDotifyUser(username,password,securityQuestion1,securityQuestion2, securityAnswer1, securityAnswer2);
        if (username == null){
            isAccountCreated = false;
        }
        return isAccountCreated;
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

    /**
     * Creates an Dotify User from the federated identities
     *
     * @param username The user's chosen username
     */
    private void createDotifyUser(final String username, final String password, final String secQuestion1, final String secQuestion2,
                                  final String secAnswer1, final String secAnswer2) {
        //Create an dotifyUser object to send
        DotifyUser dotifyUser = new DotifyUser(username, password, secQuestion1, secQuestion2, secAnswer1, secAnswer2);

        //Start at POST request to create the user
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
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        //Create the POST request
        Call<DotifyUser> request = dotifyHttpInterface.createUser(dotifyUser.getUsername(), dotifyUser.getPassword(),
                dotifyUser.getQuestion1(), dotifyUser.getQuestion2(), dotifyUser.getAnswer1(), dotifyUser.getAnswer2());
        //Call the request asynchronously
//        try{
            request.enqueue(new Callback<DotifyUser>() {
                @Override
                public void onResponse(Call<DotifyUser> call, retrofit2.Response<DotifyUser> response) {
                    if (response.code() == 201) {
                        Log.d(TAG, "createDotifyUser-> onClick-> onSuccess-> onResponse: Successful Response Code " + response.code());
                        //Now send the user to the login screen
                        Intent signoutIntent = new Intent(getActivity(), StartUpContainer.class);
                        startActivity(signoutIntent);
                        getActivity().finish();

                    } else {
                        Log.d(TAG, "createDotifyUser-> onClick-> onSuccess-> onResponse: Failed response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<DotifyUser> call, Throwable t) {
                    //The request has unexpectedly failed
                    Log.d(TAG, "createDotifyUser-> onClick-> onSuccess-> onResponse: Unexpected request failure");
                    t.printStackTrace();
                }
            });
//        }
//        catch (Exception e){
//            Log.d(TAG, "Server not responding.");
//        }

    }

}
