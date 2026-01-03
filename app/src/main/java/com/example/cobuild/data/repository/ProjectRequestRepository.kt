package com.example.cobuild.data.repository

import com.example.cobuild.data.model.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProjectRequestRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Sends a join request for the given project.
     */
    fun sendJoinRequest(
        project: Project,
        onResult: (Boolean) -> Unit
    ) {
        val user = auth.currentUser ?: return onResult(false)

        val requestId = firestore.collection("project_requests").document().id

        val requestData = hashMapOf(
            "requestId" to requestId,
            "projectId" to project.id,
            "projectTitle" to project.title,
            "ownerId" to project.ownerId,
            "requesterId" to user.uid,
            "requesterName" to (user.displayName ?: "User"),
            "status" to "pending",
            "createdAt" to System.currentTimeMillis(),
            "timeline" to project.timeline,
            "teamSize" to project.teamSize,
            "projectType" to project.projectType,
            "commitment" to project.commitmentLevel,
            "experienceLevel" to project.experienceLevel
        )

        firestore.collection("project_requests")
            .document(requestId)
            .set(requestData)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Fetches a Project by its ID from Firestore.
     */
    suspend fun getProjectById(projectId: String): Project? {
        return try {
            val doc = firestore.collection("projects")
                .document(projectId)
                .get()
                .await()

            if (doc.exists()) {
                Project(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    ownerName = doc.getString("ownerName") ?: "",
                    description = doc.getString("description") ?: "",
                    goal = doc.getString("goal") ?: "",
                    skills = doc.get("skills") as? List<String> ?: emptyList(),
                    timeline = doc.getString("timeline"),
                    teamSize = doc.getString("teamSize"),
                    projectType = doc.getString("projectType"),
                    commitmentLevel = doc.getString("commitmentLevel"),
                    experienceLevel = doc.getString("experienceLevel"),
                    link = doc.getString("link")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
