package com.example.rtc_client.data.repositories;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rtc_client.api.RetrofitClient;
import com.example.rtc_client.api.objects.AgoraTokenResponse;
import com.example.rtc_client.api.objects.AuthResponse;
import com.example.rtc_client.api.routes.AgoraRoutes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgoraRepository {
    Application application;
    public AgoraRepository(Application application){
        this.application=application;
    }

    public LiveData<AgoraTokenResponse> getAgoraToken(String username, String roomName){
        MutableLiveData<AgoraTokenResponse> agoraTokenResponse=new MutableLiveData<>();
        Call<AgoraTokenResponse> call= RetrofitClient.getInstance(application).create(AgoraRoutes.class).getToken(username, roomName);
        call.enqueue(new Callback<AgoraTokenResponse>() {
            @Override
            public void onResponse(Call<AgoraTokenResponse> call, Response<AgoraTokenResponse> response) {
                if(!response.isSuccessful()){
                    return;
                }
                agoraTokenResponse.setValue(response.body());
            }

            @Override
            public void onFailure(Call<AgoraTokenResponse> call, Throwable t) {
                call.clone().enqueue(this);
            }
        });
        return agoraTokenResponse;
    }
}
