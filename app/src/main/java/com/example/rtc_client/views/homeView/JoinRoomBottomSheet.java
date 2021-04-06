package com.example.rtc_client.views.homeView;

import android.os.Bundle;
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

    public void updateButtonState(boolean isEnabled){
        if(isEnabled){
            joinButton.setAlpha(1f);
        }else{
            joinButton.setAlpha(0.3f);
        }
        joinButton.setEnabled(isEnabled);
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
    }


    public void setOnRoomJoinListener(JoinRoomBottomSheet.OnRoomCreateListener listener){
        this.listener=listener;
    }

    public void joinRoom(){
        String address=addressEditText.getText().toString().trim();
        if(address.isEmpty()){
            Toast.makeText(getActivity(), "Please enter the room address.", Toast.LENGTH_SHORT).show();
            return;
        }


        //disable button
        updateButtonState(false);

        String username= LocalStorage.getString("username",getActivity().getApplication());
        viewModel.joinRoom(address,username).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String string) {
                if(string==null)
                    return;

                if(string.equals("1")){
                    listener.onRoomJoin();

                    //dismiss
                    dismiss();
                }else if(string.equals("-1")){
                    //show fail message
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();

                    //enable button
                    updateButtonState(true);
                }else{
                    Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

                    //enable button
                    updateButtonState(true);
                }

            }
        });
    }

}
