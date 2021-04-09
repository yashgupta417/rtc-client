package com.roomify_app.rtc_client.data.repositories;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.roomify_app.rtc_client.api.RetrofitClient;
import com.roomify_app.rtc_client.api.routes.UserRoutes;
import com.roomify_app.rtc_client.data.models.Room;
import com.roomify_app.rtc_client.data.models.User;
import com.roomify_app.rtc_client.utils.LocalStorage;
import com.roomify_app.rtc_client.utils.Utils;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    Application application;
    MutableLiveData<Integer> updateUserResult;
    public UserRepository(Application application){
        this.application=application;
        updateUserResult=new MutableLiveData<>();
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

    public LiveData<Integer> updateUser(Uri image, User user){

        String username= LocalStorage.getString("username",application);

        updateUserResult.setValue(0);
        if(image==null){
            updateUser(user,username);
        }else
            updateUserImage(image,user,username);
        return  updateUserResult;
    }


    //step-1 of user update
    private void updateUserImage(Uri image, User user,String username){
        //converting Uri to imagePart
        MultipartBody.Part imagePart= Utils.uriToImagePart(image,application);

        Call<User> call= RetrofitClient.getInstance(application).create(UserRoutes.class).updateUserImage(imagePart,username);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    updateUserResult.setValue(-1);
                    return;
                }
                updateUser(user,username);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                updateUserResult.setValue(-1);
            }
        });
    }

    //step-2 of user update
    private void updateUser(User user,String username){
        Call<User> call=RetrofitClient.getInstance(application).create(UserRoutes.class).updateUser(user,username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    updateUserResult.setValue(-1);
                    return;
                }
                updateUserResult.setValue(1);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                updateUserResult.setValue(-1);
            }
        });
    }
}
