package com.fake.opscsprints

data class Comment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)