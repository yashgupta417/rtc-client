package com.example.rtc_client.views.roomView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rtc_client.R;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.GlideApp;
import com.example.rtc_client.utils.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {
    ArrayList<User> members;
    Context context;

    public MembersAdapter(ArrayList<User> members, Context context) {
        this.members = members;
        this.context = context;
    }


    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item,parent,false);
        MemberViewHolder myViewHolder=new MemberViewHolder(v,listener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User user=members.get(position);
        holder.username.setText("@"+user.getUsername());

        if(user.getImage()!=null){
            holder.image.setPadding(0,0,0,0);
            GlideApp.with(context).load(user.getImage()).into(holder.image);
        }else{
            ColorDrawable colorDrawable=new ColorDrawable(Utils.getRandomColour());
            GlideApp.with(context).load(colorDrawable).into(holder.imageBG);
        }
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
        return members.size();
    }

    private onItemClickListener listener;
    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }
    public static class MemberViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image,imageBG;
        TextView username;
        public MemberViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            image=itemView.findViewById(R.id.member_image);
            imageBG=itemView.findViewById(R.id.member_image_bg);
            username=itemView.findViewById(R.id.member_name);

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
