package com.androingenio.askimposibles.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by julianmartinez on 20/10/17.
 */

public class util {




    /**
     *
     * @param uniqueId
     * @param token
     */
    public static void uptadeToken(String uniqueId, String token) {


        FirebaseDatabase.getInstance().getReference().child("Usuarios").child(uniqueId).child("token").setValue(token);


    }


}
