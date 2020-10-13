package com.iniyan.androidsmsuserconsentapi;

import android.content.Intent;

public interface SmsBroadcastReceiverListener {

    void onSuccess(Intent intent);
    void onFailure();
}
