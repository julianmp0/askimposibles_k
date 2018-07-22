package com.androingenio.askimposibles.utils.models


/**
 * Created by julianmartinez on 2/11/17.
 */

class User{
    var uId:String=""
    var nombre:String=""
    var foto:String=""
    var link:String=""
    var faceId:String=""
    var email:String=""
    var birthday:String=""
    var ageRange:String=""
    var gender:String=""
    override fun toString(): String {
        return "User(uId='$uId', nombre='$nombre', foto='$foto', link='$link', faceId='$faceId', email='$email', birthday='$birthday', ageRange='$ageRange', gender='$gender')"
    }


}





