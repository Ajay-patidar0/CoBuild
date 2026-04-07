//package com.example.cobuild.ui.project
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.outlined.FolderOpen
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.cobuild.ui.project.components.ProjectCard
//import com.example.cobuild.ui.project.components.ProjectTabSwitcher
//import com.google.firebase.auth.FirebaseAuth
//
//// --- Theme Colors ---
//private val BackgroundColor = Color(0xFFF8FAFC)
//private val TextPrimary = Color(0xFF1E293B)
//private val TextSecondary = Color(0xFF64748B)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProjectListScreen(
//    onBackClick: () -> Unit,
//    onProjectClick: (String) -> Unit,
//    // Use viewModel() factory for correct lifecycle management
//    viewModel: ProjectListViewModel = viewModel()
//) {
//    val userId = FirebaseAuth.getInstance().currentUser?.uid
//    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Joined, 1 = Posted
//
//    // Trigger data load
//    LaunchedEffect(userId) {
//        userId?.let { viewModel.loadProjects(it) }
//    }
//
//    val joinedProjects by viewModel.joinedProjects.collectAsState()
//    val postedProjects by viewModel.postedProjects.collectAsState()
//
//    val projects = if (selectedTab == 0) joinedProjects else postedProjects
//
//    Scaffold(
//        containerColor = BackgroundColor,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        "My Projects",
//                        fontWeight = FontWeight.SemiBold,
//                        color = TextPrimary
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back",
//                            tint = TextPrimary
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//
//            // Tab Switcher
//            ProjectTabSwitcher(
//                selectedTab = selectedTab,
//                onTabChange = { selectedTab = it }
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Content List
//            if (projects.isEmpty()) {
//                // Empty State
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Icon(
//                            imageVector = Icons.Outlined.FolderOpen,
//                            contentDescription = null,
//                            tint = TextSecondary.copy(alpha = 0.5f),
//                            modifier = Modifier.size(64.dp)
//                        )
//                        Spacer(modifier = Modifier.height(12.dp))
//                        Text(
//                            text = if (selectedTab == 0) "You haven't joined any projects yet."
//                            else "You haven't posted any projects yet.",
//                            color = TextSecondary,
//                            fontSize = 16.sp
//                        )
//                    }
//                }
//            } else {
//                LazyColumn(
//                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    items(projects) { project ->
//                        ProjectCard(
//                            project = project,
//                            onClick = { onProjectClick(project.id) }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

package com.example.cobuild.ui.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cobuild.ui.project.components.MembersBottomSheet
import com.example.cobuild.ui.project.components.ProjectCard
import com.example.cobuild.ui.project.components.ProjectTabSwitcher
import com.google.firebase.auth.FirebaseAuth

/* ---------- THEME COLORS ---------- */
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    onBackClick: () -> Unit,
    onProjectClick: (String) -> Unit,
    onEditProject: (String) -> Unit,
    viewModel: ProjectListViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Joined, 1 = Posted

    // 🔥 Members bottom sheet state
    var showMembers by remember { mutableStateOf(false) }
    var selectedMembers by remember { mutableStateOf<List<String>>(emptyList()) }

    // Load data
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadProjects(it) }
    }

    val joinedProjects by viewModel.joinedProjects.collectAsState()
    val postedProjects by viewModel.postedProjects.collectAsState()

    val projects = if (selectedTab == 0) joinedProjects else postedProjects

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Projects",
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            /* ---------- TAB SWITCHER ---------- */
            ProjectTabSwitcher(
                selectedTab = selectedTab,
                onTabChange = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            /* ---------- CONTENT ---------- */
            if (projects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.FolderOpen,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text =
                                if (selectedTab == 0)
                                    "You haven't joined any projects yet."
                                else
                                    "You haven't posted any projects yet.",
                            color = TextSecondary,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(projects) { project ->

                        val isPostedProject = selectedTab == 1

                        ProjectCard(
                            project = project,

                            // Card click → open project details
                            onClick = {
                                onProjectClick(project.id)
                            },

                            onMembersClick = { members ->
                                selectedMembers = members
                                showMembers = true
                            },

                            // Edit icon only for posted projects
                            canEdit = isPostedProject,

                            // Edit icon click → open edit screen
                            onEditClick = {
                                onEditProject(project.id)
                            }
                        )
                    }
                }
            }
        }
    }

    /* ---------- MEMBERS BOTTOM SHEET ---------- */
    if (showMembers) {
        MembersBottomSheet(
            members = selectedMembers,
            onDismiss = { showMembers = false }
        )
    }
}


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
//import androidx.compose.material.icons.filled.Link
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
//    var memberNames by remember { mutableStateOf<Map<String,String>>(emptyMap()) }
//
//    LaunchedEffect(projectId) {
//
//        viewModel.loadProjects()
//
//        firestore.collection("projects")
//            .document(projectId)
//            .collection("tasks")
//            .addSnapshotListener { snapshot, _ ->
//
//                tasks = snapshot?.documents?.mapNotNull {
//                    it.toObject(Task::class.java)?.copy(id = it.id)
//                } ?: emptyList()
//            }
//
//        firestore.collection("users")
//            .get()
//            .addOnSuccessListener { snapshot ->
//
//                val map = mutableMapOf<String,String>()
//
//                snapshot.documents.forEach {
//
//                    val uid = it.id
//                    val name = it.getString("name") ?: "User"
//
//                    map[uid] = name
//                }
//
//                memberNames = map
//            }
//    }
//
//    val yourTasks = tasks.filter { it.assignedTo == currentUserId }
//
//    Scaffold(
//        containerColor = BackgroundColor,
//
//        topBar = {
//            TopAppBar(
//                title = { Text("Project Details") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack,null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//
//        if (proj == null) {
//
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//
//            return@Scaffold
//        }
//
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .padding(20.dp)
//        ) {
//
//            /* ---------------- PROJECT INFO ---------------- */
//
//            item {
//
//                Text(
//                    proj.title,
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight.Bold
//                )
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
//                if (proj.skills.isNotEmpty()) {
//
//                    Text("Required Skills", fontWeight = FontWeight.SemiBold)
//
//                    Spacer(Modifier.height(8.dp))
//
//                    FlowRow(
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//
//                        proj.skills.forEach {
//
//                            AssistChip(
//                                onClick = {},
//                                label = { Text(it) }
//                            )
//                        }
//                    }
//
//                    Spacer(Modifier.height(20.dp))
//                }
//            }
//
//            /* ---------------- KANBAN BOARD ---------------- */
//
//            if(tasks.isNotEmpty()) {
//
//                item {
//
//                    Text("Kanban Board", fontWeight = FontWeight.Bold)
//
//                    Spacer(Modifier.height(10.dp))
//
//                    KanbanBoard(
//                        tasks = tasks,
//                        projectId = projectId,
//                        currentUserId = currentUserId,
//                        memberNames = memberNames
//                    )
//                }
//
//                /* ---------------- TIMELINE ---------------- */
//
//                item {
//
//                    Spacer(Modifier.height(30.dp))
//
//                    Text("Timeline", fontWeight = FontWeight.Bold)
//
//                    Spacer(Modifier.height(10.dp))
//                }
//
//                items(tasks) {
//
//                    GanttTask(it)
//                }
//            }
//
//            /* ---------------- YOUR TASKS ---------------- */
//
//            if(yourTasks.isNotEmpty()) {
//
//                item {
//
//                    Spacer(Modifier.height(30.dp))
//
//                    Text("Your Tasks", fontWeight = FontWeight.Bold)
//
//                    Spacer(Modifier.height(10.dp))
//                }
//
//                items(yourTasks) {
//
//                    TaskCard(it,currentUserId,projectId,memberNames)
//                }
//            }
//
//            /* ---------------- ALL TASKS ---------------- */
//
//            if(tasks.isNotEmpty()) {
//
//                item {
//
//                    Spacer(Modifier.height(30.dp))
//
//                    Text("All Tasks", fontWeight = FontWeight.Bold)
//
//                    Spacer(Modifier.height(10.dp))
//                }
//
//                items(tasks) {
//
//                    TaskCard(it,currentUserId,projectId,memberNames)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun KanbanBoard(
//    tasks: List<Task>,
//    projectId: String,
//    currentUserId: String,
//    memberNames: Map<String,String>
//){
//
//    val todo = tasks.filter { it.status == "TODO" }
//    val progress = tasks.filter { it.status == "IN_PROGRESS" }
//    val done = tasks.filter { it.status == "DONE" }
//
//    Row(
//        modifier = Modifier
//            .horizontalScroll(rememberScrollState())
//    ) {
//
//        KanbanColumn("TODO",todo,projectId,currentUserId,memberNames)
//        KanbanColumn("IN PROGRESS",progress,projectId,currentUserId,memberNames)
//        KanbanColumn("DONE",done,projectId,currentUserId,memberNames)
//    }
//}
//
//@Composable
//fun KanbanColumn(
//    title:String,
//    tasks:List<Task>,
//    projectId:String,
//    currentUserId:String,
//    memberNames: Map<String,String>
//){
//
//    Column(
//        modifier = Modifier
//            .width(280.dp)
//            .padding(8.dp)
//    ){
//
//        Text(title,fontWeight = FontWeight.Bold)
//
//        Spacer(Modifier.height(10.dp))
//
//        tasks.forEach{
//
//            TaskCard(it,currentUserId,projectId,memberNames)
//        }
//    }
//}
//
//@Composable
//fun GanttTask(task: Task){
//
//    val progress = taskProgress(task)
//
//    Column(
//        modifier = Modifier.padding(vertical = 6.dp)
//    ){
//
//        Text(task.title)
//
//        Spacer(Modifier.height(4.dp))
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(10.dp)
//                .background(Color.LightGray)
//        ){
//
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
//@Composable
//fun TaskCard(
//    task: Task,
//    currentUserId: String,
//    projectId: String,
//    memberNames: Map<String,String>
//){
//
//    val firestore = FirebaseFirestore.getInstance()
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 6.dp),
//        shape = RoundedCornerShape(12.dp)
//    ){
//
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ){
//
//            Text(task.title,fontWeight = FontWeight.SemiBold)
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
//            AssistChip(
//                onClick = {},
//                label = { Text(task.status) }
//            )
//
//            Spacer(Modifier.height(10.dp))
//
//            if (task.assignedTo == currentUserId && task.status != "DONE") {
//
//                Button(
//                    onClick = {
//
//                        firestore.collection("projects")
//                            .document(projectId)
//                            .collection("tasks")
//                            .document(task.id)
//                            .update("status","DONE")
//                    }
//                ) {
//
//                    Icon(Icons.Default.Check,null)
//                    Spacer(Modifier.width(6.dp))
//                    Text("Mark Done")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun Section(title:String,value:String){
//
//    Text(title,fontWeight = FontWeight.SemiBold)
//
//    Spacer(Modifier.height(4.dp))
//
//    Text(value,color = TextSecondary)
//
//    Spacer(Modifier.height(16.dp))
//}
//
//@Composable
//private fun RoleBadge(text:String) {
//
//    Surface(
//        color = PrimaryColor.copy(alpha = 0.12f),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//
//        Text(
//            text = text,
//            color = PrimaryColor,
//            fontWeight = FontWeight.SemiBold,
//            fontSize = 12.sp,
//            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
//        )
//    }
//}