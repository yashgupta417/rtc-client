package com.example.rtc_client.views.roomView;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.rtc_client.data.models.Message;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.LocalStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatSocket {

    Room room;
    User user;
    Activity activity;


    private Socket socket;
    private static String URI="https://agile-savannah-88050.herokuapp.com/";

    public ChatSocket(Room room, User user, Activity activity) {
        this.room = room;
        this.user = user;
        this.activity = activity;

        init();
    }

    UpdateListener listener;
    public interface UpdateListener{
        void onOldMessages(ArrayList<Message> oldMessages);
        void onNewMessage(Message message);
        void onUserMessage(Message message);
    }

    public void setOnUpdateListener(UpdateListener listener){
        this.listener=listener;
    }
    private void init(){
        Log.i("chat","init socket");

        try {
            socket= IO.socket(URI);
        }catch (URISyntaxException e){}


        socket.on("receiveMessage",onMessageReceived);

        socket.connect();

        socket.emit("joinRoom", room.getAddress(), new Ack() {
            @Override
            public void call(Object... args) {
                JSONArray jsonArray= (JSONArray) args[0];
                Gson gson=new Gson();
                Type type = new TypeToken<List<Message>>(){}.getType();
                ArrayList<Message> oldMessages=(ArrayList<Message>) gson.fromJson(jsonArray.toString(),type);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onOldMessages(oldMessages);
                    }
                });
            }
        });
    }

    public Emitter.Listener onMessageReceived=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject jsonObject= (JSONObject) args[0];
            Log.i("msg",jsonObject.toString());
            Gson gson=new Gson();
            Message newMessage=gson.fromJson(jsonObject.toString(),Message.class);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    listener.onNewMessage(newMessage);
                }
            });
        }
    };



    public void sendMessage(Message message){
        Log.i("chat","sending message using socket.io");

        String username= LocalStorage.getString("username",activity.getApplication());
        String address=room.getAddress();

        listener.onUserMessage(message);

        //sending message
        socket.emit("sendMessage", message.getText(), username, address, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject response = (JSONObject) args[0];

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(response.getString("status").equals("sent")){
                                Toast.makeText(activity, "Message sent", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){ }
                    }
                });

            }
        });

    }


    public void disconnect(){
        socket.disconnect();
    }
}