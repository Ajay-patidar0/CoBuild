////package com.example.cobuild.ui.project
////
////import androidx.compose.foundation.layout.*
////import androidx.compose.foundation.shape.RoundedCornerShape
////import androidx.compose.material.icons.Icons
////import androidx.compose.material.icons.filled.ArrowBack
////import androidx.compose.material.icons.filled.Link
////import androidx.compose.material.icons.filled.Send
////import androidx.compose.material3.*
////import androidx.compose.runtime.*
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.graphics.Color
////import androidx.compose.ui.text.font.FontWeight
////import androidx.compose.ui.unit.dp
////import androidx.compose.ui.unit.sp
////import androidx.lifecycle.viewmodel.compose.viewModel
////import com.google.firebase.auth.FirebaseAuth
////
////private val PrimaryColor = Color(0xFF4F46E5)
////private val TextPrimary = Color(0xFF1E293B)
////private val TextSecondary = Color(0xFF64748B)
////private val BackgroundColor = Color(0xFFF8FAFC)
////
////@OptIn(ExperimentalMaterial3Api::class)
////@Composable
////fun ProjectDetailScreen(
////    projectId: String,
////    requesterName: String, // Pass the name from onboarding
////    onBackClick: () -> Unit
////) {
////    val viewModel: ProjectRequestViewModel = viewModel()
////    val isRequested by viewModel.isRequested.collectAsState()
////
////    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
////    val project by viewModel.getProjectById(projectId).collectAsState(initial = null)
////    val proj = project
////    val isOwner = proj?.ownerId == currentUserId
////
////    // Check if already requested when screen loads
////    LaunchedEffect(projectId) {
////        viewModel.checkIfRequested(projectId)
////    }
////
////    Scaffold(
////        containerColor = BackgroundColor,
////        topBar = {
////            TopAppBar(
////                title = { Text("Project Details") },
////                navigationIcon = {
////                    IconButton(onClick = onBackClick) {
////                        Icon(Icons.Default.ArrowBack, null)
////                    }
////                }
////            )
////        },
////        bottomBar = {
////            if (proj != null && !isOwner) {
////                Surface(tonalElevation = 8.dp) {
////                    Button(
////                        onClick = { viewModel.requestToJoin(proj, requesterName) },
////                        enabled = !isRequested,
////                        modifier = Modifier
////                            .fillMaxWidth()
////                            .padding(16.dp)
////                            .height(56.dp),
////                        shape = RoundedCornerShape(16.dp),
////                        colors = ButtonDefaults.buttonColors(
////                            containerColor = PrimaryColor,
////                            contentColor = Color.White,
////                            disabledContainerColor = PrimaryColor.copy(alpha = 0.6f)
////                        )
////                    ) {
////                        Text(
////                            text = if (isRequested) "Request Sent" else "Request to Join Project",
////                            fontSize = 16.sp,
////                            fontWeight = FontWeight.Bold
////                        )
////                    }
////                }
////            }
////        }
////    ) { padding ->
////        if (proj == null) {
////            Box(
////                modifier = Modifier.fillMaxSize().padding(padding),
////                contentAlignment = Alignment.Center
////            ) {
////                CircularProgressIndicator(color = PrimaryColor)
////            }
////            return@Scaffold
////        }
////
////        Column(
////            modifier = Modifier
////                .padding(padding)
////                .padding(20.dp)
////                .fillMaxSize()
////        ) {
////            Text(proj.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
////            Spacer(Modifier.height(6.dp))
////            Text("by ${proj.ownerName}", color = TextSecondary)
////            Spacer(Modifier.height(16.dp))
////            proj.description?.let { Section("Description", it) }
////            proj.goal?.let { Section("Goal", it) }
////
////            if (proj.skills.isNotEmpty()) {
////                Text("Required Skills", fontWeight = FontWeight.SemiBold)
////                Spacer(Modifier.height(8.dp))
////                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
////                    proj.skills.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
////                }
////                Spacer(Modifier.height(16.dp))
////            }
////
////            proj.link?.takeIf { it.isNotBlank() }?.let {
////                Text("Link", fontWeight = FontWeight.SemiBold)
////                Spacer(Modifier.height(4.dp))
////                Row(verticalAlignment = Alignment.CenterVertically) {
////                    Icon(Icons.Default.Link, null, tint = TextSecondary)
////                    Spacer(Modifier.width(8.dp))
////                    Text(it, color = TextSecondary)
////                }
////            }
////
////            Spacer(Modifier.height(80.dp))
////        }
////    }
////}
////
////@Composable
////private fun Section(title: String, value: String) {
////    Text(title, fontWeight = FontWeight.SemiBold)
////    Spacer(Modifier.height(4.dp))
////    Text(value, color = TextSecondary)
////    Spacer(Modifier.height(16.dp))
////}
////
////package com.example.cobuild.ui.project
////
////import androidx.compose.foundation.background
////import androidx.compose.foundation.horizontalScroll
////import androidx.compose.foundation.layout.*
////import androidx.compose.foundation.lazy.LazyColumn
////import androidx.compose.foundation.lazy.items
////import androidx.compose.foundation.rememberScrollState
////import androidx.compose.foundation.shape.RoundedCornerShape
////import androidx.compose.foundation.layout.FlowRow
////import androidx.compose.material.icons.Icons
////import androidx.compose.material.icons.filled.ArrowBack
////import androidx.compose.material.icons.filled.Check
////import androidx.compose.material3.*
////import androidx.compose.runtime.*
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.graphics.Color
////import androidx.compose.ui.text.font.FontWeight
////import androidx.compose.ui.unit.dp
////import androidx.compose.ui.unit.sp
////import androidx.lifecycle.viewmodel.compose.viewModel
////import com.example.cobuild.data.model.Task
////import com.example.cobuild.utils.taskProgress
////import com.google.firebase.auth.FirebaseAuth
////import com.google.firebase.firestore.FirebaseFirestore
////
////private val PrimaryColor = Color(0xFF4F46E5)
////private val TextSecondary = Color(0xFF64748B)
////private val BackgroundColor = Color(0xFFF8FAFC)
////
////@OptIn(ExperimentalMaterial3Api::class)
////@Composable
////fun ProjectDetailScreen(
////    projectId: String,
////    requesterName: String,
////    onBackClick: () -> Unit
////) {
////
////    val viewModel: ProjectViewModel = viewModel()
////    val firestore = FirebaseFirestore.getInstance()
////    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
////
////    val projects by viewModel.projects.collectAsState()
////    val proj = projects.firstOrNull { it.id == projectId }
////
////    val isOwner = proj?.ownerId == currentUserId
////    val isMember = proj?.members?.contains(currentUserId) == true
////
////    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
////    var memberNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
////
////    LaunchedEffect(projectId) {
////
////        viewModel.loadProjects()
////
////        firestore.collection("projects")
////            .document(projectId)
////            .collection("tasks")
////            .addSnapshotListener { snapshot, _ ->
////                tasks = snapshot?.documents?.mapNotNull {
////                    it.toObject(Task::class.java)?.copy(id = it.id)
////                } ?: emptyList()
////            }
////
////        firestore.collection("users")
////            .get()
////            .addOnSuccessListener { snapshot ->
////                val map = mutableMapOf<String, String>()
////                snapshot.documents.forEach {
////                    map[it.id] = it.getString("name") ?: "User"
////                }
////                memberNames = map
////            }
////    }
////
////    val yourTasks = tasks.filter { it.assignedTo == currentUserId }
////    val isEmpty = tasks.isEmpty()
////
////    Scaffold(
////        containerColor = BackgroundColor,
////        topBar = {
////            TopAppBar(
////                title = { Text("Project Details") },
////                navigationIcon = {
////                    IconButton(onClick = onBackClick) {
////                        Icon(Icons.Default.ArrowBack, null)
////                    }
////                }
////            )
////        }
////    ) { padding ->
////
////        if (proj == null) {
////            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
////                CircularProgressIndicator()
////            }
////            return@Scaffold
////        }
////
////        LazyColumn(
////            modifier = Modifier
////                .padding(padding)
////                .padding(20.dp)
////        ) {
////
////            /* ---------- PROJECT INFO ---------- */
////            item {
////                Text(proj.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
////
////                Spacer(Modifier.height(6.dp))
////
////                Text("by ${proj.ownerName}", color = TextSecondary)
////
////                Spacer(Modifier.height(12.dp))
////
////                if (isOwner) RoleBadge("👑 Owner")
////                else if (isMember) RoleBadge("🧑‍💻 Member")
////
////                Spacer(Modifier.height(16.dp))
////
////                proj.description?.let { Section("Description", it) }
////                proj.goal?.let { Section("Goal", it) }
////
////                Spacer(Modifier.height(20.dp))
////            }
////
////            /* ---------- TEAM MEMBERS ---------- */
////            item {
////
////                Text("Team Members", fontWeight = FontWeight.Bold)
////
////                Spacer(Modifier.height(8.dp))
////
////                proj.members.forEach { userId ->
////
////                    Text(
////                        "• ${memberNames[userId] ?: userId}",
////                        color = TextSecondary
////                    )
////                }
////
////                Spacer(Modifier.height(20.dp))
////            }
////
////            /* ---------- EMPTY STATE ---------- */
////            if (isEmpty) {
////
////                item {
////                    EmptyTaskState(projectId, currentUserId)
////                }
////
////            } else {
////
////                /* ---------- KANBAN ---------- */
////                item {
////                    Text("Kanban Board", fontWeight = FontWeight.Bold)
////                    Spacer(Modifier.height(10.dp))
////                }
////
////                item {
////                    KanbanBoard(tasks, projectId, currentUserId, memberNames)
////                }
////
////                /* ---------- TIMELINE ---------- */
////                item {
////                    Spacer(Modifier.height(30.dp))
////                    Text("Timeline", fontWeight = FontWeight.Bold)
////                }
////
////                items(tasks) {
////                    GanttTask(it)
////                }
////
////                /* ---------- YOUR TASKS ---------- */
////                item {
////                    Spacer(Modifier.height(30.dp))
////                    Text("Your Tasks", fontWeight = FontWeight.Bold)
////                }
////
////                items(yourTasks) {
////                    TaskCard(it, currentUserId, projectId, memberNames)
////                }
////
////                /* ---------- ALL TASKS ---------- */
////                item {
////                    Spacer(Modifier.height(30.dp))
////                    Text("All Tasks", fontWeight = FontWeight.Bold)
////                }
////
////                items(tasks) {
////                    TaskCard(it, currentUserId, projectId, memberNames)
////                }
////            }
////        }
////    }
////}
////
/////* ---------- EMPTY STATE ---------- */
////@Composable
////fun EmptyTaskState(
////    projectId: String,
////    currentUserId: String
////) {
////
////    var taskTitle by remember { mutableStateOf("") }
////
////    Column(
////        modifier = Modifier
////            .fillMaxWidth()
////            .padding(top = 80.dp),
////        horizontalAlignment = Alignment.CenterHorizontally
////    ) {
////
////        Text(
////            "🚀 No Tasks Yet",
////            fontSize = 20.sp,
////            fontWeight = FontWeight.Bold
////        )
////
////        Spacer(Modifier.height(10.dp))
////
////        Text(
////            "Create your first task to start",
////            color = TextSecondary
////        )
////
////        Spacer(Modifier.height(20.dp))
////
////        OutlinedTextField(
////            value = taskTitle,
////            onValueChange = { taskTitle = it },
////            label = { Text("Task Title") }
////        )
////
////        Spacer(Modifier.height(12.dp))
////
////        Button(
////            onClick = {
////                if (taskTitle.isNotBlank()) {
////                    addTask(projectId, taskTitle, currentUserId)
////                    taskTitle = ""
////                }
////            }
////        ) {
////            Text("+ Create First Task")
////        }
////    }
////}
////
/////* ---------- KANBAN ---------- */
////@Composable
////fun KanbanBoard(
////    tasks: List<Task>,
////    projectId: String,
////    currentUserId: String,
////    memberNames: Map<String, String>
////) {
////    val todo = tasks.filter { it.status == "TODO" }
////    val progress = tasks.filter { it.status == "IN_PROGRESS" }
////    val done = tasks.filter { it.status == "DONE" }
////
////    Row(Modifier.horizontalScroll(rememberScrollState())) {
////        KanbanColumn("TODO", todo, projectId, currentUserId, memberNames)
////        KanbanColumn("IN PROGRESS", progress, projectId, currentUserId, memberNames)
////        KanbanColumn("DONE", done, projectId, currentUserId, memberNames)
////    }
////}
////
////@Composable
////fun KanbanColumn(
////    title: String,
////    tasks: List<Task>,
////    projectId: String,
////    currentUserId: String,
////    memberNames: Map<String, String>
////) {
////    Column(
////        modifier = Modifier
////            .width(280.dp)
////            .padding(8.dp)
////    ) {
////        Text(title, fontWeight = FontWeight.Bold)
////        Spacer(Modifier.height(10.dp))
////
////        if (tasks.isEmpty()) {
////            Text("No tasks", color = TextSecondary)
////        } else {
////            tasks.forEach {
////                TaskCard(it, currentUserId, projectId, memberNames)
////            }
////        }
////    }
////}
////
/////* ---------- TASK CARD ---------- */
////@Composable
////fun TaskCard(
////    task: Task,
////    currentUserId: String,
////    projectId: String,
////    memberNames: Map<String, String>
////) {
////
////    val firestore = FirebaseFirestore.getInstance()
////
////    Card(
////        modifier = Modifier
////            .fillMaxWidth()
////            .padding(vertical = 6.dp),
////        shape = RoundedCornerShape(12.dp)
////    ) {
////        Column(Modifier.padding(16.dp)) {
////
////            Text(task.title, fontWeight = FontWeight.SemiBold)
////
////            Spacer(Modifier.height(4.dp))
////
////            Text(
////                "Assigned: ${memberNames[task.assignedTo] ?: task.assignedTo}",
////                fontSize = 12.sp
////            )
////
////            Spacer(Modifier.height(6.dp))
////
////            LinearProgressIndicator(
////                progress = taskProgress(task),
////                modifier = Modifier.fillMaxWidth()
////            )
////
////            Spacer(Modifier.height(6.dp))
////
////            AssistChip(onClick = {}, label = { Text(task.status) })
////
////            Spacer(Modifier.height(10.dp))
////
////            if (task.assignedTo == currentUserId && task.status != "DONE") {
////
////                Button(
////                    onClick = {
////                        firestore.collection("projects")
////                            .document(projectId)
////                            .collection("tasks")
////                            .document(task.id)
////                            .update("status", "DONE")
////                    }
////                ) {
////                    Icon(Icons.Default.Check, null)
////                    Spacer(Modifier.width(6.dp))
////                    Text("Mark Done")
////                }
////            }
////        }
////    }
////}
////
/////* ---------- TIMELINE ---------- */
////@Composable
////fun GanttTask(task: Task) {
////    val progress = taskProgress(task)
////
////    Column(Modifier.padding(vertical = 6.dp)) {
////        Text(task.title)
////        Spacer(Modifier.height(4.dp))
////
////        Box(
////            modifier = Modifier
////                .fillMaxWidth()
////                .height(10.dp)
////                .background(Color.LightGray)
////        ) {
////            Box(
////                modifier = Modifier
////                    .fillMaxWidth(progress)
////                    .height(10.dp)
////                    .background(PrimaryColor)
////            )
////        }
////    }
////}
////
/////* ---------- HELPERS ---------- */
////@Composable
////private fun Section(title: String, value: String) {
////    Text(title, fontWeight = FontWeight.SemiBold)
////    Spacer(Modifier.height(4.dp))
////    Text(value, color = TextSecondary)
////    Spacer(Modifier.height(16.dp))
////}
////
////
////fun addTask(projectId: String, title: String, userId: String) {
////
////    val firestore = FirebaseFirestore.getInstance()
////
////    val task = hashMapOf(
////        "title" to title,
////        "assignedTo" to userId,
////        "status" to "TODO",
////        "createdAt" to System.currentTimeMillis()
////    )
////
////    firestore.collection("projects")
////        .document(projectId)
////        .collection("tasks")
////        .add(task)
////}
////@Composable
////private fun RoleBadge(text: String) {
////    Surface(
////        color = PrimaryColor.copy(alpha = 0.12f),
////        shape = RoundedCornerShape(8.dp)
////    ) {
////        Text(
////            text = text,
////            color = PrimaryColor,
////            fontWeight = FontWeight.SemiBold,
////            fontSize = 12.sp,
////            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
////        )
////    }
////}
////
////
////package com.example.cobuild.ui.project
////
////import androidx.compose.foundation.background
////import androidx.compose.foundation.horizontalScroll
////import androidx.compose.foundation.layout.*
////import androidx.compose.foundation.lazy.LazyColumn
////import androidx.compose.foundation.lazy.items
////import androidx.compose.foundation.rememberScrollState
////import androidx.compose.foundation.shape.CircleShape
////import androidx.compose.foundation.shape.RoundedCornerShape
////import androidx.compose.foundation.layout.FlowRow
////import androidx.compose.material.icons.Icons
////import androidx.compose.material.icons.filled.ArrowBack
////import androidx.compose.material.icons.filled.Check
////import androidx.compose.material.icons.filled.Edit
////import androidx.compose.material3.*
////import androidx.compose.runtime.*
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.draw.clip
////import androidx.compose.ui.graphics.Brush
////import androidx.compose.ui.graphics.Color
////import androidx.compose.ui.text.font.FontWeight
////import androidx.compose.ui.unit.dp
////import androidx.compose.ui.unit.sp
////import androidx.lifecycle.viewmodel.compose.viewModel
////import com.example.cobuild.data.model.Task
////import com.example.cobuild.utils.taskProgress
////import com.google.firebase.auth.FirebaseAuth
////import com.google.firebase.firestore.FirebaseFirestore
////
/////* ── colours ── */
////private val Primary     = Color(0xFF4F46E5)
////private val PrimaryBg   = Color(0xFFEDE9FE)
////private val DarkBar     = Color(0xFF0F172A)
////private val BgColor     = Color(0xFFF8FAFC)
////private val Surface1    = Color.White
////private val TextPrimary = Color(0xFF1E293B)
////private val TextMuted   = Color(0xFF64748B)
////private val Border      = Color(0xFFE2E8F0)
////
////@OptIn(ExperimentalMaterial3Api::class)
////@Composable
////fun ProjectDetailScreen(
////    projectId: String,
////    requesterName: String,
////    onBackClick: () -> Unit,
////    onEditClick: ((String) -> Unit)? = null   // only wired for owners
////) {
////    val viewModel     : ProjectViewModel = viewModel()
////    val firestore      = FirebaseFirestore.getInstance()
////    val currentUserId  = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
////
////    val projects by viewModel.projects.collectAsState()
////    val proj     = projects.firstOrNull { it.id == projectId }
////
////    val isOwner  = proj?.ownerId  == currentUserId
////    val isMember = proj?.members?.contains(currentUserId) == true
////
////    var tasks       by remember { mutableStateOf<List<Task>>(emptyList()) }
////    var memberNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
////
////    LaunchedEffect(projectId) {
////        viewModel.loadProjects()
////
////        firestore.collection("projects").document(projectId)
////            .collection("tasks")
////            .addSnapshotListener { snap, _ ->
////                tasks = snap?.documents?.mapNotNull {
////                    it.toObject(Task::class.java)?.copy(id = it.id)
////                } ?: emptyList()
////            }
////
////        firestore.collection("users").get().addOnSuccessListener { snap ->
////            memberNames = snap.documents.associate { it.id to (it.getString("name") ?: "User") }
////        }
////    }
////
////    val yourTasks = tasks.filter { it.assignedTo == currentUserId }
////
////    Scaffold(
////        containerColor = BgColor,
////        topBar = {
////            TopAppBar(
////                title = {
////                    Text("Project Details", color = Color.White,
////                        fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
////                },
////                navigationIcon = {
////                    IconButton(onClick = onBackClick) {
////                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
////                    }
////                },
////                actions = {
////                    if (isOwner && onEditClick != null) {
////                        IconButton(onClick = { onEditClick(projectId) }) {
////                            Icon(Icons.Default.Edit, "Edit", tint = Color.White)
////                        }
////                    }
////                },
////                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBar)
////            )
////        }
////    ) { padding ->
////
////        if (proj == null) {
////            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
////                CircularProgressIndicator(color = Primary)
////            }
////            return@Scaffold
////        }
////
////        LazyColumn(
////            modifier = Modifier.padding(padding)
////        ) {
////
////            /* ── HERO BANNER ── */
////            item {
////                Box(
////                    modifier = Modifier
////                        .fillMaxWidth()
////                        .background(Brush.verticalGradient(listOf(DarkBar, Color(0xFF1E1B4B))))
////                        .padding(horizontal = 20.dp, vertical = 28.dp)
////                ) {
////                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
////                        Text(proj.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
////                        Text("by ${proj.ownerName}", fontSize = 14.sp, color = Color.White.copy(alpha = 0.65f))
////                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
////                            StatusChip(proj.status.name)
////                            if (isOwner)  RoleChip("👑 Owner",  Color(0xFFFEF9C3), Color(0xFF92400E))
////                            if (isMember && !isOwner) RoleChip("🧑‍💻 Member", PrimaryBg, Primary)
////                        }
////                    }
////                }
////            }
////
////            /* ── BODY ── */
////            item {
////                Column(
////                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
////                    verticalArrangement = Arrangement.spacedBy(20.dp)
////                ) {
////
////                    /* INFO */
////                    if (proj.description.isNotBlank()) InfoSection("Description", proj.description)
////                    if (proj.goal.isNotBlank())        InfoSection("Goal", proj.goal)
////
////                    /* STATS GRID */
////                    val stats = buildList {
////                        add("Members"    to proj.members.size.toString())
////                        if (proj.timeline.isNotBlank())        add("Timeline"    to proj.timeline)
////                        if (proj.teamSize.isNotBlank())        add("Team Size"   to proj.teamSize)
////                        if (proj.projectType.isNotBlank())     add("Type"        to proj.projectType)
////                        if (proj.commitmentLevel.isNotBlank()) add("Commitment"  to proj.commitmentLevel)
////                        if (proj.experienceLevel.isNotBlank()) add("Experience"  to proj.experienceLevel)
////                    }
////
////                    if (stats.isNotEmpty()) {
////                        SectionHeader("Project Info")
////                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
////                            stats.chunked(2).forEach { row ->
////                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
////                                    row.forEach { (l, v) -> StatCard(l, v, Modifier.weight(1f)) }
////                                    if (row.size == 1) Spacer(Modifier.weight(1f))
////                                }
////                            }
////                        }
////                    }
////
////                    /* SKILLS */
////                    if (proj.skills.isNotEmpty()) {
////                        SectionHeader("Skills")
////                        FlowRow(
////                            horizontalArrangement = Arrangement.spacedBy(8.dp),
////                            verticalArrangement   = Arrangement.spacedBy(8.dp)
////                        ) {
////                            proj.skills.forEach { skill: String ->
////                                SkillTag(skill)
////                            }
////                        }
////                    }
////                }
////            }
////
////            /* ── TEAM MEMBERS ── */
////            item {
////                Column(Modifier.padding(horizontal = 20.dp)) {
////                    SectionHeader("Team Members")
////                    Spacer(Modifier.height(10.dp))
////                }
////            }
////            items(proj.members) { uid ->
////                MemberRow(
////                    name      = memberNames[uid] ?: uid,
////                    isOwner   = uid == proj.ownerId,
////                    modifier  = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
////                )
////            }
////
////            /* ── DIVIDER ── */
////            item { Spacer(Modifier.height(24.dp)); Divider(color = Border, modifier = Modifier.padding(horizontal = 20.dp)) }
////
////            /* ── KANBAN ── */
////            item {
////                Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
////                    SectionHeader("Kanban Board")
////                }
////            }
////
////            if (tasks.isEmpty()) {
////                item {
////                    EmptyTasksCard(
////                        projectId     = projectId,
////                        currentUserId = currentUserId,
////                        canCreate     = isOwner || isMember
////                    )
////                }
////            } else {
////                item {
////                    KanbanBoard(tasks, projectId, currentUserId, memberNames)
////                }
////
////                /* ── YOUR TASKS ── */
////                if (yourTasks.isNotEmpty()) {
////                    item {
////                        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)) {
////                            SectionHeader("Your Tasks")
////                        }
////                    }
////                    items(yourTasks) { task ->
////                        Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
////                            TaskCard(task, currentUserId, projectId, memberNames)
////                        }
////                    }
////                }
////
////                /* ── ALL TASKS ── */
////                item {
////                    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)) {
////                        SectionHeader("All Tasks")
////                    }
////                }
////                items(tasks) { task ->
////                    Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
////                        TaskCard(task, currentUserId, projectId, memberNames)
////                    }
////                }
////
////                /* ── TIMELINE ── */
////                item {
////                    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)) {
////                        SectionHeader("Timeline")
////                    }
////                }
////                items(tasks) { task ->
////                    Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
////                        GanttTask(task)
////                    }
////                }
////            }
////
////            item { Spacer(Modifier.height(40.dp)) }
////        }
////    }
////}
////
/////* ─── KANBAN ─────────────────────────────────────────────────────────────── */
////
////@Composable
////fun KanbanBoard(
////    tasks: List<Task>,
////    projectId: String,
////    currentUserId: String,
////    memberNames: Map<String, String>
////) {
////    Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp)) {
////        listOf("TODO", "IN_PROGRESS", "DONE").forEach { status ->
////            KanbanColumn(
////                title         = status.replace("_", " "),
////                tasks         = tasks.filter { it.status == status },
////                projectId     = projectId,
////                currentUserId = currentUserId,
////                memberNames   = memberNames
////            )
////        }
////    }
////}
////
////@Composable
////fun KanbanColumn(
////    title: String,
////    tasks: List<Task>,
////    projectId: String,
////    currentUserId: String,
////    memberNames: Map<String, String>
////) {
////    val (headerBg, headerFg) = when (title) {
////        "IN PROGRESS" -> Color(0xFFEDE9FE) to Primary
////        "DONE"        -> Color(0xFFDCFCE7) to Color(0xFF166534)
////        else          -> Color(0xFFF1F5F9) to TextMuted
////    }
////
////    Column(
////        modifier = Modifier
////            .width(260.dp)
////            .padding(8.dp)
////    ) {
////        Surface(
////            shape = RoundedCornerShape(8.dp),
////            color = headerBg,
////            modifier = Modifier.fillMaxWidth()
////        ) {
////            Text(
////                title,
////                fontWeight = FontWeight.Bold,
////                fontSize   = 13.sp,
////                color      = headerFg,
////                modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
////            )
////        }
////
////        Spacer(Modifier.height(8.dp))
////
////        if (tasks.isEmpty()) {
////            Text("No tasks here", color = TextMuted, fontSize = 13.sp,
////                modifier = Modifier.padding(8.dp))
////        } else {
////            tasks.forEach { TaskCard(it, currentUserId, projectId, memberNames) }
////        }
////    }
////}
////
/////* ─── TASK CARD ──────────────────────────────────────────────────────────── */
////
////@Composable
////fun TaskCard(
////    task: Task,
////    currentUserId: String,
////    projectId: String,
////    memberNames: Map<String, String>
////) {
////    val firestore = FirebaseFirestore.getInstance()
////
////    Card(
////        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
////        shape    = RoundedCornerShape(14.dp),
////        colors   = CardDefaults.cardColors(containerColor = Surface1),
////        border   = androidx.compose.foundation.BorderStroke(1.dp, Border)
////    ) {
////        Column(Modifier.padding(14.dp)) {
////
////            Row(
////                Modifier.fillMaxWidth(),
////                horizontalArrangement = Arrangement.SpaceBetween,
////                verticalAlignment     = Alignment.Top
////            ) {
////                Column(Modifier.weight(1f)) {
////                    Text(task.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
////                    Spacer(Modifier.height(3.dp))
////                    Text(
////                        "→ ${memberNames[task.assignedTo] ?: task.assignedTo.take(8)}",
////                        fontSize = 12.sp,
////                        color    = TextMuted
////                    )
////                }
////                StatusPill(task.status)
////            }
////
////            if (task.description.isNotBlank()) {
////                Spacer(Modifier.height(6.dp))
////                Text(task.description, fontSize = 13.sp, color = TextMuted)
////            }
////
////            Spacer(Modifier.height(10.dp))
////
////            val prog = taskProgress(task)
////            LinearProgressIndicator(
////                progress  = prog,
////                modifier  = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)).height(5.dp),
////                color     = Primary,
////                trackColor = Border
////            )
////
////            if (task.assignedTo == currentUserId && task.status != "DONE") {
////                Spacer(Modifier.height(10.dp))
////                Button(
////                    onClick = {
////                        firestore.collection("projects").document(projectId)
////                            .collection("tasks").document(task.id)
////                            .update("status", "DONE")
////                    },
////                    shape  = RoundedCornerShape(10.dp),
////                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
////                    modifier = Modifier.height(36.dp)
////                ) {
////                    Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp))
////                    Spacer(Modifier.width(6.dp))
////                    Text("Mark Done", fontSize = 13.sp)
////                }
////            }
////        }
////    }
////}
////
/////* ─── GANTT / TIMELINE ───────────────────────────────────────────────────── */
////
////@Composable
////fun GanttTask(task: Task) {
////    val prog = taskProgress(task)
////    Card(
////        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
////        shape    = RoundedCornerShape(12.dp),
////        colors   = CardDefaults.cardColors(containerColor = Surface1),
////        border   = androidx.compose.foundation.BorderStroke(1.dp, Border)
////    ) {
////        Column(Modifier.padding(12.dp)) {
////            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
////                Text(task.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
////                Text("${(prog * 100).toInt()}%", fontSize = 12.sp, color = Primary)
////            }
////            Spacer(Modifier.height(6.dp))
////            Box(
////                Modifier.fillMaxWidth().height(8.dp)
////                    .clip(RoundedCornerShape(4.dp))
////                    .background(Border)
////            ) {
////                Box(
////                    Modifier.fillMaxWidth(prog).height(8.dp)
////                        .clip(RoundedCornerShape(4.dp))
////                        .background(Primary)
////                )
////            }
////        }
////    }
////}
////
/////* ─── EMPTY STATE ────────────────────────────────────────────────────────── */
////
////@Composable
////fun EmptyTasksCard(
////    projectId: String,
////    currentUserId: String,
////    canCreate: Boolean
////) {
////    var taskTitle by remember { mutableStateOf("") }
////
////    Card(
////        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
////        shape    = RoundedCornerShape(16.dp),
////        colors   = CardDefaults.cardColors(containerColor = Surface1),
////        border   = androidx.compose.foundation.BorderStroke(1.dp, Border)
////    ) {
////        Column(
////            Modifier.fillMaxWidth().padding(24.dp),
////            horizontalAlignment = Alignment.CenterHorizontally
////        ) {
////            Text("🚀", fontSize = 36.sp)
////            Spacer(Modifier.height(8.dp))
////            Text("No Tasks Yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
////            Spacer(Modifier.height(4.dp))
////            Text("Create the first task to get started", fontSize = 14.sp, color = TextMuted)
////
////            if (canCreate) {
////                Spacer(Modifier.height(20.dp))
////                OutlinedTextField(
////                    value         = taskTitle,
////                    onValueChange = { taskTitle = it },
////                    label         = { Text("Task title") },
////                    shape         = RoundedCornerShape(12.dp),
////                    modifier      = Modifier.fillMaxWidth()
////                )
////                Spacer(Modifier.height(12.dp))
////                Button(
////                    onClick = {
////                        if (taskTitle.isNotBlank()) {
////                            addTask(projectId, taskTitle, currentUserId)
////                            taskTitle = ""
////                        }
////                    },
////                    modifier = Modifier.fillMaxWidth().height(46.dp),
////                    shape    = RoundedCornerShape(12.dp),
////                    colors   = ButtonDefaults.buttonColors(containerColor = Primary)
////                ) {
////                    Text("+ Create First Task")
////                }
////            }
////        }
////    }
////}
////
/////* ─── SMALL HELPERS ──────────────────────────────────────────────────────── */
////
////@Composable
////private fun SectionHeader(text: String) =
////    Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
////
////@Composable
////private fun InfoSection(label: String, value: String) {
////    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
////        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
////        Text(value, fontSize = 14.sp, color = TextPrimary, lineHeight = 22.sp)
////    }
////}
////
////@Composable
////private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
////    Card(
////        modifier = modifier,
////        shape    = RoundedCornerShape(12.dp),
////        colors   = CardDefaults.cardColors(containerColor = Surface1),
////        border   = androidx.compose.foundation.BorderStroke(1.dp, Border)
////    ) {
////        Column(
////            Modifier.fillMaxWidth().padding(12.dp),
////            horizontalAlignment = Alignment.CenterHorizontally
////        ) {
////            Text(label, fontSize = 11.sp, color = TextMuted)
////            Spacer(Modifier.height(3.dp))
////            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
////        }
////    }
////}
////
////@Composable
////private fun SkillTag(skill: String) {
////    Surface(
////        shape  = RoundedCornerShape(20.dp),
////        color  = PrimaryBg,
////        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.3f))
////    ) {
////        Text(
////            skill,
////            fontSize = 13.sp,
////            color    = Primary,
////            fontWeight = FontWeight.Medium,
////            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
////        )
////    }
////}
////
////@Composable
////private fun StatusChip(status: String) {
////    val (bg, fg) = when (status.uppercase()) {
////        "IN_PROGRESS"  -> Color(0xFF6EE7B7) to Color(0xFF065F46)
////        "COMPLETED"    -> Color(0xFFA5B4FC) to Color(0xFF3730A3)
////        else           -> Color(0xFFE2E8F0)  to Color(0xFF475569)
////    }
////    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
////        Text(
////            status.replace("_", " "),
////            fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = fg,
////            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
////        )
////    }
////}
////
////@Composable
////private fun StatusPill(status: String) {
////    val (bg, fg) = when (status) {
////        "DONE"        -> Color(0xFFDCFCE7) to Color(0xFF166534)
////        "IN_PROGRESS" -> Color(0xFFEDE9FE) to Primary
////        else          -> Color(0xFFF1F5F9)  to TextMuted
////    }
////    Surface(shape = RoundedCornerShape(8.dp), color = bg) {
////        Text(
////            status.replace("_", " "),
////            fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = fg,
////            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
////        )
////    }
////}
////
////@Composable
////private fun RoleChip(text: String, bg: Color, fg: Color) {
////    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
////        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = fg,
////            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
////    }
////}
////
////@Composable
////private fun MemberRow(name: String, isOwner: Boolean, modifier: Modifier = Modifier) {
////    Row(
////        modifier = modifier.fillMaxWidth(),
////        verticalAlignment = Alignment.CenterVertically,
////        horizontalArrangement = Arrangement.spacedBy(12.dp)
////    ) {
////        Box(
////            modifier = Modifier
////                .size(36.dp)
////                .clip(CircleShape)
////                .background(if (isOwner) Primary else Color(0xFFE2E8F0)),
////            contentAlignment = Alignment.Center
////        ) {
////            Text(
////                name.firstOrNull()?.uppercase() ?: "?",
////                color = if (isOwner) Color.White else TextMuted,
////                fontWeight = FontWeight.Bold,
////                fontSize = 14.sp
////            )
////        }
////        Text(name, fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
////        if (isOwner) {
////            Spacer(Modifier.weight(1f))
////            Text("Owner", fontSize = 11.sp, color = Primary, fontWeight = FontWeight.SemiBold)
////        }
////    }
////}
////
////fun addTask(projectId: String, title: String, userId: String) {
////    FirebaseFirestore.getInstance()
////        .collection("projects").document(projectId)
////        .collection("tasks")
////        .add(hashMapOf(
////            "title"      to title,
////            "assignedTo" to userId,
////            "status"     to "TODO",
////            "description" to "",
////            "startDate"  to System.currentTimeMillis(),
////            "deadline"   to 0L,
////            "createdAt"  to System.currentTimeMillis()
////        ))
////}
//
//
//package com.example.cobuild.ui.project
//
//import androidx.compose.animation.*
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.cobuild.data.model.Task
//import com.example.cobuild.utils.taskProgress
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import java.text.SimpleDateFormat
//import java.util.*
//
///* ── tokens ── */
//private val Bg0          = Color(0xFF0A0F1E)
//private val Bg1          = Color(0xFF0F172A)
//private val Bg2          = Color(0xFF1E293B)
//private val Bg3          = Color(0xFF273548)
//private val AccentViolet = Color(0xFF818CF8)
//private val AccentCyan   = Color(0xFF22D3EE)
//private val AccentGreen  = Color(0xFF34D399)
//private val AccentAmber  = Color(0xFFFBBF24)
//private val TextHigh     = Color(0xFFF1F5F9)
//private val TextMid      = Color(0xFF94A3B8)
//private val TextLow      = Color(0xFF475569)
//private val Border       = Color(0xFF1E293B)
//
//private fun sColor(s: String) = when (s) {
//    "DONE"        -> AccentGreen
//    "IN_PROGRESS" -> AccentAmber
//    else          -> AccentViolet
//}
//private fun sLabel(s: String) = when (s) {
//    "DONE"        -> "Done"
//    "IN_PROGRESS" -> "In Progress"
//    else          -> "To Do"
//}
//private fun timeAgo(ms: Long): String {
//    if (ms == 0L) return ""
//    val d = System.currentTimeMillis() - ms
//    return when {
//        d < 60_000     -> "just now"
//        d < 3_600_000  -> "${d / 60_000}m ago"
//        d < 86_400_000 -> "${d / 3_600_000}h ago"
//        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(ms))
//    }
//}
//
///* ══════════════════ MAIN SCREEN ══════════════════════════════════════════ */
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProjectDetailScreen(
//    projectId: String,
//    requesterName: String,
//    onBackClick: () -> Unit,
//    onEditClick: ((String) -> Unit)? = null,
//    onOpenGroupChat: ((String) -> Unit)? = null,   // projectId → group chat
//    onMemberClick: ((String) -> Unit)? = null       // userId → profile view
//) {
//    val vm            : ProjectViewModel = viewModel()
//    val firestore      = FirebaseFirestore.getInstance()
//    val currentUserId  = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
//
//    val projects by vm.projects.collectAsState()
//    val proj     = projects.firstOrNull { it.id == projectId }
//
//    val isOwner  = proj?.ownerId == currentUserId
//    val isMember = proj?.members?.contains(currentUserId) == true
//    val canAct   = isOwner || isMember
//
//    var tasks        by remember { mutableStateOf<List<Task>>(emptyList()) }
//    var memberNames  by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
//    var selectedTask by remember { mutableStateOf<Task?>(null) }
//
//    var showInfo     by remember { mutableStateOf(true) }
//    var showMembers  by remember { mutableStateOf(false) }
//    var showKanban   by remember { mutableStateOf(true) }
//    var showYours    by remember { mutableStateOf(true) }
//    var showAll      by remember { mutableStateOf(false) }
//    var showTimeline by remember { mutableStateOf(false) }
//
//    LaunchedEffect(projectId) {
//        vm.loadProjects()
//        firestore.collection("projects").document(projectId)
//            .collection("tasks")
//            .addSnapshotListener { snap, _ ->
//                tasks = snap?.documents?.mapNotNull {
//                    it.toObject(Task::class.java)?.copy(id = it.id)
//                } ?: emptyList()
//            }
//        firestore.collection("users").get().addOnSuccessListener { snap ->
//            memberNames = snap.documents.associate { it.id to (it.getString("name") ?: "User") }
//        }
//    }
//
//    val yourTasks = tasks.filter { it.assignedTo == currentUserId }
//
//    selectedTask?.let { task ->
//        TaskDetailSheet(
//            task        = task,
//            projectId   = projectId,
//            currentUid  = currentUserId,
//            memberNames = memberNames,
//            members     = proj?.members ?: emptyList(),
//            canEdit     = canAct,
//            onDismiss   = { selectedTask = null }
//        )
//    }
//
//    Scaffold(
//        containerColor = Bg0,
//        topBar = {
//            TopAppBar(
//                title = { Text("Project Details", color = TextHigh,
//                    fontWeight = FontWeight.SemiBold, fontSize = 17.sp) },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, null, tint = TextHigh)
//                    }
//                },
//                actions = {
//                    /* group chat button */
//                    if (canAct && onOpenGroupChat != null) {
//                        IconButton(onClick = { onOpenGroupChat(projectId) }) {
//                            Icon(Icons.Default.Groups, "Group Chat", tint = AccentCyan)
//                        }
//                    }
//                    /* edit button — owner only */
//                    if (isOwner && onEditClick != null) {
//                        IconButton(onClick = { onEditClick(projectId) }) {
//                            Icon(Icons.Default.Edit, "Edit", tint = AccentViolet)
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg1)
//            )
//        }
//    ) { padding ->
//
//        if (proj == null) {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator(color = AccentViolet)
//            }
//            return@Scaffold
//        }
//
//        LazyColumn(
//            modifier = Modifier.padding(padding),
//            contentPadding = PaddingValues(bottom = 40.dp)
//        ) {
//
//            /* ── HERO ── */
//            item {
//                Box(
//                    Modifier.fillMaxWidth()
//                        .background(Brush.verticalGradient(listOf(Bg1, Bg0)))
//                        .padding(horizontal = 20.dp, vertical = 28.dp)
//                ) {
//                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
//                        Text(proj.title, fontSize = 26.sp, fontWeight = FontWeight.Bold,
//                            color = TextHigh, lineHeight = 32.sp)
//                        Text("by ${proj.ownerName}", fontSize = 14.sp, color = TextMid)
//                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                            DChip(proj.status.name.replace("_", " "),
//                                sColor(proj.status.name).copy(.15f), sColor(proj.status.name))
//                            if (isOwner)       DChip("👑 Owner",  AccentAmber.copy(.15f), AccentAmber)
//                            else if (isMember) DChip("🧑‍💻 Member", AccentViolet.copy(.15f), AccentViolet)
//                        }
//                        /* overall progress */
//                        if (tasks.isNotEmpty()) {
//                            val pct = tasks.count { it.status == "DONE" }.toFloat() / tasks.size
//                            Row(Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween) {
//                                Text("Project Progress", fontSize = 12.sp, color = TextMid)
//                                Text("${(pct * 100).toInt()}%", fontSize = 12.sp,
//                                    color = AccentCyan, fontWeight = FontWeight.Bold)
//                            }
//                            LinearProgressIndicator(pct,
//                                Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
//                                color = AccentCyan, trackColor = Bg2, strokeCap = StrokeCap.Round)
//                        }
//                        /* group chat shortcut */
//                        if (canAct && onOpenGroupChat != null) {
//                            OutlinedButton(
//                                onClick = { onOpenGroupChat(projectId) },
//                                border = BorderStroke(1.dp, AccentCyan.copy(.5f)),
//                                shape  = RoundedCornerShape(10.dp),
//                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
//                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
//                            ) {
//                                Icon(Icons.Default.Groups, null,
//                                    modifier = Modifier.size(16.dp))
//                                Spacer(Modifier.width(6.dp))
//                                Text("Team Chat", fontSize = 13.sp)
//                            }
//                        }
//                    }
//                }
//            }
//
//            /* ── PROJECT INFO ── */
//            item {
//                Collapsible("Project Info", showInfo, { showInfo = !showInfo }) {
//                    if (proj.description.isNotBlank()) InfoRow("Description", proj.description)
//                    if (proj.goal.isNotBlank())        InfoRow("Goal", proj.goal)
//                    val stats = buildList {
//                        add("Members"   to proj.members.size.toString())
//                        if (proj.timeline.isNotBlank())        add("Timeline"   to proj.timeline)
//                        if (proj.teamSize.isNotBlank())        add("Team Size"  to proj.teamSize)
//                        if (proj.projectType.isNotBlank())     add("Type"       to proj.projectType)
//                        if (proj.commitmentLevel.isNotBlank()) add("Commitment" to proj.commitmentLevel)
//                        if (proj.experienceLevel.isNotBlank()) add("Experience" to proj.experienceLevel)
//                    }
//                    if (stats.isNotEmpty()) {
//                        Spacer(Modifier.height(12.dp))
//                        stats.chunked(2).forEach { row ->
//                            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp),
//                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                                row.forEach { (l, v) -> StatCard2(l, v, Modifier.weight(1f)) }
//                                if (row.size == 1) Spacer(Modifier.weight(1f))
//                            }
//                        }
//                    }
//                    if (proj.skills.isNotEmpty()) {
//                        Spacer(Modifier.height(12.dp))
//                        Text("Skills", fontSize = 12.sp, color = TextMid, fontWeight = FontWeight.SemiBold)
//                        Spacer(Modifier.height(8.dp))
//                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                            proj.skills.forEach { s: String ->
//                                DChip(s, AccentViolet.copy(.15f), AccentViolet)
//                            }
//                        }
//                    }
//                }
//            }
//
//            /* ── TEAM ── */
//            item {
//                Collapsible("Team  ·  ${proj.members.size}", showMembers, { showMembers = !showMembers }) {
//                    proj.members.forEach { uid ->
//                        MemberRow2(
//                            name      = memberNames[uid] ?: uid,
//                            isOwner   = uid == proj.ownerId,
//                            onClick   = { onMemberClick?.invoke(uid) }
//                        )
//                    }
//                }
//            }
//
//            /* ══ tasks — members & owners only ══ */
//            if (canAct) {
//
//                item {
//                    Collapsible("Kanban  ·  ${tasks.size} tasks", showKanban, { showKanban = !showKanban }) {
//                        if (tasks.isEmpty()) {
//                            /* ── OWNER: can create; MEMBER: read-only message ── */
//                            if (isOwner) {
//                                EmptyTaskOwner(projectId, currentUserId)
//                            } else {
//                                EmptyTaskMember()
//                            }
//                        } else {
//                            DarkKanban(tasks, projectId, memberNames) { selectedTask = it }
//                        }
//                    }
//                }
//
//                if (yourTasks.isNotEmpty()) {
//                    item {
//                        Collapsible("Your Tasks  ·  ${yourTasks.size}", showYours, { showYours = !showYours }) {
//                            yourTasks.forEach { task ->
//                                FullTaskCard(task, projectId, currentUserId, memberNames,
//                                    showSlider = true, onClick = { selectedTask = task })
//                            }
//                        }
//                    }
//                }
//
//                item {
//                    Collapsible("All Tasks  ·  ${tasks.size}", showAll, { showAll = !showAll }) {
//                        tasks.forEach { task ->
//                            FullTaskCard(task, projectId, currentUserId, memberNames,
//                                showSlider = task.assignedTo == currentUserId,
//                                onClick = { selectedTask = task })
//                        }
//                    }
//                }
//
//                item {
//                    Collapsible("Timeline", showTimeline, { showTimeline = !showTimeline }) {
//                        tasks.forEach { GanttRow(it) }
//                    }
//                }
//
//            } else {
//                item {
//                    Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
//                        DChip("Join this project to see tasks & chat",
//                            AccentViolet.copy(.10f), AccentViolet)
//                    }
//                }
//            }
//
//            item { Spacer(Modifier.height(40.dp)) }
//        }
//    }
//}
//
///* ══════════════════ TASK DETAIL SHEET ════════════════════════════════════ */
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TaskDetailSheet(
//    task: Task, projectId: String, currentUid: String,
//    memberNames: Map<String, String>, members: List<String>,
//    canEdit: Boolean, onDismiss: () -> Unit
//) {
//    val firestore = FirebaseFirestore.getInstance()
//    var comments   by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
//    var newComment by remember { mutableStateOf("") }
//    var progress   by remember { mutableStateOf(taskProgress(task)) }
//    var memberExpanded by remember { mutableStateOf(false) }
//
//    LaunchedEffect(task.id) {
//        firestore.collection("projects").document(projectId)
//            .collection("tasks").document(task.id)
//            .collection("comments").orderBy("createdAt")
//            .addSnapshotListener { snap, _ ->
//                comments = snap?.documents?.mapNotNull { it.data } ?: emptyList()
//            }
//        firestore.collection("projects").document(projectId)
//            .collection("tasks").document(task.id)
//            .get().addOnSuccessListener { doc ->
//                progress = (doc.getLong("progress")?.toFloat() ?: (taskProgress(task) * 100f)) / 100f
//            }
//    }
//
//    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Bg2,
//        dragHandle = {
//            Box(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
//                contentAlignment = Alignment.Center) {
//                Box(Modifier.width(40.dp).height(4.dp).background(TextLow, RoundedCornerShape(2.dp)))
//            }
//        }
//    ) {
//        Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)
//            .verticalScroll(rememberScrollState())) {
//
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Top) {
//                Text(task.title, fontSize = 20.sp, fontWeight = FontWeight.Bold,
//                    color = TextHigh, modifier = Modifier.weight(1f))
//                DChip(sLabel(task.status), sColor(task.status).copy(.15f), sColor(task.status))
//            }
//            if (task.description.isNotBlank()) {
//                Spacer(Modifier.height(8.dp))
//                Text(task.description, fontSize = 14.sp, color = TextMid, lineHeight = 22.sp)
//            }
//            Spacer(Modifier.height(16.dp))
//            Row(verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//                Avatar(memberNames[task.assignedTo] ?: "?", AccentViolet, 36)
//                Column {
//                    Text(memberNames[task.assignedTo] ?: task.assignedTo,
//                        fontSize = 14.sp, color = TextHigh, fontWeight = FontWeight.Medium)
//                    if (task.startDate > 0) Text("Started ${timeAgo(task.startDate)}", fontSize = 12.sp, color = TextMid)
//                    if (task.deadline > 0) {
//                        val over = task.deadline < System.currentTimeMillis() && task.status != "DONE"
//                        Text("Due ${SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(task.deadline))}",
//                            fontSize = 12.sp,
//                            color = if (over) Color(0xFFEF4444) else AccentAmber)
//                    }
//                }
//            }
//            Spacer(Modifier.height(20.dp))
//            HDivider()
//            Spacer(Modifier.height(16.dp))
//
//            /* progress */
//            Text("Progress  ·  ${(progress * 100).toInt()}%",
//                fontSize = 13.sp, color = TextMid, fontWeight = FontWeight.SemiBold)
//            Spacer(Modifier.height(8.dp))
//            LinearProgressIndicator(progress,
//                Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
//                color = AccentCyan, trackColor = Bg3, strokeCap = StrokeCap.Round)
//
//            if (task.assignedTo == currentUid && canEdit) {
//                Slider(progress, { progress = it },
//                    onValueChangeFinished = {
//                        val pct = (progress * 100).toInt()
//                        val ns  = when { pct >= 100 -> "DONE"; pct > 0 -> "IN_PROGRESS"; else -> "TODO" }
//                        firestore.collection("projects").document(projectId)
//                            .collection("tasks").document(task.id)
//                            .update(mapOf("progress" to pct, "status" to ns))
//                    },
//                    colors = SliderDefaults.colors(thumbColor = AccentCyan,
//                        activeTrackColor = AccentCyan, inactiveTrackColor = Bg3))
//
//                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    listOf(25, 50, 75, 100).forEach { pct ->
//                        OutlinedButton(
//                            onClick = {
//                                progress = pct / 100f
//                                val ns = if (pct >= 100) "DONE" else "IN_PROGRESS"
//                                firestore.collection("projects").document(projectId)
//                                    .collection("tasks").document(task.id)
//                                    .update(mapOf("progress" to pct, "status" to ns))
//                            },
//                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
//                            shape = RoundedCornerShape(8.dp),
//                            border = BorderStroke(1.dp, AccentCyan.copy(.4f)),
//                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
//                            modifier = Modifier.weight(1f).height(32.dp)
//                        ) { Text("$pct%", fontSize = 12.sp) }
//                    }
//                }
//            }
//
//            /* reassign */
//            if (canEdit) {
//                Spacer(Modifier.height(16.dp)); HDivider(); Spacer(Modifier.height(16.dp))
//                Text("Reassign", fontSize = 13.sp, color = TextMid, fontWeight = FontWeight.SemiBold)
//                Spacer(Modifier.height(8.dp))
//                ExposedDropdownMenuBox(memberExpanded, { memberExpanded = !memberExpanded }) {
//                    OutlinedTextField(
//                        value = memberNames[task.assignedTo] ?: task.assignedTo,
//                        onValueChange = {}, readOnly = true,
//                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(memberExpanded) },
//                        modifier = Modifier.menuAnchor().fillMaxWidth(),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedBorderColor = AccentViolet, unfocusedBorderColor = Border,
//                            focusedTextColor = TextHigh, unfocusedTextColor = TextHigh,
//                            unfocusedContainerColor = Bg3, focusedContainerColor = Bg3)
//                    )
//                    ExposedDropdownMenu(memberExpanded, { memberExpanded = false }, containerColor = Bg2) {
//                        members.forEach { uid ->
//                            DropdownMenuItem(
//                                text = { Text(memberNames[uid] ?: uid, color = TextHigh) },
//                                onClick = {
//                                    firestore.collection("projects").document(projectId)
//                                        .collection("tasks").document(task.id)
//                                        .update("assignedTo", uid)
//                                    memberExpanded = false
//                                })
//                        }
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(20.dp)); HDivider(); Spacer(Modifier.height(16.dp))
//
//            /* comments */
//            Text("Comments  ·  ${comments.size}", fontSize = 13.sp, color = TextMid, fontWeight = FontWeight.SemiBold)
//            Spacer(Modifier.height(12.dp))
//            if (comments.isEmpty()) {
//                Text("No comments yet.", fontSize = 13.sp, color = TextLow, modifier = Modifier.padding(vertical = 8.dp))
//            } else {
//                comments.forEach { c ->
//                    CommentBubble(c["authorName"] as? String ?: "User",
//                        c["text"] as? String ?: "",
//                        c["createdAt"] as? Long ?: 0L,
//                        c["authorId"] as? String == currentUid)
//                    Spacer(Modifier.height(8.dp))
//                }
//            }
//            Spacer(Modifier.height(12.dp))
//            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                OutlinedTextField(
//                    value = newComment, onValueChange = { newComment = it },
//                    placeholder = { Text("Write a comment…", color = TextLow, fontSize = 14.sp) },
//                    singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
//                    colors = OutlinedTextFieldDefaults.colors(
//                        focusedBorderColor = AccentViolet, unfocusedBorderColor = Border,
//                        focusedTextColor = TextHigh, unfocusedTextColor = TextHigh,
//                        unfocusedContainerColor = Bg3, focusedContainerColor = Bg3)
//                )
//                IconButton(
//                    onClick = {
//                        if (newComment.isNotBlank()) {
//                            val uid  = FirebaseAuth.getInstance().currentUser?.uid ?: return@IconButton
//                            val name = FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
//                            firestore.collection("projects").document(projectId)
//                                .collection("tasks").document(task.id)
//                                .collection("comments")
//                                .add(mapOf("authorId" to uid, "authorName" to name,
//                                    "text" to newComment.trim(), "createdAt" to System.currentTimeMillis()))
//                            newComment = ""
//                        }
//                    },
//                    modifier = Modifier.size(48.dp).background(AccentViolet, RoundedCornerShape(12.dp))
//                ) { Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
//            }
//        }
//    }
//}
//
///* ══════════════════ KANBAN ═══════════════════════════════════════════════ */
//
//@Composable
//fun DarkKanban(
//    tasks: List<Task>, projectId: String,
//    memberNames: Map<String, String>, onTaskClick: (Task) -> Unit
//) {
//    Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp)) {
//        listOf("TODO" to AccentViolet, "IN_PROGRESS" to AccentAmber, "DONE" to AccentGreen)
//            .forEach { (status, color) ->
//                KanbanCol(status, color, tasks.filter { it.status == status },
//                    projectId, memberNames, onTaskClick)
//            }
//    }
//}
//
//@Composable
//private fun KanbanCol(
//    status: String, color: Color, tasks: List<Task>,
//    projectId: String, memberNames: Map<String, String>, onTaskClick: (Task) -> Unit
//) {
//    val firestore = FirebaseFirestore.getInstance()
//    Column(Modifier.width(220.dp).padding(horizontal = 6.dp)) {
//        Row(Modifier.fillMaxWidth().background(color.copy(.12f), RoundedCornerShape(10.dp))
//            .padding(horizontal = 12.dp, vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween) {
//            Row(verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
//                Box(Modifier.size(8.dp).background(color, CircleShape))
//                Text(sLabel(status), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
//            }
//            Text("${tasks.size}", fontSize = 12.sp, color = color.copy(.7f))
//        }
//        Spacer(Modifier.height(8.dp))
//        if (tasks.isEmpty()) {
//            Box(Modifier.fillMaxWidth().height(60.dp)
//                .background(Bg2, RoundedCornerShape(12.dp))
//                .border(1.dp, Border, RoundedCornerShape(12.dp)),
//                contentAlignment = Alignment.Center) {
//                Text("Empty", color = TextLow, fontSize = 13.sp)
//            }
//        } else {
//            tasks.forEach { task ->
//                Card(onClick = { onTaskClick(task) }, modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(12.dp),
//                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
//                    elevation = CardDefaults.cardElevation(0.dp)) {
//                    Box(Modifier.fillMaxWidth()
//                        .background(Brush.linearGradient(listOf(Bg2, Bg3)), RoundedCornerShape(12.dp))
//                        .border(1.dp, Border, RoundedCornerShape(12.dp)).padding(12.dp)) {
//                        Column {
//                            Row(verticalAlignment = Alignment.Top) {
//                                Box(Modifier.width(3.dp).height(16.dp)
//                                    .background(sColor(task.status), RoundedCornerShape(2.dp)))
//                                Spacer(Modifier.width(8.dp))
//                                Text(task.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
//                                    color = TextHigh, maxLines = 2, overflow = TextOverflow.Ellipsis)
//                            }
//                            Spacer(Modifier.height(8.dp))
//                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically) {
//                                Row(verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.spacedBy(5.dp)) {
//                                    Avatar(memberNames[task.assignedTo] ?: "?", AccentViolet.copy(.25f), 20)
//                                    Text((memberNames[task.assignedTo] ?: "").take(10),
//                                        fontSize = 11.sp, color = TextMid)
//                                }
//                                if (task.startDate > 0) Text(timeAgo(task.startDate), fontSize = 10.sp, color = TextLow)
//                            }
//                            if (task.status != "DONE") {
//                                Spacer(Modifier.height(8.dp))
//                                val next = if (task.status == "TODO") "IN_PROGRESS" else "DONE"
//                                val lbl  = if (task.status == "TODO") "→ Start" else "✓ Done"
//                                val clr  = if (task.status == "TODO") AccentViolet else AccentGreen
//                                TextButton(
//                                    onClick = {
//                                        firestore.collection("projects").document(projectId)
//                                            .collection("tasks").document(task.id)
//                                            .update(mapOf("status" to next,
//                                                "progress" to if (next == "DONE") 100 else 50))
//                                    },
//                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
//                                    modifier = Modifier.fillMaxWidth().height(28.dp)
//                                        .background(clr.copy(.10f), RoundedCornerShape(6.dp))
//                                ) { Text(lbl, fontSize = 11.sp, color = clr, fontWeight = FontWeight.SemiBold) }
//                            }
//                        }
//                    }
//                }
//                Spacer(Modifier.height(6.dp))
//            }
//        }
//    }
//}
//
///* ══════════════════ FULL TASK CARD ═══════════════════════════════════════ */
//
//@Composable
//fun FullTaskCard(
//    task: Task, projectId: String, currentUid: String,
//    memberNames: Map<String, String>, showSlider: Boolean, onClick: () -> Unit
//) {
//    val firestore = FirebaseFirestore.getInstance()
//    var progress  by remember(task.id) { mutableStateOf(taskProgress(task)) }
//    val animProg  by animateFloatAsState(progress, tween(600), label = "p")
//
//    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
//        elevation = CardDefaults.cardElevation(0.dp)) {
//        Box(Modifier.fillMaxWidth()
//            .background(Brush.linearGradient(listOf(Bg2, Bg3)), RoundedCornerShape(16.dp))
//            .border(1.dp, Brush.linearGradient(listOf(Color.White.copy(.06f), Color.Transparent)),
//                RoundedCornerShape(16.dp)).padding(16.dp)) {
//            Column {
//                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.Top) {
//                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
//                        Box(Modifier.width(3.dp).height(18.dp)
//                            .background(sColor(task.status), RoundedCornerShape(2.dp)))
//                        Spacer(Modifier.width(10.dp))
//                        Text(task.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
//                            color = TextHigh, maxLines = 2, overflow = TextOverflow.Ellipsis)
//                    }
//                    Spacer(Modifier.width(8.dp))
//                    DChip(sLabel(task.status), sColor(task.status).copy(.15f), sColor(task.status))
//                }
//                if (task.description.isNotBlank()) {
//                    Spacer(Modifier.height(6.dp))
//                    Text(task.description, fontSize = 12.sp, color = TextMid,
//                        maxLines = 2, overflow = TextOverflow.Ellipsis)
//                }
//                Spacer(Modifier.height(12.dp))
//                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically) {
//                    Row(verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                        Avatar(memberNames[task.assignedTo] ?: "?", AccentViolet, 34)
//                        Column {
//                            Text(memberNames[task.assignedTo] ?: task.assignedTo, fontSize = 13.sp, color = TextHigh)
//                            if (task.startDate > 0) Text(timeAgo(task.startDate), fontSize = 11.sp, color = TextLow)
//                        }
//                    }
//                    if (task.deadline > 0) {
//                        val over = task.deadline < System.currentTimeMillis() && task.status != "DONE"
//                        Row(verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(3.dp)) {
//                            Icon(Icons.Default.DateRange, null,
//                                tint = if (over) Color(0xFFEF4444) else AccentAmber,
//                                modifier = Modifier.size(12.dp))
//                            Text(SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(task.deadline)),
//                                fontSize = 11.sp, color = if (over) Color(0xFFEF4444) else AccentAmber)
//                        }
//                    }
//                }
//                Spacer(Modifier.height(12.dp))
//                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                    Text("Progress", fontSize = 11.sp, color = TextMid)
//                    Text("${(animProg * 100).toInt()}%", fontSize = 11.sp, color = AccentCyan, fontWeight = FontWeight.Bold)
//                }
//                Spacer(Modifier.height(5.dp))
//                LinearProgressIndicator(animProg,
//                    Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
//                    color = AccentCyan, trackColor = Bg3, strokeCap = StrokeCap.Round)
//
//                if (showSlider) {
//                    Slider(progress, { progress = it },
//                        onValueChangeFinished = {
//                            val pct = (progress * 100).toInt()
//                            val ns  = when { pct >= 100 -> "DONE"; pct > 0 -> "IN_PROGRESS"; else -> "TODO" }
//                            firestore.collection("projects").document(projectId)
//                                .collection("tasks").document(task.id)
//                                .update(mapOf("progress" to pct, "status" to ns))
//                        },
//                        colors = SliderDefaults.colors(thumbColor = AccentCyan,
//                            activeTrackColor = AccentCyan, inactiveTrackColor = Bg3))
//                }
//                Spacer(Modifier.height(4.dp))
//                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
//                    verticalAlignment = Alignment.CenterVertically) {
//                    Icon(Icons.Default.ChatBubbleOutline, null, tint = TextLow, modifier = Modifier.size(12.dp))
//                    Spacer(Modifier.width(4.dp))
//                    Text("Tap for details & comments", fontSize = 11.sp, color = TextLow)
//                }
//            }
//        }
//    }
//}
//
///* ══════════════════ GANTT ════════════════════════════════════════════════ */
//
//@Composable
//private fun GanttRow(task: Task) {
//    val prog    = taskProgress(task)
//    val animProg by animateFloatAsState(prog, tween(800), label = "g")
//    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)
//        .background(Bg2, RoundedCornerShape(10.dp))
//        .border(1.dp, Border, RoundedCornerShape(10.dp)).padding(12.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//        Box(Modifier.size(8.dp).background(sColor(task.status), CircleShape))
//        Text(task.title, fontSize = 13.sp, color = TextHigh, modifier = Modifier.weight(1f),
//            maxLines = 1, overflow = TextOverflow.Ellipsis)
//        Box(Modifier.width(100.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(Bg3)) {
//            Box(Modifier.fillMaxWidth(animProg).height(6.dp)
//                .clip(RoundedCornerShape(3.dp)).background(sColor(task.status)))
//        }
//        Text("${(animProg * 100).toInt()}%", fontSize = 11.sp, color = sColor(task.status),
//            fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
//    }
//}
//
///* ══════════════════ EMPTY STATES ═════════════════════════════════════════ */
//
///** Owner: can type a task title and create */
//@Composable
//private fun EmptyTaskOwner(projectId: String, currentUserId: String) {
//    var taskTitle by remember { mutableStateOf("") }
//    Column(Modifier.fillMaxWidth().background(Bg3, RoundedCornerShape(16.dp))
//        .border(1.dp, Border, RoundedCornerShape(16.dp)).padding(20.dp),
//        horizontalAlignment = Alignment.CenterHorizontally) {
//        Text("🚀", fontSize = 36.sp)
//        Spacer(Modifier.height(8.dp))
//        Text("No Tasks Yet", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextHigh)
//        Spacer(Modifier.height(4.dp))
//        Text("Create the first task to get started", fontSize = 13.sp, color = TextMid)
//        Spacer(Modifier.height(16.dp))
//        OutlinedTextField(
//            value = taskTitle, onValueChange = { taskTitle = it },
//            placeholder = { Text("Task title", color = TextLow, fontSize = 14.sp) },
//            singleLine = true, modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = AccentViolet, unfocusedBorderColor = Border,
//                focusedTextColor = TextHigh, unfocusedTextColor = TextHigh,
//                unfocusedContainerColor = Bg2, focusedContainerColor = Bg2)
//        )
//        Spacer(Modifier.height(12.dp))
//        Button(
//            onClick = { if (taskTitle.isNotBlank()) { addTask(projectId, taskTitle, currentUserId); taskTitle = "" } },
//            modifier = Modifier.fillMaxWidth().height(44.dp), shape = RoundedCornerShape(12.dp),
//            colors = ButtonDefaults.buttonColors(containerColor = AccentViolet)
//        ) { Text("+ Create First Task", fontWeight = FontWeight.SemiBold) }
//    }
//}
//
///** Member: read-only — no input shown */
//@Composable
//private fun EmptyTaskMember() {
//    Column(Modifier.fillMaxWidth().background(Bg3, RoundedCornerShape(16.dp))
//        .border(1.dp, Border, RoundedCornerShape(16.dp)).padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally) {
//        Text("📋", fontSize = 36.sp)
//        Spacer(Modifier.height(8.dp))
//        Text("No tasks yet", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextHigh)
//        Spacer(Modifier.height(4.dp))
//        Text("The project owner hasn't added any tasks yet.\nCheck back soon!", fontSize = 13.sp,
//            color = TextMid, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
//    }
//}
//
///* ══════════════════ COLLAPSIBLE SECTION ══════════════════════════════════ */
//
//@Composable
//private fun Collapsible(
//    title: String, expanded: Boolean, onToggle: () -> Unit,
//    content: @Composable ColumnScope.() -> Unit
//) {
//    val rot by animateFloatAsState(if (expanded) 180f else 0f, label = "r")
//    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
//        .background(Bg2, RoundedCornerShape(16.dp))
//        .border(1.dp, Border, RoundedCornerShape(16.dp))) {
//        Row(Modifier.fillMaxWidth().clickable(onClick = onToggle)
//            .padding(horizontal = 16.dp, vertical = 14.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween) {
//            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextHigh)
//            Icon(Icons.Default.KeyboardArrowDown, null, tint = TextMid,
//                modifier = Modifier.size(20.dp).rotate(rot))
//        }
//        AnimatedVisibility(expanded, enter = expandVertically() + fadeIn(),
//            exit = shrinkVertically() + fadeOut()) {
//            Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 14.dp), content = content)
//        }
//    }
//}
//
///* ══════════════════ ATOMS ════════════════════════════════════════════════ */
//
//@Composable
//fun DChip(text: String, bg: Color, fg: Color) {
//    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
//        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = fg,
//            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
//    }
//}
//
//@Composable
//private fun InfoRow(label: String, value: String) {
//    Column(Modifier.padding(bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
//        Text(label, fontSize = 12.sp, color = TextMid, fontWeight = FontWeight.SemiBold)
//        Text(value, fontSize = 14.sp, color = TextHigh, lineHeight = 22.sp)
//    }
//}
//
//@Composable
//private fun StatCard2(label: String, value: String, modifier: Modifier = Modifier) {
//    Column(modifier.background(Bg3, RoundedCornerShape(12.dp))
//        .border(1.dp, Border, RoundedCornerShape(12.dp)).padding(12.dp),
//        horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(label, fontSize = 11.sp, color = TextMid)
//        Spacer(Modifier.height(3.dp))
//        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextHigh)
//    }
//}
//
//@Composable
//fun Avatar(name: String, color: Color, size: Int = 36) {
//    Box(Modifier.size(size.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
//        Text(name.firstOrNull()?.uppercase() ?: "?",
//            color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size / 2.5).sp)
//    }
//}
//
//@Composable
//private fun MemberRow2(name: String, isOwner: Boolean, onClick: () -> Unit) {
//    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//        Avatar(name, if (isOwner) AccentViolet else Bg3, 36)
//        Text(name, fontSize = 14.sp, color = TextHigh, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
//        if (isOwner) DChip("Owner", AccentAmber.copy(.15f), AccentAmber)
//        else Icon(Icons.Default.ChevronRight, null, tint = TextLow, modifier = Modifier.size(16.dp))
//    }
//}
//
//@Composable
//private fun CommentBubble(author: String, text: String, time: Long, isMe: Boolean) {
//    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start) {
//        if (!isMe) { Avatar(author, AccentViolet, 32); Spacer(Modifier.width(8.dp)) }
//        Column(Modifier.weight(1f, false).widthIn(max = 260.dp)
//            .background(if (isMe) AccentViolet.copy(.20f) else Bg3,
//                RoundedCornerShape(topStart = if (isMe) 12.dp else 4.dp,
//                    topEnd = if (isMe) 4.dp else 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
//            .padding(10.dp)) {
//            if (!isMe) {
//                Text(author, fontSize = 11.sp, color = AccentViolet, fontWeight = FontWeight.SemiBold)
//                Spacer(Modifier.height(2.dp))
//            }
//            Text(text, fontSize = 13.sp, color = TextHigh)
//            Spacer(Modifier.height(3.dp))
//            Text(timeAgo(time), fontSize = 10.sp, color = TextLow, modifier = Modifier.align(Alignment.End))
//        }
//        if (isMe) { Spacer(Modifier.width(8.dp)); Avatar(author, AccentCyan.copy(.4f), 32) }
//    }
//}
//
//@Composable
//private fun HDivider() = Box(Modifier.fillMaxWidth().height(1.dp).background(Border))
//
//fun addTask(projectId: String, title: String, userId: String) {
//    FirebaseFirestore.getInstance()
//        .collection("projects").document(projectId).collection("tasks")
//        .add(hashMapOf(
//            "title" to title, "assignedTo" to userId, "status" to "TODO",
//            "progress" to 0, "description" to "",
//            "startDate" to System.currentTimeMillis(), "deadline" to 0L,
//            "createdAt" to System.currentTimeMillis()
//        ))
//}
package com.example.cobuild.ui.project

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cobuild.data.model.Task
import com.example.cobuild.utils.taskProgress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/* ══ DESIGN TOKENS ═══════════════════════════════════════════════════════════
   Top bar  → dark navy  (#0F172A)
   Page bg  → off-white  (#F8FAFC)
   Cards    → pure white with light border
   Accent   → indigo     (#4F46E5)
   ══════════════════════════════════════════════════════════════════════════ */

