package com.androingenio.askimposibles.utils;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by julianmartinez on 20/10/17.
 */

public class util {




    /**
     *
     * @param mDatabase
     * @param uniqueId
     * @param token
     */
    public static void uptadeToken(DatabaseReference mDatabase, String uniqueId, String token) {


        mDatabase.child("Usuarios").child(uniqueId).child("token").setValue(token);


    }


}
