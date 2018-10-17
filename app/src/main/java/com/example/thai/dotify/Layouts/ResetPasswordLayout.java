package com.example.thai.dotify.Layouts;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.thai.dotify.R;

/**
 * the ResetPasswordLayout object represents account data to be updated
 */
public class ResetPasswordLayout extends Fragment {

    private EditText resetPasswordEditText;
    private EditText resetConfirmPasswordEditText;
    private Button resetResetPasswordButton;


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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * creates the View object to display the ResetPasswordLayout object
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return new View object
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.forget_password_reset_password_layout, container, false);

        //Initialize all of the views
        resetPasswordEditText = (EditText) view.findViewById(R.id.reset_new_password_edit_text);
        resetConfirmPasswordEditText = (EditText) view.findViewById(R.id.reset_confirm_password_edit_text);
        resetResetPasswordButton = (Button) view.findViewById(R.id.reset_password_button);

        resetResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }




}