private val TopBarColor  = Color(0xFF0F172A)   // dark navy — top bar only
private val PageBg       = Color(0xFFF8FAFC)   // light page background
private val CardBg       = Color.White
private val CardBg2      = Color(0xFFF1F5F9)   // subtle inner card bg
private val Primary      = Color(0xFF4F46E5)   // indigo accent
private val PrimaryLight = Color(0xFFEDE9FE)
private val TextPrimary  = Color(0xFF1E293B)
private val TextSub      = Color(0xFF64748B)
private val TextHint     = Color(0xFF94A3B8)
private val Border       = Color(0xFFE2E8F0)
private val DividerColor = Color(0xFFF1F5F9)

private fun sColor(s: String) = when (s) {
    "DONE"        -> Color(0xFF16A34A)
    "IN_PROGRESS" -> Color(0xFFD97706)
    else          -> Primary
}
private fun sBg(s: String) = when (s) {
    "DONE"        -> Color(0xFFDCFCE7)
    "IN_PROGRESS" -> Color(0xFFFEF3C7)
    else          -> PrimaryLight
}
private fun sLabel(s: String) = when (s) {
    "DONE"        -> "Done"
    "IN_PROGRESS" -> "In Progress"
    else          -> "To Do"
}
private fun timeAgo(ms: Long): String {
    if (ms == 0L) return ""
    val d = System.currentTimeMillis() - ms
    return when {
        d < 60_000     -> "just now"
        d < 3_600_000  -> "${d / 60_000}m ago"
        d < 86_400_000 -> "${d / 3_600_000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(ms))
    }
}

