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

        // ✅ JOINED PROJECTS (REALTIME)
        firestore.collection("projects")
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, _ ->
                _joinedProjects.value = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(Project::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
            }

        // ✅ POSTED PROJECTS (REALTIME)
        firestore.collection("projects")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, _ ->
                _postedProjects.value = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(Project::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
            }
    }
}
