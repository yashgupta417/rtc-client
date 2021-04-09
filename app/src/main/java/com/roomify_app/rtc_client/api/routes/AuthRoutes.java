package com.roomify_app.rtc_client.api.routes;


import com.roomify_app.rtc_client.api.objects.AuthResponse;
import com.roomify_app.rtc_client.api.objects.Message;
import com.roomify_app.rtc_client.data.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthRoutes {
    @POST("/signup")
    Call<User> signUp(@Body User user);

    @POST("/login")
    Call<AuthResponse> login(@Body User user);

    @GET("")
    Call<Message> checkUsernameValidOrNot(@Query("username") String username);

}
