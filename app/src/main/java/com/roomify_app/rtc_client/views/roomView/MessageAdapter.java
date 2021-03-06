package com.roomify_app.rtc_client.views.roomView;

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
import com.roomify_app.rtc_client.data.models.Message;
import com.roomify_app.rtc_client.utils.GlideApp;
import com.roomify_app.rtc_client.utils.Utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    public ArrayList<Message> messages;
    public Context context;
    public Map<String,Integer> colourMap;

    public MessageAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
        colourMap=new HashMap<>();
    }


    private onItemClickListener listener;
    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public String timeStampToDate(long time){
        //Log.i("chat",Long.toString(time));

        Date date=new Date(time);
        DateFormat dateFormat=new SimpleDateFormat("hh:mm a");

        String dateString=dateFormat.format(date);

        //Log.i("chat",dateString);
        return dateString;
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image,imageBG;
        TextView sender, text, time;
        RelativeLayout detailsParent;
        public MessageViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            imageBG=itemView.findViewById(R.id.image_bg);
            text=itemView.findViewById(R.id.text);
            sender=itemView.findViewById(R.id.sender);
            time=itemView.findViewById(R.id.time);
            detailsParent=itemView.findViewById(R.id.details_parent);

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

        if(message.isSent()){
            holder.detailsParent.setAlpha(1f);
        }else{
            holder.detailsParent.setAlpha(0.4f);
        }

        //assigning a colour to username if null
        String key=message.getSender().getUsername();
        if(colourMap.get(key)==null)
            colourMap.put(key,Utils.getRandomColour());

        holder.sender.setText("@"+message.getSender().getUsername());
        holder.sender.setTextColor(colourMap.get(key));

        holder.text.setText(message.getText());

        holder.time.setText(timeStampToDate(message.getTimestamp()));


        if(message.getSender().getImage()!=null) {
            holder.image.setPadding(0,0,0,0);
            GlideApp.with(context).load(message.getSender().getImage()).into(holder.image);
        }else{
            ColorDrawable colorDrawable=new ColorDrawable(colourMap.get(key));
            GlideApp.with(context).load(colorDrawable).into(holder.imageBG);
        }

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
