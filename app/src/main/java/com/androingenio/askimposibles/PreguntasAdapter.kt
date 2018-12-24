package com.androingenio.askimposibles

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.androingenio.askimposibles.utils.models.Pregunta
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.util.ArrayList

class PreguntasAdapter(var mContext: Context,var mDatabaseReference: DatabaseReference?,var estados: Array<String>) : RecyclerView.Adapter<PreguntasAdapter.PreguntaViewHolder>() {

    private val TAG = "PostDetailActivity"

    private var mChildEventListener: ChildEventListener?=null

    private val mCommentIds = ArrayList<String>()
    private val mComments = ArrayList<Pregunta>()


    init{

        // Create child event listener
        // [START child_event_listener_recycler]
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new comment has been added, add it to the displayed list
                val comment = dataSnapshot.getValue(Pregunta::class.java)

                // [START_EXCLUDE]
                // Update RecyclerView
                mCommentIds.add(dataSnapshot.key!!)
                mComments.add(comment!!)
                notifyItemInserted(mComments.size - 1)
                // [END_EXCLUDE]
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val newComment = dataSnapshot.getValue(Pregunta::class.java)
                val commentKey = dataSnapshot.key

                // [START_EXCLUDE]
                val commentIndex = mCommentIds.indexOf(commentKey)
                if (commentIndex > -1) {
                    // Replace with the new data
                    mComments.set(commentIndex, newComment!!)

                    // Update the RecyclerView
                    notifyItemChanged(commentIndex)
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + commentKey!!)
                }
                // [END_EXCLUDE]
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataSnapshot.key

                // [START_EXCLUDE]
                val commentIndex = mCommentIds.indexOf(commentKey)
                if (commentIndex > -1) {
                    // Remove data from the list
                    mCommentIds.removeAt(commentIndex)
                    mComments.removeAt(commentIndex)

                    // Update the RecyclerView
                    notifyItemRemoved(commentIndex)
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey!!)
                }
                // [END_EXCLUDE]
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataSnapshot.getValue(Pregunta::class.java)
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(mContext, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show()
            }
        }
        mDatabaseReference!!.addChildEventListener(childEventListener)
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.item_pregunta, parent, false)
        return PreguntaViewHolder(view)
    }


    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        val comment = mComments[position]
        holder.pA.setText(comment.preguntaA)
        holder.pB.setText(comment.preguntaB)
        Picasso.get().load(comment.imgA).into(holder.imgA)
        Picasso.get().load(comment.imgB).into(holder.imgB)
        val estado = Integer.parseInt(comment.estado)
        holder.txtEstado.text = estados[estado]
        if (estado == 1) {
            holder.txtEstado.setTextColor(Color.parseColor("#08BE2C"))
            val votosA = Integer.parseInt(comment.votosA).toFloat()
            val votosB = Integer.parseInt(comment.votosB).toFloat()
            val sumaTotal = votosA + votosB
            holder.progressBar.setMax(sumaTotal)
            holder.progressBar.setProgress(votosA)
            holder.progressBar.setVisibility(View.VISIBLE)
            holder.rlView.setVisibility(View.VISIBLE)
            holder.txtVotosA.setText(votosA.toInt().toString() + " votos")
            holder.txtVotosB.setText(votosB.toInt().toString() + " votos")
            val df = DecimalFormat("###.##")
            val porcenA = votosA / sumaTotal * 100
            val porcenB = votosB / sumaTotal * 100
            Log.d(TAG, "Votos A: $votosA")
            Log.d(TAG, "Votos B: $votosB")
            Log.d(TAG, "Suma Votos: $sumaTotal")
            Log.d(TAG, "Porcentaje A: $porcenA")
            Log.d(TAG, "Porcentaje B: $porcenB")

            holder.txtPorcentajeA.setText(df.format(porcenA.toDouble()) + "%")
            holder.txtPorcentajeB.setText(df.format(porcenB.toDouble()) + "%")


        } else if (estado == 2) {
            holder.txtEstado.setTextColor(Color.parseColor("#E20000"))
        }

    }

    override fun getItemCount(): Int {
        return mComments.size
    }

    fun cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference!!.removeEventListener(mChildEventListener!!)
        }
    }


    inner class PreguntaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pA: TextView = itemView.findViewById(R.id.txtPreguntaA)
        var pB: TextView = itemView.findViewById(R.id.txtPreguntaB)
        var txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        var imgA: ImageView = itemView.findViewById(R.id.imgPreguntaA)
        var imgB: ImageView = itemView.findViewById(R.id.imgPreguntaB)
        var progressBar: RoundCornerProgressBar = itemView.findViewById(R.id.progressMisPregunta)

        var txtVotosA: TextView = itemView.findViewById(R.id.txtPreguntaAVotosItem)
        var txtVotosB: TextView = itemView.findViewById(R.id.txtPreguntaBVotosItem)
        var txtPorcentajeA: TextView = itemView.findViewById(R.id.txtPreguntaAPorcentajeItem)
        var txtPorcentajeB: TextView = itemView.findViewById(R.id.txtPreguntaBPorcentajeItem)
        var rlView: RelativeLayout = itemView.findViewById(R.id.rlProgressVotos)

    }
}
