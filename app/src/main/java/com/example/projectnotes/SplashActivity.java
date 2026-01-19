package com.example.projectnotes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(
                    SplashActivity.this,
                    LocationPermissionActivity.class
            ));
            finish();
        }, 1500);
    }
}
