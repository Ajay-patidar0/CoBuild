//package com.example.cobuild.ui.project
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Link
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.google.firebase.auth.FirebaseAuth
//
//private val PrimaryColor = Color(0xFF4F46E5)
//private val TextPrimary = Color(0xFF1E293B)
//private val TextSecondary = Color(0xFF64748B)
//private val BackgroundColor = Color(0xFFF8FAFC)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProjectDetailScreen(
//    projectId: String,
//    requesterName: String, // Pass the name from onboarding
//    onBackClick: () -> Unit
//) {
//    val viewModel: ProjectRequestViewModel = viewModel()
//    val isRequested by viewModel.isRequested.collectAsState()
//
//    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
//    val project by viewModel.getProjectById(projectId).collectAsState(initial = null)
//    val proj = project
//    val isOwner = proj?.ownerId == currentUserId
//
//    // Check if already requested when screen loads
//    LaunchedEffect(projectId) {
//        viewModel.checkIfRequested(projectId)
//    }
//
//    Scaffold(
//        containerColor = BackgroundColor,
//        topBar = {
//            TopAppBar(
//                title = { Text("Project Details") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, null)
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            if (proj != null && !isOwner) {
//                Surface(tonalElevation = 8.dp) {
//                    Button(
//                        onClick = { viewModel.requestToJoin(proj, requesterName) },
//                        enabled = !isRequested,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp)
//                            .height(56.dp),
//                        shape = RoundedCornerShape(16.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = PrimaryColor,
//                            contentColor = Color.White,
//                            disabledContainerColor = PrimaryColor.copy(alpha = 0.6f)
//                        )
//                    ) {
//                        Text(
//                            text = if (isRequested) "Request Sent" else "Request to Join Project",
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//            }
//        }
//    ) { padding ->
//        if (proj == null) {
//            Box(
//                modifier = Modifier.fillMaxSize().padding(padding),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator(color = PrimaryColor)
//            }
//            return@Scaffold
//        }
//
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(20.dp)
//                .fillMaxSize()
//        ) {
//            Text(proj.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
//            Spacer(Modifier.height(6.dp))
//            Text("by ${proj.ownerName}", color = TextSecondary)
//            Spacer(Modifier.height(16.dp))
//            proj.description?.let { Section("Description", it) }
//            proj.goal?.let { Section("Goal", it) }
//
//            if (proj.skills.isNotEmpty()) {
//                Text("Required Skills", fontWeight = FontWeight.SemiBold)
//                Spacer(Modifier.height(8.dp))
//                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    proj.skills.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
//                }
//                Spacer(Modifier.height(16.dp))
//            }
//
//            proj.link?.takeIf { it.isNotBlank() }?.let {
//                Text("Link", fontWeight = FontWeight.SemiBold)
//                Spacer(Modifier.height(4.dp))
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(Icons.Default.Link, null, tint = TextSecondary)
//                    Spacer(Modifier.width(8.dp))
//                    Text(it, color = TextSecondary)
//                }
//            }
//
//            Spacer(Modifier.height(80.dp))
//        }
//    }
//}
//
//@Composable
//private fun Section(title: String, value: String) {
//    Text(title, fontWeight = FontWeight.SemiBold)
//    Spacer(Modifier.height(4.dp))
//    Text(value, color = TextSecondary)
//    Spacer(Modifier.height(16.dp))
//}
//
//package com.example.cobuild.ui.project
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.horizontalScroll
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.cobuild.data.model.Task
//import com.example.cobuild.utils.taskProgress
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//private val PrimaryColor = Color(0xFF4F46E5)
//private val TextSecondary = Color(0xFF64748B)
//private val BackgroundColor = Color(0xFFF8FAFC)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProjectDetailScreen(
//    projectId: String,
//    requesterName: String,
//    onBackClick: () -> Unit
//) {
//
//    val viewModel: ProjectViewModel = viewModel()
//    val firestore = FirebaseFirestore.getInstance()
//    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
//
//    val projects by viewModel.projects.collectAsState()
//    val proj = projects.firstOrNull { it.id == projectId }
//
//    val isOwner = proj?.ownerId == currentUserId
//    val isMember = proj?.members?.contains(currentUserId) == true
//
//    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
//    var memberNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
//
//    LaunchedEffect(projectId) {
//
//        viewModel.loadProjects()
//
//        firestore.collection("projects")
//            .document(projectId)
//            .collection("tasks")
//            .addSnapshotListener { snapshot, _ ->
//                tasks = snapshot?.documents?.mapNotNull {
//                    it.toObject(Task::class.java)?.copy(id = it.id)
//                } ?: emptyList()
//            }
//
//        firestore.collection("users")
//            .get()
//            .addOnSuccessListener { snapshot ->
//                val map = mutableMapOf<String, String>()
//                snapshot.documents.forEach {
//                    map[it.id] = it.getString("name") ?: "User"
//                }
//                memberNames = map
//            }
//    }
//
//    val yourTasks = tasks.filter { it.assignedTo == currentUserId }
//    val isEmpty = tasks.isEmpty()
//
//    Scaffold(
//        containerColor = BackgroundColor,
//        topBar = {
//            TopAppBar(
//                title = { Text("Project Details") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//
//        if (proj == null) {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//            return@Scaffold
//        }
//
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .padding(20.dp)
//        ) {
//
//            /* ---------- PROJECT INFO ---------- */
//            item {
//                Text(proj.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
//
//                Spacer(Modifier.height(6.dp))
//
//                Text("by ${proj.ownerName}", color = TextSecondary)
//
//                Spacer(Modifier.height(12.dp))
//
//                if (isOwner) RoleBadge("👑 Owner")
//                else if (isMember) RoleBadge("🧑‍💻 Member")
//
//                Spacer(Modifier.height(16.dp))
//
//                proj.description?.let { Section("Description", it) }
//                proj.goal?.let { Section("Goal", it) }
//
//                Spacer(Modifier.height(20.dp))
//            }
//
//            /* ---------- TEAM MEMBERS ---------- */
//            item {
//
//                Text("Team Members", fontWeight = FontWeight.Bold)
//
//                Spacer(Modifier.height(8.dp))
//
//                proj.members.forEach { userId ->
//
//                    Text(
//                        "• ${memberNames[userId] ?: userId}",
//                        color = TextSecondary
//                    )
//                }
//
//                Spacer(Modifier.height(20.dp))
//            }
//
//            /* ---------- EMPTY STATE ---------- */
//            if (isEmpty) {
//
//                item {
//                    EmptyTaskState(projectId, currentUserId)
//                }
//
//            } else {
//
//                /* ---------- KANBAN ---------- */
//                item {
//                    Text("Kanban Board", fontWeight = FontWeight.Bold)
//                    Spacer(Modifier.height(10.dp))
//                }
//
//                item {
//                    KanbanBoard(tasks, projectId, currentUserId, memberNames)
//                }
//
//                /* ---------- TIMELINE ---------- */
//                item {
//                    Spacer(Modifier.height(30.dp))
//                    Text("Timeline", fontWeight = FontWeight.Bold)
//                }
//
//                items(tasks) {
//                    GanttTask(it)
//                }
//
//                /* ---------- YOUR TASKS ---------- */
//                item {
//                    Spacer(Modifier.height(30.dp))
//                    Text("Your Tasks", fontWeight = FontWeight.Bold)
//                }
//
//                items(yourTasks) {
//                    TaskCard(it, currentUserId, projectId, memberNames)
//                }
//
//                /* ---------- ALL TASKS ---------- */
//                item {
//                    Spacer(Modifier.height(30.dp))
//                    Text("All Tasks", fontWeight = FontWeight.Bold)
//                }
//
//                items(tasks) {
//                    TaskCard(it, currentUserId, projectId, memberNames)
//                }
//            }
//        }
//    }
//}
//
///* ---------- EMPTY STATE ---------- */
//@Composable
//fun EmptyTaskState(
//    projectId: String,
//    currentUserId: String
//) {
//
//    var taskTitle by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 80.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Text(
//            "🚀 No Tasks Yet",
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(Modifier.height(10.dp))
//
//        Text(
//            "Create your first task to start",
//            color = TextSecondary
//        )
//
//        Spacer(Modifier.height(20.dp))
//
//        OutlinedTextField(
//            value = taskTitle,
//            onValueChange = { taskTitle = it },
//            label = { Text("Task Title") }
//        )
//
//        Spacer(Modifier.height(12.dp))
//
//        Button(
//            onClick = {
//                if (taskTitle.isNotBlank()) {
//                    addTask(projectId, taskTitle, currentUserId)
//                    taskTitle = ""
//                }
//            }
//        ) {
//            Text("+ Create First Task")
//        }
//    }
//}
//
///* ---------- KANBAN ---------- */
//@Composable
//fun KanbanBoard(
//    tasks: List<Task>,
//    projectId: String,
//    currentUserId: String,
//    memberNames: Map<String, String>
//) {
//    val todo = tasks.filter { it.status == "TODO" }
//    val progress = tasks.filter { it.status == "IN_PROGRESS" }
//    val done = tasks.filter { it.status == "DONE" }
//
//    Row(Modifier.horizontalScroll(rememberScrollState())) {
//        KanbanColumn("TODO", todo, projectId, currentUserId, memberNames)
//        KanbanColumn("IN PROGRESS", progress, projectId, currentUserId, memberNames)
//        KanbanColumn("DONE", done, projectId, currentUserId, memberNames)
//    }
//}
//
//@Composable
//fun KanbanColumn(
//    title: String,
//    tasks: List<Task>,
//    projectId: String,
//    currentUserId: String,
//    memberNames: Map<String, String>
//) {
//    Column(
//        modifier = Modifier
//            .width(280.dp)
//            .padding(8.dp)
//    ) {
//        Text(title, fontWeight = FontWeight.Bold)
//        Spacer(Modifier.height(10.dp))
//
//        if (tasks.isEmpty()) {
//            Text("No tasks", color = TextSecondary)
//        } else {
//            tasks.forEach {
//                TaskCard(it, currentUserId, projectId, memberNames)
//            }
//        }
//    }
//}
//
///* ---------- TASK CARD ---------- */
//@Composable
//fun TaskCard(
//    task: Task,
//    currentUserId: String,
//    projectId: String,
//    memberNames: Map<String, String>
//) {
//
//    val firestore = FirebaseFirestore.getInstance()
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 6.dp),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Column(Modifier.padding(16.dp)) {
//
//            Text(task.title, fontWeight = FontWeight.SemiBold)
//
//            Spacer(Modifier.height(4.dp))
//
//            Text(
//                "Assigned: ${memberNames[task.assignedTo] ?: task.assignedTo}",
//                fontSize = 12.sp
//            )
//
//            Spacer(Modifier.height(6.dp))
//
//            LinearProgressIndicator(
//                progress = taskProgress(task),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(6.dp))
//
//            AssistChip(onClick = {}, label = { Text(task.status) })
//
//            Spacer(Modifier.height(10.dp))
//
//            if (task.assignedTo == currentUserId && task.status != "DONE") {
//
//                Button(
//                    onClick = {
//                        firestore.collection("projects")
//                            .document(projectId)
//                            .collection("tasks")
//                            .document(task.id)
//                            .update("status", "DONE")
//                    }
//                ) {
//                    Icon(Icons.Default.Check, null)
//                    Spacer(Modifier.width(6.dp))
//                    Text("Mark Done")
//                }
//            }
//        }
//    }
//}
//
///* ---------- TIMELINE ---------- */
//@Composable
//fun GanttTask(task: Task) {
//    val progress = taskProgress(task)
//
//    Column(Modifier.padding(vertical = 6.dp)) {
//        Text(task.title)
//        Spacer(Modifier.height(4.dp))
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(10.dp)
//                .background(Color.LightGray)
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(progress)
//                    .height(10.dp)
//                    .background(PrimaryColor)
//            )
//        }
//    }
//}
//
///* ---------- HELPERS ---------- */
//@Composable
//private fun Section(title: String, value: String) {
//    Text(title, fontWeight = FontWeight.SemiBold)
//    Spacer(Modifier.height(4.dp))
//    Text(value, color = TextSecondary)
//    Spacer(Modifier.height(16.dp))
//}
//
//
//fun addTask(projectId: String, title: String, userId: String) {
//
//    val firestore = FirebaseFirestore.getInstance()
//
//    val task = hashMapOf(
//        "title" to title,
//        "assignedTo" to userId,
//        "status" to "TODO",
//        "createdAt" to System.currentTimeMillis()
//    )
//
//    firestore.collection("projects")
//        .document(projectId)
//        .collection("tasks")
//        .add(task)
//}
//@Composable
//private fun RoleBadge(text: String) {
//    Surface(
//        color = PrimaryColor.copy(alpha = 0.12f),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Text(
//            text = text,
//            color = PrimaryColor,
//            fontWeight = FontWeight.SemiBold,
//            fontSize = 12.sp,
//            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
//        )
//    }
//}
//
package com.example.cobuild.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cobuild.data.model.Task
import com.example.cobuild.utils.taskProgress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/* ── colours ── */
private val Primary     = Color(0xFF4F46E5)
private val PrimaryBg   = Color(0xFFEDE9FE)
private val DarkBar     = Color(0xFF0F172A)
private val BgColor     = Color(0xFFF8FAFC)
private val Surface1    = Color.White
private val TextPrimary = Color(0xFF1E293B)
private val TextMuted   = Color(0xFF64748B)
private val Border      = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    requesterName: String,
    onBackClick: () -> Unit,
    onEditClick: ((String) -> Unit)? = null   // only wired for owners
) {
    val viewModel     : ProjectViewModel = viewModel()
    val firestore      = FirebaseFirestore.getInstance()
    val currentUserId  = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    val projects by viewModel.projects.collectAsState()
    val proj     = projects.firstOrNull { it.id == projectId }

    val isOwner  = proj?.ownerId  == currentUserId
    val isMember = proj?.members?.contains(currentUserId) == true

    var tasks       by remember { mutableStateOf<List<Task>>(emptyList()) }
    var memberNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(projectId) {
        viewModel.loadProjects()

        firestore.collection("projects").document(projectId)
            .collection("tasks")
            .addSnapshotListener { snap, _ ->
                tasks = snap?.documents?.mapNotNull {
                    it.toObject(Task::class.java)?.copy(id = it.id)
                } ?: emptyList()
            }

        firestore.collection("users").get().addOnSuccessListener { snap ->
            memberNames = snap.documents.associate { it.id to (it.getString("name") ?: "User") }
        }
    }

    val yourTasks = tasks.filter { it.assignedTo == currentUserId }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text("Project Details", color = Color.White,
                        fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    if (isOwner && onEditClick != null) {
                        IconButton(onClick = { onEditClick(projectId) }) {
                            Icon(Icons.Default.Edit, "Edit", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBar)
            )
        }
    ) { padding ->

        if (proj == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {

            /* ── HERO BANNER ── */
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(DarkBar, Color(0xFF1E1B4B))))
                        .padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(proj.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("by ${proj.ownerName}", fontSize = 14.sp, color = Color.White.copy(alpha = 0.65f))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatusChip(proj.status.name)
                            if (isOwner)  RoleChip("👑 Owner",  Color(0xFFFEF9C3), Color(0xFF92400E))
                            if (isMember && !isOwner) RoleChip("🧑‍💻 Member", PrimaryBg, Primary)
                        }
                    }
                }
            }

            /* ── BODY ── */
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    /* INFO */
                    if (proj.description.isNotBlank()) InfoSection("Description", proj.description)
                    if (proj.goal.isNotBlank())        InfoSection("Goal", proj.goal)

                    /* STATS GRID */
                    val stats = buildList {
                        add("Members"    to proj.members.size.toString())
                        if (proj.timeline.isNotBlank())        add("Timeline"    to proj.timeline)
                        if (proj.teamSize.isNotBlank())        add("Team Size"   to proj.teamSize)
                        if (proj.projectType.isNotBlank())     add("Type"        to proj.projectType)
                        if (proj.commitmentLevel.isNotBlank()) add("Commitment"  to proj.commitmentLevel)
                        if (proj.experienceLevel.isNotBlank()) add("Experience"  to proj.experienceLevel)
                    }

                    if (stats.isNotEmpty()) {
                        SectionHeader("Project Info")
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            stats.chunked(2).forEach { row ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    row.forEach { (l, v) -> StatCard(l, v, Modifier.weight(1f)) }
                                    if (row.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    /* SKILLS */
                    if (proj.skills.isNotEmpty()) {
                        SectionHeader("Skills")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(8.dp)
                        ) {
                            proj.skills.forEach { skill: String ->
                                SkillTag(skill)
                            }
                        }
                    }
                }
            }

            /* ── TEAM MEMBERS ── */
            item {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader("Team Members")
                    Spacer(Modifier.height(10.dp))
                }
            }
            items(proj.members) { uid ->
                MemberRow(
                    name      = memberNames[uid] ?: uid,
                    isOwner   = uid == proj.ownerId,
                    modifier  = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            /* ── DIVIDER ── */
            item { Spacer(Modifier.height(24.dp)); Divider(color = Border, modifier = Modifier.padding(horizontal = 20.dp)) }

            /* ── KANBAN ── */
            item {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    SectionHeader("Kanban Board")
                }
            }

            if (tasks.isEmpty()) {
                item {
                    EmptyTasksCard(
                        projectId     = projectId,
                        currentUserId = currentUserId,
                        canCreate     = isOwner || isMember
                    )
                }
            } else {
                item {
                    KanbanBoard(tasks, projectId, currentUserId, memberNames)
                }

                /* ── YOUR TASKS ── */
                if (yourTasks.isNotEmpty()) {
                    item {
                        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)) {
                            SectionHeader("Your Tasks")
                        }
                    }
                    items(yourTasks) { task ->
                        Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                            TaskCard(task, currentUserId, projectId, memberNames)
                        }
                    }
                }

                /* ── ALL TASKS ── */
                item {
                    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)) {
                        SectionHeader("All Tasks")
                    }
                }
                items(tasks) { task ->
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        TaskCard(task, currentUserId, projectId, memberNames)
                    }
                }

                /* ── TIMELINE ── */
                item {
                    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)) {
                        SectionHeader("Timeline")
                    }
                }
                items(tasks) { task ->
                    Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        GanttTask(task)
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

/* ─── KANBAN ─────────────────────────────────────────────────────────────── */

@Composable
fun KanbanBoard(
    tasks: List<Task>,
    projectId: String,
    currentUserId: String,
    memberNames: Map<String, String>
) {
    Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp)) {
        listOf("TODO", "IN_PROGRESS", "DONE").forEach { status ->
            KanbanColumn(
                title         = status.replace("_", " "),
                tasks         = tasks.filter { it.status == status },
                projectId     = projectId,
                currentUserId = currentUserId,
                memberNames   = memberNames
            )
        }
    }
}

