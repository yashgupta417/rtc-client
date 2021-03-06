package com.example.rtc_client.api.routes;

import com.example.rtc_client.api.objects.CreateRoomRequest;
import com.example.rtc_client.data.models.Room;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RoomRoutes {
    @POST("/createRoom")
    Call<Room> createRoom(@Body CreateRoomRequest createRoomRequest);

    @GET("/joinRoom")
    Call<Room> joinRoom(@Query("address") String address, @Query("username") String username);

    @GET("/room/{address}")
    Call<Room> getRoom(@Path("address") String address);
}
