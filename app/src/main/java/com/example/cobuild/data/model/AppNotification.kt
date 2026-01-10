package com.example.cobuild.data.model

data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val projectId: String = "",
    val projectTitle: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
