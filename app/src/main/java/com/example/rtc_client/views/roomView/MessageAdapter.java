package com.example.rtc_client.views.roomView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rtc_client.R;
import com.example.rtc_client.data.models.Message;
import com.example.rtc_client.utils.GlideApp;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    public ArrayList<Message> messages;
    public Context context;

    public MessageAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }


    private onItemClickListener listener;
    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public String timeStampToDate(long time){
        Date date=new Date(time);
        DateFormat dateFormat=new SimpleDateFormat("hh:mm a");

        String dateString=dateFormat.format(date);
        return dateString;
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView sender, text, time;
        public MessageViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            text=itemView.findViewById(R.id.text);
            sender=itemView.findViewById(R.id.sender);
            time=itemView.findViewById(R.id.time);

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

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);
        MessageViewHolder myViewHolder=new MessageViewHolder(v,listener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message=messages.get(position);
        holder.sender.setText("@"+message.getSender().getUsername());
        holder.text.setText(message.getText());
        holder.time.setText(timeStampToDate(message.getTimestamp()));

        if(message.getSender().getImage()!=null)
            GlideApp.with(context).load(message.getSender().getImage()).into(holder.image);
    }

    public int getItemViewType(int position) {
        return  position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }
}
