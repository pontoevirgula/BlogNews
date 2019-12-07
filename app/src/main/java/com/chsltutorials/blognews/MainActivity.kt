package com.chsltutorials.blognews

import android.os.Bundle
import com.chsltutorials.blognews.activity.LoginActivity
import com.chsltutorials.blognews.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToOtherActivity(LoginActivity::class.java)
    }
}
