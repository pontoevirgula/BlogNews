package com.chsltutorials.blognews.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
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
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import kotlinx.android.synthetic.main.popup_add_post.*

class HomeActivity : BaseActivity(),
                    NavigationView.OnNavigationItemSelectedListener {

    lateinit var addPopUpDialog : Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()


        fab.setOnClickListener { addPopUpDialog.show() }

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

    }

    private fun initPopUp() {
        addPopUpDialog = Dialog(this)
        addPopUpDialog.setContentView(R.layout.popup_add_post)
        addPopUpDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addPopUpDialog.window.setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT)
        addPopUpDialog.window.attributes.gravity = Gravity.TOP

        Glide.with(this)
            .load(currentUser.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(addPopUpDialog.ivPhotoPopup)

        addPopUpDialog.ivAddPopup.setOnClickListener {
            addPopUpDialog.popupProgressBar.visibility = View.VISIBLE
            it.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.currentUser?.let {
            currentUser = it
            updateNavHeader()
            initPopUp()
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
}
