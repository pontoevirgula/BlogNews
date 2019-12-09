package com.chsltutorials.blognews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.adapter.PostAdapter
import com.chsltutorials.blognews.model.Post
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    lateinit var postAdapter : PostAdapter
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    var postList: MutableList<Post> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Publicações")
    }

    override fun onStart() {
        super.onStart()
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnap in dataSnapshot.children){
                    val post = postSnap.getValue(Post::class.java)
                    postList.add(post.let { it!! })
                }
                context.let {
                    rvPost.layoutManager = LinearLayoutManager(it)
                    rvPost.adapter = PostAdapter(it!!,postList)
                }


            }
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }


}
