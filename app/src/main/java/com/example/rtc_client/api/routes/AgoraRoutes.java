package com.example.rtc_client.api.routes;

import com.example.rtc_client.api.objects.AgoraTokenResponse;
import com.example.rtc_client.api.objects.AuthResponse;
import com.example.rtc_client.data.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AgoraRoutes {

    @GET("/agora/token")
    Call<AgoraTokenResponse> getToken(@Query("username") String username, @Query("roomName") String roomName);
}
