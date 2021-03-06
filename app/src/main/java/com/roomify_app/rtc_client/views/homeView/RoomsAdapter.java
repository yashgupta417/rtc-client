package com.roomify_app.rtc_client.views.homeView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roomify_app.rtc_client.R;
import com.roomify_app.rtc_client.data.models.Room;
import com.roomify_app.rtc_client.utils.GlideApp;
import com.roomify_app.rtc_client.utils.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {
    Context context;
    ArrayList<Room> rooms;
    ArrayList<Integer> colours;

    public RoomsAdapter(ArrayList<Room> rooms,Context context){

        this.context=context;
        this.rooms=rooms;


    }

    public void setColours(){
        colours=new ArrayList<>();
        for(int i=0;i<this.rooms.size();i++){
            colours.add(Utils.getRandomColour());
        }
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


        if(room.getImage()!=null) {
            //removing padding
            holder.roomImage.setPadding(0,0,0,0);

            GlideApp.with(context).load(room.getImage()).into(holder.roomImage);
        }
        else{
            //setting background
            ColorDrawable colorDrawable=new ColorDrawable(colours.get(position));
            GlideApp.with(context).load(colorDrawable).into(holder.roomImageBG);
        }
        //setting gradient background
//        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,colours.get(position));
//        gradientDrawable.setCornerRadius(5f);
//        holder.parent.setBackground(gradientDrawable);
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
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }
    public static class RoomViewHolder extends RecyclerView.ViewHolder{
        CircleImageView roomImage,roomImageBG;
        TextView roomName, roomMembersCount;
        RelativeLayout parent;
        public RoomViewHolder(@NonNull View itemView,final onItemClickListener listener) {
            super(itemView);

            roomImage=itemView.findViewById(R.id.room_image);
            roomName=itemView.findViewById(R.id.room_name);
            roomMembersCount=itemView.findViewById(R.id.member_count);
            roomImageBG=itemView.findViewById(R.id.room_image_bg);
            //parent=itemView.findViewById(R.id.parent);

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



        }
    }


}
