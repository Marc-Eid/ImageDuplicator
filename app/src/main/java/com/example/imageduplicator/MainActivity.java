package com.example.imageduplicator;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;

import java.io.File;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch s = findViewById(R.id.switch2);

        String path = Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";
        DirectoryObserver directoryFileObserver = new DirectoryObserver(new File(path));

        //listen for switch toggling
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //if switch is checked
                    if (checkPermission()){ //if perission is granted listen to folder
                        Log.w("TEST", "switch is on");
                        directoryFileObserver.startWatching();
                    }
                    else{
                        requestPermission();
                    }
                } else {
                    Log.w("TEST", "switch is off");
                    directoryFileObserver.stopWatching();
                }
            }
        });


        //make a service that runs periodically over the directory where duplicates are stored and then delete those older than 1 day






//        MaterialButton destinationStorage = findViewById(R.id.dest);
//
//        destinationStorage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (checkPermission()){
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("file/*");
//                    startActivity(intent);
//
//                }else{
//                    requestPermission();
//                }
//            }
//        });




    }

    private Boolean checkPermission(){
       return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            Toast.makeText(MainActivity.this, "Storage permission is required, allow from settings", Toast.LENGTH_SHORT).show();
        }else
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }
    }



}