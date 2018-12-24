package com.androingenio.askimposibles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.androingenio.askimposibles.utils.Base
import com.androingenio.askimposibles.utils.models.utils.getUid
import com.androingenio.askimposibles.utils.util
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.ShareLinkContent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity:AppCompatActivity() {


    internal var TAG = MainActivity::class.java.simpleName

    var base = Base.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        val token = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: $token")
        var mDatabase = FirebaseDatabase.getInstance().reference
        util.uptadeToken(mDatabase, base.uniqueId, token)
        Log.d("foto",base.imgUser)
        Picasso.get().load(base.imgUser).into(imgUser)
        txtNombreUser.text = base.nombreUser
        App.getmFirebaseAnalytics().setCurrentScreen(this@MainActivity, "MainActivity", null)

        val content = ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.androingenio.askimposibles"))
                .setShareHashtag(ShareHashtag.Builder()
                        .setHashtag("#PreguntasImposibles")
                        .build())
                .setQuote(getString(R.string.txtSplash))
                .build()
        share_btn.shareContent = content
        mDatabase = FirebaseDatabase.getInstance().reference.child("Usuarios").child(getUid())
        mDatabase.addListenerForSingleValueEvent(object  : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
        btnJugar.setOnClickListener {
            startActivity(Intent(this@MainActivity, Juego::class.java))
        }
        btnMisPreguntas.setOnClickListener {
            startActivity(Intent(this@MainActivity, MisPreguntasActivity::class.java))
        }
    }
}