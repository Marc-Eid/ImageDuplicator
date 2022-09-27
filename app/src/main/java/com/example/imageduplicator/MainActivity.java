package com.example.imageduplicator;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    String path = Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";
    DirectoryObserver directoryFileObserver = new DirectoryObserver(new File(path));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch s = (Switch) findViewById(R.id.switch2);

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (checkPermission()){
                        Log.w("TEST", "switch is on");



                       // print file names to make sure that the path is correctly accessed
//                        File directory = new File(path);
//                        File[] files = directory.listFiles();
//                        for ( File file  : files   ) {
//                            Log.d("files", file.getName() + file.getAbsolutePath());
//                        }



                        //listen for files from folder x
                        directoryFileObserver.startWatching();

                        //copy file to folder y
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