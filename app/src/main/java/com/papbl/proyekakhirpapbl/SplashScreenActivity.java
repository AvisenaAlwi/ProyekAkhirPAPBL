package com.papbl.proyekakhirpapbl;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed( () -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 1000);
    }
}
