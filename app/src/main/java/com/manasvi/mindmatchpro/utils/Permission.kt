package com.manasvi.mindmatchpro.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.manasvi.mindmatchpro.CreateAcitvity

fun isPermissionGranted(context : Context, permission : String):Boolean{
    return ContextCompat.checkSelfPermission(context,permission)== PackageManager.PERMISSION_GRANTED

}

fun requestPermission (acitvity: Activity ?,permission: String,requestCode : Int){
    ActivityCompat.requestPermissions(acitvity!! , arrayOf(permission),requestCode)

}

