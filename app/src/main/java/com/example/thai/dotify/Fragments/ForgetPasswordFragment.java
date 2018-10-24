package com.example.thai.dotify.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Telephony;
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

import com.example.thai.dotify.DotifyUser;
import com.example.thai.dotify.R;
import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;
import com.example.thai.dotify.StartUpContainer;
import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.example.thai.dotify.Utilities.JSONUtilities;
import com.example.thai.dotify.Utilities.SentToServerRequest;
import com.example.thai.dotify.Utilities.UserUtilities;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

/**
 * this object displays when user incorrectly enters his/her password
 */
public class ForgetPasswordFragment extends Fragment{

    private OnChangeFragmentListener onChangeFragmentListener;

    private Button userNameSubmitButton, securityQuestionSubmitButton, resetPasswordButton;
    private EditText usernameEditText, securityQuestion1EditText, securityQuestion2EditText,
                passwordEditText, confirmPasswordEditText;
    private TextView securityQuestion1TextView, securityQuestion2TextView;
    private ViewStub usernameStub, securityQuestionStub, resetPasswordStub;
    private Context activityContext;
    public static List<String> listOfSecQuestions;
    public static String securityToken;

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

    /**
     * inflate current fragment object
     * @param context
     * @param attrs
     * @param savedInstanceState
     */
    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
    }

    /**
     * Sets the OnChangeFragmentListener to communicate from this fragment to the activity
     * @param onChangeFragmentListener The listener for communication
     */
    public void setOnChangeFragmentListener(OnChangeFragmentListener onChangeFragmentListener) {
        this.onChangeFragmentListener = onChangeFragmentListener;
    }

    /**
     * attaches a context object providing information about app's environment
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
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

        securityToken = null;
        listOfSecQuestions = new ArrayList<>();

        return view;
    }

    /**
     * Tell the system which view stub to display
     * @param viewType The view stub that will be display
     */
    public void switchStubView(ViewStubType viewType){
        View currView;
        switch (viewType)
        {
            case USERNAME: //username entered incorrectly multiple times
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
            case SECURITY_QUESTION: //security questions entered incorrectly
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
            case RESET_PASSWORD: //user wants to modify his/her password
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


    /**
     * Control actions for reset password username layout
     */
    private void usernameController(View currView){
        //Initialize view layout
        usernameEditText = (EditText) currView.findViewById(R.id.reset_password_user_name_edit_text);
        userNameSubmitButton = (Button) currView.findViewById(R.id.forget_password_username_submit_button);
        userNameSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove the current layout
                getSecurityQuestions(usernameEditText.getText().toString());
            }
        });
    }

    /**
     * Control actions for reset password username layout
     * @param currView
     */
    private void securityQuestionController(View currView){
        //Initialize view layout
        securityQuestion1TextView = (TextView) currView.findViewById(R.id.forget_password_security_question_1_text_view);
        securityQuestion2TextView = (TextView) currView.findViewById(R.id.forget_password_security_question_2_text_view);
        securityQuestion1EditText = (EditText) currView.findViewById(R.id.forget_password_security_question_answer_1_edit_text);
        securityQuestion2EditText = (EditText) currView.findViewById(R.id.forget_password_security_question_answer_2_edit_text);
        securityQuestionSubmitButton = (Button) currView.findViewById(R.id.security_question_submit_button);

        securityQuestion1TextView.setText(listOfSecQuestions.get(0));
        securityQuestion2TextView.setText(listOfSecQuestions.get(1));

        securityQuestionSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateSecurityAnswers(usernameEditText.getText().toString(), securityQuestion1EditText.getText().toString(),
                        securityQuestion2EditText.getText().toString());
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
                    resetPassword(securityToken, usernameEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });
    }

    /**
     * Get's the 2 security questions from the given username. It will add to the List<String>
     * listOfSecQuestions the security Questions.
     * @param username The username of the person you want to do the retreival from
     */
    private void getSecurityQuestions(final String username){
        Call<ResponseBody> request = GetFromServerRequest.getSecurityQuestions(username);

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    int respCode = response.code();
                    if (respCode == 200) {
                        Log.d(TAG, "getSecurityQuestions-> onResponse: Success Code : " + response.code());
                        ResponseBody obtained = response.body();
                        try{
                            String ob = obtained.string();
                            JsonObject securityQuestions = JSONUtilities.ConvertStringToJSON(ob);
                            String sq1 = securityQuestions.get("securityQuestion1").getAsString();
                            String sq2 = securityQuestions.get("securityQuestion2").getAsString();
                            listOfSecQuestions.add(sq1);
                            listOfSecQuestions.add(sq2);
                            Log.d(TAG, "The security questions are "+listOfSecQuestions.get(0)+listOfSecQuestions.get(1));
                            //Switch stubs here
                            usernameStub.setVisibility(View.GONE);
                            switchStubView(ViewStubType.SECURITY_QUESTION);
                        }
                        catch(Exception ex){
                            Log.d(TAG, "This didn't work.");
                        }
                        //now send the user to the security question page
                    } else {
                        Log.d(TAG, "getSecurityQuestions-> onResponse: Invalid Credentials : " + response.code());
                        //If failed, the user needs to reenter their username
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.w(TAG, "resetQuestions-> onFailure");
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Sends a request to the server to see if the security questions are correct
     * @param username The username of the user that you want to verify
     * @param securityAnswer1 The answer to the first Security Question
     * @param securityAnswer2 THe asnwer to the second Security Question
     */
    public void validateSecurityAnswers(final String username, final String securityAnswer1, final String securityAnswer2){
        Call<ResponseBody> request = GetFromServerRequest.validateSecurityAnswers(username, securityAnswer1, securityAnswer2);

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    int respCode = response.code();
                    if (respCode == 202) {
                        Log.d(TAG, "validateSecurityAnswers-> onResponse: Success Code : " + response.code());
                        //We get a security token back that we send with the user to reset their password
                        ResponseBody obtained = response.body();
                        try{
                            String ob = obtained.string();
                            JsonObject token = JSONUtilities.ConvertStringToJSON(ob);
                            securityToken = token.get("token").getAsString();
                        }
                        catch(Exception exception){
                            Log.d(TAG, "This didn't work.");
                        }
                        //Remove the current layout
                        securityQuestionStub.setVisibility(View.GONE);
                        //Sent this layout to the view stub
                        switchStubView(ViewStubType.RESET_PASSWORD);
                        //send the user to the reset password stub
                    }
                }
                else{
                    Log.d(TAG, "validateSecurityAnswers-> onResponse: Invalid Credentials : " + response.code());
                    //Security Answers are incorrect. Ask user to try again
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.w(TAG, "validateSecurityAnswers-> onFailure");
            }
        });
    }

    /**
     * Put request to reset the password
     * @param newPassword The new password to reset to
     */
    private void resetPassword(final String securityToken, final String username, final String newPassword) {
        Call<DotifyUser> request = SentToServerRequest.resetPassword(securityToken, username, newPassword);
        //Call the request asynchronously
        request.enqueue(new Callback<DotifyUser>() {
            @Override
            public void onResponse(Call<DotifyUser> call, retrofit2.Response<DotifyUser> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "resetPassword-> onClick-> onSuccess-> onResponse: Successful Response Code " + response.code());
                    DotifyUser dotifyUser = response.body();
                    UserUtilities.cacheUser(activityContext, dotifyUser);
                    Log.d(TAG, "The user should be cached here.");
                    onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.LOGIN);
                    //Send the user to the login screen
                } else {
                    Log.d(TAG, "resetPassword-> onClick-> onSuccess-> onResponse: Failed response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DotifyUser> call, Throwable t) {
                //The request has unexpectedly failed
                Log.d(TAG, "resetPassword -> onClick-> onFailure-> onResponse: Unexpected request failure");
                t.printStackTrace();
            }
        });

    }

    /**
     * Returns the list of security questions from the given username
     * @return the lsit of security questions
     */
    public static List<String> getListOfSecQuestions (){
        return listOfSecQuestions;
    }
}