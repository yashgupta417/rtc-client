package com.example.rtc_client.api;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    public static Retrofit retrofit;
    public static OkHttpClient okHttpClient;

    private static String API_URL="https://agile-savannah-88050.herokuapp.com/";

    public static Retrofit getInstance(Application application){
        if(retrofit==null){
            Gson gson= new GsonBuilder().serializeNulls().create();
            HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor();
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

            okHttpClient=new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(getTokenInterceptor(application))
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .build();

            retrofit=new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
    private static Interceptor getTokenInterceptor(Application application){
        Interceptor tokenInterceptor= new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                SharedPreferences preferences=application.getSharedPreferences(application.getPackageName(), Context.MODE_PRIVATE);
                String token=preferences.getString("token",null);
                Request request;
                if(token!=null) {
                    request = chain.request().newBuilder().addHeader("Authorization", "Bearer " + token).build();
                }else{
                    request=chain.request().newBuilder().build();
                }
                return chain.proceed(request);
            }
        };
        return tokenInterceptor;
    }
}
