package com.example.rtc_client.views.profileView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.rtc_client.R;
import com.example.rtc_client.utils.LocalStorage;
import com.example.rtc_client.views.loginSignupView.LoginSignupActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView logoutTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutTextView=findViewById(R.id.logout_text);
    }

    public void logout(View view){
        //erasing login details
        LocalStorage.saveString("token",null,getApplication());
        LocalStorage.saveString("username",null,getApplication());

        //moving to login screen
        Intent intent=new Intent(getApplicationContext(), LoginSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}