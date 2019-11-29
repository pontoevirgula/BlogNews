package com.chsltutorials.blognews.base

import android.content.Intent
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.chsltutorials.blognews.activity.HomeActivity
import com.chsltutorials.blognews.activity.LoginActivity
import com.chsltutorials.blognews.activity.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

abstract class BaseActivity : AppCompatActivity() {

    lateinit var mAuth : FirebaseAuth
    lateinit var currentUser : FirebaseUser

    fun isAllFieldsCorrect(etName : EditText, etEmail : EditText, etPassword : EditText, etConfirmPassword : EditText) : Boolean {

        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val repeatedPassword = etConfirmPassword.text.toString()

        var correctRegister = true

        if (name.isEmpty()) {
            etName.error = "Campo nome vazio. Preencha-o para cadastrar"
            correctRegister = false
        }
        if(!validateEmail(email)) {
            etEmail.error = "Campo email inválido. Preencha-o para cadastrar"
            correctRegister = false
        }
        if(password.isEmpty() && password.length < 6) {
            etPassword.error = "Campo senha inválido. Preencha-o para cadastrar"
            correctRegister = false
        }
        if(repeatedPassword != password) {
            etConfirmPassword.error = "As senhas devem ser iguais para finalizar cadastro"
            correctRegister = false
        }
        return correctRegister
    }

    fun isAllFieldsCorrect(etEmail : EditText, etPassword : EditText) : Boolean {

        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        var correctRegister = true

        if(!validateEmail(email)) {
            etEmail.error = "Campo email inválido. Preencha-o para logar"
            correctRegister = false
        }
        if(password.isEmpty() || password.length < 6) {
            etPassword.error = "Campo senha inválido. Preencha-o para logar"
            correctRegister = false
        }
        return correctRegister
    }

    private fun validateEmail(email: String) = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    protected fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    protected fun goToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }

    protected fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}