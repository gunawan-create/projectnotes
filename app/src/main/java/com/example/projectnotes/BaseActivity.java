package com.example.projectnotes;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(
                LocaleHelper.setLocale(newBase, null)
        );
    }
}
