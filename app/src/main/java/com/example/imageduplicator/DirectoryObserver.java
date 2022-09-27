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
    @Override public void onEvent(int event, String path) { //the path here is just the file name

        if(path != null)
        {

            Log.e("FileObserver" , event + "");
            Log.e("FileObserver: ","File Created");
            Log.e("FileObserver: ", "File name is: " + path);

            File fileCreated = new File(path);
            Log.e("File Observer", fileCreated.getName());

            //TODO: Create a duplicate of the fileCreated and store it in a new folder

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

