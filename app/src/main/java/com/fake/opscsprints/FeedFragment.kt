package com.fake.opscsprints

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

// Placeholder fragment until Firebase is re-enabled
class FeedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Return a simple view until database functionality is restored
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }
}