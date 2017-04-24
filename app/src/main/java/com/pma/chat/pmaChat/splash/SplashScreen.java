package com.pma.chat.pmaChat.splash;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pma.chat.pmaChat.MainActivity;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.auth.LoginScreen;

/**
 * Created by Bojan on 4/17/2017.
 */

public class SplashScreen extends Activity {

    ProgressBar progressBar;
    int progressStatus = 0;
    TextView textView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        textView = (TextView)findViewById(R.id.load_per);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            textView.setText(progressStatus + "%");
                        }
                    });
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (progressStatus == 100) {
                    Intent i = new Intent(SplashScreen.this, LoginScreen.class);
                    startActivity(i);
                }
            }
        }).start();
    }
}
