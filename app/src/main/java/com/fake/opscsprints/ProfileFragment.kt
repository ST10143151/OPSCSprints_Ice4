package com.fake.loginregistration

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fake.loginregistration.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val postList = mutableListOf<PostModel>()
    private lateinit var adapter: PostAdapter

    // Use the new Activity Result API to take a picture (returns a Bitmap preview)
    private val takePicturePreviewLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // Display the captured photo in the ImageView
            binding.ivProfilePicture.setImageBitmap(bitmap)
            // TODO: Optionally upload to Firebase Storage and update user's photoURL
        } else {
            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up profile info
        val user = FirebaseAuth.getInstance().currentUser
        binding.tvDisplayName.text = user?.displayName ?: "No Name"
        binding.tvEmail.text = user?.email ?: "No Email"

        // Set up the recycler view for posts
        adapter = PostAdapter(postList)
        binding.profileRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.profileRecyclerView.adapter = adapter

        // Set up pull-to-refresh
        binding.profileSwipeRefresh.setOnRefreshListener {
            fetchUserPosts()
        }

        // Clicking the profile picture triggers camera preview
        binding.ivProfilePicture.setOnClickListener {
            takePicturePreviewLauncher.launch(null)
        }
        
        // Set up floating action button
        binding.fabCreatePost.setOnClickListener {
            startActivity(Intent(requireContext(), PostActivity::class.java))
        }

        fetchUserPosts()
    }

    private var postsListener: ListenerRegistration? = null
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Remove Firestore listener to prevent memory leaks
        postsListener?.remove()
    }
    
    private fun fetchUserPosts() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        
        // Show refresh indicator
        binding.profileSwipeRefresh.isRefreshing = true
        
        // Remove previous listener if any
        postsListener?.remove()
        
        // Set up real-time listener for user posts
        postsListener = FirebaseFirestore.getInstance().collection("posts")
            .whereEqualTo("userId", user.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    binding.profileSwipeRefresh.isRefreshing = false
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
                binding.profileSwipeRefresh.isRefreshing = false
            }
    }
}