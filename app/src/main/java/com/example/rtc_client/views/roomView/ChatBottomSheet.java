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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rtc_client.R;
import com.example.rtc_client.data.models.Message;
import com.example.rtc_client.data.models.Room;
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

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatBottomSheet extends BottomSheetDialogFragment {
    Room room;

    RecyclerView messageRecyclerView;
    MessageAdapter messageAdapter;
    EditText textEditText;
    ImageView sendImageView,backImageView;
    TextView headerTextView;

    public  ChatBottomSheet(Room room){
        this.room=room;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.bottom_sheet_chat, container, false);

        initUI(v);
        initSocket();
        return  v;
    }

    public void initUI(View v){
        messageRecyclerView=v.findViewById(R.id.message_recycler_view);
        messageRecyclerView.setHasFixedSize(true);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        textEditText=v.findViewById(R.id.text);
        sendImageView=v.findViewById(R.id.send);
        headerTextView=v.findViewById(R.id.chat_header);
        backImageView=v.findViewById(R.id.back);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        headerTextView.setText(room.getName());

        messageAdapter=new MessageAdapter(new ArrayList<>(),getContext());
        messageRecyclerView.setAdapter(messageAdapter);

        addMessageWatcher();
        addSendClickListener();
    }

    private Socket socket;
    private static String URI="https://agile-savannah-88050.herokuapp.com/";

    public void initSocket(){
        try {
            socket=IO.socket(URI);
        }catch (URISyntaxException e){}


        socket.on("receiveMessage",onMessageReceived);

        socket.connect();

        socket.emit("joinRoom", room.getAddress(), new Ack() {
            @Override
            public void call(Object... args) {
                JSONArray jsonArray= (JSONArray) args[0];
                Gson gson=new Gson();
                Type type = new TypeToken<List<Message>>(){}.getType();
                ArrayList<Message> oldMessages=(ArrayList<Message>) gson.fromJson(jsonArray.toString(),type);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       displayOldMessages(oldMessages);
                    }
                });
            }
        });
    }

    public void displayOldMessages(ArrayList<Message> messages){
        messageAdapter.messages=messages;
        messageAdapter.notifyDataSetChanged();
    }


    public Emitter.Listener onMessageReceived=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };

    public void addSendClickListener(){
        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        String message=textEditText.getText().toString().trim();
        String username= LocalStorage.getString("username",getActivity().getApplication());
        String address=room.getAddress();

        textEditText.setText("");

        //sending message
        socket.emit("sendMessage", message, username, address, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject response = (JSONObject) args[0];

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(response.getString("status").equals("sent"))
                                Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e){ }
                    }
                });

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        socket.disconnect();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return Utils.makeDialogExpanded(new BottomSheetDialog(requireContext(),getTheme()));
    }


}
