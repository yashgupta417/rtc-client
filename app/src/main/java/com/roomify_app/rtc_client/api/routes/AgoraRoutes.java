package com.roomify_app.rtc_client.api.routes;

import com.roomify_app.rtc_client.api.objects.AgoraTokenResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AgoraRoutes {

    @GET("/agora/token")
    Call<AgoraTokenResponse> getToken(@Query("username") String username, @Query("roomName") String roomName);
}
