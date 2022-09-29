package com.example.imageduplicator;

import android.annotation.SuppressLint;
import android.os.Environment;
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


            File fileCreated = new File(aboslutePath + "/" + path); //get the created file

            //verifying that the file was created.
            Log.e("File Observer", fileCreated.getName());
            Log.e("File Observer", " "+ fileCreated.getParent());
            Log.e("File Observer", "   " + fileCreated.getAbsolutePath());


            //TODO: Create a duplicate of the fileCreated and store it in a new folder

            //check if directory exists
            File destinationFolder = new File(Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images/Duplicates");
            if(!destinationFolder.isDirectory() && destinationFolder.mkdir() ){
                Log.w("File Observer", "Directory Created");
            }
            else{
                Log.w("File Observer", "Directory Exists");
            }






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

