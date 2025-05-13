package com.fake.loginregistration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.loginregistration.databinding.FragmentFeedBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private val postList = mutableListOf<PostModel>()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = PostAdapter(postList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Set up pull-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchPosts()
        }
        
        // Set up floating action button
        binding.fabCreatePost.setOnClickListener {
            startActivity(Intent(requireContext(), PostActivity::class.java))
        }

        fetchPosts()
    }

    private var postsListener: ListenerRegistration? = null
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Remove Firestore listener to prevent memory leaks
        postsListener?.remove()
    }
    
    private fun fetchPosts() {
        // Show refresh indicator
        binding.swipeRefreshLayout.isRefreshing = true
        
        // Remove previous listener if any
        postsListener?.remove()
        
        // Set up real-time listener for posts
        postsListener = FirebaseFirestore.getInstance().collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50) // Limit for performance
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    return@addSnapshotListener
                }
                
                if (snapshots != null) {
                    postList.clear()
                    for (document in snapshots) {
                        val post = document.toObject(PostModel::class.java)
                        postList.add(post)
                    }
                    adapter.notifyDataSetChanged()
                }
                
                // Hide refresh indicator
                binding.swipeRefreshLayout.isRefreshing = false
            }
    }
}
