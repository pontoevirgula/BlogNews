package com.chsltutorials.blognews.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.activity.PostDetailsActivity
import com.chsltutorials.blognews.model.Post
import kotlinx.android.synthetic.main.adapter_post_item.view.*

class PostAdapter(var context: Context, var posts : MutableList<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_post_item,parent,false))

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvRowPostTitle.text = posts[position].title
        Glide.with(context).load(posts[position].pictures).into(holder.itemView.ivRowPost)
        if (posts[position].userPhoto != null){
            Glide.with(context).load(posts[position].userPhoto).apply(RequestOptions.circleCropTransform()).into(holder.itemView.ivRowPostProfile)
        }else{
            Glide.with(context).load(R.drawable.userphoto).apply(RequestOptions.circleCropTransform()).into(holder.itemView.ivRowPostProfile)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        init {
            itemView.setOnClickListener {
                val intentPostDetailsActivity = Intent(it.context,PostDetailsActivity::class.java)
                intentPostDetailsActivity.putExtra("key",posts[adapterPosition].postKey)
                intentPostDetailsActivity.putExtra("title",posts[adapterPosition].title)
                intentPostDetailsActivity.putExtra("description",posts[adapterPosition].description)
                intentPostDetailsActivity.putExtra("pictures",posts[adapterPosition].pictures)
                intentPostDetailsActivity.putExtra("userPhoto",posts[adapterPosition].userPhoto)
                intentPostDetailsActivity.putExtra("date",posts[adapterPosition].timestamp as Long)
                //TODO SERA ADD DEPOIS "USERNAME" NO OBJETO POST
                //intentPostDetailsActivity.putExtra("userName",posts[adapterPosition].userName)
                context.startActivity(intentPostDetailsActivity)
            }
        }
    }
}

