package com.roomify_app.rtc_client.views.homeView;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.roomify_app.rtc_client.data.models.Room;
import com.roomify_app.rtc_client.data.repositories.RoomRepository;
import com.roomify_app.rtc_client.data.repositories.UserRepository;

import java.util.ArrayList;

public class HomeViewModel extends AndroidViewModel {
    RoomRepository roomRepository;
    UserRepository userRepository;
    public HomeViewModel(@NonNull Application application) {
        super(application);
        roomRepository=new RoomRepository(application);
        userRepository=new UserRepository(application);
    }

    public LiveData<Integer> createRoom(String roomName,String username){
        return roomRepository.createRoom(roomName,username);
    }

    public LiveData<ArrayList<Room>> getRooms(String username){
        return userRepository.getAllRooms(username);
    }

    public LiveData<String> joinRoom(String address, String username){
        return roomRepository.joinRoom(address,username);
    }
}
