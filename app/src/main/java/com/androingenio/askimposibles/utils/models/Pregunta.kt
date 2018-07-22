package com.androingenio.askimposibles.utils.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

import java.util.HashMap

/**
 * Created by julianmartinez on 2/11/17.
 */


@IgnoreExtraProperties
class Pregunta {

    var preguntaA: String=""
    var imgA: String=""
    var votosA: String=""
    var preguntaB: String=""
    var imgB: String=""
    var votosB: String=""
    var estado: String=""
    var uId: String=""
    var fechaIngreso: String=""


    constructor() {}

    /**
     *
     * @param preguntaA
     * @param imgA
     * @param votosA
     * @param preguntaB
     * @param imgB
     * @param votosB
     * @param estado
     * @param uId
     * @param fechaIngreso
     */
    constructor(preguntaA: String, imgA: String, votosA: String, preguntaB: String, imgB: String, votosB: String, estado: String, uId: String, fechaIngreso: String) {
        this.preguntaA = preguntaA
        this.imgA = imgA
        this.votosA = votosA
        this.preguntaB = preguntaB
        this.imgB = imgB
        this.votosB = votosB
        this.estado = estado
        this.uId = uId
        this.fechaIngreso = fechaIngreso
    }

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["preguntaA"] = preguntaA
        result["imgA"] = imgA
        result["votosA"] = votosA.toString()
        result["preguntaB"] = preguntaB.toString()
        result["imgB"] = imgB.toString()
        result["votosB"] = votosB.toString()
        result["estado"] = estado.toString()
        result["uId"] = uId.toString()
        result["fechaIngreso"] = fechaIngreso.toString()


        return result
    }

    override fun toString(): String {
        return "Pregunta(preguntaA='$preguntaA', imgA='$imgA', votosA='$votosA', preguntaB='$preguntaB', imgB='$imgB', votosB='$votosB', estado='$estado', uId='$uId', fechaIngreso='$fechaIngreso')"
    }


}
