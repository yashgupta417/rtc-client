package com.example.rtc_client.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtc_client.R;

import java.util.Random;

public class Utils {
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static void showToast(String message, Activity activity){
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.custom_toast,activity.findViewById(R.id.toast_root));

        TextView textView=view.findViewById(R.id.message);
        textView.setText(message);


        Toast toast=new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }

    static int[][] colours={{
        0xFF667db6,0xFF0082c8,0xFF0082c8,0xFF667db6 //https://uigradients.com/#Hydrogen
    },{
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
    public static int[] getGradientColours(){

        //generating a random index
        Random rand=new Random();
        int index=rand.nextInt(colours.length);

        Log.i("msg",Integer.toString(index));
        return colours[index];
    }

}
