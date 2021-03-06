package com.roomify_app.rtc_client.data.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.roomify_app.rtc_client.api.RetrofitClient;
import com.roomify_app.rtc_client.api.objects.AuthResponse;
import com.roomify_app.rtc_client.api.routes.AuthRoutes;
import com.roomify_app.rtc_client.data.models.User;
import com.roomify_app.rtc_client.utils.LocalStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    Application application;
    public AuthRepository(Application application){
        this.application=application;
    }

    public void saveLoginDetails(String username, String token){
        LocalStorage.saveString("username",username,application);
        LocalStorage.saveString("token",token,application);
    }

    public LiveData<Integer> login(User user){
        MutableLiveData<Integer> result=new MutableLiveData<Integer>();
        Call<AuthResponse> call=RetrofitClient.getInstance(application).create(AuthRoutes.class).login(user);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(!response.isSuccessful()){
                    result.setValue(-1);
                    return;
                }
                saveLoginDetails(user.getUsername(),response.body().getToken());
                result.setValue(1);
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.i("msggg",t.getMessage());
                result.setValue(-2);
            }
        });
        return result;
    }

    public LiveData<String> signUp(User user){
        MutableLiveData<String> result=new MutableLiveData<>();
        Call<User> call=RetrofitClient.getInstance(application).create(AuthRoutes.class).signUp(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    try {
                        result.setValue(response.errorBody().string());
                    }catch(Exception e){
                        //do nothing
                    }

                    return;
                }
                result.setValue("1");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                result.setValue("-2");
            }
        });

        return result;
    }

}
