package com.chsltutorials.blognews.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.base.BaseActivity
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseAuth
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {

    lateinit var email : String
    lateinit var password: String
    lateinit var progressDialogLogin : ProgressDialog

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

        tvStymulusCreateAccount.setOnClickListener{ goToOtherActivity(RegisterActivity::class.java) }
        tvRecoverPassword.setOnClickListener { showRecoverPasswordDialog() }
    }

    private fun showRecoverPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Recuperar senha")

//        val tvRecoverPassword = TextView(this)
//        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        layoutParams.set
//        tvRecoverPassword.text = "Recuperar senha"
//        tvRecoverPassword.gravity = Gravity.CENTER


        val etEmailDialog = EditText(this)
        etEmailDialog.hint = "Email"
        etEmailDialog.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        etEmailDialog.minEms = 16

        val linearLayout = LinearLayout(this)
        linearLayout.addView(etEmailDialog)
        linearLayout.setPadding(10,10,10,10)
        builder.setView(linearLayout)

        builder.setPositiveButton("Recuperar") { _, _ ->
            val emailDialog = etEmailDialog.text.toString().trim()
            if (!emailDialog.isNullOrEmpty()){
                beginRecovery(emailDialog)
            }else{
                showViewMessage(clLogin, this, "Campo email vazio", true)
                showRecoverPasswordDialog()
            }
        }
        builder.setNegativeButton("Cancelar"){dialog,_ -> dialog.dismiss() }

        builder.create().show()

    }

    private fun beginRecovery(email: String) {
        progressDialogLogin = ProgressDialog(this)
        progressDialogLogin.setMessage("Enviando email...")
        progressDialogLogin.setCanceledOnTouchOutside(false)
        progressDialogLogin.show()

        getFirebaseAuth().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialogLogin.dismiss()
                    showViewMessage(clLogin, this, "Email enviado", false)
                }else {
                    progressDialogLogin.dismiss()
                    showViewMessage(clLogin, this, "Falha ao enviar email", true)
                }
            }
            .addOnFailureListener { e->
                progressDialogLogin.dismiss()
                showViewMessage(clLogin,this," ${e.message}",true)
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
                    showViewMessage(clLogin, this,"Logado com sucesso",false)
                    goToOtherActivity(HomeActivity::class.java)
                }else{
                    Log.w("TAG", "${it.exception}")
                    showViewMessage(clLogin, this,"Falha no login",true)
                    btnLogin.visibility = View.VISIBLE
                    progressBarLogin.visibility = View.INVISIBLE
                }
            }

    }




}
