package com.example.cobuild.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobuild.data.model.Project
import com.example.cobuild.data.repository.ProjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {

    private val repository = ProjectRepository()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /* -------------------- UI STATE -------------------- */

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    /* -------------------- PROJECT DATA -------------------- */

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects = _projects.asStateFlow()

    /* -------------------- SEARCH -------------------- */

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    /**
     * Filter projects based on search query
     * Matches: title, description, ownerName, skills
     */
    val filteredProjects: StateFlow<List<Project>> =
        combine(_projects, _searchQuery) { projects, query ->

            if (query.isBlank()) {
                projects
            } else {
                val q = query.trim().lowercase()

                projects.filter { project ->
                    project.title.lowercase().contains(q) ||
                            project.description.lowercase().contains(q) ||
                            project.ownerName.lowercase().contains(q) ||
                            project.skills.any { it.lowercase().contains(q) }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /* -------------------- ADD PROJECT -------------------- */

    fun addProject(
        title: String,
        description: String,
        goal: String,
        skills: String,
        timeline: String,
        teamSize: String,
        projectType: String,
        commitmentLevel: String,
        experienceLevel: String,
        link: String
    ) {
        val user = auth.currentUser ?: return

        _isLoading.value = true
        _isSuccess.value = false

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->

                val ownerName =
                    doc.getString("name")
                        ?: doc.getString("username")
                        ?: "Anonymous"

                val skillsList = skills.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                repository.addProject(
                    ownerId = user.uid,
                    ownerName = ownerName,
                    title = title.trim(),
                    description = description.trim(),
                    goal = goal.trim(),
                    skills = skillsList,
                    timeline = timeline.trim(),
                    teamSize = teamSize.trim(),
                    projectType = projectType.trim(),
                    commitmentLevel = commitmentLevel.trim(),
                    experienceLevel = experienceLevel.trim(),
                    link = link.takeIf { it.isNotBlank() }
                ) { success ->
                    _isLoading.value = false
                    _isSuccess.value = success

                    if (success) {
                        loadProjects() // refresh list after adding
                    }
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _isSuccess.value = false
            }
    }

    fun requestToJoinProject(
        project: Project,
        onResult: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "User not logged in")
            return
        }

        val requestRef = firestore.collection("project_requests").document()
        val requestId = requestRef.id

        val requestData = hashMapOf(
            "requestId" to requestId,
            "projectId" to project.id,
            "projectTitle" to project.title,
            "projectType" to project.projectType,
            "ownerId" to project.ownerId,

            "requesterId" to user.uid,
            "requesterName" to (user.displayName ?: "Anonymous"),

            "teamSize" to project.teamSize,
            "timeline" to project.timeline,
            "commitment" to project.commitmentLevel,
            "experienceLevel" to project.experienceLevel,

            "status" to "pending",
            "createdAt" to System.currentTimeMillis()
        )

        // 🔒 Prevent duplicate requests
        firestore.collection("project_requests")
            .whereEqualTo("projectId", project.id)
            .whereEqualTo("requesterId", user.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    onResult(false, "Already requested")
                } else {
                    requestRef.set(requestData)
                        .addOnSuccessListener {
                            onResult(true, "Request sent")
                        }
                        .addOnFailureListener {
                            onResult(false, "Failed to send request")
                        }
                }
            }
    }
    /* -------------------- LOAD PROJECTS -------------------- */

    fun loadProjects() {
        viewModelScope.launch {
            repository.fetchAllProjects { list ->
                _projects.value = list
            }
        }
    }
}