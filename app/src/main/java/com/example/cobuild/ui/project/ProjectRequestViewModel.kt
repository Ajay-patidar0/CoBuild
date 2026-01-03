package com.example.cobuild.ui.project

import androidx.lifecycle.ViewModel
import com.example.cobuild.data.model.Project
import com.example.cobuild.data.repository.ProjectRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow

class ProjectRequestViewModel : ViewModel() {

    private val repository = ProjectRequestRepository()

    // Tracks if the current user has already requested to join this project
    private val _isRequested = MutableStateFlow(false)
    val isRequested: StateFlow<Boolean> = _isRequested.asStateFlow()

    /**
     * Sends a join request for the given project.
     * Updates `_isRequested` state to true if the request succeeds.
     */
    fun requestToJoin(project: Project) {
        repository.sendJoinRequest(project) { success ->
            if (success) {
                _isRequested.value = true
            }
        }
    }

    /**
     * Optional: Call this if you want to reset the state (e.g., when navigating away)
     */
    fun resetRequestState() {
        _isRequested.value = false
    }

    /**
     * Fetches a project by its ID.
     * Returns a Flow that emits the Project (or null if not found).
     */
    fun getProjectById(projectId: String): Flow<Project?> = flow {
        val project = repository.getProjectById(projectId) // implement in repository
        emit(project)
    }
}
