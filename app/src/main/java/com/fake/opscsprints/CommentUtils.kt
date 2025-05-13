package com.fake.loginregistration

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.UUID

private const val TAG = "CommentUtils"

/**
 * Sets up a real-time listener for comments on a specific post
 * @param postId The ID of the post to listen for comments
 * @param recyclerView The RecyclerView to display comments in
 * @return ListenerRegistration that should be removed in onDestroy/onDestroyView
 */
fun setupCommentListener(postId: String, recyclerView: RecyclerView): ListenerRegistration {
    val db = FirebaseFirestore.getInstance()
    val commentsList = mutableListOf<Comment>()
    val adapter = CommentAdapter(commentsList)
    
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    recyclerView.adapter = adapter
    
    // Set up real-time listener on the comments subcollection
    return db.collection("posts").document(postId).collection("comments")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e(TAG, "Error getting comments: ", error)
                return@addSnapshotListener
            }
            
            snapshots?.let { documents ->
                commentsList.clear()
                for (document in documents) {
                    val comment = document.toObject(Comment::class.java)
                    commentsList.add(comment)
                }
                adapter.notifyDataSetChanged()
            }
        }
}

/**
 * Sends a new comment to a post with atomic update of comment count
 * @param postId The ID of the post to comment on
 * @param text The comment text
 * @return Boolean indicating success
 */
fun sendComment(postId: String, text: String): Boolean {
    val currentUser = FirebaseAuth.getInstance().currentUser ?: return false
    val db = FirebaseFirestore.getInstance()
    
    // Create comment object
    val commentId = UUID.randomUUID().toString()
    val comment = Comment(
        id = commentId,
        postId = postId,
        authorId = currentUser.uid,
        text = text,
        timestamp = System.currentTimeMillis()
    )
    
    // Reference to post document and new comment document
    val postRef = db.collection("posts").document(postId)
    val commentRef = postRef.collection("comments").document(commentId)
    

    val batch = db.batch()
    
    // Add the comment document
    batch.set(commentRef, comment)
    
    // Increment the post's comment count
    batch.update(postRef, "commentCount", com.google.firebase.firestore.FieldValue.increment(1))
    
    // Commit the batch
    batch.commit().addOnSuccessListener {
        Log.d(TAG, "Comment added successfully")
        return@addOnSuccessListener
    }.addOnFailureListener { e ->
        Log.e(TAG, "Error adding comment", e)
        return@addOnFailureListener
    }
    
    return true
}