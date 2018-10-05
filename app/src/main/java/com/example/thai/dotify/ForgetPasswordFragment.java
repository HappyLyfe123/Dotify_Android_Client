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

public class ForgetPasswordFragment extends Fragment {

    private OnChangeFragmentListener onChangeFragmentListener;
    private Button usernameSubmitButton;

    ViewStub userNameStub, securityQuestionStub, resetPasswordStub;

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

        //
        usernameSubmitButton = view.findViewById(R.id.submit_button);

        userNameStub = view.findViewById(R.id.username_stub);
        securityQuestionStub = view.findViewById(R.id.security_question_stub);
        resetPasswordStub = view.findViewById(R.id.reset_password_stub);
        View inflate = userNameStub.inflate();
        return view;
    }
}
