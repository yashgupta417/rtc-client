package com.example.rtc_client.views.loginSignupView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.rtc_client.data.models.User;
import com.example.rtc_client.data.repositories.AuthRepository;

public class LoginSignupViewModel extends AndroidViewModel {

    AuthRepository authRepository;

    public LoginSignupViewModel(@NonNull Application application) {
        super(application);
        authRepository=new AuthRepository(application);
    }

    public LiveData<Integer> login(User user){
        return authRepository.login(user);
    }

    public LiveData<Integer> signUp(User user){
        return authRepository.signUp(user);
    }

}
