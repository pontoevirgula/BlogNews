package com.chsltutorials.blognews.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.adapter.PostAdapter
import com.chsltutorials.blognews.model.Post
import com.chsltutorials.blognews.util.Constants
import com.chsltutorials.blognews.util.FirebaseUtils.getFirebaseDatabaseReference
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    var postList: MutableList<Post> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        rvPost.layoutManager = linearLayoutManager
    }

    override fun onStart() {
        super.onStart()
        fetchPostsFromDatabase()
    }

    private fun fetchPostsFromDatabase() {
        getFirebaseDatabaseReference(Constants.PUBLISHEDS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for (postSnap in dataSnapshot.children) {
                    val post = postSnap.getValue(Post::class.java)
                    postList.add(post.let { it!! })
                }
                rvPost.adapter = PostAdapter(context!!, postList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(Constants.DATABASE_ERROR, databaseError.message)
            }

        })
    }


}