@Composable
fun KanbanColumn(
    title: String,
    tasks: List<Task>,
    projectId: String,
    currentUserId: String,
    memberNames: Map<String, String>
) {
    val (headerBg, headerFg) = when (title) {
        "IN PROGRESS" -> Color(0xFFEDE9FE) to Primary
        "DONE"        -> Color(0xFFDCFCE7) to Color(0xFF166534)
        else          -> Color(0xFFF1F5F9) to TextMuted
    }

    Column(
        modifier = Modifier
            .width(260.dp)
            .padding(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = headerBg,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                color      = headerFg,
                modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        if (tasks.isEmpty()) {
            Text("No tasks here", color = TextMuted, fontSize = 13.sp,
                modifier = Modifier.padding(8.dp))
        } else {
            tasks.forEach { TaskCard(it, currentUserId, projectId, memberNames) }
        }
    }
}

/* ─── TASK CARD ──────────────────────────────────────────────────────────── */

@Composable
fun TaskCard(
    task: Task,
    currentUserId: String,
    projectId: String,
    memberNames: Map<String, String>
) {
    val firestore = FirebaseFirestore.getInstance()

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = Surface1),
        border   = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(14.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(task.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "→ ${memberNames[task.assignedTo] ?: task.assignedTo.take(8)}",
                        fontSize = 12.sp,
                        color    = TextMuted
                    )
                }
                StatusPill(task.status)
            }

            if (task.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(task.description, fontSize = 13.sp, color = TextMuted)
            }

            Spacer(Modifier.height(10.dp))

            val prog = taskProgress(task)
            LinearProgressIndicator(
                progress  = prog,
                modifier  = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).height(5.dp),
                color     = Primary,
                trackColor = Border
            )

            if (task.assignedTo == currentUserId && task.status != "DONE") {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        firestore.collection("projects").document(projectId)
                            .collection("tasks").document(task.id)
                            .update("status", "DONE")
                    },
                    shape  = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Mark Done", fontSize = 13.sp)
                }
            }
        }
    }
}

