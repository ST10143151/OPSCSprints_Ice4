package com.fake.opscsprints

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// Firebase imports temporarily disabled
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.firestore.FirebaseFirestore
// import com.google.firebase.firestore.ListenerRegistration
// import com.google.firebase.firestore.Query
import java.util.UUID

private const val TAG = "CommentUtils"

// Mock interface to replace Firebase's ListenerRegistration
interface MockListenerRegistration {
    fun remove()
}

/**
 * Sets up a mock listener for comments (Firebase temporarily disabled)
 */
fun setupCommentListener(postId: String, recyclerView: RecyclerView): MockListenerRegistration {
    val commentsList = mutableListOf<Comment>()
    val adapter = CommentAdapter(commentsList)
    
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    recyclerView.adapter = adapter
    
    // Add mock data
    commentsList.add(Comment(
        id = "mock1",
        postId = postId,
        authorId = "user123",
        text = "This is a mock comment for development",
        timestamp = System.currentTimeMillis()
    ))
    
    adapter.notifyDataSetChanged()
    
    // Return mock registration
    return object : MockListenerRegistration {
        override fun remove() {
            Log.d(TAG, "Mock listener removed")
        }
    }
}

/**
 * Mock function for sending comments (Firebase temporarily disabled)
 */
fun sendComment(postId: String, text: String): Boolean {
    Log.d(TAG, "Mock comment sent: $text")
    return true
}