package com.androingenio.askimposibles.utils.models;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by julianmartinez on 2/11/17.
 */

public class utils {

    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