/* ─── GANTT / TIMELINE ───────────────────────────────────────────────────── */

@Composable
fun GanttTask(task: Task) {
    val prog = taskProgress(task)
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = Surface1),
        border   = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(task.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Text("${(prog * 100).toInt()}%", fontSize = 12.sp, color = Primary)
            }
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier.fillMaxWidth().height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Border)
            ) {
                Box(
                    Modifier.fillMaxWidth(prog).height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Primary)
                )
            }
        }
    }
}

/* ─── EMPTY STATE ────────────────────────────────────────────────────────── */

@Composable
fun EmptyTasksCard(
    projectId: String,
    currentUserId: String,
    canCreate: Boolean
) {
    var taskTitle by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Surface1),
        border   = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🚀", fontSize = 36.sp)
            Spacer(Modifier.height(8.dp))
            Text("No Tasks Yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("Create the first task to get started", fontSize = 14.sp, color = TextMuted)

            if (canCreate) {
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value         = taskTitle,
                    onValueChange = { taskTitle = it },
                    label         = { Text("Task title") },
                    shape         = RoundedCornerShape(12.dp),
                    modifier      = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            addTask(projectId, taskTitle, currentUserId)
                            taskTitle = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("+ Create First Task")
                }
            }
        }
    }
}

/* ─── SMALL HELPERS ──────────────────────────────────────────────────────── */

