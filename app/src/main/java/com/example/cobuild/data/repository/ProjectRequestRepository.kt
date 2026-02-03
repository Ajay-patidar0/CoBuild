package com.example.cobuild.data.repository

import com.example.cobuild.data.model.Project
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProjectRequestRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun sendJoinRequest(
        project: Project,
        onResult: (Boolean) -> Unit
    ) {
        val user = auth.currentUser ?: return onResult(false)

        val docRef = firestore.collection("project_requests").document()

        val requestData = mapOf(
            "requestId" to docRef.id,
            "projectId" to project.id,
            "projectTitle" to project.title,
            "ownerId" to project.ownerId,
            "requesterId" to user.uid,
            "requesterName" to (user.displayName ?: "User"),
            "status" to "pending",
            "createdAt" to Timestamp.now(),
            "timeline" to project.timeline,
            "teamSize" to project.teamSize,
            "projectType" to project.projectType,
            "commitmentLevel" to project.commitmentLevel,
            "experienceLevel" to project.experienceLevel
        )

        docRef.set(requestData)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
