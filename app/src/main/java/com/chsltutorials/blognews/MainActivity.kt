package com.chsltutorials.blognews

import android.content.Intent
import android.os.Bundle
import com.chsltutorials.blognews.activity.LoginActivity
import com.chsltutorials.blognews.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, LoginActivity::class.java))
    }
}
