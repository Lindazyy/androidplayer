package com.daasuu.exoplayerfilter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity{

    private Button startBtn;
    private static EditText editText;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        setUpViews();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);}

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setUpViews() {

        editText = (EditText)findViewById(R.id.videoAddress);
        startBtn = (Button) findViewById(R.id.btnStart);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                //intent.putExtra("send","http://live.360.plex-vr.com/live/index.m3u8");
                intent.putExtra("send",editText.getText().toString());
                //intent.putExtra("send",Environment.getExternalStorageDirectory().toString()+"/DCIM/test.mp4");
                intent.setClass(MainActivity.this,PlayerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}