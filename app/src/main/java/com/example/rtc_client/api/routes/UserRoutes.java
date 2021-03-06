package com.example.rtc_client.api.routes;

import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserRoutes {
    @GET("user/{username}/rooms")
    Call<ArrayList<Room>> getAllRooms(@Path("username") String username);

    @GET("user/{username}")
    Call<User> getUser(@Path("username") String username);
}