/* ══ MAIN SCREEN ═════════════════════════════════════════════════════════════ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    requesterName: String,
    onBackClick: () -> Unit,
    onEditClick: ((String) -> Unit)? = null,
    onOpenGroupChat: ((String) -> Unit)? = null,
    onMemberClick: ((String) -> Unit)? = null
) {
    val vm            : ProjectViewModel = viewModel()
    val firestore      = FirebaseFirestore.getInstance()
    val currentUserId  = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    val projects by vm.projects.collectAsState()
    val proj     = projects.firstOrNull { it.id == projectId }

    val isOwner  = proj?.ownerId == currentUserId
    val isMember = proj?.members?.contains(currentUserId) == true
    val canAct   = isOwner || isMember

    var tasks        by remember { mutableStateOf<List<Task>>(emptyList()) }
    var memberNames  by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    var showInfo     by remember { mutableStateOf(true) }
    var showMembers  by remember { mutableStateOf(false) }
    var showKanban   by remember { mutableStateOf(true) }
    var showYours    by remember { mutableStateOf(true) }
    var showAll      by remember { mutableStateOf(false) }
    var showTimeline by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        vm.loadProjects()
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

    /* task detail sheet */
    selectedTask?.let { task ->
        TaskDetailSheet(
            task        = task,
            projectId   = projectId,
            currentUid  = currentUserId,
            memberNames = memberNames,
            members     = proj?.members ?: emptyList(),
            canEdit     = canAct,
            onDismiss   = { selectedTask = null }
        )
    }

    Scaffold(
        containerColor = PageBg,
        topBar = {
            /* ── DARK NAVY TOP BAR only ── */
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
                    if (canAct && onOpenGroupChat != null) {
                        IconButton(onClick = { onOpenGroupChat(projectId) }) {
                            Icon(Icons.Default.Groups, "Group Chat", tint = Color(0xFF93C5FD))
                        }
                    }
                    if (isOwner && onEditClick != null) {
                        IconButton(onClick = { onEditClick(projectId) }) {
                            Icon(Icons.Default.Edit, "Edit", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarColor)
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
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 48.dp)
        ) {

            /* ── HERO CARD — white card with indigo accent strip ── */
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape  = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, Border),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    /* indigo accent top strip */
                    Box(
                        Modifier.fillMaxWidth().height(5.dp)
                            .background(Brush.horizontalGradient(listOf(Primary, Color(0xFF818CF8))))
                    )

                    Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        /* title */
                        Text(proj.title, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = TextPrimary, lineHeight = 28.sp)
                        Text("by ${proj.ownerName}", fontSize = 13.sp, color = TextSub)

                        /* badges */
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LightChip(
                                text = proj.status.name.replace("_", " "),
                                bg   = sBg(proj.status.name),
                                fg   = sColor(proj.status.name)
                            )
                            if (isOwner)       LightChip("👑 Owner",    Color(0xFFFEF9C3), Color(0xFF92400E))
                            else if (isMember) LightChip("🧑‍💻 Member", PrimaryLight,        Primary)
                        }

                        /* overall progress bar */
                        if (tasks.isNotEmpty()) {
                            val pct = tasks.count { it.status == "DONE" }.toFloat() / tasks.size
                            Spacer(Modifier.height(2.dp))
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Overall Progress", fontSize = 12.sp, color = TextSub)
                                Text("${(pct * 100).toInt()}%", fontSize = 12.sp,
                                    color = Primary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress   = pct,
                                modifier   = Modifier.fillMaxWidth().height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color      = Primary,
                                trackColor = Border,
                                strokeCap  = StrokeCap.Round
                            )
                        }

                        /* team chat button */
                        if (canAct && onOpenGroupChat != null) {
                            Spacer(Modifier.height(2.dp))
                            OutlinedButton(
                                onClick = { onOpenGroupChat(projectId) },
                                border  = BorderStroke(1.dp, Primary.copy(.4f)),
                                shape   = RoundedCornerShape(10.dp),
                                colors  = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Groups, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Team Chat", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            /* ── PROJECT INFO ── */
            item {
                LightSection("Project Info", showInfo, { showInfo = !showInfo }) {
                    if (proj.description.isNotBlank()) LightInfoRow("Description", proj.description)
                    if (proj.goal.isNotBlank())        LightInfoRow("Goal", proj.goal)

                    val stats = buildList {
                        add("Members"   to proj.members.size.toString())
                        if (proj.timeline.isNotBlank())        add("Timeline"   to proj.timeline)
                        if (proj.teamSize.isNotBlank())        add("Team Size"  to proj.teamSize)
                        if (proj.projectType.isNotBlank())     add("Type"       to proj.projectType)
                        if (proj.commitmentLevel.isNotBlank()) add("Commitment" to proj.commitmentLevel)
                        if (proj.experienceLevel.isNotBlank()) add("Experience" to proj.experienceLevel)
                    }
                    if (stats.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        stats.chunked(2).forEach { row ->
                            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { (l, v) -> LightStatCard(l, v, Modifier.weight(1f)) }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }

                    if (proj.skills.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Skills", fontSize = 12.sp, color = TextSub,
                            fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            proj.skills.forEach { s: String ->
                                LightChip(s, PrimaryLight, Primary)
                            }
                        }
                    }
                }
            }

            /* ── TEAM MEMBERS ── */
            item {
                LightSection("Team  ·  ${proj.members.size}", showMembers,
                    { showMembers = !showMembers }) {
                    proj.members.forEach { uid ->
                        LightMemberRow(
                            name    = memberNames[uid] ?: uid,
                            isOwner = uid == proj.ownerId,
                            onClick = { onMemberClick?.invoke(uid) }
                        )
                        if (uid != proj.members.last())
                            Divider(color = DividerColor, modifier = Modifier.padding(start = 52.dp))
                    }
                }
            }

            /* ══ TASK SECTIONS — members & owners only ══ */
            if (canAct) {

                item {
                    LightSection("Kanban  ·  ${tasks.size} tasks", showKanban,
                        { showKanban = !showKanban }) {
                        if (tasks.isEmpty()) {
                            if (isOwner) EmptyTaskOwner(projectId, currentUserId)
                            else         EmptyTaskMember()
                        } else {
                            LightKanban(tasks, projectId, memberNames) { selectedTask = it }
                        }
                    }
                }

                if (yourTasks.isNotEmpty()) {
                    item {
                        LightSection("Your Tasks  ·  ${yourTasks.size}", showYours,
                            { showYours = !showYours }) {
                            yourTasks.forEach { task ->
                                LightTaskCard(task, projectId, currentUserId, memberNames,
                                    showSlider = true, onClick = { selectedTask = task })
                            }
                        }
                    }
                }

                item {
                    LightSection("All Tasks  ·  ${tasks.size}", showAll,
                        { showAll = !showAll }) {
                        tasks.forEach { task ->
                            LightTaskCard(task, projectId, currentUserId, memberNames,
                                showSlider = task.assignedTo == currentUserId,
                                onClick = { selectedTask = task })
                        }
                    }
                }

                item {
                    LightSection("Timeline", showTimeline, { showTimeline = !showTimeline }) {
                        tasks.forEach { LightGanttRow(it) }
                    }
                }

            } else {
                item {
                    Box(Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center) {
                        LightChip("Join this project to see tasks & chat",
                            PrimaryLight, Primary)
                    }
                }
            }
        }
    }
}

