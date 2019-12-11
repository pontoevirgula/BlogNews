package com.chsltutorials.blognews.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.model.Post
import kotlinx.android.synthetic.main.adapter_post_item.view.*

class PostAdapter(var context: Context, var posts : MutableList<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_post_item,parent,false))

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvRowTitle.text = posts[position].title
        Glide.with(context).load(posts[position].pictures).into(holder.itemView.ivRowPost)
        Glide.with(context).load(posts[position].userPhoto).apply(RequestOptions.circleCropTransform()).into(holder.itemView.ivRowPostProfile)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

