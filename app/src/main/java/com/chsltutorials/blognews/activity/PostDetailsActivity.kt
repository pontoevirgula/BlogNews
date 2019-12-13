package com.chsltutorials.blognews.activity

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
import com.chsltutorials.blognews.util.showMessageAlert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_post_details.*
import java.util.*
import kotlin.collections.ArrayList


class PostDetailsActivity : BaseActivity() {

    private var commentList: MutableList<Comment> = ArrayList()
    lateinit var postKey : String
    lateinit var commentField : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        //deixar barra de status transparente
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        supportActionBar!!.hide()


        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser!!
        firebaseDatabase = FirebaseDatabase.getInstance()

        ibPostDetailAddComment.setOnClickListener {
            it.visibility = View.INVISIBLE
            commentField = etPostDetailComment.text.toString()
            if (verifyCommentField(commentField)) {
                databaseReference = firebaseDatabase.getReference("Comentários").child(postKey).push()
                val comment = Comment(
                    id = currentUser.uid,
                    name = currentUser.displayName!!,
                    content = commentField,
                    image = currentUser.photoUrl.toString()
                )
                databaseReference.setValue(comment)
                    .addOnSuccessListener {
                        showMessageAlert(this, "Comentário adicionado")
                        etPostDetailComment.setText("")
                        ibPostDetailAddComment.visibility = View.VISIBLE
                    }
                    .addOnFailureListener { e ->
                        showMessageAlert(this, "Falha ao adicionar comentário : ${e.message}")
                        ibPostDetailAddComment.visibility = View.VISIBLE
                    }
            }
        }

        intent?.extras?.let {
            val postImage = it.getString("pictures")
            Glide.with(this).load(postImage).into(ivPostDetail)

            val userPhotoPost = it.getString("userPhoto")
            Glide.with(this).load(userPhotoPost).apply(RequestOptions.circleCropTransform()).into(ivPostDetailUserPhoto)

            Glide.with(this).load(currentUser.photoUrl).apply(RequestOptions.circleCropTransform()).into(ivPostDetailCurrentUser)

            tvPostTitle.text = it.getString("title")

            tvPostDetailDesc.text = it.getString("description")

            postKey = it.getString("key")!!

            tvPostDetailDate.text = timestampToString(it.getLong("date"))
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        rvComment.layoutManager = layoutManager
        //rvComment.addItemDecoration(SimpleDividerItemDecoration(this))
    }

    override fun onStart() {
        super.onStart()
        if (commentList.size > 0) {
            commentList.clear()
        }
        initList()
    }

    private fun initList() {
        databaseReference = firebaseDatabase.getReference("Comentários").child(postKey)
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(commentSnap in dataSnapshot.children) {
                    val comment = commentSnap.getValue(Comment::class.java)
                    commentList.add(comment.let { it!! })
                }
                rvComment.adapter = CommentAdapter(this@PostDetailsActivity,commentList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ERRO NO DATABASE",databaseError.message)
            }
        })
    }

    private fun timestampToString(time: Long): String? {
        val calendar = Calendar.getInstance(Locale.US)
        calendar.timeInMillis = time
        return DateFormat.format("dd-MM-yyyy", calendar).toString()
    }

    private fun verifyCommentField(comment : String) : Boolean{

        var fieldIsNotEmpty = true

        if (comment.isEmpty()){
            showMessageAlert(applicationContext,"Campo comentário está vazio.")
            ibPostDetailAddComment.visibility = View.VISIBLE
            fieldIsNotEmpty = false
        }

        return fieldIsNotEmpty
    }
}
