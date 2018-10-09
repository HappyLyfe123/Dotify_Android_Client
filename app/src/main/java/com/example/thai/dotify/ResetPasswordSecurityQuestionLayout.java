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
    private List<String>  listOfSecurityQuestions;

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
        securityQuestion1.setText(listOfSecurityQuestions.get(0));
        securityQuestion2.setText(listOfSecurityQuestions.get(1));
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
