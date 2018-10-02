package com.example.thai.dotify;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class StartUpContainer extends Activity implements LoginFragment.OnChangeFragmentListener,
        CreateAccountFragment.OnChangeFragmentListener, ForgetPasswordFragment.OnChangeFragmentListener,
        View.OnClickListener {

    private LoginFragment loginFragment;
    private CreateAccountFragment createAccountFragment;
    private ForgetPasswordFragment forgetPasswordFragment;
    private Toolbar toolbar;
    private boolean isLoginPage;

    //Enumerator
    public enum AuthFragmentType {
        LOGIN,
        FORGOT_PASSWORD,
        CREATE_ACCOUNT,
        ACCOUNT_CREATED,
        BACK_BUTTON;
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

        toolbar = findViewById(R.id.start_up_toolbar);
        toolbar.setVisibility(View.GONE);

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
            case BACK_BUTTON:
                getFragmentManager().popBackStackImmediate();
                break;
            case ACCOUNT_CREATED:

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
            case CREATE_ACCOUNT:
                beginFragment(AuthFragmentType.CREATE_ACCOUNT, true, true);
                break;
            case BACK_BUTTON:
                beginFragment(AuthFragmentType.BACK_BUTTON, true, false);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
