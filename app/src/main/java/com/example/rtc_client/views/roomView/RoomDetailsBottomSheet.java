package com.example.rtc_client.views.roomView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rtc_client.R;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.GlideApp;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomDetailsBottomSheet extends BottomSheetDialogFragment {
    Room room;
    public RoomDetailsBottomSheet(Room room){
        this.room=room;
    }

    RecyclerView membersRecyclerView;
    MembersAdapter membersAdapter;
    TextView addressTextView, ownerTextView, roomNameTextView;
    CircleImageView ownerImage;
    ImageView roomImage, copyAddressImage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.bottom_sheet_room_details, container, false);

        initUI(v);
        return  v;
    }

    public void initUI(View v){

        //
        roomImage=v.findViewById(R.id.image);
        GlideApp.with(this).load(getResources().getDrawable(R.drawable.room_image_placeholder)).into(roomImage);

        //
        roomNameTextView=v.findViewById(R.id.name);
        roomNameTextView.setText(room.getName());

        //
        addressTextView=v.findViewById(R.id.address);
        addressTextView.setText(room.getAddress());

        //
        ownerTextView=v.findViewById(R.id.owner_username);
        ownerTextView.setText("@"+room.getOwner().getUsername());

        //
        ownerImage=v.findViewById(R.id.owner_image);
        GlideApp.with(this).load(getResources().getDrawable(R.drawable.room_image_placeholder)).into(ownerImage);

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

}
