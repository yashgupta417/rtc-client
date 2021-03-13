package com.example.rtc_client.views.homeView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.rtc_client.R;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.utils.LocalStorage;
import com.example.rtc_client.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CreateRoomBottomSheet extends BottomSheetDialogFragment {

    EditText roomNameEditText;
    Button createRoomButton;
    HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.bottom_sheet_create_room, container, false);

        roomNameEditText=view.findViewById(R.id.room_name);
        createRoomButton=view.findViewById(R.id.create_room);
        viewModel=ViewModelProviders.of(this).get(HomeViewModel.class);

        activateButton();

        return view;
    }

    public void updateButtonState(boolean isEnabled){
        if(isEnabled){
            createRoomButton.setAlpha(1f);
        }else{
            createRoomButton.setAlpha(0.3f);
        }
        createRoomButton.setEnabled(isEnabled);
    }
    private OnRoomCreateListener listener;
    public interface OnRoomCreateListener{
        void onRoomCreate();

    }

    public void setOnRoomCreateListener(OnRoomCreateListener listener){
        this.listener=listener;
    }

    public void activateButton(){
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoom();
            }
        });
    }
    public void createRoom(){

        String roomName=roomNameEditText.getText().toString().trim();
        if(roomName.isEmpty()){
            Toast.makeText(getActivity(), "Please enter room name", Toast.LENGTH_SHORT).show();
            return;
        }

        //disabling button
        updateButtonState(false);

        String username=LocalStorage.getString("username",getActivity().getApplication());

        viewModel.createRoom(roomName,username).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer result) {
                if(result==1){
                    listener.onRoomCreate();

                    //dismiss sheet
                    dismiss();
                }else if(result<0){
                    //show fail message
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();

                    //enable button
                    updateButtonState(true);
                }
            }
        });
    }
}
