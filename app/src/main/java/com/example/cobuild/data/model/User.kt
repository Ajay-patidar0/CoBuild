package com.example.cobuild.data.model


data class User(
    val userId: String = "",
    val name: String = "",
    val bio: String = "",
    val skills: List<String> = emptyList(),
    val role: String = "",
    val profileImageUrl: String = ""
)
