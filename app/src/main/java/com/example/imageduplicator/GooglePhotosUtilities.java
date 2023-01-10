package com.example.imageduplicator;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
            @Override
            public void run() {
                try {

                    URL url = new URL("https://photoslibrary.googleapis.com/v1/mediaItems:batchCreate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                  conn.addRequestProperty("client_id", BuildConfig.CLIENT_ID);
//                  conn.addRequestProperty("client_secret", BuildConfig.CLIENT_SECRET);
                    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    String payload = "{   \"newMediaItems\": [  {\"description\": duplicate image, \"simpleMediaItem\": {  {\"uploadToken\" : " + photo + ", \"fileName\": DuplicateImage }   }     }  ]   }";


                    // Send the POST data
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(payload);
                    writer.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    writer.close();
                    reader.close();

                    // Do something with the response
                    // For example, you can print it to the log:
                    Log.d("HTTP POST", response.toString());



                        // Create a NewMediaItem with the following components:
                        // - uploadToken obtained from the previous upload request
                        // - filename that will be shown to the user in Google Photos
                        // - description that will be shown to the user in Google Photos
//                        NewMediaItem newMediaItem = NewMediaItemFactory
//                                .createNewMediaItem(uploadToken, "duplicateImage", "itemDescription");
//                        List<NewMediaItem> newItems = Arrays.asList(newMediaItem);
//
//
//                    PhotosLibrarySettings settings =
//                            PhotosLibrarySettings.newBuilder()
//                                    .setCredentialsProvider(
//                                            FixedCredentialsProvider.create(/* Add credentials here. */))
//                                    .build();
//
//                    try (PhotosLibraryClient photosLibraryClient =
//                                 PhotosLibraryClient.initialize(settings)) {
//
//                        BatchCreateMediaItemsResponse response = photosLibraryClient.batchCreateMediaItems(newItems);
//                        for (NewMediaItemResult itemsResponse : response.getNewMediaItemResultsList()) {
//                            Status status = itemsResponse.getStatus();
//                            if (status.getCode() == Code.OK_VALUE) {
//                                // The item is successfully created in the user's library
//                                MediaItem createdItem = itemsResponse.getMediaItem();
//                            } else {
//                                // The item could not be created. Check the status and try again
//                            }
//                        }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("POST", e.toString());
                }
            }
        });

        networkThread.start();
    }


}
