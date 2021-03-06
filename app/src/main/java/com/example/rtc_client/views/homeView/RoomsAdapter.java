package com.example.rtc_client.views.homeView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rtc_client.R;
import com.example.rtc_client.data.models.Room;

import java.util.ArrayList;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {
    Context context;
    ArrayList<Room> rooms;

    public RoomsAdapter(ArrayList<Room> rooms,Context context){
        this.context=context;
        this.rooms=rooms;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item,parent,false);
        RoomViewHolder myViewHolder=new RoomViewHolder(v,listener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room=rooms.get(position);

        holder.roomName.setText(room.getName());
        holder.roomMembersCount.setText(Integer.toString(room.getMembersCount())+" members");
    }

    @Override
    public int getItemViewType(int position) {
        return  position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return rooms.size();
    }

    private onItemClickListener listener;
    public interface onItemClickListener{
        void onItemClick(int position);
        void onEnterClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }
    public static class RoomViewHolder extends RecyclerView.ViewHolder{
        ImageView roomImage;
        TextView roomName, roomMembersCount;
        TextView enter;
        public RoomViewHolder(@NonNull View itemView,final onItemClickListener listener) {
            super(itemView);

            roomImage=itemView.findViewById(R.id.room_image);
            roomName=itemView.findViewById(R.id.room_name);
            roomMembersCount=itemView.findViewById(R.id.member_count);
            enter=itemView.findViewById(R.id.enter);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onEnterClick(position);
                        }
                    }
                }
            });


        }
    }


}
