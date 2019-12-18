package com.chsltutorials.blognews.base

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

abstract class BaseActivity : AppCompatActivity() {

    lateinit var currentUser : FirebaseUser
    lateinit var databaseReference : DatabaseReference


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
                showViewMessage(null, context,"Por favor aceite as permissões requisitadas",true)
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

    fun isAllFieldsCorrect(view : View, etName : EditText, etEmail : EditText, etPassword : EditText, etConfirmPassword : EditText, pickedImage : String) : Boolean {

        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val repeatedPassword = etConfirmPassword.text.toString()

        var correctRegister = true

        if (pickedImage.isEmpty()){
            showViewMessage(view, applicationContext,"Selecione uma imagem para foto de perfil",true)
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

    fun showViewMessage(view : View?, context: Context, message : String,  errorMessage : Boolean) {
        if (errorMessage && view != null) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null)
            snackbar.view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            snackbar.show()
        }else if (!errorMessage && view != null) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null)
            snackbar.view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            snackbar.show()
        }else{
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
        }
    }

}