package com.chsltutorials.blognews.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.base.BaseActivity
import com.chsltutorials.blognews.fragments.HomeFragment
import com.chsltutorials.blognews.fragments.ProfileFragment
import com.chsltutorials.blognews.fragments.SettingsFragment
import com.chsltutorials.blognews.model.Post
import com.chsltutorials.blognews.util.showMessageAlert
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import kotlinx.android.synthetic.main.popup_add_post.*

class HomeActivity : BaseActivity(),
                    NavigationView.OnNavigationItemSelectedListener {

    lateinit var popUpDialog : Dialog
    var pickedImagePopup : Uri? = null
    lateinit var title: String
    lateinit var description : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        mAuth.currentUser?.let {
            currentUser = it
            updateNavHeader()
            initPopUp()
            fab.setOnClickListener { popUpDialog.show() }
            setUpPermissionToPostImage()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

    }


    private fun setUpPermissionToPostImage() {
        popUpDialog.ivPopupSelected.setOnClickListener {
            verifySDK(this,CODE, REQUESTCODE)
        }
    }

    private fun initPopUp() {
        title = popUpDialog.etTitlePopup.text.toString()
        description = popUpDialog.etDescriptionPopup.text.toString()

        popUpDialog = Dialog(this)
        popUpDialog.setContentView(R.layout.popup_add_post)
        popUpDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popUpDialog.window.setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT)
        popUpDialog.window.attributes.gravity = Gravity.TOP

        Glide.with(this)
            .load(currentUser.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(popUpDialog.ivPhotoPopup)

        pickedImagePopup.let {  popUpDialog.ivPopupSelected.setImageURI(it) }

        popUpDialog.ivAddPopup.setOnClickListener {
            popUpDialog.popupProgressBar.visibility = View.VISIBLE
            it.visibility = View.INVISIBLE

            if(title.isNotEmpty() && description.isNotEmpty() && pickedImagePopup != null){
                addPostToFirebaseDatabase()
            }else{
                showMessageAlert(this,"Por favor, preencha todos os campos obrigatórios")
                popUpDialog.popupProgressBar.visibility = View.INVISIBLE
                popUpDialog.ivAddPopup.visibility = View.VISIBLE
            }

        }
    }

    private fun addPostToFirebaseDatabase() {
        val storageReference = FirebaseStorage.getInstance().reference.child("blog_imagens")
        val imagePath = storageReference.child(pickedImagePopup!!.lastPathSegment)
        imagePath.putFile(pickedImagePopup!!)
            .addOnSuccessListener {
                val imageDownloadLink = it.uploadSessionUri.toString()
                val database = FirebaseDatabase.getInstance()
                val myReference = database.getReference("Publicações").push()
                val myKey = myReference.key

                val post = Post(myKey,
                    title,
                    description,
                    imageDownloadLink,
                    currentUser.uid,
                    currentUser.photoUrl.toString())

                myReference.setValue(post).addOnSuccessListener {
                    showMessageAlert(this,"Postagem adicionado com sucesso")
                    popUpDialog.popupProgressBar.visibility = View.INVISIBLE
                    popUpDialog.ivAddPopup.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                showMessageAlert(this,it.message!!)
                popUpDialog.popupProgressBar.visibility = View.INVISIBLE
                popUpDialog.ivAddPopup.visibility = View.VISIBLE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
                && requestCode == REQUESTCODE
                && data != null ){
            //usuario escolheu a imagem, que deve ser salva no objeto Uri
            pickedImagePopup = data.data!!
            popUpDialog.ivPopupSelected.setImageURI(pickedImagePopup)

//            Glide.with(applicationContext)
//                .load(pickedImagePopup)
//                .into(addPopUpDialog.ivPopupSelected)

        }
    }



    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportActionBar?.title = "Home"
                supportFragmentManager.beginTransaction().replace(R.id.flContainer, HomeFragment())
                    .commit()
            }

            R.id.nav_profile -> {
                supportActionBar?.title = "Perfil"
                supportFragmentManager.beginTransaction().replace(R.id.flContainer, ProfileFragment())
                    .commit()

            }
            R.id.nav_settings -> {
                supportActionBar?.title = "Configurações"
                supportFragmentManager.beginTransaction().replace(R.id.flContainer, SettingsFragment())
                    .commit()
            }

            R.id.nav_log_out -> {
                FirebaseAuth.getInstance().signOut()
                goToRegister()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun updateNavHeader(){
        val headerView = navView.getHeaderView(0)
        headerView.tvNavUsername.text = currentUser.displayName
        headerView.tvNavEmail.text = currentUser.email

        Glide.with(this)
            .load(currentUser.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(headerView.ivNavUserPhoto)
    }

    companion object{
        const val CODE = 2
        const val REQUESTCODE = 2
    }
}
