package com.example.cobuild.ui.project

import androidx.lifecycle.ViewModel
import com.example.cobuild.data.model.Project
import com.example.cobuild.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProjectViewModel : ViewModel() {

    private val repository = ProjectRepository()

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
        link: String
    ) {
        _isLoading.value = true
        repository.addProject(
            title,
            description,
            goal,
            skills,
            link
        ) { success ->
            _isLoading.value = false
            _isSuccess.value = success
        }
    }

    fun loadProjects() {
        repository.fetchAllProjects {
            _projects.value = it
        }
    }
}
