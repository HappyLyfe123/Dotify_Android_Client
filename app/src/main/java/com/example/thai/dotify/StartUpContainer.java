package com.example.thai.dotify;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import static com.example.thai.dotify.R.color.black;

public class StartUpContainer extends Activity implements LoginFragment.OnChangeFragmentListener,
        CreateAccountFragment.OnChangeFragmentListener, ForgetPasswordFragment.OnChangeFragmentListener,
        View.OnClickListener {

    private LoginFragment loginFragment;
    private CreateAccountFragment createAccountFragment;
    private ForgetPasswordFragment forgetPasswordFragment;
    private boolean isLoginPage;
    private RelativeLayout toolbar;
    private ImageButton backButton;
    private Button createAccountButton;
    private boolean goHomeEnable;

    //Enumerator
    public enum AuthFragmentType {
        LOGIN,
        FORGOT_PASSWORD,
        CREATE_ACCOUNT,
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_up_container);

        //Initialize Login Fragment
        loginFragment = new LoginFragment();
        loginFragment.setOnChangeFragmentListener(this);

        //Initialize Create Account Fragment
        createAccountFragment = new CreateAccountFragment();
        createAccountFragment.setOnChangeFragmentListener(this);

        //Initialize forget password Fragment
        forgetPasswordFragment = new ForgetPasswordFragment();
        forgetPasswordFragment.setOnChangeFragmentListener(this);


        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        backButton = (ImageButton) findViewById(R.id.back_button);
        createAccountButton = (Button) findViewById(R.id.create_account_button);

        backButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        goHomeEnable = true;


        //Check why this activity was started
        beginFragment(AuthFragmentType.LOGIN, true, false);


    }

    /**
     * Helper method that replaces the current fragment with one that is specified
     *
     * @param fragmentType   The fragment that should now appear
     * @param setTransition  If the fragment should be transitioned in to the viewer
     * @param addToBackStack If the fragment should be added to the activity's back-stack
     */
    private void beginFragment(AuthFragmentType fragmentType, boolean setTransition, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (fragmentType) {
            case LOGIN:
                fragmentTransaction.replace(R.id.main_display_container, loginFragment);
                break;
            case CREATE_ACCOUNT:
                toolbar.setVisibility(View.VISIBLE);
                createAccountButton.setVisibility(View.VISIBLE);
                goHomeEnable = false;
                fragmentTransaction.replace(R.id.main_display_container, createAccountFragment);
                break;
            case FORGOT_PASSWORD:
                toolbar.setVisibility(View.VISIBLE);
                createAccountButton.setVisibility(View.GONE);
                getFragmentManager().popBackStackImmediate();
                break;
        }
        if(addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void buttonClicked(AuthFragmentType fragmentType) {
        switch (fragmentType){
            case LOGIN:

                break;
            case CREATE_ACCOUNT:
                beginFragment(AuthFragmentType.CREATE_ACCOUNT, true, true);
                break;
            case FORGOT_PASSWORD:
                beginFragment(AuthFragmentType.FORGOT_PASSWORD, true, true);
                break;
        }
    }

    @Override
    public void enableCreateButton(boolean enableCreateAccountButton){
        if(enableCreateAccountButton){
            createAccountButton.setEnabled(true);
            createAccountButton.setTextColor(getResources().getColor(R.color.black));
        }
        else{
            createAccountButton.setEnabled(false);
            createAccountButton.setTextColor(getResources().getColor(R.color.createAccount));
        }
    }

    //Handle default back button
    @Override
    public void onBackPressed() {
        if(goHomeEnable) {
            super.onBackPressed();
        }
        goBackPreviousPage();
    }

    //Button Click listener for the activity
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                goBackPreviousPage();
                break;
            case R.id.create_account_button:
                break;
        }
    }

    //Remove current viewing screen and go back to previous screen
    private void goBackPreviousPage(){
        getFragmentManager().popBackStackImmediate();
        if(getFragmentManager().getBackStackEntryCount() == 0){
            //To allow default back button to go to home page
            goHomeEnable = true;
            toolbar.setVisibility(View.GONE);
        }

    }
}