@Composable
private fun SectionHeader(text: String) =
    Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

@Composable
private fun InfoSection(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
        Text(value, fontSize = 14.sp, color = TextPrimary, lineHeight = 22.sp)
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = Surface1),
        border   = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 11.sp, color = TextMuted)
            Spacer(Modifier.height(3.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun SkillTag(skill: String) {
    Surface(
        shape  = RoundedCornerShape(20.dp),
        color  = PrimaryBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.3f))
    ) {
        Text(
            skill,
            fontSize = 13.sp,
            color    = Primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun StatusChip(status: String) {
    val (bg, fg) = when (status.uppercase()) {
        "IN_PROGRESS"  -> Color(0xFF6EE7B7) to Color(0xFF065F46)
        "COMPLETED"    -> Color(0xFFA5B4FC) to Color(0xFF3730A3)
        else           -> Color(0xFFE2E8F0)  to Color(0xFF475569)
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            status.replace("_", " "),
            fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = fg,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun StatusPill(status: String) {
    val (bg, fg) = when (status) {
        "DONE"        -> Color(0xFFDCFCE7) to Color(0xFF166534)
        "IN_PROGRESS" -> Color(0xFFEDE9FE) to Primary
        else          -> Color(0xFFF1F5F9)  to TextMuted
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bg) {
        Text(
            status.replace("_", " "),
            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = fg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun RoleChip(text: String, bg: Color, fg: Color) {
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = fg,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
    }
}

@Composable
private fun MemberRow(name: String, isOwner: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isOwner) Primary else Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                name.firstOrNull()?.uppercase() ?: "?",
                color = if (isOwner) Color.White else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Text(name, fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
        if (isOwner) {
            Spacer(Modifier.weight(1f))
            Text("Owner", fontSize = 11.sp, color = Primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

fun addTask(projectId: String, title: String, userId: String) {
    FirebaseFirestore.getInstance()
        .collection("projects").document(projectId)
        .collection("tasks")
        .add(hashMapOf(
            "title"      to title,
            "assignedTo" to userId,
            "status"     to "TODO",
            "description" to "",
            "startDate"  to System.currentTimeMillis(),
            "deadline"   to 0L,
            "createdAt"  to System.currentTimeMillis()
        ))
}