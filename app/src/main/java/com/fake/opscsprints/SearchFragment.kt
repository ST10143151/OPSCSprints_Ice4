package com.fake.opscsprints

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fake.opscsprints.databinding.FragmentSearchBinding
import com.fake.opscsprints.databinding.ItemUserBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val postList = mutableListOf<PostModel>()
    private val userList = mutableListOf<UserModel>()
    private lateinit var postAdapter: PostAdapter
    private lateinit var userAdapter: UserAdapter
    
    // Track all listeners to clean up
    private val listeners = mutableListOf<ListenerRegistration>()
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up all listeners to prevent memory leaks
        listeners.forEach { it.remove() }
        listeners.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up adapters
        postAdapter = PostAdapter(postList)
        userAdapter = UserAdapter(userList)

        // Set default adapter (Users)
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchRecyclerView.adapter = userAdapter

        // Set up tab selection listener
        binding.searchTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        // Users tab
                        binding.searchRecyclerView.adapter = userAdapter
                        binding.searchInputLayout.hint = "Search users..."
                        performSearch(binding.searchEditText.text.toString(), isUserSearch = true)
                    }
                    1 -> {
                        // Posts tab
                        binding.searchRecyclerView.adapter = postAdapter
                        binding.searchInputLayout.hint = "Search posts by tags or keywords..."
                        performSearch(binding.searchEditText.text.toString(), isUserSearch = false)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Set up search input listener
        binding.searchEditText.addTextChangedListener {
            val query = it.toString().trim()
            val isUserSearch = binding.searchTabLayout.selectedTabPosition == 0
            performSearch(query, isUserSearch)
        }

        // Set up pull-to-refresh
        binding.searchSwipeRefresh.setOnRefreshListener {
            val query = binding.searchEditText.text.toString().trim()
            val isUserSearch = binding.searchTabLayout.selectedTabPosition == 0
            performSearch(query, isUserSearch)
        }
        
        // Set up floating action button
        binding.fabCreatePost.setOnClickListener {
            startActivity(Intent(requireContext(), PostActivity::class.java))
        }
    }

    private fun performSearch(query: String, isUserSearch: Boolean) {
        if (query.isBlank()) {
            // Clear results if the query is empty
            if (isUserSearch) {
                userList.clear()
                userAdapter.notifyDataSetChanged()
            } else {
                postList.clear()
                postAdapter.notifyDataSetChanged()
            }
            binding.searchSwipeRefresh.isRefreshing = false
            return
        }

        if (isUserSearch) {
            // Search for users
            binding.searchSwipeRefresh.isRefreshing = true
            
            // First try to search by display name using real-time listener
            val listener = FirebaseFirestore.getInstance().collection("users")
                .whereGreaterThanOrEqualTo("displayNameLower", query.lowercase())
                .whereLessThanOrEqualTo("displayNameLower", query.lowercase() + "\uf8ff")
                .limit(20)
                .addSnapshotListener { documents, error ->
                    
            // Add listener to list for cleanup
            listeners.add(listener)
                    if (error != null) {
                        binding.searchSwipeRefresh.isRefreshing = false
                        return@addSnapshotListener
                    }
                    
                    if (documents != null) {
                        userList.clear()
                        for (document in documents) {
                            val user = document.toObject(UserModel::class.java)
                            userList.add(user)
                        }
                        
                        // If no results by display name, try email
                        if (userList.isEmpty() && query.contains("@")) {
                            searchUsersByEmail(query)
                        } else {
                            userAdapter.notifyDataSetChanged()
                            binding.searchSwipeRefresh.isRefreshing = false
                        }
                    } else {
                        binding.searchSwipeRefresh.isRefreshing = false
                    }
                }
        }
        } else {
            // Search for posts
            binding.searchSwipeRefresh.isRefreshing = true
            
            // Use snapshot listener for real-time updates
            val listener = FirebaseFirestore.getInstance().collection("posts")
                .orderBy("timestamp")
                .addSnapshotListener { documents, error ->
                
            // Add listener to list for cleanup
            listeners.add(listener)
                    if (error != null) {
                        binding.searchSwipeRefresh.isRefreshing = false
                        return@addSnapshotListener
                    }
                    
                    if (documents != null) {
                        postList.clear()
                        for (document in documents) {
                            val post = document.toObject(PostModel::class.java)
                            // Search in caption, tags, or username
                            if (post.caption.contains(query, ignoreCase = true) || 
                                post.tags?.any { it.contains(query, ignoreCase = true) } == true ||
                                post.username.contains(query, ignoreCase = true)) {
                                postList.add(post)
                            }
                        }
                        postAdapter.notifyDataSetChanged()
                    }
                    
                    binding.searchSwipeRefresh.isRefreshing = false
                }
        }
    }

    private fun searchUsersByEmail(email: String) {
        val listener = FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("email", email)
            .limit(5)
            .addSnapshotListener { documents, error ->
            
        // Add to listener list for cleanup
        listeners.add(listener)
                if (error != null) {
                    binding.searchSwipeRefresh.isRefreshing = false
                    return@addSnapshotListener
                }
                
                if (documents != null) {
                    for (document in documents) {
                        val user = document.toObject(UserModel::class.java)
                        userList.add(user)
                    }
                    userAdapter.notifyDataSetChanged()
                }
                
                binding.searchSwipeRefresh.isRefreshing = false
            }
    }
    
    // Inner class for user search results
    inner class UserAdapter(private val users: List<UserModel>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
        
        inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return UserViewHolder(binding)
        }
        
        override fun getItemCount() = users.size
        
        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = users[position]
            holder.binding.userName.text = user.displayName
            holder.binding.userEmail.text = user.email
            
            // Load user avatar if available
            if (user.photoUrl.isNotEmpty()) {
                // In a real app, you would use Glide or Picasso here
                // Glide.with(holder.itemView).load(user.photoUrl).into(holder.binding.userAvatar)
            }
            
            // Handle user selection
            holder.itemView.setOnClickListener {
                // Navigate to user profile
                val intent = Intent(requireContext(), ProfileActivity::class.java)
                intent.putExtra("USER_ID", user.uid)
                startActivity(intent)
            }
        }
    }
}

// Data class for user model
data class UserModel(
    val uid: String = "",
    val displayName: String = "",
    val displayNameLower: String = "",
    val email: String = "",
    val photoUrl: String = ""
)