package com.roomify_app.rtc_client.views.roomView;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roomify_app.rtc_client.R;
import com.roomify_app.rtc_client.data.models.Message;
import com.roomify_app.rtc_client.data.models.Room;
import com.roomify_app.rtc_client.data.models.User;
import com.roomify_app.rtc_client.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.UUID;

public class ChatBottomSheet extends BottomSheetDialogFragment {
    Room room;
    User user;
    ChatSocket socket;

    RecyclerView messageRecyclerView;
    MessageAdapter messageAdapter;
    EditText textEditText;
    ImageView sendImageView,backImageView;
    TextView headerTextView;
    ArrayList<Message> oldMessages;
    RelativeLayout noChatParent;


    public  ChatBottomSheet(Room room,User user, ChatSocket socket,ArrayList<Message> oldMessages){
        this.room=room;
        this.user=user;
        this.socket=socket;
        this.oldMessages=oldMessages;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.bottom_sheet_chat, container, false);


        initUI(v);

        return  v;
    }

    public void initUI(View v){
        Log.i("chat","init sheet");

        messageRecyclerView=v.findViewById(R.id.message_recycler_view);
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        textEditText=v.findViewById(R.id.text);
        sendImageView=v.findViewById(R.id.send);
        headerTextView=v.findViewById(R.id.chat_header);
        backImageView=v.findViewById(R.id.back);
        noChatParent=v.findViewById(R.id.no_chats_parent);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        headerTextView.setText(room.getName());

        messageAdapter=new MessageAdapter(new ArrayList<>(),getContext());
        messageRecyclerView.setAdapter(messageAdapter);

        updateMessages(oldMessages);

        addMessageWatcher();
        addSendClickListener();
    }

    public void updateNoChatUI(Integer count){
        if(count==0)
            noChatParent.setVisibility(View.VISIBLE);
        else
            noChatParent.setVisibility(View.GONE);
    }

    public void updateMessages(ArrayList<Message> messages){

        Log.i("chat","messages in sheet"+Integer.toString(messages.size()));

        //handling no chats case
        updateNoChatUI(messages.size());

        messageAdapter.messages=messages;
        messageAdapter.notifyItemInserted(messages.size()-1);
        messageRecyclerView.scrollToPosition(messages.size()-1);
    }

    public void updateMessageStatus(ArrayList<Message> messages,int pos){
        //handling no chats case
        updateNoChatUI(messages.size());

        messageAdapter.messages=messages;

        messageAdapter.notifyItemChanged(pos);
    }


    public void addSendClickListener(){
        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("chat","send clicked");
                sendMessage();
            }
        });
    }

    public void addMessageWatcher(){
        textEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String message=editable.toString().trim();

                //if empty then make send disabled, else make is enabled
                updateSendState(!message.isEmpty());
            }
        });
    }

    public void updateSendState(boolean sendAllowed){

        sendImageView.setEnabled(sendAllowed);

        if(sendAllowed)
            sendImageView.setAlpha(1f);
        else
            sendImageView.setAlpha(0.3f);
    }

    public void sendMessage(){
        Log.i("chat","sending message");
        //wrapping message
        String text=textEditText.getText().toString().trim();
        long timeStamp= System.currentTimeMillis();

        String uniqueLocalId = UUID.randomUUID().toString();

        Message message=new Message(uniqueLocalId, text,user,timeStamp,false);
        //Log.i("chat",Long.toString(timeStamp));

        //updating UI
        textEditText.setText("");

        //sending message
        socket.sendMessage(message);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return Utils.makeDialogExpanded(new BottomSheetDialog(requireContext(),getTheme()));

    }


}
