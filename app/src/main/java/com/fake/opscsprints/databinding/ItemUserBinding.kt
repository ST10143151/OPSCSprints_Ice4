package com.fake.opscsprints.databinding

import android.view.View
import android.widget.TextView

// Temporary binding class to help with compilation
class ItemUserBinding {
    val root: View = View(null)
    val userName: TextView = TextView(null)
    val userEmail: TextView = TextView(null)
    
    companion object {
        fun inflate(inflater: Any, parent: Any?, attachToRoot: Boolean): ItemUserBinding {
            return ItemUserBinding()
        }
    }
}