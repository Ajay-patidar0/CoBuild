package com.example.cobuild.data.model

data class Project(
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",

    val title: String = "",
    val description: String? = null,
    val goal: String? = null,

    val skills: List<String> = emptyList(),

    val timeline: String? = null,
    val teamSize: String? = null,
    val projectType: String? = null,
    val commitmentLevel: String? = null,
    val experienceLevel: String? = null,

    val link: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)


