package com.example.rtc_client.data.repositories;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rtc_client.api.RetrofitClient;
import com.example.rtc_client.api.routes.UserRoutes;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    Application application;
    public UserRepository(Application application){
        this.application=application;
    }

    public LiveData<ArrayList<Room>> getAllRooms(String username){
        MutableLiveData<ArrayList<Room>> rooms=new MutableLiveData<>();
        Call<ArrayList<Room>> call= RetrofitClient.getInstance(application).create(UserRoutes.class).getAllRooms(username);
        call.enqueue(new Callback<ArrayList<Room>>() {
            @Override
            public void onResponse(Call<ArrayList<Room>> call, Response<ArrayList<Room>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(application, "something went wrong", Toast.LENGTH_SHORT).show();
                    return;
                }
                rooms.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Room>> call, Throwable t) {

                call.clone().enqueue(this);
            }
        });
        return rooms;
    }

    public LiveData<User> getUser(String username){
        MutableLiveData<User> user=new MutableLiveData<>();
        Call<User> call=RetrofitClient.getInstance(application).create(UserRoutes.class).getUser(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(application, "something went wrong", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.setValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.clone().enqueue(this);
            }
        });
        return user;
    }
}
