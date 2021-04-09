package com.roomify_app.rtc_client.views.roomView;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.roomify_app.rtc_client.api.objects.AgoraTokenResponse;
import com.roomify_app.rtc_client.data.models.Message;
import com.roomify_app.rtc_client.data.models.Room;
import com.roomify_app.rtc_client.data.models.User;
import com.roomify_app.rtc_client.data.repositories.AgoraRepository;
import com.roomify_app.rtc_client.data.repositories.RoomRepository;
import com.roomify_app.rtc_client.data.repositories.UserRepository;

import java.util.ArrayList;

public class RoomViewModel extends AndroidViewModel {
    RoomRepository roomRepository;
    AgoraRepository agoraRepository;
    UserRepository userRepository;

    public ArrayList<Message> messages;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        messages=new ArrayList<>();
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

    public LiveData<Room> updateRoom(Room room,String address){
        return roomRepository.updateRoom(room,address);
    }

    public LiveData<Room> updateRoomImage(Uri uri,String address){
        return roomRepository.updateRoomImage(uri,address);
    }
}
