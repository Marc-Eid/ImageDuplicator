package com.example.imageduplicator;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.annotation.SuppressLint;

import android.content.Context;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


public class DirectoryObserver extends FileObserver {

    String absolutePath;
    ArrayList<String> destinationsList;

    @SuppressLint("NewApi")
    public DirectoryObserver(File file) {
        super(file, FileObserver.MOVED_TO);
        absolutePath = file.getPath();
    }

    @SuppressLint("NewApi")
    @Override public void onEvent(int event, String path) { //the path here is just the new file name

        if(path != null) {
            //some logs
            Log.e("FileObserver", event + "");
            Log.e("FileObserver: ", "File Created");
            Log.e("FileObserver: ", "File name is: " + path);


            //get the created file
            File fileCreated = new File(absolutePath + "/" + path);

            //if default switch is selected send new copies to the app folder
            if(destinationsList.contains("default")){
            //get the default destination folder
            File destinationFolder = new File(Environment.getExternalStorageDirectory() + "/Android/media/" + BuildConfig.APPLICATION_ID);

            if (!destinationFolder.isDirectory() && destinationFolder.mkdir()) { //check if directory exists, if not create it
                Log.w("File Observer", "Directory Created");
            } else {
                Log.w("File Observer", "Directory Exists");
            }

            //create new File
            File duplicateImage = new File(destinationFolder + "/" + fileCreated.getName());

            try {

                if (duplicateImage.createNewFile()) ;
                Log.e("FileObserver", "File Created");
                copyFile(fileCreated, duplicateImage);

            } catch (Exception e) {
                Log.e("FileObserver", "error in copying the file");
                Log.e("FileObserver", e.toString());
            }
        }



        }
        else{
            Log.e("FileObserver: ","path is null");
        }
    }


    //copy file to destination folder
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source;
        FileChannel destination;

        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();

        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }

    }


    public void setDestinationsList(ArrayList<String> destinations){
        this.destinationsList = destinations;
    }

    public ArrayList<String> getDestinationsList(){
        return destinationsList;
    }


    public void close(){
        super.finalize();
        Log.w("Test", "Observer Closed");
    }
}

