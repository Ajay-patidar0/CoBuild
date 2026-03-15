//package com.example.cobuild.ui.project
//
//import androidx.lifecycle.ViewModel
//import com.example.cobuild.data.model.Project
//import com.example.cobuild.data.repository.ProjectRepository
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//class ProjectViewModel : ViewModel() {
//
//    private val repository = ProjectRepository()
//    private val auth = FirebaseAuth.getInstance()
//    private val firestore = FirebaseFirestore.getInstance()
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading = _isLoading.asStateFlow()
//
//    private val _isSuccess = MutableStateFlow(false)
//    val isSuccess = _isSuccess.asStateFlow()
//
//    private val _projects = MutableStateFlow<List<Project>>(emptyList())
//    val projects = _projects.asStateFlow()
//
//    fun addProject(
//        title: String,
//        description: String,
//        goal: String,
//        skills: String,
//        timeline: String,
//        teamSize: String,
//        projectType: String,
//        commitmentLevel: String,
//        experienceLevel: String,
//        link: String
//    ) {
//        val user = auth.currentUser ?: return
//
//        _isLoading.value = true
//        _isSuccess.value = false
//
//        firestore.collection("users")
//            .document(user.uid)
//            .get()
//            .addOnSuccessListener { doc ->
//
//                val ownerName =
//                    doc.getString("name")
//                        ?: doc.getString("username")
//                        ?: "Anonymous"
//
//                val skillsList = skills.split(",")
//                    .map { it.trim() }
//                    .filter { it.isNotEmpty() }
//
//                repository.addProject(
//                    ownerId = user.uid,
//                    ownerName = ownerName,
//                    title = title.trim(),
//                    description = description.trim(),
//                    goal = goal.trim(),
//                    skills = skillsList,
//                    timeline = timeline.trim(),
//                    teamSize = teamSize.trim(),
//                    projectType = projectType.trim(),
//                    commitmentLevel = commitmentLevel.trim(),
//                    experienceLevel = experienceLevel.trim(),
//                    link = link.takeIf { it.isNotBlank() },
//                ) { success ->
//                    _isLoading.value = false
//                    _isSuccess.value = success
//                }
//            }
//            .addOnFailureListener {
//                _isLoading.value = false
//                _isSuccess.value = false
//            }
//    }
//
//    fun loadProjects() {
//        repository.fetchAllProjects { list ->
//            _projects.value = list
//        }
//    }
//}
package com.example.cobuild.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobuild.data.model.Project
import com.example.cobuild.data.repository.ProjectRepository
import com.example.cobuild.home.ProjectFilters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf

class ProjectViewModel : ViewModel() {

    private val repository = ProjectRepository()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /* -------------------- STATES -------------------- */

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects = _projects.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(ProjectFilters())
    val filters = _filters.asStateFlow()

    /* -------------------- USER SKILLS -------------------- */

    private val _userSkills = MutableStateFlow<List<String>>(emptyList())
    val userSkills = _userSkills.asStateFlow()

    fun loadUserSkills(uid: String) {

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                _userSkills.value = doc.get("skills") as? List<String> ?: emptyList()
            }
    }

    /* -------------------- SKILL MATCH LOGIC -------------------- */

    fun calculateSkillMatch(projectSkills: List<String>): Int {

        val skills = userSkills.value

        if (projectSkills.isEmpty()) return 0
        if (skills.isEmpty()) return 0

        val matchCount = projectSkills.count { projectSkill: String ->
            skills.any { userSkill: String ->
                userSkill.equals(projectSkill, ignoreCase = true)
            }
        }

        return ((matchCount.toFloat() / projectSkills.size) * 100).toInt()
    }

    /* -------------------- FILTERED PROJECTS -------------------- */

    val filteredProjects: StateFlow<List<Project>> =
        combine(
            _projects,
            _searchQuery,
            _filters
        ) { projects, query, filters ->

            var result = projects

            // 🔎 SEARCH FILTER
            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                result = result.filter { project ->
                    project.title.lowercase().contains(q) ||
                            project.description.lowercase().contains(q) ||
                            project.ownerName.lowercase().contains(q) ||
                            project.skills.any { it.lowercase().contains(q) }
                }
            }

            // 🎯 COMMITMENT LEVEL
            filters.commitmentLevel?.let {
                result = result.filter { project ->
                    project.commitmentLevel == it
                }
            }

            // 🎯 EXPERIENCE LEVEL
            filters.experienceLevel?.let {
                result = result.filter { project ->
                    project.experienceLevel == it
                }
            }

            // 🎯 PROJECT TYPE
            filters.projectType?.let {
                result = result.filter { project ->
                    project.projectType == it
                }
            }

            // 🎯 STATUS
            filters.status?.let {
                result = result.filter { project ->
                    project.status.name == it
                }
            }

            // 🎯 TIMELINE
            filters.timeline?.let {
                result = result.filter { project ->
                    project.timeline == it
                }
            }

            // 🎯 TEAM SIZE
            filters.teamSize?.let {
                result = result.filter { project ->
                    project.teamSize == it
                }
            }

            // 🎯 SKILLS (multi-select match)
            if (filters.skills.isNotEmpty()) {
                result = result.filter { project ->
                    filters.skills.any { skill ->
                        project.skills.contains(skill)
                    }
                }
            }

            result
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /* -------------------- SEARCH -------------------- */

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /* -------------------- APPLY FILTERS -------------------- */

    fun applyFilters(newFilters: ProjectFilters) {
        _filters.value = newFilters
    }

    fun clearFilters() {
        _filters.value = ProjectFilters()
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

    /* -------------------- LOAD PROJECTS -------------------- */

    fun loadProjects() {
        repository.fetchAllProjects { list ->
            _projects.value = list
        }
    }

    /* -------------------- REQUEST TO JOIN -------------------- */

    fun requestToJoin(project: Project, requesterName: String) {
        val currentUser = auth.currentUser ?: return
        val requesterId = currentUser.uid

        val requestId = firestore.collection("project_requests").document().id

        val requestData = hashMapOf(
            "requestId" to requestId,
            "projectId" to project.id,
            "projectTitle" to project.title,
            "ownerId" to project.ownerId,
            "requesterId" to requesterId,
            "requesterName" to requesterName,
            "status" to "pending",
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("project_requests")
            .document(requestId)
            .set(requestData)
            .addOnSuccessListener {
                createChatIfNotExists(
                    ownerId = project.ownerId,
                    requesterId = requesterId,
                    projectId = project.id
                )
            }
    }

    /* -------------------- CREATE CHAT SAFELY -------------------- */

    private fun createChatIfNotExists(
        ownerId: String,
        requesterId: String,
        projectId: String
    ) {
        firestore.collection("chats")
            .whereArrayContains("participants", ownerId)
            .get()
            .addOnSuccessListener { snapshot ->

                val existingChat = snapshot.documents.firstOrNull { doc ->
                    val participants = doc["participants"] as? List<*>
                    val pid = doc["projectId"] as? String

                    participants?.contains(requesterId) == true &&
                            pid == projectId
                }

                if (existingChat != null) return@addOnSuccessListener

                val chatData = hashMapOf(
                    "participants" to listOf(ownerId, requesterId),
                    "projectId" to projectId,
                    "lastMessage" to "",
                    "lastTimestamp" to System.currentTimeMillis()
                )

                firestore.collection("chats").add(chatData)
            }
    }
}