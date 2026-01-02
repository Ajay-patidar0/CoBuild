package com.example.cobuild.data.repository

import com.example.cobuild.data.model.Project
import com.google.firebase.firestore.FirebaseFirestore

class ProjectRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun addProject(
        ownerId: String,
        ownerName: String,
        title: String,
        description: String,
        goal: String,
        skills: List<String>,
        timeline: String,
        teamSize: String,
        projectType: String,
        commitmentLevel: String,
        experienceLevel: String,
        link: String?,
        onResult: (Boolean) -> Unit
    ) {
        val projectId = firestore.collection("projects").document().id

        val project = Project(
            id = projectId,
            ownerId = ownerId,
            ownerName = ownerName,
            title = title,
            description = description,
            goal = goal,
            skills = skills,
            timeline = timeline,
            teamSize = teamSize,
            projectType = projectType,
            commitmentLevel = commitmentLevel,
            experienceLevel = experienceLevel,
            link = link
        )

        firestore.collection("projects")
            .document(projectId)
            .set(project)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun fetchAllProjects(
        onResult: (List<Project>) -> Unit
    ) {
        firestore.collection("projects")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.toObjects(Project::class.java))
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
