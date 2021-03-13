package com.example.rtc_client.views.profileView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtc_client.R;
import com.example.rtc_client.data.models.User;
import com.example.rtc_client.utils.GlideApp;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {

    ProfileViewModel viewModel;
    EditText nameEditText;
    CircleImageView imageView;
    Uri uri;
    User userUpdates;
    Button updateButton;

    boolean isEdited=false;

    private int PERMISSION_REQUEST_CODE=2;
    private int IMAGE_REQUEST_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        init();
    }

    public void init(){

        //init view model
        viewModel= ViewModelProviders.of(this).get(ProfileViewModel.class);

        //init views
        nameEditText=findViewById(R.id.nameEditText);
        imageView=findViewById(R.id.image);
        updateButton=findViewById(R.id.update);


        setUpdateButtonState(false);

        String name=getIntent().getStringExtra("name");
        String image=getIntent().getStringExtra("image");

        nameEditText.setText(name);
        if(image!=null)
            GlideApp.with(this).load(image).into(imageView);

        //init userUpdates
        userUpdates=new User();

        setUpEditTextListener();
    }

    public void finishActivity(View view){
        finish();
    }
    public void setUpEditTextListener(){
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isEdited=true;
                String name=charSequence.toString().trim();
                userUpdates.setName(name);

                setUpdateButtonState(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void selectImage(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
            }else{
                selectImage();
            }
        }else{
            selectImage();
        }
    }
    public void selectImage(){
        Intent intent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,IMAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            if(data.getData()!=null){
                uri=data.getData();
                setImageView();
            }
        }
    }

    public void setImageView(){
        isEdited=true;
        setUpdateButtonState(true);
        GlideApp.with(this).load(uri).into(imageView);
    }

    public void setUpdateButtonState(Boolean enabled){
        if(enabled){
            updateButton.setAlpha(1f);
            updateButton.setEnabled(true);
        }else{
            updateButton.setEnabled(false);
            updateButton.setAlpha(0.2f);
        }
    }

    public void update(View view){

        //checking name not empty
        if(nameEditText.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        //disabling update button
        setUpdateButtonState(false);


        viewModel.updateUser(uri,userUpdates).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer result) {
                if(result==1){
                    Toast.makeText(UpdateProfileActivity.this, "update success", Toast.LENGTH_SHORT).show();
                    finish();
                }else if(result==-1){
                    Toast.makeText(UpdateProfileActivity.this, "update failed", Toast.LENGTH_SHORT).show();

                    //enabling update button back
                    setUpdateButtonState(true);
                }
            }
        });
    }
}