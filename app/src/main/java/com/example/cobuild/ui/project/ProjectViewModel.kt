package com.example.cobuild.ui.project

import androidx.lifecycle.ViewModel
import com.example.cobuild.data.model.Project
import com.example.cobuild.data.repository.ProjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProjectViewModel : ViewModel() {

    private val repository = ProjectRepository()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects = _projects.asStateFlow()

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
                    link = link.takeIf { it.isNotBlank() },
                ) { success ->
                    _isLoading.value = false
                    _isSuccess.value = success
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                _isSuccess.value = false
            }
    }

    fun loadProjects() {
        repository.fetchAllProjects { list ->
            _projects.value = list
        }
    }
}
