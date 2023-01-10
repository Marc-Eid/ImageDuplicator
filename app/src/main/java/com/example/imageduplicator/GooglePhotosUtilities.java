package com.example.imageduplicator;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;

public class GooglePhotosUtilities {


    public static GoogleTokenResponse getGoogleTokenResponse(GoogleSignInAccount account) {

        GoogleTokenResponse tokenResponse = null;

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    GoogleTokenResponse tokenResponse =
                            new GoogleAuthorizationCodeTokenRequest(
                                    new NetHttpTransport(),
                                    JacksonFactory.getDefaultInstance(),
                                    "https://oauth2.googleapis.com/token",
                                    BuildConfig.CLIENT_ID,
                                    BuildConfig.CLIENT_SECRET,
                                    account.getServerAuthCode(),
                                    "")
                                    .execute();

                    Log.e("Access Token", tokenResponse.getAccessToken());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        networkThread.start();
        return tokenResponse;
    }



}
