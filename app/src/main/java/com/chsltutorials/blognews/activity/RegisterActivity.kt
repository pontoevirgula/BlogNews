package com.chsltutorials.blognews.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chsltutorials.blognews.base.BaseActivity
import com.chsltutorials.blognews.util.showMessageAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {

    lateinit var name : String
    lateinit var email : String
    lateinit var password : String
    lateinit var pickedImage : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.chsltutorials.blognews.R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        progressBar.visibility = View.INVISIBLE

        ivUser.setOnClickListener { verifySDK(this, CODE, REQUESTCODE) }

        btnSave.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            btnSave.visibility = View.INVISIBLE

            if (isAllFieldsCorrect(etName,etEmail,etPassword,etConfirmPassword,pickedImage.toString())){
                name = etName.text.toString()
                email = etEmail.text.toString()
                password = etPassword.text.toString()
                createUserAccount(name, email, password)
            }else{
                btnSave.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
            }
        }

        tvStymulusLogin.setOnClickListener { goToLogin() }
    }

    private fun createUserAccount(name: String, email: String, password: String) {

        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    showMessageAlert(this,"Conta criada")
                    updateUserInfo(name,pickedImage,mAuth.currentUser)
                }else{
                    Log.w("TAG", "${it.exception}")
                    showMessageAlert(this,"Falha ao criar conta. ${it.exception?.message}")
                    btnSave.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }
            }

    }

    private fun updateUserInfo(name: String, pickedImage: Uri, currentUser: FirebaseUser?) {
        val storage = FirebaseStorage.getInstance().reference.child("users_photos")
        val imageFilePath = storage.child(pickedImage.lastPathSegment)
        imageFilePath.putFile(pickedImage).addOnSuccessListener {
            imageFilePath.downloadUrl.addOnSuccessListener {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(it)
                    .build()

                currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                    if(it.isSuccessful){
                        showMessageAlert(this,"Cadastro completo")
                        goToHome()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == REQUESTCODE
            && data != null ){
            //usuario escolheu a imagem, que deve ser salva no objeto Uri
            pickedImage = data.data!!

            Glide.with(applicationContext)
                .load(pickedImage)
                .apply(RequestOptions.circleCropTransform())
                .into(ivUser)

        }
    }


    companion object{
        const val CODE = 1
        const val REQUESTCODE = 1
    }
}
