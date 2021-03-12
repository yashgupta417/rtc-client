package com.example.rtc_client.api.routes;

import com.example.rtc_client.api.objects.CreateRoomRequest;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RoomRoutes {
    @POST("/createRoom")
    Call<Room> createRoom(@Body CreateRoomRequest createRoomRequest);

    @GET("/joinRoom")
    Call<Room> joinRoom(@Query("address") String address, @Query("username") String username);

    @GET("/room/{address}")
    Call<Room> getRoom(@Path("address") String address);

    @PATCH("room/{address}")
    Call<Room> updateRoom(@Body Room room);

    @Multipart
    @PATCH("room/{address}/image")
    Call<Room> updateRoomImage(@Part MultipartBody.Part image);
}
