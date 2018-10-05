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

    private Button userNameSubmitButton;

    private ViewStub currentViewStub,userNameStub, securityQuestionStub, resetPasswordStub;
    public byte currentStubNum;

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
        userNameStub = (ViewStub) view.findViewById(R.id.username_stub);
        securityQuestionStub = (ViewStub) view.findViewById(R.id.security_question_stub);
        resetPasswordStub = (ViewStub) view.findViewById(R.id.reset_password_stub);

        switchStubView((byte)1);

        return view;
    }

    /**
     * Tell the system which view stub to display
     *
     * @param viewNum The view stub that will be display
     */
    public void switchStubView(byte viewNum){
        if(currentViewStub != null)
            currentViewStub.setVisibility(View.GONE);
        currentStubNum = viewNum;
        switch (viewNum){
            case 1:
                currentViewStub = userNameStub;

                break;
            case 2:
                currentViewStub = securityQuestionStub;
                break;
            case 3:
                currentViewStub = resetPasswordStub;
                break;

        }
    }
}
