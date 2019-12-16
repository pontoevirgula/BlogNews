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
import com.chsltutorials.blognews.util.Constants
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseAuth
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseStorageReference
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {

    lateinit var name : String
    lateinit var email : String
    lateinit var password : String
    lateinit var pickedImage : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.chsltutorials.blognews.R.layout.activity_register)

        progressBar.visibility = View.INVISIBLE

        ivUser.setOnClickListener { verifySDK(this, Constants.CODE_REGISTER, Constants.REQUESTCODE_REGISTER) }

        btnSave.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            btnSave.visibility = View.INVISIBLE

            if (isAllFieldsCorrect(clRegister,etName,etEmail,etPassword,etConfirmPassword,pickedImage.toString())){
                name = etName.text.toString()
                email = etEmail.text.toString()
                password = etPassword.text.toString()
                createUserAccount(name, email, password)
            }else{
                btnSave.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
            }
        }

        tvStymulusLogin.setOnClickListener { goToOtherActivity(LoginActivity::class.java) }
    }

    private fun createUserAccount(name: String, email: String, password: String) {

        getFirebaseAuth().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    showViewMessage(clRegister,this,"Conta criada",false)
                    updateUserInfo(name,pickedImage,getFirebaseAuth().currentUser)
                }else{
                    Log.w("TAG", "${it.exception}")
                    showViewMessage(clRegister,this,"Falha ao criar conta. ${it.exception?.message}",true)
                    btnSave.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }
            }

    }

    private fun updateUserInfo(name: String, pickedImage: Uri, currentUser: FirebaseUser?) {
        val imageFilePath = getFirebaseStorageReference(Constants.USER_PHOTOS).child(pickedImage.lastPathSegment!!)
        imageFilePath.putFile(pickedImage).addOnSuccessListener {
            imageFilePath.downloadUrl.addOnSuccessListener {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(it)
                    .build()

                currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                    if(it.isSuccessful){
                        showViewMessage(clRegister,this,"Cadastro completo",false)
                        goToOtherActivity(HomeActivity::class.java)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.REQUESTCODE_REGISTER
            && data != null ){
            //usuario escolheu a imagem, que deve ser salva no objeto Uri
            pickedImage = data.data!!

            Glide.with(applicationContext)
                .load(pickedImage)
                .apply(RequestOptions.circleCropTransform())
                .into(ivUser)

        }
    }

}
