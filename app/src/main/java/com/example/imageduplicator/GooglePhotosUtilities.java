package com.example.imageduplicator;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class GooglePhotosUtilities {

    static GoogleTokenResponse tokenResponse;

    public static GoogleTokenResponse getGoogleTokenResponse(GoogleSignInAccount account) {



        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                     tokenResponse =
                            new GoogleAuthorizationCodeTokenRequest(
                                    new NetHttpTransport(),
                                    JacksonFactory.getDefaultInstance(),
                                    "https://oauth2.googleapis.com/token",
                                    BuildConfig.CLIENT_ID,
                                    BuildConfig.CLIENT_SECRET,
                                    account.getServerAuthCode(),
                                    "")
                                    .execute();

                    Log.e("Access Token G", tokenResponse.getAccessToken());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        networkThread.start();
        return tokenResponse;
    }

    public static void postToGooglePhotos(String accessToken, File photo) {


        Thread networkThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {

                    //step 1; getting the token


                    // Read the image file into a byte array
                    byte[] imageBytes = Files.readAllBytes(photo.toPath());

                    // Create a new upload session
                    URL uploadUrl = new URL("https://photoslibrary.googleapis.com/v1/uploads");
                    HttpURLConnection uploadConnection = (HttpURLConnection) uploadUrl.openConnection();
                    uploadConnection.setRequestMethod("POST");
                    uploadConnection.setDoOutput(true);
                    uploadConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                    uploadConnection.setRequestProperty("Content-type", "application/octet-stream");
                    uploadConnection.setRequestProperty("X-Goog-Upload-Protocol", "raw");
                    OutputStream outStream = uploadConnection.getOutputStream();
                    outStream.write(imageBytes);
                    outStream.flush();
                    outStream.close();

                    // Get the upload token from the response
                    BufferedReader uploadResponse = new BufferedReader(new InputStreamReader(uploadConnection.getInputStream()));
                    String uploadToken = uploadResponse.readLine();
                    uploadResponse.close();



                    //step 2: send image to google photos, using the upload token
                    URL url = new URL("https://photoslibrary.googleapis.com/v1/mediaItems:batchCreate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                    conn.setRequestProperty("Content-Type", "application/json");


                    String payload = "{\"newMediaItems\": [{\"description\":\"This is a duplicate example image\",\"simpleMediaItem\":{\"fileName\":\"duplicate.jpg\",\"uploadToken\": \"" + uploadToken + "\"}}]}";
                    Log.e("Payload", payload);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(payload);
                    writer.flush();


                    InputStream response = conn.getInputStream();
                    InputStreamReader isReader = new InputStreamReader(response);
                    BufferedReader reader = new BufferedReader(isReader);
                    StringBuffer sb = new StringBuffer();
                    String str;
                    while((str = reader.readLine())!= null) {
                        sb.append(str);
                    }

                    // Do something with the response
                    // For example, you can print it to the log:
                    Log.e("HTTP POST", str + " ");




                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("POST", e.toString());
                }
            }
        });

        networkThread.start();
    }


}
