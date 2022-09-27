package com.example.imageduplicator;

import android.annotation.SuppressLint;
import android.os.FileObserver;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

public class DirectoryObserver extends FileObserver {

    String aboslutePath;


    @SuppressLint("NewApi")
    public DirectoryObserver(File file) {
        super(file, FileObserver.MOVED_TO);
        aboslutePath = file.getPath();
    }

    @SuppressLint("NewApi")
    @Override public void onEvent(int event, String path) {

        if(path != null)
        {

            Log.e("FileObserver" , event + "");
            Log.e("FileObserver: ","File Created");

            //to-do: capture last file along some files within +/- 30s


        }
        else{
            Log.e("FileObserver: ","path is null");
        }
    }

    public void close(){
        super.finalize();
        Log.w("Test", "Observer Closed");
    }
}

