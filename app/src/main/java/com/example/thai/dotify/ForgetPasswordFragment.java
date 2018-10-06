package com.example.thai.dotify;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ForgetPasswordFragment extends Fragment{

    private OnChangeFragmentListener onChangeFragmentListener;

    private Button userNameSubmitButton, securityQuestionSubmitButton, resetPasswordButton;
    private EditText usernameEditText, securityQuestion1EditText, securityQuestion2EditText,
                passwordEditText, confirmPasswordEditText;
    private TextView securityQuestion1TextView, securityQuestion2TextView;
    private ViewStub usernameStub, securityQuestionStub, resetPasswordStub;

    private enum ViewStubType{
        USERNAME,
        SECURITY_QUESTION,
        RESET_PASSWORD
    }

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
                if(usernameStub.getLayoutResource() == 0){
                    usernameStub.setLayoutResource(R.layout.forget_password_username_layout);
                    currView = usernameStub.inflate();
                    usernameController(currView);
                }
                else{
                    usernameStub.setVisibility(View.VISIBLE);
                }
                break;
            case SECURITY_QUESTION:
                if(securityQuestionStub.getLayoutResource() == 0){
                    securityQuestionStub.setLayoutResource(R.layout.forget_password_security_question_layout);
                    currView = securityQuestionStub.inflate();
                    securityQuestionController(currView);
                }
                else{
                    securityQuestionStub.setVisibility(View.VISIBLE);
                }
                break;
            case RESET_PASSWORD:
                if(resetPasswordStub.getLayoutResource() == 0){
                    resetPasswordStub.setLayoutResource(R.layout.forget_password_reset_password_layout);
                    currView = resetPasswordStub.inflate();
                    resetPasswordController(currView);
                }
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
                usernameStub.setVisibility(View.GONE);
                switchStubView(ViewStubType.SECURITY_QUESTION);
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
                securityQuestionStub.setVisibility(View.GONE);
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

                }

            }
        });
    }
}
