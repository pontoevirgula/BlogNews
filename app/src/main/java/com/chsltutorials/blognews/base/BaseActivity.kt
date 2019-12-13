package com.chsltutorials.blognews.base

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chsltutorials.blognews.activity.HomeActivity
import com.chsltutorials.blognews.activity.LoginActivity
import com.chsltutorials.blognews.activity.RegisterActivity
import com.chsltutorials.blognews.util.showMessageAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

abstract class BaseActivity : AppCompatActivity() {

    lateinit var mAuth : FirebaseAuth
    lateinit var currentUser : FirebaseUser
    lateinit var databaseReference : DatabaseReference
    lateinit var firebaseDatabase : FirebaseDatabase

    protected fun verifySDK(context : Context, code : Int,requestCode : Int){
        if(Build.VERSION.SDK_INT >= 22){
            checkAndRequestForPermission(context,code,requestCode)
        }else{
            openGallery(requestCode)
        }
    }

    private fun checkAndRequestForPermission(context : Context, code : Int,requestCode : Int) {

        if( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                //SE NEGAR PERMISSÃO
                showMessageAlert(context,"Por favor aceite as permissões requisitadas")
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),code)
            }

        } else{
            openGallery(requestCode)
        }

    }

    private fun openGallery(requestCode : Int) {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, requestCode)
    }

    fun isAllFieldsCorrect(etName : EditText, etEmail : EditText, etPassword : EditText, etConfirmPassword : EditText, pickedImage : String) : Boolean {

        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val repeatedPassword = etConfirmPassword.text.toString()

        var correctRegister = true

        if (pickedImage.isEmpty()){
            showMessageAlert(applicationContext,"Selecione uma imagem para foto de perfil")
            correctRegister = false
        }
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

    protected fun goToOtherActivity(activity : Class<*>) {
        startActivity(Intent(this, activity))
        finish()
    }

}