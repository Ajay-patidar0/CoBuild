//package com.example.cobuild.ui.project
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.cobuild.data.model.Task
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EditProjectScreen(
//    projectId: String,
//    onBack: () -> Unit
//) {
//
//    val firestore = FirebaseFirestore.getInstance()
//    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
//
//    var title by remember { mutableStateOf("") }
//    var description by remember { mutableStateOf("") }
//
//    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
//    var members by remember { mutableStateOf<List<String>>(emptyList()) }
//
//    var newTaskTitle by remember { mutableStateOf("") }
//    var selectedMember by remember { mutableStateOf("") }
//    var deadline by remember { mutableStateOf("") }
//
//    var memberNames by remember { mutableStateOf<Map<String,String>>(emptyMap()) }
//
//    var expanded by remember { mutableStateOf(false) }
//
//    /* ---------------- LOAD PROJECT ---------------- */
//
//    LaunchedEffect(projectId) {
//
//        firestore.collection("projects")
//            .document(projectId)
//            .addSnapshotListener { doc, _ ->
//
//                doc?.let {
//
//                    title = it.getString("title") ?: ""
//                    description = it.getString("description") ?: ""
//
//                    members = it.get("members") as? List<String> ?: emptyList()
//                }
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
//
//        firestore.collection("projects")
//            .document(projectId)
//            .collection("tasks")
//            .addSnapshotListener { snapshot, _ ->
//
//                tasks = snapshot?.documents?.mapNotNull { doc ->
//                    doc.toObject(Task::class.java)?.copy(id = doc.id)
//                } ?: emptyList()
//            }
//    }
//
//    Scaffold(
//
//        topBar = {
//            TopAppBar(
//                title = { Text("Edit Project") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.ArrowBack,null)
//                    }
//                }
//            )
//        },
//
//        floatingActionButton = {
//
//            FloatingActionButton(
//                onClick = {
//
//                    if (newTaskTitle.isBlank()) return@FloatingActionButton
//
//                    val taskId = firestore.collection("tmp").document().id
//
//                    val task = Task(
//                        id = taskId,
//                        title = newTaskTitle.trim(),
//                        assignedTo = selectedMember,
//                        deadline = deadline.toLongOrNull() ?: 0
//                    )
//
//                    firestore.collection("projects")
//                        .document(projectId)
//                        .collection("tasks")
//                        .document(taskId)
//                        .set(task)
//
//                    newTaskTitle = ""
//                    selectedMember = ""
//                    deadline = ""
//                }
//            ) {
//                Icon(Icons.Default.Add,null)
//            }
//        }
//
//    ) { padding ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp)
//        ) {
//
//            Text("Project Info",fontSize = 18.sp)
//
//            Spacer(Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = title,
//                onValueChange = { title = it },
//                label = { Text("Project Title") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = description,
//                onValueChange = { description = it },
//                label = { Text("Description") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(10.dp))
//
//            Button(
//                onClick = {
//
//                    firestore.collection("projects")
//                        .document(projectId)
//                        .update(
//                            mapOf(
//                                "title" to title,
//                                "description" to description
//                            )
//                        )
//                }
//            ) {
//                Text("Save Project")
//            }
//
//            Spacer(Modifier.height(24.dp))
//
//            Text("Create Task",fontSize = 18.sp)
//
//            Spacer(Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = newTaskTitle,
//                onValueChange = { newTaskTitle = it },
//                label = { Text("Task Title") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(8.dp))
//
//            /* ---------- MEMBER DROPDOWN ---------- */
//
//            ExposedDropdownMenuBox(
//                expanded = expanded,
//                onExpandedChange = { expanded = !expanded }
//            ) {
//
//                OutlinedTextField(
//                    value = memberNames[selectedMember] ?: "",
//                    onValueChange = {},
//                    readOnly = true,
//                    label = { Text("Assign Member") },
//                    modifier = Modifier
//                        .menuAnchor()
//                        .fillMaxWidth()
//                )
//
//                ExposedDropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//
//                    members.forEach { uid ->
//
//                        DropdownMenuItem(
//                            text = { Text(memberNames[uid] ?: uid) },
//                            onClick = {
//
//                                selectedMember = uid
//                                expanded = false
//                            }
//                        )
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(8.dp))
//
//            OutlinedTextField(
//                value = deadline,
//                onValueChange = { deadline = it },
//                label = { Text("Deadline (timestamp)") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(20.dp))
//
//            Text("Project Tasks",fontSize = 20.sp)
//
//            Spacer(Modifier.height(10.dp))
//
//            LazyColumn {
//
//                items(tasks) { task ->
//
//                    TaskCard(
//                        task = task,
//                        projectId = projectId,
//                        currentUser = currentUser,
//                        memberNames = memberNames
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun TaskCard(
//    task: Task,
//    projectId: String,
//    currentUser: String?,
//    memberNames: Map<String,String>
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
//
//        Row(
//            modifier = Modifier.padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//
//            Column {
//
//                Text(task.title,fontSize = 16.sp)
//
//                Spacer(Modifier.height(4.dp))
//
//                Text(
//                    "Assigned: ${memberNames[task.assignedTo] ?: task.assignedTo}",
//                    fontSize = 12.sp
//                )
//
//                Spacer(Modifier.height(4.dp))
//
//                AssistChip(
//                    onClick = {},
//                    label = { Text(task.status) }
//                )
//            }
//
//            if (task.assignedTo == currentUser && task.status != "DONE") {
//
//                IconButton(
//                    onClick = {
//
//                        firestore.collection("projects")
//                            .document(projectId)
//                            .collection("tasks")
//                            .document(task.id)
//                            .update("status","DONE")
//                    }
//                ) {
//                    Icon(Icons.Default.Check,null)
//                }
//            }
//        }
//    }
//}

package com.example.cobuild.ui.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cobuild.data.model.Task
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
private val ErrorRed    = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProjectScreen(
    projectId: String,
    onBack: () -> Unit
) {
    val firestore   = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    /* ── project fields ── */
    var title           by remember { mutableStateOf("") }
    var description     by remember { mutableStateOf("") }
    var goal            by remember { mutableStateOf("") }
    var timeline        by remember { mutableStateOf("") }
    var teamSize        by remember { mutableStateOf("") }
    var projectType     by remember { mutableStateOf("") }
    var commitmentLevel by remember { mutableStateOf("") }
    var experienceLevel by remember { mutableStateOf("") }
    var link            by remember { mutableStateOf("") }
    var skills          by remember { mutableStateOf<List<String>>(emptyList()) }
    var newSkill        by remember { mutableStateOf("") }

    /* ── members / tasks ── */
    var members     by remember { mutableStateOf<List<String>>(emptyList()) }
    var memberNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var tasks       by remember { mutableStateOf<List<Task>>(emptyList()) }

    /* ── new task form ── */
    var newTaskTitle  by remember { mutableStateOf("") }
    var newTaskDesc   by remember { mutableStateOf("") }
    var selectedMember by remember { mutableStateOf("") }
    var memberDropdown by remember { mutableStateOf(false) }

    /* ── ui state ── */
    var saveSuccess by remember { mutableStateOf(false) }
    var showAddTask by remember { mutableStateOf(false) }

    /* ── load ── */
    LaunchedEffect(projectId) {
        firestore.collection("projects").document(projectId)
            .addSnapshotListener { doc, _ ->
                doc?.let {
                    title           = it.getString("title")           ?: ""
                    description     = it.getString("description")     ?: ""
                    goal            = it.getString("goal")            ?: ""
                    timeline        = it.getString("timeline")        ?: ""
                    teamSize        = it.getString("teamSize")        ?: ""
                    projectType     = it.getString("projectType")     ?: ""
                    commitmentLevel = it.getString("commitmentLevel") ?: ""
                    experienceLevel = it.getString("experienceLevel") ?: ""
                    link            = it.getString("link")            ?: ""
                    skills          = it.get("skills") as? List<String> ?: emptyList()
                    members         = it.get("members") as? List<String> ?: emptyList()
                }
            }

        firestore.collection("users").get().addOnSuccessListener { snap ->
            memberNames = snap.documents.associate { it.id to (it.getString("name") ?: "User") }
        }

        firestore.collection("projects").document(projectId)
            .collection("tasks")
            .addSnapshotListener { snap, _ ->
                tasks = snap?.documents?.mapNotNull {
                    it.toObject(Task::class.java)?.copy(id = it.id)
                } ?: emptyList()
            }
    }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Project", color = Color.White,
                        fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            firestore.collection("projects").document(projectId)
                                .update(mapOf(
                                    "title"           to title,
                                    "description"     to description,
                                    "goal"            to goal,
                                    "timeline"        to timeline,
                                    "teamSize"        to teamSize,
                                    "projectType"     to projectType,
                                    "commitmentLevel" to commitmentLevel,
                                    "experienceLevel" to experienceLevel,
                                    "link"            to link,
                                    "skills"          to skills
                                ))
                            saveSuccess = true
                        }
                    ) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBar)
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {

            /* ── SAVE BANNER ── */
            if (saveSuccess) {
                item {
                    Surface(color = Color(0xFF22C55E), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Project saved successfully", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }

            /* ── SECTION: PROJECT INFO ── */
            item {
                SectionCard(title = "Project Info") {
                    EditField("Project Title", title) { title = it }
                    EditField("Description", description, minLines = 3) { description = it }
                    EditField("Goal", goal, minLines = 2) { goal = it }
                    EditField("Link (optional)", link) { link = it }
                }
            }

            /* ── SECTION: PROJECT DETAILS ── */
            item {
                SectionCard(title = "Project Details") {

                    /* commitment */
                    ChipSelector(
                        label    = "Commitment Level",
                        options  = listOf("Low", "Medium", "High"),
                        selected = commitmentLevel,
                        onSelect = { commitmentLevel = it }
                    )

                    /* experience */
                    ChipSelector(
                        label    = "Experience Level",
                        options  = listOf("Beginner", "Intermediate", "Advanced"),
                        selected = experienceLevel,
                        onSelect = { experienceLevel = it }
                    )

                    /* project type */
                    ChipSelector(
                        label    = "Project Type",
                        options  = listOf("Mobile App", "Web App", "ML / AI", "Open Source", "Research", "Other"),
                        selected = projectType,
                        onSelect = { projectType = it }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            EditField("Timeline", timeline) { timeline = it }
                        }
                        Box(Modifier.weight(1f)) {
                            EditField("Team Size", teamSize) { teamSize = it }
                        }
                    }
                }
            }

            /* ── SECTION: SKILLS ── */
            item {
                SectionCard(title = "Skills Required") {
                    /* existing skills */
                    if (skills.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            skills.forEach { skill: String ->
                                InputChip(
                                    selected = true,
                                    onClick  = { skills = skills - skill },
                                    label    = { Text(skill) },
                                    trailingIcon = {
                                        Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp))
                                    }
                                )
                            }
                        }
                    }

                    /* add skill */
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value         = newSkill,
                            onValueChange = { newSkill = it },
                            placeholder   = { Text("Add a skill", fontSize = 14.sp) },
                            singleLine    = true,
                            modifier      = Modifier.weight(1f),
                            shape         = RoundedCornerShape(12.dp)
                        )
                        IconButton(
                            onClick = {
                                if (newSkill.isNotBlank() && !skills.contains(newSkill.trim())) {
                                    skills = skills + newSkill.trim()
                                    newSkill = ""
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                        ) {
                            Icon(Icons.Default.Add, null, tint = Primary)
                        }
                    }
                }
            }

            /* ── SECTION: TASKS ── */
            item {
                SectionCard(title = "Tasks  (${tasks.size})") {

                    /* add task toggle */
                    OutlinedButton(
                        onClick = { showAddTask = !showAddTask },
                        shape   = RoundedCornerShape(10.dp),
                        border  = BorderStroke(1.dp, Primary),
                        colors  = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(if (showAddTask) Icons.Default.Close else Icons.Default.Add, null,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (showAddTask) "Cancel" else "Add New Task")
                    }

                    if (showAddTask) {
                        Spacer(Modifier.height(12.dp))
                        EditField("Task Title", newTaskTitle) { newTaskTitle = it }
                        EditField("Task Description (optional)", newTaskDesc, minLines = 2) { newTaskDesc = it }

                        /* assign member dropdown */
                        ExposedDropdownMenuBox(
                            expanded          = memberDropdown,
                            onExpandedChange  = { memberDropdown = !memberDropdown }
                        ) {
                            OutlinedTextField(
                                value         = memberNames[selectedMember] ?: "Assign to member",
                                onValueChange = {},
                                readOnly      = true,
                                label         = { Text("Assign To") },
                                trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberDropdown) },
                                modifier      = Modifier.menuAnchor().fillMaxWidth(),
                                shape         = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded         = memberDropdown,
                                onDismissRequest = { memberDropdown = false }
                            ) {
                                members.forEach { uid ->
                                    DropdownMenuItem(
                                        text    = { Text(memberNames[uid] ?: uid) },
                                        onClick = { selectedMember = uid; memberDropdown = false }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    val taskId = firestore.collection("tmp").document().id
                                    firestore.collection("projects").document(projectId)
                                        .collection("tasks").document(taskId)
                                        .set(mapOf(
                                            "id"          to taskId,
                                            "title"       to newTaskTitle.trim(),
                                            "description" to newTaskDesc.trim(),
                                            "assignedTo"  to selectedMember,
                                            "assignedName" to (memberNames[selectedMember] ?: ""),
                                            "status"      to "TODO",
                                            "startDate"   to System.currentTimeMillis(),
                                            "deadline"    to 0L
                                        ))
                                    newTaskTitle   = ""
                                    newTaskDesc    = ""
                                    selectedMember = ""
                                    showAddTask    = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Create Task")
                        }
                    }
                }
            }

            /* ── TASK LIST ── */
            items(tasks) { task ->
                EditTaskRow(
                    task        = task,
                    memberNames = memberNames,
                    projectId   = projectId
                )
            }

            /* ── SECTION: MEMBERS ── */
            item {
                SectionCard(title = "Members  (${members.size})") {
                    members.forEach { uid ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(memberNames[uid] ?: uid, fontSize = 14.sp, color = TextPrimary)
                            if (uid != currentUser) {
                                IconButton(
                                    onClick = {
                                        firestore.collection("projects").document(projectId)
                                            .update("members",
                                                com.google.firebase.firestore.FieldValue.arrayRemove(uid))
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, "Remove", tint = ErrorRed,
                                        modifier = Modifier.size(16.dp))
                                }
                            } else {
                                Text("You (Owner)", fontSize = 12.sp, color = TextMuted)
                            }
                        }
                        Divider(color = Border)
                    }
                }
            }
        }
    }
}

