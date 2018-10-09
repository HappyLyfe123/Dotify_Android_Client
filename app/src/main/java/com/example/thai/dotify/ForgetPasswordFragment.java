package com.example.thai.dotify;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class ForgetPasswordFragment extends Fragment{

    private OnChangeFragmentListener onChangeFragmentListener;

    private Button userNameSubmitButton, securityQuestionSubmitButton, resetPasswordButton;
    private EditText usernameEditText, securityQuestion1EditText, securityQuestion2EditText,
                passwordEditText, confirmPasswordEditText;
    private TextView securityQuestion1TextView, securityQuestion2TextView;
    private ViewStub usernameStub, securityQuestionStub, resetPasswordStub;
    private Context activtyContext;
    private static String[] listOfSecQuestions;

    //enum of possible fragments to display
    private enum ViewStubType{
        USERNAME,
        SECURITY_QUESTION,
        RESET_PASSWORD
    }

    /***
     * fragment interface
     */
    public interface OnChangeFragmentListener {
        void buttonClicked(StartUpContainer.AuthFragmentType fragmentType);
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
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
        activtyContext = context;
    }

    /***
     * creates a View object for this fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        //Initialize view stub
        usernameStub = (ViewStub) view.findViewById(R.id.forget_password_username_stub);
        securityQuestionStub = (ViewStub) view.findViewById(R.id.forget_password_security_question_stub);
        resetPasswordStub = (ViewStub) view.findViewById(R.id.forget_password_reset_password_stub);
        switchStubView(ViewStubType.USERNAME);

        return view;
    }

    /**
     * Tell the system which view stub to display
     *
     * @param viewType The view stub that will be display
     */
    public void switchStubView(ViewStubType viewType){
        View currView;
        switch (viewType){
            case USERNAME:
                //Check if a layout already exist
                if(usernameStub.getLayoutResource() == 0){
                    usernameStub.setLayoutResource(R.layout.forget_password_username_layout);
                    currView = usernameStub.inflate();
                    usernameController(currView);
                }
                //If layout already exist just set its to visible
                else{
                    usernameStub.setVisibility(View.VISIBLE);
                }
                break;
            case SECURITY_QUESTION:
                //Check if a layout already exist
                if(securityQuestionStub.getLayoutResource() == 0){
                    securityQuestionStub.setLayoutResource(R.layout.forget_password_security_question_layout);
                    currView = securityQuestionStub.inflate();
                    securityQuestionController(currView);
                }
                //If layout already exist just set its to visible
                else{
                    securityQuestionStub.setVisibility(View.VISIBLE);
                }
                break;
            case RESET_PASSWORD:
                //Check if a layout already exist
                if(resetPasswordStub.getLayoutResource() == 0){
                    resetPasswordStub.setLayoutResource(R.layout.forget_password_reset_password_layout);
                    currView = resetPasswordStub.inflate();
                    resetPasswordController(currView);
                }
                //If layout already exist just set its to visible
                else{
                    resetPasswordStub.setVisibility(View.VISIBLE);
                }
                break;
        }


    }


    //Control actions for reset password username layout
    private void usernameController(View currView){
        //Initialize view layout
        usernameEditText = (EditText) currView.findViewById(R.id.reset_password_user_name_edit_text);
        userNameSubmitButton = (Button) currView.findViewById(R.id.forget_password_username_submit_button);
        userNameSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove the current layout
                if (usernameEditText.getText().toString() == null){
                    Log.d(TAG, "Username Field is reading as null");
                }
                getSecurityQuestions("PenguinDan");
                if (listOfSecQuestions != null){
                    usernameStub.setVisibility(View.GONE);
                    //Sent this layout to the view stub
                    switchStubView(ViewStubType.SECURITY_QUESTION);
                }
                else{
                    Log.d(TAG, "Security Questions are null");
                }

            }
        });
    }

    //Control actions for reset password username layout
    private void securityQuestionController(View currView){
        //Initialize view layout
        securityQuestion1TextView = (TextView) currView.findViewById(R.id.forget_password_security_question_1_text_view);
        securityQuestion2TextView = (TextView) currView.findViewById(R.id.forget_password_security_question_2_text_view);
        securityQuestion1EditText = (EditText) currView.findViewById(R.id.forget_password_security_question_answer_1_edit_text);
        securityQuestion2EditText = (EditText) currView.findViewById(R.id.forget_password_security_question_answer_2_edit_text);
        securityQuestionSubmitButton = (Button) currView.findViewById(R.id.security_question_submit_button);

        securityQuestionSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove the current layout
                securityQuestionStub.setVisibility(View.GONE);
                //Sent this layout to the view stub
                switchStubView(ViewStubType.USERNAME);
            }
        });
    }

    //Control actions for reset password username layout
    private void resetPasswordController(View currView){
        passwordEditText = (EditText) currView.findViewById(R.id.reset_new_password_edit_text);
        confirmPasswordEditText = (EditText) currView.findViewById(R.id.reset_confirm_password_edit_text);
        resetPasswordButton = (Button) currView.findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordEditText.getText().equals(confirmPasswordEditText.getText())){
                    resetPassword(ResetPasswordSecurityQuestionLayout.getSecurityToken(),
                            usernameEditText.getText().toString(), passwordEditText.getText().toString());
                }

            }
        });
    }

    /**
     * Get's the 2 security questions from the given username. It will add to the List<String>
     *     listOfSecQuestions the security Questions.
     * @param username The username of the person you want to do the retreival from
     */
    private void getSecurityQuestions(final String username){
        //Start a GET request to get the user's security questions
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));
        //Add logging interceptor
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();

        //Create the GET request
        Call<String> request = dotifyHttpInterface.getResetQuestions(
                getString(R.string.appKey),
                username
        );

        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()){
                    int respCode = response.code();
                    if (respCode == Dotify.ACCEPTED) {
                        Log.d(TAG, "resetQuestions-> onResponse: Success Code : " + response.code());
                        String securityQuestions = response.body();
                        //now send the user to the reset password page
                        //onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.LOGIN);
                    }
                }
                else{
                    Log.d(TAG, "resetQuestions-> onResponse: Invalid Credentials : " + response.code());
                    //If failed, the user needs to reenter their username

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.w(TAG, "resetQuestions-> onFailure");
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Put request to reset the password
     * @param newPassword The new password to reset to
     */
    private void resetPassword(final String securityToken, final String username, final String newPassword) {
        //Start a PUT request to reset the user's password
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));

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
        //Add logging interceptor
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();
        //Create the PUT Request
        Call<DotifyUser> request = dotifyHttpInterface.updatePassword(securityToken,
                username, newPassword);
        //Call the request asynchronously
//        try{
        request.enqueue(new Callback<DotifyUser>() {
            @Override
            public void onResponse(Call<DotifyUser> call, retrofit2.Response<DotifyUser> response) {
                if (response.code() == 201) {
                    Log.d(TAG, "upDateDotifyUser-> onClick-> onSuccess-> onResponse: Successful Response Code " + response.code());
                    //Create the DotifyUser account
                    //startMainActivity();
                } else {
                    Log.d(TAG, "updateDotifyUser-> onClick-> onSuccess-> onResponse: Failed response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DotifyUser> call, Throwable t) {
                //The request has unexpectedly failed
                Log.d(TAG, "updateDotifyUser -> onClick-> onSuccess-> onResponse: Unexpected request failure");
                t.printStackTrace();
            }
        });

    }
    /**
     * Returns the list of security questions from the given username
     * @return the lsit of security questions
     */
    public static String[] getListOfSecQuestions (){
        return listOfSecQuestions;
    }

}
