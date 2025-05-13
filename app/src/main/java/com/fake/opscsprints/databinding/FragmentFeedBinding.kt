package com.fake.opscsprints.databinding

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Temporary binding class to help with compilation
class FragmentFeedBinding {
    val root: View = View(null)
    val recyclerView: RecyclerView = RecyclerView(null)
    val swipeRefreshLayout: SwipeRefreshLayout = SwipeRefreshLayout(null)
    val fabCreatePost: FloatingActionButton = FloatingActionButton(null)
    
    companion object {
        fun inflate(inflater: Any, container: Any?, attachToRoot: Boolean): FragmentFeedBinding {
            return FragmentFeedBinding()
        }
    }
}