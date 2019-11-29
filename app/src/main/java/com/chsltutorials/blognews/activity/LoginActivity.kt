package com.chsltutorials.blognews.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.base.BaseActivity
import com.chsltutorials.blognews.util.showMessageAlert
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    lateinit var email : String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        progressBarLogin.visibility = View.INVISIBLE

        btnLogin.setOnClickListener {
            progressBarLogin.visibility = View.VISIBLE
            btnLogin.visibility = View.INVISIBLE

            if (isAllFieldsCorrect(etEmailLogin,etPasswordLogin)){
                email = etEmailLogin.text.toString()
                password = etPasswordLogin.text.toString()
                signInUser(email,password)
            }else{
                btnLogin.visibility = View.VISIBLE
                progressBarLogin.visibility = View.INVISIBLE
            }
        }

        tvStymulusCreateAccount.setOnClickListener {
            goToRegister()
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.currentUser?.let {
            //se ja estiver conectado sera redirecionado a home
            currentUser = it
            goToHome()
        }
    }

    private fun signInUser(email: String, password: String) {

        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    btnLogin.visibility = View.VISIBLE
                    progressBarLogin.visibility = View.INVISIBLE
                    showMessageAlert(this,"Logado com sucesso")
                    goToHome()
                }else{
                    Log.w("TAG", "${it.exception}")
                    showMessageAlert(this,"Falha no login")
                    btnLogin.visibility = View.VISIBLE
                    progressBarLogin.visibility = View.INVISIBLE
                }
            }

    }




}
