package com.fake.opscsprints

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

// Simple placeholder adapter until Firebase is re-enabled
class PostAdapter(private val posts: List<PostModel>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    
    inner class PostViewHolder(private val parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
    )
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(parent)
    }
    
    override fun getItemCount(): Int = posts.size
    
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // No binding needed for placeholder
    }
}