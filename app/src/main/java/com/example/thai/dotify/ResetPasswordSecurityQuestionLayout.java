package com.example.thai.dotify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thai.dotify.Server.Dotify;
import com.example.thai.dotify.Server.DotifyHttpInterface;

import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;

import static android.support.constraint.Constraints.TAG;


public class ResetPasswordSecurityQuestionLayout extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TextView securityQuestion1, securityQuestion2;
    private String[] listOfSecurityQuestions;
    private static String securityToken;

    public ResetPasswordSecurityQuestionLayout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.forget_password_security_question_layout, container, false);
        securityQuestion1 = getActivity().findViewById(R.id.forget_password_security_question_1_text_view);
        securityQuestion2 = getActivity().findViewById(R.id.forget_password_security_question_2_text_view);
        listOfSecurityQuestions = ForgetPasswordFragment.getListOfSecQuestions();
        return view;
    }

//    @Override
//    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
//        securityQuestion1.setText("Hello");
//        securityQuestion2.setText("There");
//        super.onInflate(activity, attrs, savedInstanceState);
//    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        //Set the security questions to the first and second question respetively
        securityQuestion1.setText(listOfSecurityQuestions[0]);
        securityQuestion2.setText(listOfSecurityQuestions[1]);
}

    /**
     * Sends a request to the server to see if the security questions are correct
     * @param username The username of the user that you want to verify
     * @param securityAnswer1 The answer to the first Security Question
     * @param securityAnswer2 THe asnwer to the second Security Question
     */
    private void validateSecurityAnswers(final String username, final String securityAnswer1, final String securityAnswer2){
        //Start a GET request to login the user
        final Dotify dotify = new Dotify(getActivity().getString(R.string.base_URL));
        //Add logging interceptor
        dotify.addLoggingInterceptor(HttpLoggingInterceptor.Level.BODY);
        DotifyHttpInterface dotifyHttpInterface = dotify.getHttpInterface();

        //Create the GET request
        Call<String> request = dotifyHttpInterface.validateSecAnswers(
                getString(R.string.appKey),
                username,
                securityAnswer1,
                securityAnswer2
        );

        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()){
                    int respCode = response.code();
                    if (respCode == Dotify.ACCEPTED) {
                        Log.d(TAG, "validateSecurityAnswers-> onResponse: Success Code : " + response.code());
                        //We get a security token back that we send with the user to reset their password
                        securityToken = response.body();
                        //onChangeFragmentListener.buttonClicked(StartUpContainer.AuthFragmentType.LOGIN);
                    }
                }
                else{
                    Log.d(TAG, "validateSecurityAnswers-> onResponse: Invalid Credentials : " + response.code());
                    //Security Answers are incorrect. Ask user to try again
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.w(TAG, "validateSecurityAnswers-> onFailure");
            }
        });
    }

    /**
     * Getter method to get the security token
     * @return the security token
     */
    public static String getSecurityToken(){
        return securityToken;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }
}
