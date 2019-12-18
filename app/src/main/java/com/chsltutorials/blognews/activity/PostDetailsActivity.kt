package com.chsltutorials.blognews.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.adapter.CommentAdapter
import com.chsltutorials.blognews.base.BaseActivity
import com.chsltutorials.blognews.model.Comment
import com.chsltutorials.blognews.util.Constants
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseAuth
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseDatabaseReference
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_post_details.*
import java.util.*
import kotlin.collections.ArrayList


class PostDetailsActivity : BaseActivity() {

    private var commentList: MutableList<Comment> = ArrayList()
    lateinit var postKey : String
    lateinit var commentField : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        //deixar barra de status transparente
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        supportActionBar!!.hide()


        ibPostDetailAddComment.setOnClickListener {
            it.visibility = View.INVISIBLE
            commentField = etPostDetailComment.text.toString()
            if (verifyCommentField(commentField)) {
                val commentPush = databaseReference.push()
                getFirebaseAuth().currentUser?.let {
                    val comment = Comment(
                        id = getFirebaseUser()!!.uid,
                        name = getFirebaseUser()!!.displayName!!,
                        content = commentField,
                        image = getFirebaseUser()!!.photoUrl.toString()
                    )
                    commentPush.setValue(comment)
                        .addOnSuccessListener {
                            showViewMessage(nsvPostDetail, this, "Comentário adicionado",false)
                            etPostDetailComment.setText("")
                            ibPostDetailAddComment.visibility = View.VISIBLE
                        }
                        .addOnFailureListener { e ->
                            showViewMessage(nsvPostDetail, this, "Falha ao adicionar comentário : ${e.message}",true)
                            ibPostDetailAddComment.visibility = View.VISIBLE
                        }
                }
            }
        }

        intent?.extras?.let {
            val postImage = it.getString("pictures")
            Glide.with(this).load(postImage).into(ivPostDetail)

            val userPhotoPost = it.getString("userPhoto")
            if (userPhotoPost != null) {
                Glide.with(this).load(userPhotoPost).apply(RequestOptions.circleCropTransform()).into(ivPostDetailUserPhoto)
            }else{
                Glide.with(this).load(R.drawable.userphoto).apply(RequestOptions.circleCropTransform()).into(ivPostDetailUserPhoto)
            }

            if (getFirebaseUser()?.photoUrl != null) {
                Glide.with(this).load(getFirebaseUser()!!.photoUrl).apply(RequestOptions.circleCropTransform()).into(ivPostDetailCurrentUser)
            }else{
                Glide.with(this).load(R.drawable.userphoto).apply(RequestOptions.circleCropTransform()).into(ivPostDetailCurrentUser)
            }

            tvPostTitle.text = it.getString("title")

            tvPostDetailDesc.text = it.getString("description")

            postKey = it.getString("key")!!
            databaseReference = getFirebaseDatabaseReference(Constants.COMMENTS).child(postKey)

            tvPostDetailDate.text = "Postou no dia ${timestampToString(it.getLong("date"))}"
        }

        configRecyclerView()
    }

    private fun configRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        rvComment.layoutManager = linearLayoutManager
    }

    override fun onStart() {
        super.onStart()
        fetchCommentsFromDatabase()
    }

    private fun fetchCommentsFromDatabase() {
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentList.clear()
                for(commentSnap in dataSnapshot.children) {
                    val comment = commentSnap.getValue(Comment::class.java)
                    commentList.add(comment.let { it!! })
                }
                rvComment.adapter = CommentAdapter(this@PostDetailsActivity,commentList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(Constants.DATABASE_ERROR,databaseError.message)
            }
        })
    }

    private fun timestampToString(time: Long): String? {
        val calendar = Calendar.getInstance(Locale.US)
        calendar.timeInMillis = time
        val format = DateFormat.format("dd/MM/yyyy HH:mm", calendar).toString()
        return format.replace(" ", " às ")
    }

    private fun verifyCommentField(comment : String) : Boolean{

        var fieldIsNotEmpty = true

        if (comment.isEmpty()){
            showViewMessage(nsvPostDetail, this,"Campo comentário está vazio.",true)
            ibPostDetailAddComment.visibility = View.VISIBLE
            fieldIsNotEmpty = false
        }

        return fieldIsNotEmpty
    }
}
