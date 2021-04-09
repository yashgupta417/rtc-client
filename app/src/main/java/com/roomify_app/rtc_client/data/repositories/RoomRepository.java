package com.roomify_app.rtc_client.data.repositories;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.roomify_app.rtc_client.api.RetrofitClient;
import com.roomify_app.rtc_client.api.objects.CreateRoomRequest;
import com.roomify_app.rtc_client.api.routes.RoomRoutes;
import com.roomify_app.rtc_client.data.models.Room;
import com.roomify_app.rtc_client.utils.Utils;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomRepository {
    Application application;
    public RoomRepository(Application application){
        this.application=application;
    }

    public LiveData<Integer> createRoom(String roomName, String username){
        MutableLiveData<Integer> result=new MutableLiveData<Integer>();
        Call<Room> call= RetrofitClient.getInstance(application).create(RoomRoutes.class).createRoom(new CreateRoomRequest(roomName,username));
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if(!response.isSuccessful()){
                    result.setValue(-1);
                    return;
                }
                result.setValue(1);
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Log.i("msgggggggggg",t.getMessage());
                result.setValue(-2);
            }
        });
        return result;
    }

    public LiveData<String> joinRoom(String address,String username){
        MutableLiveData<String> result=new MutableLiveData<>();
        Call<Room> call=RetrofitClient.getInstance(application).create(RoomRoutes.class).joinRoom(address,username);
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if(!response.isSuccessful()){
                    try {
                        String error=response.errorBody().string();
                        result.setValue(error);
                    }catch (Exception e){
                        Log.i("msg",e.getLocalizedMessage());
                    }

                    return;
                }
                result.setValue("1");
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                result.setValue("-1");
            }
        });
        return result;
    }

    public LiveData<Room> getRoom(String address){
        MutableLiveData<Room> room=new MutableLiveData<>();
        Call<Room> call=RetrofitClient.getInstance(application).create(RoomRoutes.class).getRoom(address);
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if(!response.isSuccessful()){
                    Log.i("msggggggggggg","error 1");
                    return;
                }
                room.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Log.i("msggggggggggg","error 2");
            }
        });
        return room;
    }

    public LiveData<Room> updateRoom(Room room,String address){
        MutableLiveData<Room> result=new MutableLiveData<>();

        Call<Room> call=RetrofitClient.getInstance(application).create(RoomRoutes.class).updateRoom(room,address);

        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if(!response.isSuccessful()){
                    result.setValue(new Room());
                    return;
                }

                result.setValue(response.body());

            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                result.setValue(new Room());
            }
        });

        return result;
    }

    public LiveData<Room> updateRoomImage(Uri uri,String address){
        MutableLiveData<Room> result=new MutableLiveData<>();

        MultipartBody.Part imagePart=Utils.uriToImagePart(uri,application);
        Call<Room> call=RetrofitClient.getInstance(application).create(RoomRoutes.class).updateRoomImage(imagePart,address);

        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if(!response.isSuccessful()){
                    result.setValue(new Room());
                    return;
                }

                result.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                result.setValue(new Room());
            }
        });

        return result;
    }
}
