package com.androingenio.askimposibles

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.androingenio.askimposibles.utils.Base
import com.androingenio.askimposibles.utils.models.Pregunta
import com.androingenio.askimposibles.utils.models.utils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_insert_pregunta.*
import kotlinx.android.synthetic.main.content_insert_pregunta.*
import java.util.HashMap

class InsertPreguntaActivity:AppCompatActivity() {

    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_insert_pregunta)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mDatabase = FirebaseDatabase.getInstance().reference

        fabInsert.setOnClickListener({ insertPregunta() })

        App.getmFirebaseAnalytics().setCurrentScreen(this@InsertPreguntaActivity, "InsertPreguntaActivity", null)
    }

    private fun insertPregunta() {


        val params = Bundle()
        params.putString("idUser", utils.getUid())
        params.putString("nombreUser", Base.getInstance(this).nombreUser)
        App.getmFirebaseAnalytics().logEvent("InsertPregunta", params)

        val PreguntaA = etPreguntaA.getText().toString()
        val PreguntaB = etPreguntaB.getText().toString()

        // Title is required
        if (TextUtils.isEmpty(PreguntaA)) {
            etPreguntaA.setError("Requerida")
            return
        }

        // Body is required
        if (TextUtils.isEmpty(PreguntaB)) {
            etPreguntaB.setError("Requerida")
            return
        }

        setEditingEnabled(false)
        val userId = utils.getUid()


        val key = mDatabase!!.child("Preguntas").push().key
        val post = Pregunta(PreguntaA, "", "0", PreguntaB, "", "0", "0", userId, System.currentTimeMillis().toString())
        val postValues = post.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/Preguntas/" + key!!] = postValues
        childUpdates["/User-Preguntas/$userId/$key"] = postValues

        mDatabase!!.updateChildren(childUpdates)


        setEditingEnabled(true)
        finish()
    }

    private fun setEditingEnabled(enabled: Boolean) {
        etPreguntaA.isEnabled = enabled
        etPreguntaB.isEnabled = enabled
        if (enabled) {
            fabInsert.visibility = View.VISIBLE
        } else {
            fabInsert.visibility = View.GONE
        }
    }


}