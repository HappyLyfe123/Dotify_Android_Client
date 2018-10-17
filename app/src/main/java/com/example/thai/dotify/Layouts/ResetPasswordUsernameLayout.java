package com.example.thai.dotify.Layouts;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thai.dotify.R;

/**
 * the ResetPasswordUsernameLayout object allows user to enter a new username
 */
public class ResetPasswordUsernameLayout extends Fragment {

    private OnFragmentInteractionListener mListener;

    /**
     * add a Bundle object to the ResetPasswordUsernameLayout object
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * create a View object to display the ResetPasswordUsernameLayout object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.forget_password_username_layout, container, false);
    }

    /**
     * inflate the ResetPasswordUsernameLayout object
     * @param context
     * @param attrs
     * @param savedInstanceState
     */
    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {

        super.onInflate(context, attrs, savedInstanceState);
    }

    /**
     * add information about app's environment to the ResetPasswordUsernameLayout object
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * detach fragment
     */
    @Override
    public void onDetach() {
        super.onDetach();
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
