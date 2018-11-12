package com.example.thai.dotify;

import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.thai.dotify.Fragments.CreateAccountFragment;
import com.example.thai.dotify.Fragments.ForgetPasswordFragment;
import com.example.thai.dotify.Fragments.LoginFragment;
import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.example.thai.dotify.Utilities.SentToServerRequest;

/**
 * the StartUpContainer object represents the app's home apge
 */
public class StartUpContainer extends AppCompatActivity implements LoginFragment.OnChangeFragmentListener,
        CreateAccountFragment.CreateAccountListener, ForgetPasswordFragment.OnChangeFragmentListener,
        View.OnClickListener {

    private LoginFragment loginFragment;
    private CreateAccountFragment createAccountFragment;
    private ForgetPasswordFragment forgetPasswordFragment;
    private RelativeLayout toolbar;
    private ImageButton backButton;
    private Button createAccountButton;
    private TextView titleTextView;
    private boolean goHomeEnable;
    private boolean isLoginPage;
    private boolean isOnCreateAccount, isOnForgetPassword;


    //Enumerator
    public enum AuthFragmentType {
        LOGIN,
        FORGOT_PASSWORD,
        CREATE_ACCOUNT,

    }

    /**
     * initialize the StartUpContainer object
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        titleTextView = (TextView) findViewById(R.id.start_up_title_text_view);

        backButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        goHomeEnable = true;

        //Check why this activity was started
        beginFragment(AuthFragmentType.LOGIN, true, false);


    }

    /**
     * Helper method that replaces the current fragment with one that is specified
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
                titleTextView.setText(R.string.create_account_title);
                goHomeEnable = false;
                fragmentTransaction.replace(R.id.main_display_container, createAccountFragment);
                break;
            case FORGOT_PASSWORD:
                toolbar.setVisibility(View.VISIBLE);
                createAccountButton.setVisibility(View.GONE);
                titleTextView.setText(R.string.forget_password_title);
                fragmentTransaction.replace(R.id.main_display_container, forgetPasswordFragment);
                break;
        }
        if(addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    /**
     * invoked when a user selects one of the buttons in the StartUpContainer object is selected
     * @param fragmentType
     */
    @Override
    public void buttonClicked(AuthFragmentType fragmentType) {
        switch (fragmentType){
            //Start main activity container
            case LOGIN:
                Intent myIntent = new Intent(StartUpContainer.this, MainActivityContainer.class);
                startActivity(myIntent);
                break;
            //Load create account fragment
            case CREATE_ACCOUNT:
                beginFragment(AuthFragmentType.CREATE_ACCOUNT, true, true);
                break;
            //Load forgot password fragment
            case FORGOT_PASSWORD:
                beginFragment(AuthFragmentType.FORGOT_PASSWORD, true, true);
                isOnForgetPassword = true;
                break;
        }
    }

    /**
     * event handler for the buttons in the StartUpContainer object
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                goBackPreviousPage();
                break;
            case R.id.create_account_button:
                if(createAccountFragment.createAccount()){
                }

                break;
        }
    }

    /**
     * check if create button is enabled
     * @param enableCreateAccountButton - boolean variable to verify button is enabled
     */
    @Override
    public void enableCreateButton(boolean enableCreateAccountButton){
        //Enable create account button and change it color
        if(enableCreateAccountButton){
            createAccountButton.setEnabled(true);
            createAccountButton.setTextColor(getResources().getColor(R.color.black));
        }
        //Disable create account button and change its color
        else{
            createAccountButton.setEnabled(false);
            createAccountButton.setTextColor(getResources().getColor(R.color.createAccount));
        }
    }

    /**
     * Handle default back button
     */
    @Override
    public void onBackPressed() {
        //If the current page is the login page then will will go to the
        //main apps page
        if(goHomeEnable) {
            super.onBackPressed();
        }
        goBackPreviousPage();
    }

    /**
     * Remove current viewing screen and go back to previous screen
     */
    private void goBackPreviousPage(){

        //Remove the current view of back stack
        getFragmentManager().popBackStackImmediate();
        if(getFragmentManager().getBackStackEntryCount() == 0){
            //To allow default back button to go to home page
            goHomeEnable = true;
            toolbar.setVisibility(View.GONE);
        }

    }

}
