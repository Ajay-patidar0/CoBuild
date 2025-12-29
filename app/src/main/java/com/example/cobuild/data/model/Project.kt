package com.example.cobuild.data.model

data class Project(
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val title: String = "",
    val description: String = "",
    val goal: String = "",
    val skills: List<String> = emptyList(),
    val link: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
