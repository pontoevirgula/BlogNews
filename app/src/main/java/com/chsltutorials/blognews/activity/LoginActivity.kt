package com.chsltutorials.blognews.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.base.BaseActivity
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseAuth
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {

    lateinit var email : String
    lateinit var password: String
    lateinit var progressDialogLogin : ProgressDialog
    lateinit var googleSignInClient : GoogleSignInClient

    companion object{
        private const val RC_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBarLogin.visibility = View.INVISIBLE

        val googlesignOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.default_web_client_id.toString())
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,googlesignOptions)

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
                //tvMessageError.visibility = View.VISIBLE
            }
        }

        btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        tvStymulusCreateAccount.setOnClickListener{ goToOtherActivity(RegisterActivity::class.java) }
        tvRecoverPassword.setOnClickListener { showRecoverPasswordDialog() }
    }

    private fun isAllFieldsCorrectToTest(etEmail : EditText, etPassword : EditText) : Boolean {

        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        var correctRegister = true

        if(email.isEmpty()) {
            //etEmail.error = getString(R.string.invalid_email)
            correctRegister = false
        }
        if(password.isEmpty() || password.length < 6) {
            //etPassword.error = "Campo senha inválido. Preencha-o para logar"
            correctRegister = false
        }
        return correctRegister
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(this," ${e.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        getFirebaseAuth().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = getFirebaseAuth().currentUser
                    Toast.makeText(this,"${user?.email}",Toast.LENGTH_SHORT).show()
                    //goToOtherActivity(HomeActivity::class.java)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this,"Falha ao logar com a conta Google...",Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }.addOnFailureListener {
                Toast.makeText(this," ${it.message}",Toast.LENGTH_SHORT).show()
            }
    }



}
