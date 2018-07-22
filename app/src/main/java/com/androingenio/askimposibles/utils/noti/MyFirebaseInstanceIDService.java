package com.androingenio.askimposibles.utils.noti;

import android.util.Log;

import com.androingenio.askimposibles.utils.Base;
import com.androingenio.askimposibles.utils.util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by julianmartinez on 27/10/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        util.uptadeToken(mDatabase, Base.getInstance(getApplicationContext()).getUniqueId(),refreshedToken);

    }
}
