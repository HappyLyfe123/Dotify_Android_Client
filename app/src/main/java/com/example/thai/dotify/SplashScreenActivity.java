package com.example.thai.dotify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {

    /***
     * invoked at beginning
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(SplashScreenActivity.this, StartUpContainer.class);
        startActivity(intent);

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
