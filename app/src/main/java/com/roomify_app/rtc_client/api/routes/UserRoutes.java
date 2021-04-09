package com.roomify_app.rtc_client.api.routes;

import com.roomify_app.rtc_client.data.models.Room;
import com.roomify_app.rtc_client.data.models.User;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserRoutes {
    @GET("user/{username}/rooms")
    Call<ArrayList<Room>> getAllRooms(@Path("username") String username);

    @GET("user/{username}")
    Call<User> getUser(@Path("username") String username);

    @PATCH("user/{username}")
    Call<User> updateUser(@Body User user,@Path("username") String username);

    @Multipart
    @PATCH("user/{username}/image")
    Call<User> updateUserImage(@Part MultipartBody.Part image,@Path("username") String username);
}
