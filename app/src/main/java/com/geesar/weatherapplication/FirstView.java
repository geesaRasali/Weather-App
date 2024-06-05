package com.geesar.weatherapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class FirstView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(FirstView.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 9000);
    }
}
