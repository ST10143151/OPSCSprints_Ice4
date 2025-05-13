package com.fake.opscsprints

data class PostModel(
    val postId: String = "",
    val imageUrl: String = "",
    val imageBase64: String = "",
    val caption: String = "",
    val userId: String = "",
    val username: String = "",
    val timestamp: Long = 0L,
    val likes: Int = 0,
    val commentCount: Int = 0,
    val tags: List<String>? = null
)
