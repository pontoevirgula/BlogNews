package com.chsltutorials.blognews.activity

import android.os.Bundle
import android.view.WindowManager
import android.text.format.DateFormat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.base.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_post_details.*
import java.util.*


class PostDetailsActivity : BaseActivity() {

    lateinit var postKey : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        //deixar barra de status transparente
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        supportActionBar!!.hide()


        mAuth = FirebaseAuth.getInstance()
        currentUser = FirebaseAuth.getInstance().currentUser!!

        intent?.extras?.let {
            val postImage = it.getString("pictures")
            Glide.with(this).load(postImage).into(ivPostDetail)

            val userPhotoPost = it.getString("userPhoto")
            Glide.with(this).load(userPhotoPost).apply(RequestOptions.circleCropTransform()).into(ivPostDetailUserPhoto)

            Glide.with(this).load(currentUser.photoUrl).apply(RequestOptions.circleCropTransform()).into(ivPostDetailCurrentUser)

            tvPostTitle.text = it.getString("title")

            tvPostDetailDesc.text = it.getString("description")

            postKey = it.getString("key")!!

            val date = timestampToString(it.getLong("date"))
            tvPostDetailDate.text = date
        }
    }

    private fun timestampToString(time: Long): String? {
        val calendar = Calendar.getInstance(Locale.US)
        calendar.timeInMillis = time
        return DateFormat.format("dd-MM-yyyy", calendar).toString()
    }
}
