package com.iniyan.androidsmsuserconsentapi;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    SmsBroadcastReceiver smsBroadcastReceiver ;
    private int CREDENTIAL_PICKER_REQUEST = 1;
    int REQ_USER_CONSENT = 100;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestHint();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerToSmsBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    private void  registerToSmsBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent, REQ_USER_CONSENT);
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(),"onFaliure",Toast.LENGTH_SHORT).show();
            }
        };


        IntentFilter intentFilter =new  IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {

            if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);

                // String  code = fetchVerificationCode(message);
                Log.d("TAG", "onActivityResult: " + message);

                Log.d("TAG", "onActivityResult: Fetch " + fetchVerificationCode(message));
                Toast.makeText(getApplicationContext(), fetchVerificationCode(message), Toast.LENGTH_SHORT).show();

            }
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST) {

            // Obtain the phone number from the result
            if (resultCode == Activity.RESULT_OK && data != null) {
                Credential credential = (Credential) data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string

                String mobileNo = credential.getId();
                Log.e("TAG", "onActivityResult: mobileNo " + mobileNo);
                startSmsUserConsent();
            }

        }
    }
    private String  fetchVerificationCode( String message ) {
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        final Matcher matcher = pattern.matcher(message);
        if (matcher.find())
            return matcher.group(0);
        else  return "No Number Found";
    }

    private void  startSmsUserConsent() {
        SmsRetrieverClient smsRetriever =   SmsRetriever.getClient(this);
        //We can add user phone number or leave it blank
        smsRetriever.startSmsUserConsent(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "LISTENING_SUCCESS");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "LISTENING_FAILURE");
                    }
                });

    }


    // Construct a request for phone numbers and show the picker
    private void requestHint () {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        CredentialsClient credentialsClient = Credentials.getClient(this);
        PendingIntent intent = credentialsClient.getHintPickerIntent(hintRequest);
        try {
            startIntentSenderForResult(
                    intent.getIntentSender(),
                    CREDENTIAL_PICKER_REQUEST,
                    null, 0, 0, 0
            );
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

}
