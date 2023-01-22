package com.example.imageduplicator;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
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
import java.security.KeyStore;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> destinationsList = new ArrayList<>();
    final int RC_SIGN_IN = 1;

    GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get Context
        Context context = getApplicationContext();

        //get last signed in google account

        account = GooglePhotosUtilities.getLastSignedInAccount(context);


        //Get Whatsapp directory
        String whatsappPath = Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";
        DirectoryObserver directoryFileObserver = new DirectoryObserver(new File(whatsappPath));


        Switch activateSwitch = findViewById(R.id.switch2);


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

        //display google account info
        TextView googleAccountInfo = findViewById(R.id.googleAccountInfo);



        //Google Photos
        GoogleSignInClient mGoogleSignInClient;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.EMAIL), new Scope(Scopes.DRIVE_APPS), new Scope("https://www.googleapis.com/auth/photoslibrary"))
                .requestEmail()
                .requestServerAuthCode(BuildConfig.CLIENT_ID)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        Switch googleSwitch = findViewById(R.id.googlePhotos);
        ImageView greenCheck = findViewById(R.id.check);
        ImageView xMark = findViewById(R.id.x);

        //listen for switch toggling
        googleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //if switch is checked
                    //if permission is granted listen to folder
                    Log.w("TEST", "Google switch is on");

                    destinationsList.add("google photos");
                    directoryFileObserver.setDestinationsList(destinationsList);


                    //make intent
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);

                    //get google account
                    account = GooglePhotosUtilities.getLastSignedInAccount(context);



                    if(account != null)
                        googleAccountInfo.setText("Account: " + account.getEmail() + "\n"+ "Server Auth Code: " + account.getServerAuthCode());

                    if(account != null) {
                        Log.e("Account Name", account.getDisplayName());
                        Log.e("Account Auth Code", account.getServerAuthCode() + " ");
                    }


                    //exchage auth code with access token
                    GoogleTokenResponse tokenResponse = null;
                    if(account != null && account.getServerAuthCode() != null){
                        tokenResponse = GooglePhotosUtilities.getGoogleTokenResponse(account);
                    }

                    //get access token and pass it to DirectoryObserver
                    if(tokenResponse != null) {
                        String accessToken = tokenResponse.getAccessToken();

                        //give the Directory file observer the access token to call google photos API
                        Log.e("Token passed to DirObs", accessToken);
                        directoryFileObserver.setAccessToken(accessToken);
                    }

                    if (account != null && account.getServerAuthCode() != null) {
                        greenCheck.setVisibility(ImageView.VISIBLE);
                        xMark.setVisibility(ImageView.INVISIBLE);
                    }

                    Toast toast = Toast.makeText(context, "Google Photos selected", Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    Log.w("TEST", "Google switch is off");

                    destinationsList.remove("google photos");
                    directoryFileObserver.setDestinationsList(destinationsList);

                    greenCheck.setVisibility(ImageView.INVISIBLE);
                    xMark.setVisibility(ImageView.VISIBLE);

                    Toast toast = Toast.makeText(context, "Google Photos unselected", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        if(account != null)
            googleAccountInfo.setText("Account: " + account.getEmail() + "\n" + "Server Auth Code: " + account.getServerAuthCode());



    }

    private Boolean checkPermission() {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Storage permission is required, allow from settings", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 111);
        }
    }



}