package com.androingenio.askimposibles

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.androingenio.askimposibles.utils.Base
import com.androingenio.askimposibles.utils.models.Pregunta
import com.androingenio.askimposibles.utils.models.utils
import com.androingenio.askimposibles.utils.permisos.RPResultListener
import com.androingenio.askimposibles.utils.permisos.RuntimePermissionUtil
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.*
import github.nisrulz.screenshott.ScreenShott
import kotlinx.android.synthetic.main.activity_juego.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

class Juego:AppCompatActivity() {

    internal lateinit var preguntasRef: DatabaseReference
    internal lateinit var database: FirebaseDatabase
    private val mCommentIds = mutableListOf<String>()
    private val mComments = mutableListOf<Pregunta>()
    internal var preguntaActual: Pregunta? = null
    internal var actualKey: String = ""
    internal var x: Int = 0
    private val requestWritePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var bitmap: Bitmap? = null
    internal var hasWritePermission: Boolean = false
    private var mInterstitialAd: InterstitialAd? = null
    internal var Ad = false
    internal var countDownTimer: CountDownTimer? = null
    private val TAG = "JuegoActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_juego)
        MobileAds.initialize(this)
        adView.loadAd(AdRequest.Builder().build())
        database = FirebaseDatabase.getInstance()
        preguntasRef = database.reference
        hasWritePermission = RuntimePermissionUtil.checkPermissonGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        enableViews(false)
        mInterstitialAd = newInterstitialAd()
        loadInterstitial()
        val myTopPostsQuery = preguntasRef.child("Preguntas").orderByChild("estado").equalTo("1")
        myTopPostsQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    mCommentIds.add(dataSnapshot1.getKey()!!)
                    mComments.add(dataSnapshot1.getValue(Pregunta::class.java)!!)
                }

                showPregunta()
            }

        })
        txtPreguntaAJuego.setOnClickListener {
            enableViews(false)
            val votosA = Integer.parseInt(preguntaActual!!.votosA) + 1
            preguntaActual!!.votosA=votosA.toString()
            updatePregunta()
        }
        txtPreguntaBJuego.setOnClickListener{
            enableViews(false)
            val votosB = Integer.parseInt(preguntaActual!!.votosB) + 1
            preguntaActual!!.votosB=votosB.toString()
            updatePregunta()
        }
        btnShare.setOnClickListener {
            share()
        }
    }
    private fun share(){
        val params = Bundle()
        params.putString("idUser", utils.getUid())
        params.putString("nombreUser", Base.getInstance(this).nombreUser)
        App.getmFirebaseAnalytics().logEvent("ClickShare", params)
        bitmap = ScreenShott.getInstance().takeScreenShotOfView(viewScreen)
        if (bitmap != null) {
            if (hasWritePermission) {
                saveScreenshot()


                val bmpUri = getLocalBitmapUri(bitmap!!) // see previous remote images section
                val imageUris = ArrayList<Uri>()
                imageUris.add(bmpUri!!) // A

                val shareIntent = Intent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.txtShare))
                shareIntent.action = Intent.ACTION_SEND_MULTIPLE
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
                shareIntent.type = "image/*"
                startActivity(Intent.createChooser(shareIntent, "Share to.."))


            } else {
                RuntimePermissionUtil.requestPermission(this@Juego, requestWritePermission, 100)
            }
        }


    }

    private fun enableViews(b: Boolean) {
        txtPreguntaAJuego.isEnabled = b
        txtPreguntaBJuego.isEnabled = b
    }

    private fun newInterstitialAd(): InterstitialAd {
        val interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.interstitial_ad_unit_id)
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Ad = true
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Ad = false
            }

            override fun onAdClosed() {
                // Proceed to the next level.
                goToNextLevel()
            }
        }
        return interstitialAd
    }

    private fun showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd!!.isLoaded()) {
            mInterstitialAd!!.show()
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
            goToNextLevel()
        }
    }

    private fun loadInterstitial() {
        // Disable the next level button and load the ad.
        Ad = false
        val adRequest = AdRequest.Builder().build()
        mInterstitialAd!!.loadAd(adRequest)
    }

    private fun goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.

        mInterstitialAd = newInterstitialAd()
        loadInterstitial()
    }

    override fun onStop() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }

        super.onStop()
    }

    private fun getLocalBitmapUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png")
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            try {
                out!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            bmpUri = Uri.fromFile(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return bmpUri
    }

    private fun saveScreenshot() {
        try {
            val file = ScreenShott.getInstance()
                    .saveScreenshotToPicturesFolder(this@Juego, bitmap, "Preguntas Imposbles")

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            100 -> {

                RuntimePermissionUtil.onRequestPermissionsResult(grantResults, object : RPResultListener {
                    override fun onPermissionGranted() {
                        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            saveScreenshot()
                        }
                    }

                    override fun onPermissionDenied() {
                        Toast.makeText(this@Juego, "Permission Denied! You cannot save image!",
                                Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun showPregunta() {
        if (Ad) {
            val x = Random().nextInt(15)
            if (x == 5) {
                showInterstitial()
            }
        }
        for (z in mComments) {
            Log.d(TAG, "Pregunta--- " + z.toString())
        }
        for (z in mCommentIds) {
            Log.d(TAG, "z--- $z")
        }
        Log.d(TAG, "preguntas size" + mComments.size)
        Log.d(TAG, "preguntas size" + mCommentIds.size)
        if (mComments.size > 0) {
            if (mCommentIds.size == mComments.size) {
                enableViews(true)
                x = Random().nextInt(mCommentIds.size)
                preguntaActual = mComments[x]
                actualKey = mCommentIds[x]
                Log.d(TAG, "pregunta actual" + preguntaActual.toString())
                Glide.with(this).load(preguntaActual!!.imgA).into(imgPreguntaAJuego)
                Glide.with(this).load(preguntaActual!!.imgB).into(imgPreguntaBJuego)
                txtPreguntaAJuego.text = preguntaActual!!.preguntaA
                txtPreguntaBJuego.text = preguntaActual!!.preguntaB
                if (progressPreguntaJuego.visibility == View.VISIBLE) {
                    progressPreguntaJuego.visibility = View.GONE
                    linearProgressVotos.visibility = View.GONE
                }
            }
        } else {
            clearViews()
            Toast.makeText(this, "Se acabaron las preguntas", Toast.LENGTH_SHORT).show()
            enableViews(false)
            finish()
        }
    }
    private fun clearViews() {
        txtPreguntaAJuego.setText(null)
        txtPreguntaBJuego.setText(null)
        imgPreguntaAJuego.setImageDrawable(null)
        imgPreguntaBJuego.setImageDrawable(null)
    }
    private fun updatePregunta() {
        val params = Bundle()
        params.putString("idUser", utils.getUid())
        params.putString("idPregunta", actualKey)
        params.putString("nombreUser", Base.getInstance(this).nombreUser)
        App.getmFirebaseAnalytics().logEvent("UpdatePregunta", params)
        val max = (Integer.parseInt(preguntaActual!!.votosA) + Integer.parseInt(preguntaActual!!.votosB)).toFloat()
        progressPreguntaJuego.setMax(max)
        progressPreguntaJuego.setProgress(java.lang.Float.parseFloat(preguntaActual!!.votosA))
        txtPreguntaAJuego.text = preguntaActual!!.votosA + " Votos"
        txtPreguntaBJuego.text = preguntaActual!!.votosB + " Votos"
        val df = DecimalFormat("###.##")
        val porcenA = Integer.parseInt(preguntaActual!!.votosA) / max * 100
        val porcenB = Integer.parseInt(preguntaActual!!.votosB) / max * 100
        txtPreguntaAPorcentaje.text = df.format(porcenA.toDouble()) + "%"
        txtPreguntaBPorcentaje.text = df.format(porcenB.toDouble()) + "%"
        if (progressPreguntaJuego.getVisibility() == View.GONE) {
            progressPreguntaJuego.setVisibility(View.VISIBLE)
            linearProgressVotos.setVisibility(View.VISIBLE)
        }
        val globalPostRef = database.reference.child("Preguntas").child(actualKey)
        val userPostRef = database.reference.child("User-Preguntas").child(preguntaActual!!.uId!!).child(actualKey)
        starTransaction(globalPostRef)
        starTransaction(userPostRef)
        val commentIndex = mCommentIds.indexOf(actualKey)
        Log.d(TAG, "commentIndex $commentIndex")
        Log.d(TAG, "x $x")
        if (commentIndex > -1) {
            // Remove data from the list
            mCommentIds.removeAt(commentIndex)
            mComments.removeAt(commentIndex)
        } else {
            Log.w(TAG, "onChildRemoved:unknown_child:$actualKey")
        }
        Log.d(TAG, "Ya se puede mostrar otra pregunta")
        countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                showPregunta()
            }
        }
        countDownTimer!!.start()
    }

    private fun starTransaction(pRef: DatabaseReference) {
        pRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(Pregunta::class.java)
                        ?: return Transaction.success(mutableData)
                p.votosB=(preguntaActual!!.votosB)
                p.votosA=(preguntaActual!!.votosA)
                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }
            override fun onComplete(databaseError: DatabaseError?, b: Boolean,
                                    dataSnapshot: DataSnapshot?) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:$databaseError")
            }
        })
    }

}