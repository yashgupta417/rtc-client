package com.example.rtc_client.views.homeView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.rtc_client.R;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.utils.LocalStorage;
import com.example.rtc_client.utils.Utils;
import com.example.rtc_client.views.profileView.ProfileActivity;
import com.example.rtc_client.views.roomView.RoomActivity;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class HomeActivity extends AppCompatActivity {

    Button createRoomButton, joinRoomButton;
    HomeViewModel viewModel;
    GifImageView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createRoomButton=findViewById(R.id.create_room);
        joinRoomButton=findViewById(R.id.join_room);
        loader=findViewById(R.id.loader);
        viewModel= ViewModelProviders.of(this).get(HomeViewModel.class);

        fetchAndShowUserRooms();

    }

    public void moveToProfileScreen(View view){
        Intent intent=new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    public void moveToRoomActivity(String address){
        Intent intent=new Intent(getApplicationContext(), RoomActivity.class);
        intent.putExtra("address",address);
        startActivity(intent);
    }

    public void fetchAndShowUserRooms(){
        loader.setVisibility(View.VISIBLE);
        String username=LocalStorage.getString("username",getApplication());
        viewModel.getRooms(username).observe(this, new Observer<ArrayList<Room>>() {
            @Override
            public void onChanged(ArrayList<Room> rooms) {
                if(rooms!=null) {
                    loader.setVisibility(View.GONE);
                    showUserRooms(rooms);
                }
            }
        });
    }

    public void showUserRooms(ArrayList<Room> rooms){
        RecyclerView roomsRecyclerView=findViewById(R.id.rooms_recycler_view);

        //adding layout manager
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        roomsRecyclerView.setHasFixedSize(true);


        RoomsAdapter roomsAdapter=new RoomsAdapter(rooms,getApplicationContext());
        roomsAdapter.setOnItemClickListener(new RoomsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String address=roomsAdapter.rooms.get(position).getAddress();
                moveToRoomActivity(address);
            }
        });
        roomsRecyclerView.setAdapter(roomsAdapter);
    }




    public void showCreateRoomLayout(View view){
        CreateRoomBottomSheet createRoomBottomSheet=new CreateRoomBottomSheet();
        createRoomBottomSheet.show(getSupportFragmentManager(),"createRoom");

        createRoomBottomSheet.setOnRoomCreateListener(new CreateRoomBottomSheet.OnRoomCreateListener(){
            @Override
            public void onRoomCreate() {
                Utils.showToast("Room created",HomeActivity.this);
                fetchAndShowUserRooms();
            }

            @Override
            public void onFailure() {
                Utils.showToast("Something went wrong",HomeActivity.this);
            }
        });
    }

    public void showJoinRoomLayout(View view){
        JoinRoomBottomSheet joinRoomBottomSheet=new JoinRoomBottomSheet();
        joinRoomBottomSheet.show(getSupportFragmentManager(),"joinRoom");

        joinRoomBottomSheet.setOnRoomJoinListener(new JoinRoomBottomSheet.OnRoomCreateListener() {
            @Override
            public void onRoomJoin() {
                Utils.showToast("Room Joined",HomeActivity.this);
                fetchAndShowUserRooms();
            }

            @Override
            public void onFailure() {
                Utils.showToast("Something went wrong",HomeActivity.this);
            }
        });
    }


}