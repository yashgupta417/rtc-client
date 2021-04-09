package com.roomify_app.rtc_client.views.SplashView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.roomify_app.rtc_client.R;
import com.roomify_app.rtc_client.views.loginSignupView.LoginSignupActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);



        Intent mainIntent = new Intent(getApplicationContext(), LoginSignupActivity.class);
        startActivity(mainIntent);
        finish();
    }
}