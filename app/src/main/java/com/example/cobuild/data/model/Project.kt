package com.example.cobuild.data.model

data class Project(
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",

    val title: String = "",
    val description: String = "",
    val goal: String = "",

    val skills: List<String> = emptyList(),

    val timeline: String = "",
    val teamSize: String = "",
    val projectType: String = "",
    val commitmentLevel: String = "",
    val experienceLevel: String = "",

    val link: String? = null,

    val createdAt: Long = 0L,

    val status: ProjectStatus = ProjectStatus.YET_TO_START
)

enum class ProjectStatus {
    YET_TO_START,
    IN_PROGRESS,
    COMPLETED
}
