package com.example.imageduplicator;

import android.annotation.SuppressLint;

import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;


public class DirectoryObserver extends FileObserver {

    String absolutePath;
    ArrayList<String> destinationsList;
    String accessToken;

    @SuppressLint("NewApi")
    public DirectoryObserver(File file) {
        super(file, FileObserver.MOVED_TO);
        absolutePath = file.getPath();
    }

    @SuppressLint("NewApi")
    @Override
    public void onEvent(int event, String path) { //the path here is just the new file name

        if (path != null) {
            //some logs
            Log.e("FileObserver", event + "");
            Log.e("FileObserver: ", "File Created");
            Log.e("FileObserver: ", "File name is: " + path);


            //get the created file
            File fileCreated = new File(absolutePath + "/" + path);
            File destinationFolder = new File(Environment.getExternalStorageDirectory() + "/Android/media/" + BuildConfig.APPLICATION_ID);

            //if default switch is selected send new copies to the app folder
            if (destinationsList.contains("default")) {
                //get the default destination folder


                if (!destinationFolder.isDirectory() && destinationFolder.mkdir()) { //check if directory exists, if not create it
                    Log.w("File Observer", "Directory Created");
                } else {
                    Log.w("File Observer", "Directory Exists");
                }

                //create new File
                File duplicateImage = new File(destinationFolder + "/" + fileCreated.getName());

                try {

                    if (duplicateImage.createNewFile())
                        Log.e("FileObserver", "File Created");
                    copyFile(fileCreated, duplicateImage);

                } catch (Exception e) {
                    Log.e("FileObserver", "error in copying the file");
                    Log.e("FileObserver", e.toString());
                }
            }


            if(destinationsList.contains("google photos")){
                Log.e("AccessToken Dir", accessToken + "");

                    GooglePhotosUtilities.postToGooglePhotos(accessToken, fileCreated);

            }


        } else {
            Log.e("FileObserver: ", "path is null");
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


    public void setDestinationsList(ArrayList<String> destinations) {
        this.destinationsList = destinations;
    }

    public ArrayList<String> getDestinationsList() {
        return destinationsList;
    }

    public void setAccessToken(String token){
        accessToken = token;
    }


    public void close() {
        super.finalize();
        Log.w("Test", "Observer Closed");
    }
}

