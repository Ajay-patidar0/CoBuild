package com.example.cobuild.ui.project

import androidx.lifecycle.ViewModel
import com.example.cobuild.data.model.Project
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProjectListViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _joinedProjects = MutableStateFlow<List<Project>>(emptyList())
    val joinedProjects: StateFlow<List<Project>> = _joinedProjects

    private val _postedProjects = MutableStateFlow<List<Project>>(emptyList())
    val postedProjects: StateFlow<List<Project>> = _postedProjects

    fun loadProjects(userId: String) {

        // Joined Projects
        firestore.collection("projects")
            .whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, _ ->
                _joinedProjects.value =
                    snapshot?.toObjects(Project::class.java) ?: emptyList()
            }

        // Posted Projects
        firestore.collection("projects")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, _ ->
                _postedProjects.value =
                    snapshot?.toObjects(Project::class.java) ?: emptyList()
            }
    }
}
