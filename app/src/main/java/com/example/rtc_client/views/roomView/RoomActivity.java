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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
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
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.LocalStorage;
import com.example.rtc_client.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.agora.rtc.IRtcChannelEventHandler;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import pl.droidsonroids.gif.GifImageView;

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
    Room room;



    RoomViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        viewModel= ViewModelProviders.of(this).get(RoomViewModel.class);
        fetchRoomDetails();

        if(!havePermissions()){
            requestPermissions();
        }else{
            fetchRoomDetails();
        }
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
                Utils.showToast("Can't proceed without permissions.",this);
                finish();
            }else{
                fetchRoomDetails();
            }
        }
    }

    public void fetchRoomDetails(){
        //debugging
        Log.i("room activity message","fetching room details");

        String address=getIntent().getStringExtra("address");
        viewModel.getRoom(address).observe(this, new Observer<Room>() {
            @Override
            public void onChanged(Room room) {
                if(room!=null){
                    RoomActivity.this.room=room;
                    start();
                }
            }
        });
    }

    public void start(){
        initUI();
        initRTC();
        initRtcUI();
        fetchTokenAndJoinChannel();
    }

    RecyclerView membersRecyclerView;
    MembersAdapter membersAdapter;
    TextView  addressTextView;
    RelativeLayout roomDetailsParent;
    ImageView toggleDetailsImageView;
    ImageView audioButton, videoButton;
    String username;
    Integer uid;
    GifImageView loader;

    public void initUI(){
        //debugging
        Log.i("room activity message","initUI");
        //setting up loader
        loader=findViewById(R.id.loader);
        loader.setVisibility(View.VISIBLE);

        //setting up members recycler view
        membersRecyclerView=findViewById(R.id.members_recycler_view);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        membersRecyclerView.setHasFixedSize(true);

        //adding adapter
        membersAdapter=new MembersAdapter((ArrayList<User>) room.getMembers(),this);
        membersRecyclerView.setAdapter(membersAdapter);

        //setting room name
        roomNameHeaderTextView=findViewById(R.id.header_room_name);
        roomNameHeaderTextView.setText(room.getName());


        //setting address
        addressTextView=findViewById(R.id.address_textView);
        addressTextView.setText(room.getAddress());

        //initiating other views
        roomDetailsParent=findViewById(R.id.room_details_rl);
        toggleDetailsImageView=findViewById(R.id.up_down_arrow);

        audioButton=findViewById(R.id.audio_button);
        videoButton=findViewById(R.id.video_button);

        //setting username
        username= LocalStorage.getString("username",getApplication());

        //setting viewModel
        viewModel=ViewModelProviders.of(this).get(RoomViewModel.class);

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

    }

    public void initRtcUI(){
        //debugging
        Log.i("room activity message","initRTCUI");


        participantRecyclerView=findViewById(R.id.participants_recycler_view);
        participantRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        participantRecyclerView.setHasFixedSize(true);

        participantAdapter=new CallParticipantAdapter(rtcEngine,this);
        participantRecyclerView.setAdapter(participantAdapter);
    }


    public void fetchTokenAndJoinChannel(){
        //debugging
        Log.i("room activity message","Fetching token");

        //fetch token from server
        viewModel.getAgoraToken(username,room.getName()).observe(this, new Observer<AgoraTokenResponse>() {
            @Override
            public void onChanged(AgoraTokenResponse response) {
                if(response!=null){
                    //debugging
                    Log.i("room activity message","token: "+ response.getToken());

                    joinChannel(response.getToken(),response.getUserString());
                }
            }
        });
    }

    public void joinChannel(String token,String userString){
        //debugging
        Log.i("room activity message","Joining channel");

        //joining channel
        rtcEngine.joinChannelWithUserAccount(token,room.getName(),userString);
    }


    private IRtcEngineEventHandler rtcEngineEventHandler=new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            //debugging
            Log.i("room activity message","channel joined");
            RoomActivity.this.uid=uid;

            addVideo(uid,true);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //debugging
                    Log.i("room activity message","remote joined");
                    Toast.makeText(RoomActivity.this, "remote joined" + Integer.toString(uid), Toast.LENGTH_SHORT).show();

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
            rtcEngine.getUserInfoByUid(uid,userInfo);
            userAccount = userInfo.userAccount;
            if(userAccount!=null)
                break;
        }

        //decoding userAccount
        userAccount=userAccount.replaceAll("#","\"");
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

        //hide loader if isMe=true
        if(isMe) loader.setVisibility(View.GONE);

    }



    public void removeUserFromList(int uid){
        participantAdapter.removeParticipant(uid);
    }

    public boolean detailsExpanded=false;
    public void toggleDetails(View view){
        if(detailsExpanded){
            roomDetailsParent.setVisibility(View.GONE);
            toggleDetailsImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_baseline_expand_more_100));
            detailsExpanded=false;
        }else{
            roomDetailsParent.setVisibility(View.VISIBLE);
            toggleDetailsImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_baseline_expand_less_100));
            detailsExpanded=true;
        }
    }

    //handle exit
    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveRoom();
    }

    public void leaveRoom(){
        rtcEngine.leaveChannel();
        RtcEngine.destroy();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    public void exitRoom(View view){
        leaveRoom();
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





    //copy room address
    public void copyAddressToClipboard(View view){
        //getting room address
        String label="room address";
        String address=room.getAddress();

        //copying address to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, address);
        clipboard.setPrimaryClip(clip);

        //showing toast
        Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
    }

    //post photos on server for room and user

    //allow room and user editing

    //implement chat
}