/* ══ TASK DETAIL BOTTOM SHEET ════════════════════════════════════════════════ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailSheet(
    task: Task, projectId: String, currentUid: String,
    memberNames: Map<String, String>, members: List<String>,
    canEdit: Boolean, onDismiss: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    var comments       by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var newComment     by remember { mutableStateOf("") }
    var progress       by remember { mutableStateOf(taskProgress(task)) }
    var memberExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(task.id) {
        firestore.collection("projects").document(projectId)
            .collection("tasks").document(task.id)
            .collection("comments").orderBy("createdAt")
            .addSnapshotListener { snap, _ ->
                comments = snap?.documents?.mapNotNull { it.data } ?: emptyList()
            }
        firestore.collection("projects").document(projectId)
            .collection("tasks").document(task.id)
            .get().addOnSuccessListener { doc ->
                progress = (doc.getLong("progress")?.toFloat() ?: (taskProgress(task) * 100f)) / 100f
            }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = CardBg
    ) {
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            /* header */
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                Text(task.title, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    color = TextPrimary, modifier = Modifier.weight(1f))
                LightChip(sLabel(task.status), sBg(task.status), sColor(task.status))
            }
            if (task.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(task.description, fontSize = 14.sp, color = TextSub, lineHeight = 22.sp)
            }
            Spacer(Modifier.height(16.dp))

            /* assignee */
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LightAvatar(memberNames[task.assignedTo] ?: "?", Primary, 40)
                Column {
                    Text(memberNames[task.assignedTo] ?: task.assignedTo,
                        fontSize = 15.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    if (task.startDate > 0)
                        Text("Started ${timeAgo(task.startDate)}", fontSize = 12.sp, color = TextSub)
                    if (task.deadline > 0) {
                        val over = task.deadline < System.currentTimeMillis() && task.status != "DONE"
                        Text(
                            "Due ${SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(task.deadline))}",
                            fontSize = 12.sp,
                            color = if (over) Color(0xFFEF4444) else Color(0xFFD97706)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Divider(color = Border)
            Spacer(Modifier.height(16.dp))

            /* progress */
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress", fontSize = 13.sp, color = TextSub, fontWeight = FontWeight.SemiBold)
                Text("${(progress * 100).toInt()}%", fontSize = 13.sp,
                    color = Primary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress   = progress,
                modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color      = Primary,
                trackColor = Border,
                strokeCap  = StrokeCap.Round
            )

            if (task.assignedTo == currentUid && canEdit) {
                Slider(
                    value         = progress,
                    onValueChange = { progress = it },
                    onValueChangeFinished = {
                        val pct = (progress * 100).toInt()
                        val ns  = when { pct >= 100 -> "DONE"; pct > 0 -> "IN_PROGRESS"; else -> "TODO" }
                        firestore.collection("projects").document(projectId)
                            .collection("tasks").document(task.id)
                            .update(mapOf("progress" to pct, "status" to ns))
                    },
                    colors = SliderDefaults.colors(
                        thumbColor         = Primary,
                        activeTrackColor   = Primary,
                        inactiveTrackColor = Border
                    )
                )
                /* quick presets */
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(25, 50, 75, 100).forEach { pct ->
                        OutlinedButton(
                            onClick = {
                                progress = pct / 100f
                                val ns = if (pct >= 100) "DONE" else "IN_PROGRESS"
                                firestore.collection("projects").document(projectId)
                                    .collection("tasks").document(task.id)
                                    .update(mapOf("progress" to pct, "status" to ns))
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape  = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Primary.copy(.4f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) { Text("$pct%", fontSize = 12.sp) }
                    }
                }
            }

            /* reassign */
            if (canEdit) {
                Spacer(Modifier.height(16.dp))
                Divider(color = Border)
                Spacer(Modifier.height(16.dp))
                Text("Reassign Task", fontSize = 13.sp, color = TextSub,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(memberExpanded, { memberExpanded = !memberExpanded }) {
                    OutlinedTextField(
                        value         = memberNames[task.assignedTo] ?: task.assignedTo,
                        onValueChange = {},
                        readOnly      = true,
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(memberExpanded) },
                        modifier      = Modifier.menuAnchor().fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(memberExpanded, { memberExpanded = false }) {
                        members.forEach { uid ->
                            DropdownMenuItem(
                                text = { Text(memberNames[uid] ?: uid) },
                                onClick = {
                                    firestore.collection("projects").document(projectId)
                                        .collection("tasks").document(task.id)
                                        .update("assignedTo", uid)
                                    memberExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Divider(color = Border)
            Spacer(Modifier.height(16.dp))

            /* comments */
            Text("Comments  ·  ${comments.size}", fontSize = 13.sp,
                color = TextSub, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            if (comments.isEmpty()) {
                Text("No comments yet. Be the first!",
                    fontSize = 13.sp, color = TextHint,
                    modifier = Modifier.padding(vertical = 8.dp))
            } else {
                comments.forEach { c ->
                    LightCommentBubble(
                        author = c["authorName"] as? String ?: "User",
                        text   = c["text"] as? String ?: "",
                        time   = c["createdAt"] as? Long ?: 0L,
                        isMe   = c["authorId"] as? String == currentUid
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value         = newComment,
                    onValueChange = { newComment = it },
                    placeholder   = { Text("Write a comment…", fontSize = 14.sp) },
                    singleLine    = true,
                    modifier      = Modifier.weight(1f),
                    shape         = RoundedCornerShape(14.dp)
                )
                IconButton(
                    onClick = {
                        if (newComment.isNotBlank()) {
                            val uid  = FirebaseAuth.getInstance().currentUser?.uid ?: return@IconButton
                            val name = FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
                            firestore.collection("projects").document(projectId)
                                .collection("tasks").document(task.id)
                                .collection("comments")
                                .add(mapOf("authorId" to uid, "authorName" to name,
                                    "text" to newComment.trim(),
                                    "createdAt" to System.currentTimeMillis()))
                            newComment = ""
                        }
                    },
                    modifier = Modifier.size(48.dp).background(Primary, RoundedCornerShape(14.dp))
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

/* ══ KANBAN ══════════════════════════════════════════════════════════════════ */

@Composable
fun LightKanban(
    tasks: List<Task>, projectId: String,
    memberNames: Map<String, String>, onTaskClick: (Task) -> Unit
) {
    Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp)) {
        listOf(
            "TODO"        to Primary,
            "IN_PROGRESS" to Color(0xFFD97706),
            "DONE"        to Color(0xFF16A34A)
        ).forEach { (status, color) ->
            LightKanbanCol(status, color, tasks.filter { it.status == status },
                projectId, memberNames, onTaskClick)
        }
    }
}

@Composable
private fun LightKanbanCol(
    status: String, color: Color, tasks: List<Task>,
    projectId: String, memberNames: Map<String, String>, onTaskClick: (Task) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    Column(Modifier.width(220.dp).padding(horizontal = 6.dp)) {
        /* column header */
        Row(
            Modifier.fillMaxWidth()
                .background(color.copy(.10f), RoundedCornerShape(10.dp))
                .border(1.dp, color.copy(.2f), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(8.dp).background(color, CircleShape))
                Text(sLabel(status), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
            }
            Text("${tasks.size}", fontSize = 12.sp, color = color.copy(.7f))
        }
        Spacer(Modifier.height(8.dp))

        if (tasks.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().height(60.dp)
                    .background(CardBg2, RoundedCornerShape(12.dp))
                    .border(1.dp, Border, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Empty", color = TextHint, fontSize = 13.sp)
            }
        } else {
            tasks.forEach { task ->
                Card(
                    onClick   = { onTaskClick(task) },
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = CardBg),
                    border    = BorderStroke(1.dp, Border),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        /* accent strip + title */
                        Row(verticalAlignment = Alignment.Top) {
                            Box(Modifier.width(3.dp).height(16.dp)
                                .background(color, RoundedCornerShape(2.dp)))
                            Spacer(Modifier.width(8.dp))
                            Text(task.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                color = TextPrimary, maxLines = 2,
                                overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                LightAvatar(memberNames[task.assignedTo] ?: "?", Primary, 22)
                                Text((memberNames[task.assignedTo] ?: "").take(10),
                                    fontSize = 11.sp, color = TextSub)
                            }
                            if (task.startDate > 0)
                                Text(timeAgo(task.startDate), fontSize = 10.sp, color = TextHint)
                        }
                        /* quick move */
                        if (task.status != "DONE") {
                            Spacer(Modifier.height(8.dp))
                            val next = if (task.status == "TODO") "IN_PROGRESS" else "DONE"
                            val lbl  = if (task.status == "TODO") "→ Start" else "✓ Done"
                            OutlinedButton(
                                onClick = {
                                    firestore.collection("projects").document(projectId)
                                        .collection("tasks").document(task.id)
                                        .update(mapOf("status" to next,
                                            "progress" to if (next == "DONE") 100 else 50))
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape  = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, color.copy(.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
                                modifier = Modifier.fillMaxWidth().height(28.dp)
                            ) {
                                Text(lbl, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

/* ══ LIGHT TASK CARD ═════════════════════════════════════════════════════════ */

@Composable
fun LightTaskCard(
    task: Task, projectId: String, currentUid: String,
    memberNames: Map<String, String>, showSlider: Boolean, onClick: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    var progress  by remember(task.id) { mutableStateOf(taskProgress(task)) }
    val animProg  by animateFloatAsState(progress, tween(600), label = "p")

    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        border    = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            /* title + status */
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.width(3.dp).height(18.dp)
                        .background(sColor(task.status), RoundedCornerShape(2.dp)))
                    Spacer(Modifier.width(10.dp))
                    Text(task.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                        color = TextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.width(8.dp))
                LightChip(sLabel(task.status), sBg(task.status), sColor(task.status))
            }

            if (task.description.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(task.description, fontSize = 12.sp, color = TextSub,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }

            Spacer(Modifier.height(12.dp))

            /* assignee + deadline */
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LightAvatar(memberNames[task.assignedTo] ?: "?", Primary, 34)
                    Column {
                        Text(memberNames[task.assignedTo] ?: task.assignedTo,
                            fontSize = 13.sp, color = TextPrimary)
                        if (task.startDate > 0)
                            Text(timeAgo(task.startDate), fontSize = 11.sp, color = TextHint)
                    }
                }
                if (task.deadline > 0) {
                    val over = task.deadline < System.currentTimeMillis() && task.status != "DONE"
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.DateRange, null,
                            tint = if (over) Color(0xFFEF4444) else Color(0xFFD97706),
                            modifier = Modifier.size(12.dp))
                        Text(
                            SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(task.deadline)),
                            fontSize = 11.sp,
                            color = if (over) Color(0xFFEF4444) else Color(0xFFD97706)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /* progress */
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Progress", fontSize = 11.sp, color = TextSub)
                Text("${(animProg * 100).toInt()}%", fontSize = 11.sp,
                    color = Primary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(5.dp))
            LinearProgressIndicator(
                progress   = animProg,
                modifier   = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                color      = Primary,
                trackColor = Border,
                strokeCap  = StrokeCap.Round
            )

            if (showSlider) {
                Slider(
                    value         = progress,
                    onValueChange = { progress = it },
                    onValueChangeFinished = {
                        val pct = (progress * 100).toInt()
                        val ns  = when { pct >= 100 -> "DONE"; pct > 0 -> "IN_PROGRESS"; else -> "TODO" }
                        firestore.collection("projects").document(projectId)
                            .collection("tasks").document(task.id)
                            .update(mapOf("progress" to pct, "status" to ns))
                    },
                    colors = SliderDefaults.colors(
                        thumbColor         = Primary,
                        activeTrackColor   = Primary,
                        inactiveTrackColor = Border
                    )
                )
            }

            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ChatBubbleOutline, null,
                    tint = TextHint, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text("Tap for details & comments", fontSize = 11.sp, color = TextHint)
            }
        }
    }
}

/* ══ GANTT ═══════════════════════════════════════════════════════════════════ */

@Composable
private fun LightGanttRow(task: Task) {
    val prog    = taskProgress(task)
    val animProg by animateFloatAsState(prog, tween(800), label = "g")
    Card(
        modifier  = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        border    = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(8.dp).background(sColor(task.status), CircleShape))
            Text(task.title, fontSize = 13.sp, color = TextPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Box(Modifier.width(100.dp).height(7.dp).clip(RoundedCornerShape(4.dp)).background(Border)) {
                Box(Modifier.fillMaxWidth(animProg).height(7.dp)
                    .clip(RoundedCornerShape(4.dp)).background(sColor(task.status)))
            }
            Text("${(animProg * 100).toInt()}%", fontSize = 11.sp,
                color = sColor(task.status), fontWeight = FontWeight.Bold,
                modifier = Modifier.width(32.dp))
        }
    }
}

/* ══ EMPTY STATES ════════════════════════════════════════════════════════════ */

@Composable
private fun EmptyTaskOwner(projectId: String, currentUserId: String) {
    var taskTitle by remember { mutableStateOf("") }
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, Border)) {
        Column(Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🚀", fontSize = 36.sp)
            Spacer(Modifier.height(8.dp))
            Text("No Tasks Yet", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("Create the first task to get started",
                fontSize = 13.sp, color = TextSub, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value         = taskTitle,
                onValueChange = { taskTitle = it },
                placeholder   = { Text("Task title", fontSize = 14.sp) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp)
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
            ) { Text("+ Create First Task", fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun EmptyTaskMember() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, Border)) {
        Column(Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📋", fontSize = 36.sp)
            Spacer(Modifier.height(8.dp))
            Text("No tasks yet", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("The project owner hasn't added any tasks yet.\nCheck back soon!",
                fontSize = 13.sp, color = TextSub, textAlign = TextAlign.Center)
        }
    }
}

/* ══ COLLAPSIBLE SECTION ═════════════════════════════════════════════════════ */

@Composable
private fun LightSection(
    title: String, expanded: Boolean, onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val rot by animateFloatAsState(if (expanded) 180f else 0f, label = "r")
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        border    = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().clickable(onClick = onToggle)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Icon(Icons.Default.KeyboardArrowDown, null, tint = TextSub,
                    modifier = Modifier.size(20.dp).rotate(rot))
            }
            AnimatedVisibility(expanded,
                enter = expandVertically() + fadeIn(),
                exit  = shrinkVertically() + fadeOut()) {
                Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    content = content
                )
            }
        }
    }
}

/* ══ ATOMS ═══════════════════════════════════════════════════════════════════ */

@Composable
fun LightChip(text: String, bg: Color, fg: Color) {
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
private fun LightInfoRow(label: String, value: String) {
    Column(Modifier.padding(bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, fontSize = 12.sp, color = TextSub, fontWeight = FontWeight.SemiBold)
        Text(value, fontSize = 14.sp, color = TextPrimary, lineHeight = 22.sp)
    }
}

@Composable
private fun LightStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(CardBg2, RoundedCornerShape(12.dp))
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 11.sp, color = TextSub)
        Spacer(Modifier.height(3.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun LightAvatar(name: String, color: Color, size: Int = 36) {
    Box(
        Modifier.size(size.dp)
            .background(color.copy(.15f), CircleShape)
            .border(1.dp, color.copy(.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(name.firstOrNull()?.uppercase() ?: "?",
            color = color, fontWeight = FontWeight.Bold,
            fontSize = (size / 2.6).sp)
    }
}

@Composable
private fun LightMemberRow(name: String, isOwner: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LightAvatar(name, if (isOwner) Primary else Color(0xFF64748B), 38)
        Text(name, fontSize = 14.sp, color = TextPrimary,
            fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        if (isOwner) LightChip("Owner", PrimaryLight, Primary)
        else Icon(Icons.Default.ChevronRight, null, tint = TextHint, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun LightCommentBubble(author: String, text: String, time: Long, isMe: Boolean) {
    Row(Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start) {
        if (!isMe) {
            LightAvatar(author, Primary, 30)
            Spacer(Modifier.width(8.dp))
        }
        Column(
            Modifier.weight(1f, false).widthIn(max = 260.dp)
                .background(
                    if (isMe) PrimaryLight else CardBg2,
                    RoundedCornerShape(
                        topStart    = if (isMe) 14.dp else 4.dp,
                        topEnd      = if (isMe) 4.dp else 14.dp,
                        bottomStart = 14.dp, bottomEnd = 14.dp
                    )
                )
                .border(1.dp, Border, RoundedCornerShape(
                    topStart = if (isMe) 14.dp else 4.dp,
                    topEnd   = if (isMe) 4.dp else 14.dp,
                    bottomStart = 14.dp, bottomEnd = 14.dp))
                .padding(10.dp)
        ) {
            if (!isMe) {
                Text(author, fontSize = 11.sp, color = Primary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
            }
            Text(text, fontSize = 13.sp, color = TextPrimary)
            Spacer(Modifier.height(3.dp))
            Text(timeAgo(time), fontSize = 10.sp, color = TextHint,
                modifier = Modifier.align(Alignment.End))
        }
        if (isMe) {
            Spacer(Modifier.width(8.dp))
            LightAvatar(author, Primary, 30)
        }
    }
}

fun addTask(projectId: String, title: String, userId: String) {
    FirebaseFirestore.getInstance()
        .collection("projects").document(projectId).collection("tasks")
        .add(hashMapOf(
            "title" to title, "assignedTo" to userId, "status" to "TODO",
            "progress" to 0, "description" to "",
            "startDate" to System.currentTimeMillis(), "deadline" to 0L,
            "createdAt" to System.currentTimeMillis()
        ))
}
