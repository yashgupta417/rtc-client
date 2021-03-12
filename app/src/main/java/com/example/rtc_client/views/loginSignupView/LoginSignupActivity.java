package com.example.rtc_client.views.loginSignupView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtc_client.R;
import com.example.rtc_client.api.objects.AuthResponse;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.LocalStorage;
import com.example.rtc_client.utils.Utils;
import com.example.rtc_client.views.homeView.HomeActivity;

public class LoginSignupActivity extends AppCompatActivity {
    Button submitButton, alternateButton;
    EditText name,username,email,password;
    TextView formHead;
    LoginSignupViewModel viewModel;

    Integer UIState;
    static Integer SIGN_UP_STATE=0;
    static Integer LOGIN_STATE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        if(isLoggedIn()){
            moveToHomeScreen();
        }

        submitButton= findViewById(R.id.submit);
        alternateButton= findViewById(R.id.alternate);
        name=findViewById(R.id.name);
        username=findViewById(R.id.username);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        formHead=findViewById(R.id.form_head_text);

        viewModel=ViewModelProviders.of(this).get(LoginSignupViewModel.class);

        initUIState();
        loadUIState();

        alternateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleUIState();
                loadUIState();
            }
        });
    }

    public boolean isLoggedIn(){
        return LocalStorage.getString("token",getApplication())!=null;
    }

    public void initUIState(){
        UIState=LOGIN_STATE;
    }
    public void toggleUIState(){
        //toggling UI state
        if(UIState==LOGIN_STATE)
            UIState=SIGN_UP_STATE;
        else
            UIState= LOGIN_STATE;
    }

    public void loadUIState(){
        refreshFormFields();
        if(UIState==LOGIN_STATE)
            setLoginForm();
        else
            setSignUpForm();
    }
    public void refreshFormFields(){
        username.setText("");
        name.setText("");
        email.setText("");
        password.setText("");
    }
    public void setLoginForm(){
        submitButton.setText("Login");
        alternateButton.setText("Create a new account.");
        name.setVisibility(View.GONE);
        email.setVisibility(View.GONE);

        formHead.setText("LOG IN");
    }

    public void setSignUpForm(){
        submitButton.setText("Signup");
        alternateButton.setText("Already have a account?Login.");
        name.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);

        formHead.setText("SIGN UP");
    }

    public void submit(View view){
        if(UIState==LOGIN_STATE)
            login();
        else
            signUp();
    }


    public void signUp(){
        String usernameString=username.getText().toString();
        String passwordString=password.getText().toString();
        String nameString=name.getText().toString();
        String emailString=email.getText().toString();



        if(usernameString.trim().isEmpty() || passwordString.trim().isEmpty() || nameString.trim().isEmpty() || emailString.trim().isEmpty()){
            Utils.showToast("Please fill all the details",this);
            return;
        }

        if(passwordString.trim().length()<8){
            Utils.showToast("Password must contain 8 characters",this);
            return;
        }

        User user=new User(nameString,usernameString,emailString,passwordString,null,null,null);
        signUp(user);

    }

    public void signUp(User user){
        viewModel.signUp(user).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer==-1){
                    Utils.showToast("Something went wrong.",LoginSignupActivity.this);
                }else if(integer==1){
                    login();
                }
            }
        });
    }


    public void login(){
        String usernameString=username.getText().toString();
        String passwordString=password.getText().toString();

        //Validating details
        if(usernameString.trim().isEmpty() || passwordString.trim().isEmpty()){
            Utils.showToast("Enter username and password",this);
            return;
        }

        User user=new User(null,username.getText().toString(),null,password.getText().toString(),null,null,null);
        login(user);
    }

    public void login(User user){
        viewModel.login(user).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer==-2){
                    Utils.showToast("Something went wrong.",LoginSignupActivity.this);
                }else if(integer==-1){
                    Utils.showToast("Invalid username/password",LoginSignupActivity.this);
                }else if(integer==1){
                    moveToHomeScreen();
                }
            }
        });
    }

    public void moveToHomeScreen(){
        Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }
}
