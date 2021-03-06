package com.example.rtc_client.views.homeView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.rtc_client.R;
import com.example.rtc_client.utils.LocalStorage;
import com.example.rtc_client.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class JoinRoomBottomSheet extends BottomSheetDialogFragment {

    EditText addressEditText;
    Button joinButton;
    HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.bottom_sheet_join_room, container, false);

        addressEditText=v.findViewById(R.id.room_address);
        joinButton=v.findViewById(R.id.join_room_button);
        viewModel= ViewModelProviders.of(this).get(HomeViewModel.class);
        activateButton();

        return v;
    }

    public void activateButton(){
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinRoom();
            }
        });
    }

    private JoinRoomBottomSheet.OnRoomCreateListener listener;
    public interface OnRoomCreateListener{
        void onRoomJoin();
        void onFailure();
    }


    public void setOnRoomJoinListener(JoinRoomBottomSheet.OnRoomCreateListener listener){
        this.listener=listener;
    }

    public void joinRoom(){
        String address=addressEditText.getText().toString().trim();
        if(address.isEmpty()){
            Utils.showToast("Please enter the room address.",getActivity());
            return;
        }
        String username= LocalStorage.getString("username",getActivity().getApplication());
        viewModel.joinRoom(address,username).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer==1){
                    listener.onRoomJoin();
                }else if(integer==-1){
                    listener.onFailure();
                }
                dismiss();
            }
        });
    }

}
