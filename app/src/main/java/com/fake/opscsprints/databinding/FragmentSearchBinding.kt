package com.fake.opscsprints.databinding

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout

// Temporary binding class to help with compilation
class FragmentSearchBinding {
    val root: View = View(null)
    val searchRecyclerView: RecyclerView = RecyclerView(null)
    val searchTabLayout: TabLayout = TabLayout(null)
    val searchEditText: EditText = EditText(null)
    val searchInputLayout: TextInputLayout = TextInputLayout(null)
    val searchSwipeRefresh: SwipeRefreshLayout = SwipeRefreshLayout(null)
    val fabCreatePost: FloatingActionButton = FloatingActionButton(null)
    
    companion object {
        fun inflate(inflater: Any, container: Any?, attachToRoot: Boolean): FragmentSearchBinding {
            return FragmentSearchBinding()
        }
    }
}