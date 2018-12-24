package com.androingenio.askimposibles

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.WindowManager
import com.androingenio.askimposibles.utils.models.Pregunta
import com.androingenio.askimposibles.utils.models.utils.getUid
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_mis_preguntas.*
import kotlinx.android.synthetic.main.content_mis_preguntas.*
import java.util.*

class MisPreguntasActivity:AppCompatActivity() {
    private val TAG = "PostListFragment"
    private var mDatabase: DatabaseReference? = null
    private val imgsurls = ArrayList<String>()
    private var mAdapter: PreguntasAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_mis_preguntas)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            startActivity(Intent(this@MisPreguntasActivity, InsertPreguntaActivity::class.java))
        }
        App.getmFirebaseAnalytics().setCurrentScreen(this@MisPreguntasActivity, "MisPreguntasActivity", null)

        mDatabase = FirebaseDatabase.getInstance().reference
                .child("User-Preguntas").child(getUid())

        recyclerMisPreguntas.layoutManager = LinearLayoutManager(this)

        mDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (dataSnapshot1 in dataSnapshot.getChildren()) {
                        val p = dataSnapshot1.getValue(Pregunta::class.java)
                        if (p != null) {
                            if (p.imgA!!.length > 10)
                                imgsurls.add(p.imgA!!)
                            if (p.imgB!!.length > 10)
                                imgsurls.add(p.imgB!!)
                        }
                    }
                    Log.d(TAG, "imgsUrls: $imgsurls")
                    val random = Random().nextInt(imgsurls.size)
                    Log.d(TAG, "imgsUrls: $random")
                    Log.d(TAG, "imgsUrls: " + imgsurls[random])
                    Picasso.get().load(imgsurls[random]).into(imageRandom)
                } else {
                    //no has agregado preguntas
                }

            }

        })

    }


    public override fun onStart() {
        super.onStart()


        val ccc= resources.getStringArray(R.array.estados_preguntas)
        // Listen for comments
        mAdapter = PreguntasAdapter(this, mDatabase, ccc)
        recyclerMisPreguntas.adapter = mAdapter
    }

    public override fun onStop() {
        super.onStop()


        // Clean up comments listener
        mAdapter!!.cleanupListener()
    }

}