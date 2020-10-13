package com.iniyan.androidsmsuserconsentapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.Objects;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    SmsBroadcastReceiverListener smsBroadcastReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Objects.equals(intent.getAction(), SmsRetriever.SMS_RETRIEVED_ACTION)) {
            Bundle extras = intent.getExtras();
            Status smsRetrieverStatus = null;
            if (  extras != null) {
                smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                if (smsRetrieverStatus != null) {
                    if(smsRetrieverStatus.getStatusCode() == CommonStatusCodes.SUCCESS ){
                        smsBroadcastReceiverListener.onSuccess((Intent) extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT));

                    }else if(smsRetrieverStatus.getStatusCode() == CommonStatusCodes.TIMEOUT){
                        smsBroadcastReceiverListener.onFailure();
                    }
                } else   smsBroadcastReceiverListener.onFailure();
            } else smsBroadcastReceiverListener.onFailure();

        }

    }
}

