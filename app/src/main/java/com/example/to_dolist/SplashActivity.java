package com.example.to_dolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //This code helps to set the splash screen to the max i.e. also removes ActionBar from the top.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Thread thread = new Thread() {
            private static final String TAG = "Splash Exception";

            public void run() {
                try {

                    sleep(2000);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Exception starting the Splash Screen!");
                }
            }
        };

        thread.start();

    }

}