package com.roomify_app.rtc_client.views.roomView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.roomify_app.rtc_client.R;
import com.roomify_app.rtc_client.data.models.AgoraUser;
import com.roomify_app.rtc_client.utils.GlideApp;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class CallParticipantAdapter extends RecyclerView.Adapter<CallParticipantAdapter.CallParticipantViewHolder> {
    RtcEngine rtcEngine;
    Context context;
    ArrayList<AgoraUser> participants;

    public CallParticipantAdapter(RtcEngine rtcEngine, Context context){
        this.participants=new ArrayList<>();
        this.rtcEngine = rtcEngine;
        this.context = context;
    }

    @NonNull
    @Override
    public CallParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.call_participant_item,parent,false);
        CallParticipantViewHolder myViewHolder=new CallParticipantViewHolder(v,listener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CallParticipantViewHolder holder, int position) {
        AgoraUser participant=participants.get(position);

        //displaying username
        holder.username.setText(participant.getUser().getUsername());

        //displaying image
        if(participant.getUser().getImage()!=null)
            GlideApp.with(context).load(participant.getUser().getImage()).into(holder.videoFallback);

        //setting audio mic
        if(participant.getAudioEnabled()){
            holder.mic.setImageResource(R.drawable.ic_baseline_mic_50);
        }else{
            holder.mic.setImageResource(R.drawable.ic_baseline_mic_off_50);
        }

        if(participant.getVideoEnabled()) {
            Log.i("msggg","video enabled");

            //setting video
            SurfaceView surfaceView = RtcEngine.CreateRendererView(context);

            holder.videoParent.setVisibility(View.VISIBLE);
            holder.videoParent.addView(surfaceView);

            VideoCanvas videoCanvas=new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, participant.getUid());

            if(participant.getMe())
                rtcEngine.setupLocalVideo(videoCanvas);
            else
                rtcEngine.setupRemoteVideo(videoCanvas);

        }else{
            Log.i("msggg","video disabled");
            holder.videoParent.setVisibility(View.GONE);
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
        return participants.size();
    }

    private onItemClickListener listener;
    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }
    public static class CallParticipantViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout parent;
        CardView videoParent;
        TextView username;
        ImageView mic;
        CircleImageView videoFallback;
        public CallParticipantViewHolder(@NonNull View itemView,final onItemClickListener listener) {
            super(itemView);
            parent=itemView.findViewById(R.id.parent);
            videoParent=itemView.findViewById(R.id.video_parent);
            username=itemView.findViewById(R.id.username);
            mic=itemView.findViewById(R.id.mic);
            videoFallback=itemView.findViewById(R.id.videoFallback);

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

    //will return index of participant with a certain uid
    public int findParticipantByUid(int uid){
        for(int i=0;i<participants.size();i++){
            if(participants.get(i).getUid()==uid)
                return i;
        }
        return -1;
    }

    public void removeParticipant(int uid){
        int index=findParticipantByUid(uid);
        if(index==-1) return;
        participants.remove(index);

        notifyItemRemoved(index);
    }

    public void addParticipant(AgoraUser agoraUser){
        participants.add(agoraUser);

        notifyItemInserted(participants.size()-1);
    }

    public void onUserAudioStateChanged(int uid,boolean isEnabled){
        int index=findParticipantByUid(uid);
        if(index==-1) return;

        participants.get(index).setAudioEnabled(isEnabled);
        notifyItemChanged(index);
    }

    public void onUserVideoStateChanged(int uid,boolean isEnabled){
        int index=findParticipantByUid(uid);
        if(index==-1) return;

        participants.get(index).setVideoEnabled(isEnabled);
        notifyItemChanged(index);
    }

}
