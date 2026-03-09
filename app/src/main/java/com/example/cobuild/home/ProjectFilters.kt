package com.example.cobuild.home

data class ProjectFilters(
    val commitmentLevel: String? = null,
    val experienceLevel: String? = null,
    val projectType: String? = null,
    val status: String? = null,
    val timeline: String? = null,
    val teamSize: String? = null,
    val skills: List<String> = emptyList()
)