package com.chsltutorials.blognews.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.adapter.PostAdapter
import com.chsltutorials.blognews.model.Post
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    var postList: MutableList<Post> = ArrayList()
    lateinit var databaseReference : DatabaseReference
    lateinit var firebaseDatabase : FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        rvPost.layoutManager = layoutManager
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Publicações")
    }

    override fun onStart() {
        super.onStart()
        if (postList.size > 0) {
            postList.clear()
        }

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnap in dataSnapshot.children){
                    val post = postSnap.getValue(Post::class.java)
                    postList.add(post.let { it!! })
                }
                rvPost.adapter = PostAdapter(context!!,postList)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ERRO NO DATABASE",databaseError.message)
            }

        })
    }


}
