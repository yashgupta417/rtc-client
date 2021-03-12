package com.example.rtc_client.views.profileView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rtc_client.R;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.GlideApp;
import com.example.rtc_client.utils.LocalStorage;
import com.example.rtc_client.utils.Utils;
import com.example.rtc_client.views.loginSignupView.LoginSignupActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView logoutTextView;
    ProfileViewModel viewModel;
    User user;
    TextView usernameTextView, nameTextView;
    CircleImageView profileImage,profileImageBG;
    GifImageView loader;
    RelativeLayout parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }



    public void init(){
        logoutTextView=findViewById(R.id.logout_text);

        //initializing views
        usernameTextView=findViewById(R.id.profile_username);
        nameTextView=findViewById(R.id.profile_name);
        profileImage=findViewById(R.id.profile_image);
        profileImageBG=findViewById(R.id.profile_image_bg);
        loader=findViewById(R.id.loader);
        parent=findViewById(R.id.parent);

        //setting up viewModel
        viewModel= ViewModelProviders.of(this).get(ProfileViewModel.class);

        //preload
        preLoad();
    }

    public void preLoad(){
        loader.setVisibility(View.VISIBLE);
        parent.setVisibility(View.GONE);
    }
    public void postLoad(){
        loader.setVisibility(View.GONE);
        parent.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onResume() {
        super.onResume();

        //fetch and display user details
        fetchAndDisplayUserDetails();
    }

    public void finishActivity(View view){
        finish();
    }
    public void fetchAndDisplayUserDetails(){
        String username=LocalStorage.getString("username",getApplication());
        viewModel.getUser(username).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user!=null){
                    ProfileActivity.this.user=user;
                    updateUI();
                }
            }
        });
    }

    public void updateUI() {

        usernameTextView.setText("@" + user.getUsername());
        nameTextView.setText(user.getName());

        if (user.getImage() != null){
            //removing padding
            profileImage.setPadding(0, 0, 0, 0);

            GlideApp.with(this).load(user.getImage()).into(profileImage);
        }
        else{
            ColorDrawable colorDrawable=new ColorDrawable(Utils.getRandomColour());
            GlideApp.with(this).load(colorDrawable).into(profileImageBG);
        }

        //will un-hide UI when load is happened first time
        postLoad();
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

    public void moveToUpdateProfileActivity(View view){
        Intent intent=new Intent(getApplicationContext(),UpdateProfileActivity.class);
        intent.putExtra("name",user.getName());
        intent.putExtra("image",user.getImage());
        startActivity(intent);
    }
}