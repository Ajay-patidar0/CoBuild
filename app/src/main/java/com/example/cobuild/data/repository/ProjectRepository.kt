//package com.example.cobuild.data.repository
//
//import com.example.cobuild.data.model.Project
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class ProjectRepository {
//
//    private val firestore = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//
//    fun addProject(
//        title: String,
//        description: String,
//        goal: String,
//        skills: String,
//        link: String,
//        onResult: (Boolean) -> Unit
//    ) {
//        val user = auth.currentUser ?: run {
//            onResult(false)
//            return
//        }
//
//        val projectRef = firestore.collection("projects").document()
//
//        val project = Project(
//            id = projectRef.id,
//            title = title,
//            description = description,
//            goal = goal,
//            skills = skills.split(",").map { it.trim() },
//            link = link,
//            ownerId = user.uid,
//            ownerName = user.displayName ?: "Anonymous"
//        )
//
//        projectRef
//            .set(project)
//            .addOnSuccessListener { onResult(true) }
//            .addOnFailureListener { onResult(false) }
//    }
//
//    fun fetchAllProjects(
//        onResult: (List<Project>) -> Unit
//    ) {
//        firestore.collection("projects")
//            .orderBy("createdAt")
//            .get()
//            .addOnSuccessListener { snapshot ->
//                val projects = snapshot.toObjects(Project::class.java)
//                onResult(projects)
//            }
//            .addOnFailureListener {
//                onResult(emptyList())
//            }
//    }
//}

package com.example.cobuild.data.repository

import com.example.cobuild.data.model.Project
import com.google.firebase.firestore.FirebaseFirestore

class ProjectRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val projectCollection = firestore.collection("projects")

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

        val projectId = projectCollection.document().id

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
            link = link,
            createdAt = System.currentTimeMillis()
        )

        projectCollection
            .document(projectId)
            .set(project)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun fetchAllProjects(onResult: (List<Project>) -> Unit) {
        projectCollection
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->
                val projects = snapshot.toObjects(Project::class.java)
                onResult(projects)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
