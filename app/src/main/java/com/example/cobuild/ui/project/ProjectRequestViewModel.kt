package com.example.cobuild.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobuild.data.model.Project
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProjectRequestViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _isRequested = MutableStateFlow(false)
    val isRequested: StateFlow<Boolean> = _isRequested

    // Load project by ID
    fun getProjectById(projectId: String) = flow<Project?> {
        val snapshot = firestore.collection("projects")
            .document(projectId)
            .get()
            .await()

        emit(snapshot.toObject(Project::class.java))
    }

    // Check if user has already requested
    private suspend fun hasRequested(projectId: String, userId: String): Boolean {
        val query = firestore.collection("project_requests")
            .whereEqualTo("projectId", projectId)
            .whereEqualTo("requesterId", userId)
            .whereEqualTo("status", "pending")
            .get()
            .await()
        return !query.isEmpty
    }

    // Request to join project
    fun requestToJoin(project: Project, requesterName: String) {
        val user = auth.currentUser ?: return

        viewModelScope.launch {
            val alreadyRequested = hasRequested(project.id, user.uid)
            if (alreadyRequested) {
                _isRequested.value = true
                return@launch
            }

            val docRef = firestore.collection("project_requests").document()
            val data = mapOf(
                "requestId" to docRef.id,
                "projectId" to project.id,
                "projectTitle" to project.title,
                "ownerId" to project.ownerId,
                "requesterId" to user.uid,
                "requesterName" to requesterName, // Use name from onboarding
                "status" to "pending",
                "createdAt" to Timestamp.now(),
                "timeline" to project.timeline,
                "teamSize" to project.teamSize,
                "projectType" to project.projectType,
                "commitmentLevel" to project.commitmentLevel,
                "experienceLevel" to project.experienceLevel
            )

            docRef.set(data).await()
            _isRequested.value = true
        }
    }

    // Initialize _isRequested when screen loads
    fun checkIfRequested(projectId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isRequested.value = hasRequested(projectId, userId)
        }
    }
}
