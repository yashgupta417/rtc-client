package com.example.rtc_client.views.roomView;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rtc_client.R;
import com.example.rtc_client.data.models.Message;
import com.example.rtc_client.data.models.Room;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.LocalStorage;
import com.example.rtc_client.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

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

        updateAdapter(oldMessages);

        addMessageWatcher();
        addSendClickListener();
    }

    public void updateNoChatUI(Integer count){
        if(count==0)
            noChatParent.setVisibility(View.VISIBLE);
        else
            noChatParent.setVisibility(View.GONE);
    }

    public void updateAdapter(ArrayList<Message> messages){

        Log.i("chat","messages in sheet"+Integer.toString(messages.size()));

        //handling no chats case
        updateNoChatUI(messages.size());

        messageAdapter.messages=messages;
        messageAdapter.notifyItemInserted(messages.size()-1);
        messageRecyclerView.scrollToPosition(messages.size()-1);
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
        long timeStamp= System.currentTimeMillis() / 1000L;
        Message message=new Message(text,user,timeStamp);

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
