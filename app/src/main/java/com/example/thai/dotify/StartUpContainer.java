package com.example.thai.dotify;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class StartUpContainer extends Activity implements LoginFragment.OnChangeFragmentListener {

    private LoginFragment loginFragment;
    private CreateAccountFragment createAccountFragment;
    public static final String START_LOGIN_ACTION = "SLA";


    //Enumerator
    public enum AuthFragmentType {
        LOGIN,
        FORGOT_PASSWORD,
        CREATE_ACCOUNT,
        VERIFY_CODE,
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
                fragmentTransaction.replace(R.id.main_display_container, createAccountFragment);
                break;
        }


        if (setTransition) {
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        //Check why this activity was started
        beginActivityFlow(getIntent().getAction());
    }

    /**
     * Gets the reason why the activity was started and begins the respective activity flow
     *
     * @param action Determines the flow of the activity
     */
    private void beginActivityFlow(String action) {

        switch (action) {
            case START_LOGIN_ACTION:
                beginFragment(AuthFragmentType.LOGIN, true, false);
                break;
        }
    }

    @Override
    public void buttonClicked(AuthFragmentType fragmentType) {
        switch (fragmentType){
            case CREATE_ACCOUNT:
                beginFragment(AuthFragmentType.CREATE_ACCOUNT, true, true);
        }
    }
}
