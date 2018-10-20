package com.example.thai.dotify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.thai.dotify.Utilities.GetFromServerRequest;
import com.example.thai.dotify.Utilities.UserUtilities;

/**
 * the SplashScreenActivity object is the starting point for the application
 */
public class SplashScreenActivity extends Activity {

    /***
     * Initializes the application
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check whether the User's information is cached and start the appropriate activity
        if (UserUtilities.isLoggedIn(this) != null) {
            startActivity(new Intent(SplashScreenActivity.this, MainActivityContainer.class));
            new GetFromServerRequest(getString(R.string.base_URL), getString(R.string.appKey), UserUtilities.getUsername());
        } else {
            startActivity(new Intent(SplashScreenActivity.this, StartUpContainer.class));
        }
    }

    /**
     * stops the application
     */
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
