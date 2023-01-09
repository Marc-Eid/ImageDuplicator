package com.example.imageduplicator;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> destinationsList = new ArrayList<>();
    final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Switch activateSwitch = findViewById(R.id.switch2);

        String whatsappPath = Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";
        DirectoryObserver directoryFileObserver = new DirectoryObserver(new File(whatsappPath));

        //listen for switch toggling
        activateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //if switch is checked
                    if (checkPermission()) { //if permission is granted listen to folder
                        Log.e("TEST", "switch is on");
                        directoryFileObserver.startWatching();
                    } else {
                        requestPermission();
                    }
                } else {
                    Log.w("TEST", "switch is off");
                    directoryFileObserver.stopWatching();
                }
            }
        });


        //get Context
        Context context = getApplicationContext();

        //Default folder
        Switch defaultSwitch = findViewById(R.id.defaultDest);
        //listen for switch toggling
        defaultSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //if switch is checked
                    //if permission is granted listen to folder
                    Log.w("TEST", "Default switch is on");

                    //update list in both this class and the directory observer class
                    destinationsList.add("default");
                    directoryFileObserver.setDestinationsList(destinationsList);

                    Toast toast = Toast.makeText(context, "Default selected", Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    Log.w("TEST", "Default switch is off");

                    destinationsList.remove("default");
                    directoryFileObserver.setDestinationsList(destinationsList);

                    Toast toast = Toast.makeText(context, "Default unselected", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        //Google Photos

        GoogleSignInClient mGoogleSignInClient;
        String serverClientId = getString(R.string.server_client_id);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.EMAIL), new Scope(Scopes.DRIVE_APPS))
                .requestEmail()
                .requestServerAuthCode(serverClientId)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        Switch googleSwitch = findViewById(R.id.googlePhotos);
        //listen for switch toggling
        googleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //if switch is checked
                    //if permission is granted listen to folder
                    Log.w("TEST", "Google switch is on");

                    destinationsList.add("google photos");
                    directoryFileObserver.setDestinationsList(destinationsList);



                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();

                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                    Log.e("Account Name", account.getDisplayName() == null ? "null" : account.getDisplayName());
                    Log.e("Account Auth Code", account.getServerAuthCode() == null ? "null" : account.getServerAuthCode());


                    Thread networkThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                GoogleTokenResponse tokenResponse =
                                        new GoogleAuthorizationCodeTokenRequest(
                                                new NetHttpTransport(),
                                                JacksonFactory.getDefaultInstance(),
                                                "https://oauth2.googleapis.com/token",
                                                "646550166432-tkj80ckrp56d2eh1b672uefdhmp3gcej.apps.googleusercontent.com",
                                                "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",
                                                account.getServerAuthCode(),
                                                "")
                                                .execute();

                                String accessToken = tokenResponse.getAccessToken();
                                directoryFileObserver.setAccessToken(accessToken);

                                Log.e("Access Token", accessToken);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    networkThread.start();




                    ///////


                    Toast toast = Toast.makeText(context, "Google Photos selected", Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    Log.w("TEST", "Google switch is off");

                    destinationsList.remove("google photos");
                    directoryFileObserver.setDestinationsList(destinationsList);

                    Toast toast = Toast.makeText(context, "Google Photos unselected", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


    }

    private Boolean checkPermission() {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Storage permission is required, allow from settings", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        }
    }



}