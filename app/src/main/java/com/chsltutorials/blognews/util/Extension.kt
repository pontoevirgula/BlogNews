package com.chsltutorials.blognews.util

import android.content.Context
import android.widget.Toast

fun showMessageAlert(context : Context, message : String) {
    Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
}