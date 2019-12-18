package com.chsltutorials.blognews.adapter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chsltutorials.blognews.R
import com.chsltutorials.blognews.model.Comment
import kotlinx.android.synthetic.main.adapter_comment_item.view.*
import java.util.*


class CommentAdapter (var context: Context, var comments : MutableList<Comment>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_comment_item,parent,false))

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(comments[position].image).apply(RequestOptions.circleCropTransform()).into(holder.itemView.ivUserComment)
        holder.itemView.tvUsername.text = comments[position].name
        holder.itemView.tvComment.text = comments[position].content
        holder.itemView.tvDateComment.text = getTimeFromTimestamp(comments[position].timestamp as Long)
    }

    private fun getTimeFromTimestamp(time: Long): String? {
        val calendar = Calendar.getInstance(Locale.US)
        calendar.timeInMillis = time
        val format = DateFormat.format("dd/MM/yyyy HH:mm", calendar).toString()
        return format.replace(" ", " às ")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}