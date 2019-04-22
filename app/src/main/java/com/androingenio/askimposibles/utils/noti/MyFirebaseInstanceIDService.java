package com.androingenio.askimposibles.utils.noti;

import android.util.Log;

import com.androingenio.askimposibles.utils.Base;
import com.androingenio.askimposibles.utils.util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by julianmartinez on 27/10/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    String TAG = MyFirebaseInstanceIDService.class.getSimpleName();



    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
