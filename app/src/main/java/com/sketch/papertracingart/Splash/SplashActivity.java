package com.sketch.papertracingart.Splash;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.sketch.papertracingart.Main.MainActivity;
import com.sketch.papertracingart.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1500;
    private ProgressBar progressBar;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        progressBar = findViewById(R.id.progress_bar);
        countDownTimer = new CountDownTimer(SPLASH_TIME_OUT, 100) {

            @Override
            public void onTick(long millisUntilFinished) {

                float v = 100 - (float) millisUntilFinished / SPLASH_TIME_OUT * 100;
                int v1 = (int) v;
                progressBar.setProgress(v1);

            }

            @Override
            public void onFinish() {
                progressBar.setProgress(100);
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        countDownTimer.start();





    }
}
