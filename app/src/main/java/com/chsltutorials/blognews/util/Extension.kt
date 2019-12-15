package com.chsltutorials.blognews.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun showMessageAlert(context : Context, message : String) {
    Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
}

fun showMessageAlert2(view : View, message : String) {
    //Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    Snackbar.make(view,message,Snackbar.LENGTH_SHORT)
        .setAction("Action",null).show()

}