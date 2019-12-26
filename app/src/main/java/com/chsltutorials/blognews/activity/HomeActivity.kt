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
import com.chsltutorials.blognews.util.Constants
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseAuth
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseDatabaseReference
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseStorageReference
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseUser
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
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
    lateinit var post : Post
    lateinit var myReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        getFirebaseAuth().currentUser?.let {
            currentUser = it
            updateNavHeader()
            initPopUp()
        }

        popUpDialog.let { popup ->
            fab.setOnClickListener { popup.show() }

            popup.ivPictureSelectedPopup.setOnClickListener {
                verifySDK(this, Constants.CODE_HOME, Constants.REQUESTCODE_HOME)
            }

            popup.ivAddPostPoup.setOnClickListener { btAddPost ->
                popup.progressBarPopup.visibility = View.VISIBLE
                btAddPost.visibility = View.INVISIBLE

                title = popup.etTitlePopup.text.toString()
                description = popup.etDescriptionPopup.text.toString()

                if (title.isNotEmpty() && description.isNotEmpty() && pickedImagePopup != null) {
                    addPostToFirebaseDatabase()
                } else {
                    showViewMessage(clHome, this,"Por favor, preencha todos os campos obrigatórios",true)
                    popup.progressBarPopup.visibility = View.INVISIBLE
                    popup.ivAddPostPoup.visibility = View.VISIBLE
                }
            }
        }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction().replace(R.id.flContainer,HomeFragment()).commit()
    }



    private fun initPopUp() {
        popUpDialog = Dialog(this)
        with(popUpDialog) {
            setContentView(R.layout.popup_add_post)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT)
            window.attributes.gravity = Gravity.TOP

            pickedImagePopup.let { ivPictureSelectedPopup.setImageURI(it) }
        }

        Glide.with(this)
            .load(currentUser.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(popUpDialog.ivPhotoProfilePopup)
    }

    private fun addPostToFirebaseDatabase() {
        val imagePath = getFirebaseStorageReference(Constants.BLOG_IMAGES).child(pickedImagePopup!!.lastPathSegment!!)
        imagePath.putFile(pickedImagePopup!!).addOnSuccessListener {
            imagePath.downloadUrl.addOnSuccessListener { uri ->
                val imageDownloadLink = uri.toString()
                if (currentUser.photoUrl != null) {
                    myReference = getFirebaseDatabaseReference(Constants.PUBLISHEDS).push()
                    val myKey = myReference.key
                    post = Post(
                        postKey = myKey,
                        title = title,
                        description = description,
                        pictures = imageDownloadLink,
                        userId = currentUser.uid,
                        userPhoto = currentUser.photoUrl.toString()
                    )
                }else{
                    myReference = getFirebaseDatabaseReference(Constants.PUBLISHEDS).push()
                    val myKey = myReference.key
                    post = Post(
                        postKey = myKey,
                        title = title,
                        description = description,
                        pictures = imageDownloadLink,
                        userId = currentUser.uid,
                        userPhoto = currentUser.photoUrl.toString()
                    )
                }

                myReference.setValue(post).addOnSuccessListener {
                    showViewMessage(clHome, this, "Postagem adicionado com sucesso",false)
                    popUpDialog.progressBarPopup.visibility = View.INVISIBLE
                    popUpDialog.ivAddPostPoup.visibility = View.VISIBLE
                }
            }
            imagePath.downloadUrl.addOnFailureListener { e ->
                showViewMessage(clHome, this,e.message!!,true)
                popUpDialog.progressBarPopup.visibility = View.INVISIBLE
                popUpDialog.ivAddPostPoup.visibility = View.VISIBLE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
                && requestCode == Constants.REQUESTCODE_HOME
                && data != null ){
            //usuario escolheu a imagem, que deve ser salva no objeto Uri
            pickedImagePopup = data.data!!
            popUpDialog.ivPictureSelectedPopup.setImageURI(pickedImagePopup)
        }
    }

    private fun updateNavHeader(){
        val headerView = navView.getHeaderView(0)
        headerView.tvNavUsername.text = currentUser.displayName
        headerView.tvNavEmail.text = currentUser.email

        if (currentUser.photoUrl != null) {
            Glide.with(this).load(currentUser.photoUrl).apply(RequestOptions.circleCropTransform()).into(headerView.ivNavUserPhoto)
        }else {
            Glide.with(this).load(R.drawable.userphoto).apply(RequestOptions.circleCropTransform()).into(headerView.ivNavUserPhoto)
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
                goToOtherActivity(LoginActivity::class.java)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
