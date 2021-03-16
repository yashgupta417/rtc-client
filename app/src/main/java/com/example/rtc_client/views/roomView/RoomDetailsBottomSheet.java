package com.example.rtc_client.views.roomView;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rtc_client.R;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.GlideApp;
import com.example.rtc_client.utils.Utils;
import com.example.rtc_client.views.homeView.JoinRoomBottomSheet;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

import static android.app.Activity.RESULT_OK;

public class RoomDetailsBottomSheet extends BottomSheetDialogFragment {
    Room room;
    public RoomDetailsBottomSheet(Room room){
        this.room=room;
    }

    RecyclerView membersRecyclerView;
    MembersAdapter membersAdapter;
    TextView addressTextView, ownerTextView, roomNameTextView, createdOnTextView;
    CircleImageView ownerImage, roomImage,roomImageBG,ownerImageBG;
    ImageView  copyAddressImage;
    RoomViewModel viewModel;
    GifImageView updateLoader;
    ImageView imageEdit, nameEdit;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.bottom_sheet_room_details, container, false);

        initUI(v);
        return  v;
    }
    private OnEditListener listener;
    public interface OnEditListener{
        void onImageEdit(String roomImage);
        void onNameEdit(String roomName);
    }


    public void setOnEditListener(OnEditListener listener){
        this.listener=listener;
    }

    public String timeStampToDate(Long time){
        Date date=new Date(time);
        DateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");

        String dateString=dateFormat.format(date);
        return dateString;
    }

    public void initUI(View v){
        //
        viewModel= ViewModelProviders.of(this).get(RoomViewModel.class);

        //
        roomImage=v.findViewById(R.id.image);
        roomImageBG=v.findViewById(R.id.image_bg);
        if(room.getImage()!=null) {
            roomImage.setPadding(0,0,0,0);
            GlideApp.with(this).load(room.getImage()).into(roomImage);
        }
        else{
            ColorDrawable colorDrawable=new ColorDrawable(Utils.getRandomColour());
            GlideApp.with(this).load(colorDrawable).into(roomImageBG);
        }

        //
        roomNameTextView=v.findViewById(R.id.name);
        roomNameTextView.setText(room.getName());

        //
        updateLoader=v.findViewById(R.id.update_loader);

        //
        createdOnTextView=v.findViewById(R.id.room_created_on);
        createdOnTextView.setText("created on: "+timeStampToDate(room.getCreatedAt()));

        //
        addressTextView=v.findViewById(R.id.address);
        addressTextView.setText(room.getAddress());

        //
        ownerTextView=v.findViewById(R.id.owner_username);
        ownerTextView.setText("@"+room.getOwner().getUsername());

        //
        ownerImage=v.findViewById(R.id.owner_image);
        ownerImageBG=v.findViewById(R.id.owner_image_bg);
        if(room.getOwner().getImage()!=null){
            ownerImage.setPadding(0,0,0,0);
            GlideApp.with(this).load(room.getOwner().getImage()).into(ownerImage);
        }else{
            ColorDrawable colorDrawable=new ColorDrawable(Utils.getRandomColour());
            GlideApp.with(this).load(colorDrawable).into(ownerImageBG);
        }


        //
        membersRecyclerView=v.findViewById(R.id.members_recycler_view);
        membersRecyclerView.setHasFixedSize(true);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        //
        membersAdapter=new MembersAdapter((ArrayList<User>) room.getMembers(),getContext());
        membersRecyclerView.setAdapter(membersAdapter);

        //
        copyAddressImage=v.findViewById(R.id.address_copy);
        copyAddressImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyAddressToClipboard();
            }
        });

        //setting up back button
        ImageView backImageView=v.findViewById(R.id.back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //
        imageEdit=v.findViewById(R.id.image_edit);
        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        nameEdit=v.findViewById(R.id.name_edit);
        nameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editName(view);
            }
        });
    }

    //copy room address
    public void copyAddressToClipboard(){
        //getting room address
        String label="room address";
        String address=room.getAddress();

        //copying address to clipboard
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, address);
        clipboard.setPrimaryClip(clip);

        //showing toast
        Toast.makeText(getActivity(), "Copied!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return Utils.makeDialogExpanded(new BottomSheetDialog(requireContext(),getTheme()));
    }


    //************Update Image Methods *************************

    public int PERMISSION_REQUEST_CODE=1;
    public int IMAGE_REQUEST_CODE=2;
    public void selectImage(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
            }else{
                selectImage();
            }
        }else{
            selectImage();
        }
    }
    public void selectImage(){
        Intent intent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            if(data.getData()!=null){
                Uri uri=data.getData();
                uploadImage(uri);
            }
        }
    }

    public void uploadImage(Uri uri){
        updateLoader.setVisibility(View.VISIBLE);

        viewModel.updateRoomImage(uri,room.getAddress()).observe(this, new Observer<Room>() {
            @Override
            public void onChanged(Room room) {
                if(room!=null && room.getImage()!=null){

                    //Updating in UI
                    setImageView(room.getImage());

                    Toast.makeText(getContext(), "Image updated successfully", Toast.LENGTH_SHORT).show();

                    //triggering listener
                    listener.onImageEdit(room.getImage());

                }else if(room!=null){
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                updateLoader.setVisibility(View.GONE);
            }
        });
    }
    public void setImageView(String uri){
        GlideApp.with(this).load(uri).into(roomImage);
    }

    //*************************************************************************



    //*********Update Room Name**************************
    public void editName(View view){
        Dialog dialog=new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_edit_name);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button submit=dialog.findViewById(R.id.dialog_submit);
        ImageView close=dialog.findViewById(R.id.dialog_close);
        EditText roomNameEditText=dialog.findViewById(R.id.dialog_room_name);

        roomNameEditText.setText(room.getName());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=roomNameEditText.getText().toString().trim();
                if(name.length()==0){
                    Toast.makeText(getContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                }else{
                    updateRoomName(name);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    public void updateRoomName(String name){
        //enabling loader
        updateLoader.setVisibility(View.VISIBLE);

        Room updatedRoom=new Room();
        updatedRoom.setName(name);
        viewModel.updateRoom(updatedRoom,room.getAddress()).observe(this, new Observer<Room>() {
            @Override
            public void onChanged(Room room) {
                if(room!=null && room.getName()!=null){

                    //updating in UI
                    roomNameTextView.setText(room.getName());

                    Toast.makeText(getContext(), "Update Successfull", Toast.LENGTH_SHORT).show();

                    //triggering listener
                    listener.onNameEdit(room.getName());
                }else{
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

                updateLoader.setVisibility(View.GONE);
            }
        });
    }

    //*****************************************************
}
