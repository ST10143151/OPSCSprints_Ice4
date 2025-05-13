package com.fake.opscsprints.databinding

import android.view.View
import android.widget.TextView

// Temporary binding class to help with compilation
class ItemCommentBinding {
    val root: View = View(null)
    val commentText: TextView = TextView(null)
    val commentAuthor: TextView = TextView(null)
    val commentTimestamp: TextView = TextView(null)
    
    companion object {
        fun inflate(inflater: Any, parent: Any?, attachToRoot: Boolean): ItemCommentBinding {
            return ItemCommentBinding()
        }
    }
}