/* ─── EDIT TASK ROW ─────────────────────────────────────────────────────── */

@Composable
private fun EditTaskRow(
    task: Task,
    memberNames: Map<String, String>,
    projectId: String
) {
    val firestore = FirebaseFirestore.getInstance()
    var expanded  by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = Surface1),
        border   = BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(task.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                    Text(
                        memberNames[task.assignedTo] ?: task.assignedTo,
                        fontSize = 12.sp, color = TextMuted
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    /* status cycle button */
                    val nextStatus = when (task.status) {
                        "TODO"        -> "IN_PROGRESS"
                        "IN_PROGRESS" -> "DONE"
                        else          -> "TODO"
                    }
                    AssistChip(
                        onClick = {
                            firestore.collection("projects").document(projectId)
                                .collection("tasks").document(task.id)
                                .update("status", nextStatus)
                        },
                        label = { Text(task.status.replace("_", " "), fontSize = 11.sp) }
                    )
                    /* delete */
                    IconButton(
                        onClick = {
                            firestore.collection("projects").document(projectId)
                                .collection("tasks").document(task.id).delete()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, "Delete task",
                            tint = ErrorRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

/* ─── REUSABLE EDIT COMPONENTS ───────────────────────────────────────────── */

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Surface1),
        border   = BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun EditField(
    label: String,
    value: String,
    minLines: Int = 1,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onChange,
        label         = { Text(label, fontSize = 13.sp) },
        minLines      = minLines,
        modifier      = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape         = RoundedCornerShape(12.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Primary,
            unfocusedBorderColor = Border
        )
    )
}

@Composable
private fun ChipSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(Modifier.padding(bottom = 12.dp)) {
        Text(label, fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option: String ->
                val isSelected = selected == option
                FilterChip(
                    selected = isSelected,
                    onClick  = { onSelect(if (isSelected) "" else option) },
                    label    = { Text(option, fontSize = 13.sp) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBg,
                        selectedLabelColor     = Primary
                    )
                )
            }
        }
    }
}