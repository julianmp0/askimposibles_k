package com.androingenio.askimposibles.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by julianmartinez on 8/09/17.
 */

public class Base {
    private static final String SHARED_PREF_NAME = "com.androingenio.askimposibles.utils";
    //private static final String TAG_TOKEN = "TagBase";

    private static Base mInstance;
    private static Context mCtx;

    private Base(Context context) {
        mCtx = context;
    }

    public static synchronized Base getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Base(context);
        }
        return mInstance;
    }



    public void editLogedUser(boolean b){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("userLoged", b);
        editor.apply();
    }


    public boolean getLogedUser(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getBoolean("userLoged",false );
    }
    public String getUniqueId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString("uId","" );
    }




    /**
     *
     * @param uId
     * @param nombre
     * @param foto
     * @param link
     * @param faceId
     * @param email
     * @param birthday
     * @param ageRange
     * @param gender
     */
    public void saveUser(String uId, String nombre, String foto, String link, String faceId, String email, String birthday, String ageRange, String gender) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uId", uId);
        editor.putString("nombre", nombre);
        editor.putString("foto", foto);
        editor.putString("link", link);
        editor.putString("faceId", faceId);
        editor.putString("email", email);
        editor.putString("birthday", birthday);
        editor.putString("ageRange", ageRange);
        editor.putString("gender", gender);
        editor.apply();
    }

    public String getImgUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString("foto","" );
    }

    public String getNombreUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString("nombre","" );
    }
}
