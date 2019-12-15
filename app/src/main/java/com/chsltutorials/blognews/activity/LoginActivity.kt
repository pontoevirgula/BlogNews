package com.chsltutorials.blognews.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.base.BaseActivity
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseAuth
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseUser
import com.chsltutorials.blognews.util.showMessageAlert
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    lateinit var email : String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
            goToOtherActivity(RegisterActivity::class.java)
        }
    }

    override fun onStart() {
        super.onStart()
        getFirebaseUser()?.let {
            //se ja estiver conectado sera redirecionado a home
            currentUser = it
            goToOtherActivity(HomeActivity::class.java)
        }
    }

    private fun signInUser(email: String, password: String) {

        getFirebaseAuth().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    btnLogin.visibility = View.VISIBLE
                    progressBarLogin.visibility = View.INVISIBLE
                    showMessageAlert(this,"Logado com sucesso")
                    goToOtherActivity(HomeActivity::class.java)
                }else{
                    Log.w("TAG", "${it.exception}")
                    showMessageAlert(this,"Falha no login")
                    btnLogin.visibility = View.VISIBLE
                    progressBarLogin.visibility = View.INVISIBLE
                }
            }

    }




}
