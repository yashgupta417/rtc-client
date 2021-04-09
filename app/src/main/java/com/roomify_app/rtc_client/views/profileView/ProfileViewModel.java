package com.roomify_app.rtc_client.views.profileView;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.roomify_app.rtc_client.data.models.User;
import com.roomify_app.rtc_client.data.repositories.UserRepository;

public class ProfileViewModel extends AndroidViewModel {

    UserRepository userRepository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository=new UserRepository(application);
    }

    public LiveData<User> getUser(String username){
        return userRepository.getUser(username);
    }

    public LiveData<Integer> updateUser(Uri image, User user){
        return userRepository.updateUser(image,user);
    }
}
