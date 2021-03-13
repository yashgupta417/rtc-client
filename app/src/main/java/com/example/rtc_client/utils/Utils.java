package com.example.rtc_client.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtc_client.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Utils {
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }


    static int[][] gradientColours={{
        0xFFff9966,0xFFff9966 //https://uigradients.com/#OrangeCoral
    },{
        0xFFDCE35B,0xFF45B649 //https://uigradients.com/#EasyMed
    },{
        0xFFee9ca7,0xFFffdde1 //https://uigradients.com/#PiggyPink
    },{
        0xFF11998e,0xFF38ef7d //https://uigradients.com/#Quepal
    },{
        0xFF56CCF2,0xFF2F80ED //https://uigradients.com/#BlueSkies
    },{
        0xFFf46b45,0xFFeea849 //https://uigradients.com/#MasterCard
    },{
        0xFFED213A,0xFF93291E //https://uigradients.com/#SinCityRed
    },{
        0xFF799F0C,0xFFACBB78 //https://uigradients.com/#Reaqua
    }};
    public static int[] getRandomGradientColours(){

        //generating a random index
        Random rand=new Random();
        int index=rand.nextInt(gradientColours.length);

        Log.i("msg",Integer.toString(index));

        //appending black
        int[] c=gradientColours[index];
        c=Arrays.copyOf(c,c.length+1);
        c[c.length-1]=0xFF000000;

        return c;
    }

    private static String getRealPathFromURI(Uri uri,Application application) {
        Cursor cursor = application.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static MultipartBody.Part uriToImagePart(Uri image, Application application){

        //compressing image
        File file = new File(getRealPathFromURI(image,application));
        File compressimagefile = null;
        try {
            compressimagefile = new Compressor(application).compressToFile(file);
        } catch (IOException e) {
            compressimagefile = file;
            e.printStackTrace();
        }


        final RequestBody requestBody = RequestBody.create(compressimagefile, MediaType.parse("multipart/form-data"));
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        return  imagePart;
    }

    private static int[] colours={
            0xff219ebc,
            0xff52b69a,
            0xffffafcc,
            0xffbde0fe,
            0xff97d8c4,
            0xff9fffcb,
            0xffa594f9,
            0xffc0d6df,
            0xfff7aef8,
            0xfff49e4c,
            0xffb5f44a,

    };
    public static int getRandomColour(){
        //generating a random index
        Random rand=new Random();
        int index=rand.nextInt(colours.length);

        return colours[index];
    }



}
