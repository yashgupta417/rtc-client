package com.example.rtc_client.views.roomView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.rtc_client.api.objects.AgoraTokenResponse;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.data.repositories.AgoraRepository;
import com.example.rtc_client.data.repositories.RoomRepository;
import com.example.rtc_client.data.repositories.UserRepository;

public class RoomViewModel extends AndroidViewModel {
    RoomRepository roomRepository;
    AgoraRepository agoraRepository;
    UserRepository userRepository;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        roomRepository=new RoomRepository(application);
        agoraRepository=new AgoraRepository(application);
        userRepository=new UserRepository(application);
    }

    public LiveData<Room> getRoom(String address){
        return roomRepository.getRoom(address);
    }

    public LiveData<User> getUser(String username){
        return userRepository.getUser(username);
    }

    public LiveData<AgoraTokenResponse> getAgoraToken(String username, String roomName){
        return agoraRepository.getAgoraToken(username,roomName);
    }
}
