package com.fake.loginregistration

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fake.loginregistration.databinding.ItemPostBinding
import com.google.firebase.firestore.FirebaseFirestore

class PostAdapter(private val posts: List<PostModel>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val binding = holder.binding

        binding.postCaption.text = post.caption
        binding.likeCount.text = "${post.likes} Likes"
        binding.commentCount.text = "${post.commentCount} Comments"

        // Decode base64 image and display it
        try {
            val imageBytes = Base64.decode(post.imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.postImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Like button logic
        binding.likeButton.setOnClickListener {
            val docRef = FirebaseFirestore.getInstance().collection("posts").document(post.postId)
            docRef.update("likes", post.likes + 1)
        }
        
        // Comment button logic
        binding.commentButton.setOnClickListener {
            // This would typically launch a CommentsActivity or show a bottom sheet dialog
            // For simplicity, we'll just show a Toast message in this implementation
            android.widget.Toast.makeText(
                binding.root.context,
                "Comments for post ${post.postId}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
}
