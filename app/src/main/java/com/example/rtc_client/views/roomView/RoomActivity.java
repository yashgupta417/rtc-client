package com.example.rtc_client.views.roomView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtc_client.R;
import com.example.rtc_client.api.objects.AgoraTokenResponse;
import com.example.rtc_client.data.models.AgoraUser;
import com.example.rtc_client.data.models.Message;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.GlideApp;
import com.example.rtc_client.utils.LocalStorage;
import com.example.rtc_client.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.IRtcChannelEventHandler;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import pl.droidsonroids.gif.GifImageView;

import static io.agora.rtc.Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO;
import static io.agora.rtc.Constants.AUDIO_SCENARIO_MEETING;
import static io.agora.rtc.Constants.REMOTE_AUDIO_STATE_DECODING;
import static io.agora.rtc.Constants.REMOTE_AUDIO_STATE_STARTING;
import static io.agora.rtc.Constants.REMOTE_AUDIO_STATE_STOPPED;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_DECODING;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_STARTING;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_STOPPED;

public class RoomActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;


    //required permissions
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    RtcEngine rtcEngine;
    RecyclerView participantRecyclerView;
    CallParticipantAdapter participantAdapter;
    TextView roomNameHeaderTextView;
    CircleImageView imageView;
    Room room;
    User user;

    RoomViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        showRoomNameOnTop();

        viewModel= ViewModelProviders.of(this).get(RoomViewModel.class);


        if(!havePermissions()){
            requestPermissions();
        }else{
            start();
        }
    }
    /*Steps
     *takePermission---->fetchRoomDetails--->initChat---->initRTC---->joinChannel
     */


    /***************Loading Dialog**********************/
    Dialog loadingDialog;
    public void showLoadingDialog(){
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_room_init);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        String name=getIntent().getStringExtra("name");
        TextView roomName=loadingDialog.findViewById(R.id.room_name);
        roomName.setText(name);

        ImageView exit=loadingDialog.findViewById(R.id.close);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadingDialog.show();
    }

    public void dismissLoadingDialog(){
        loadingDialog.dismiss();
    }

    /***************************************************/


    public void showRoomNameOnTop(){
        String name=getIntent().getStringExtra("name");

        roomNameHeaderTextView=findViewById(R.id.header_room_name);
        roomNameHeaderTextView.setText(name);
    }


    public Boolean havePermissions(){
        for(String permission: REQUESTED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==PERMISSION_REQ_ID){
            if(!havePermissions()){
                Toast.makeText(this, "Can't proceed without permissions.", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                start();
            }
        }
    }

    public void start(){
        //init UI
        initUI();

        //showing loading dialog
        showLoadingDialog();

        //join chat
        initChat();
    }



    ImageView audioButton, videoButton;
    String username;
    Integer uid;


    public void initUI(){
        //debugging
        Log.i("room activity message","initUI");

        imageView=findViewById(R.id.header_room_image);

        audioButton=findViewById(R.id.audio_button);
        videoButton=findViewById(R.id.video_button);

        //setting username
        username= LocalStorage.getString("username",getApplication());

        //setting viewModel
        viewModel=ViewModelProviders.of(this).get(RoomViewModel.class);

        participantRecyclerView=findViewById(R.id.participants_recycler_view);
        participantRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        participantRecyclerView.setHasFixedSize(true);

    }

    public void initRTC(){
        //debugging
        Log.i("room activity message","initRTC");

        try {
            rtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), rtcEngineEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        //enabling video
        rtcEngine.enableVideo();

        //setting up video configuration
        VideoEncoderConfiguration videoEncoderConfiguration= new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_840x480,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT);

        rtcEngine.setVideoEncoderConfiguration(videoEncoderConfiguration);

        //setting audio profile
        rtcEngine.setAudioProfile(AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO,AUDIO_SCENARIO_MEETING);

        //adding adapter
        participantAdapter=new CallParticipantAdapter(rtcEngine,this);
        participantRecyclerView.setAdapter(participantAdapter);

        //join channel
        joinChannel();


    }

    String token, userString;
    public void joinChannel(){
        //debugging
        Log.i("room activity message","Joining channel");

        //joining channel
        rtcEngine.joinChannelWithUserAccount(token,room.getName(),userString);
    }


    private IRtcEngineEventHandler rtcEngineEventHandler=new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //debugging
                    Log.i("room activity message","channel joined");
                    RoomActivity.this.uid=uid;

                    //add video
                    addVideo(uid,true);

                    //dismiss loader
                    //this marks the ending of room init work
                    dismissLoadingDialog();
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(RoomActivity.this, "new user joined", Toast.LENGTH_SHORT).show();
                    addVideo(uid,false);
                }
            });

        }


        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(state==REMOTE_VIDEO_STATE_STARTING || state==REMOTE_VIDEO_STATE_DECODING) {

                        //video enabled
                        participantAdapter.onUserVideoStateChanged(uid,true);
                    }else if(state==REMOTE_VIDEO_STATE_STOPPED){

                        //video disabled
                        participantAdapter.onUserVideoStateChanged(uid,false);
                    }
                }
            });

        }

        @Override
        public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(state==REMOTE_AUDIO_STATE_STARTING || state==REMOTE_AUDIO_STATE_DECODING) {
                        //audio enabled
                        participantAdapter.onUserAudioStateChanged(uid,true);
                    }else if(state==REMOTE_AUDIO_STATE_STOPPED){

                        //audio disabled
                        participantAdapter.onUserAudioStateChanged(uid,false);
                    }
                }
            });

        }

        @Override
        public void onUserOffline(int uid, int reason) {
            removeUserFromList(uid);
        }
    };


    public void addVideo(int uid,boolean isMe){


        //getting userAccount info from uid
        UserInfo userInfo=new UserInfo();


        //fetching userAccount
        //looping until userAccount is not updated
        String userAccount;
        while(true) {
            Log.i("while loop in add video","running");
            rtcEngine.getUserInfoByUid(uid,userInfo);
            userAccount = userInfo.userAccount;
            if(userAccount!=null)
                break;
        }

        //decoding userAccount
        userAccount=userAccount.replaceAll("#","\"");
        userAccount=userAccount.replaceAll(";","/");
        Log.i("msgggg",userAccount);

        //extracting user object from string
        Gson gson=new Gson();
        User user=gson.fromJson(userAccount,User.class);

        //creating a agoraUser
        AgoraUser agoraUser=new AgoraUser(user,uid,isMe,true,true);

        //debugging
        Log.i("room activity message","Add video");

        //adding video
        participantAdapter.addParticipant(agoraUser);


    }



    public void removeUserFromList(int uid){
        participantAdapter.removeParticipant(uid);
    }




    //handle exit
    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveRoomAndDisconnectSocket();
    }

    public void leaveRoomAndDisconnectSocket(){
        //leave room
        if(rtcEngine!=null) {
            rtcEngine.leaveChannel();
            RtcEngine.destroy();
        }

        //disconnect chat socket
        if(chatSocket!=null)
            chatSocket.disconnect();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    public void exitRoom(View view){
        //on destroy will handle leaving room and disconnecting socket logic
        finish();
    }



    //switch camera
    public void switchCamera(View view){
        rtcEngine.switchCamera();
    }


    //toggle audio
    Boolean audioOn=true;
    public void toggleAudio(View view){
        audioOn=!audioOn;

        //updating button resource
        rtcEngine.muteLocalAudioStream(!audioOn);
        int res = audioOn ? R.drawable.room_actions_mic_on : R.drawable.room_actions_mic_off;
        audioButton.setImageResource(res);

        //updating adapter
        participantAdapter.onUserAudioStateChanged(uid,audioOn);
    }

    //toggle video
    Boolean videoOn=true;
    public void toggleVideo(View view){
        videoOn=!videoOn;

        //updating button resource
        rtcEngine.muteLocalVideoStream(!videoOn);
        int res = videoOn ? R.drawable.room_actions_video_on : R.drawable.room_actions_video_off;
        videoButton.setImageResource(res);

        //updating adapter
        participantAdapter.onUserVideoStateChanged(uid,videoOn);
    }

    public void openRoomDetailsSheet(View view){
        RoomDetailsBottomSheet bottomSheet=new RoomDetailsBottomSheet(room);
        bottomSheet.show(getSupportFragmentManager(),"roomDetails");

        //listening for updates
        bottomSheet.setOnEditListener(new RoomDetailsBottomSheet.OnEditListener() {
            @Override
            public void onImageEdit(String roomImage) {
                room.setImage(roomImage);
            }

            @Override
            public void onNameEdit(String roomName) {
                room.setName(roomName);

                //updating roomName header
                roomNameHeaderTextView.setText(roomName);
            }
        });
    }


    public User getUser(String username){
        ArrayList<User> members=(ArrayList<User>) room.getMembers();
        for(int i=0;i<members.size();i++){
            if(members.get(i).getUsername().equals(username))
                return members.get(i);
        }
        return null;
    }

    ChatBottomSheet chatBottomSheet;
    public void openChatBottomSheet(View view){

        //initializing chat sheet
        chatBottomSheet=new ChatBottomSheet(room,getUser(username),chatSocket,messages);

        //showing chat sheet
        chatBottomSheet.show(getSupportFragmentManager(),"chat");

    }

    /*******Chat Socket***********/

    ChatSocket chatSocket;
    ArrayList<Message> messages;
    public void initChat(){
        Log.i("chat","init chat");


        String address=getIntent().getStringExtra("address");
        chatSocket=new ChatSocket(address,username,this);
        chatSocket.setOnUpdateListener(new ChatSocket.UpdateListener() {

            @Override
            public void onSocketConnected(Room room, String token, String userString) {
                RoomActivity.this.room=room;
                RoomActivity.this.token=token;
                RoomActivity.this.userString=userString;

                RoomActivity.this.user=getUser(username);

                //init rtc
                initRTC();
            }

            @Override
            public void onOldMessages(ArrayList<Message> oldMessages) {
                //storing old messages
                messages=oldMessages;

                Log.i("room",Integer.toString(messages.size()));
            }

            @Override
            public void onNewMessage(Message message) {
                messages.add(message);

                //showing new message in chat sheet if visible
                if(chatBottomSheet!=null && chatBottomSheet.isVisible())
                    chatBottomSheet.updateMessages(messages);

            }

            @Override
            public void onUserMessage(Message message) {
                messages.add(message);

                //showing new message in chat sheet if visible
                if(chatBottomSheet!=null && chatBottomSheet.isVisible())
                    chatBottomSheet.updateMessages(messages);
            }

            @Override
            public void onMessageSent(String localId) {
                int pos=0;
                for(Message message : messages){
                    if(message.getLocalId()!=null && message.getLocalId().equals(localId)){
                        message.setSent(true);
                        break;
                    }
                    pos++;
                }
                Log.i("chat","pos: "+Integer.toString(pos));

                chatBottomSheet.updateMessageStatus(messages,pos);
            }
        });
    }

    /**********************************/

}