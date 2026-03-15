package com.example.cobuild.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cobuild.data.model.Project
import com.example.cobuild.navigation.Destinations
import com.example.cobuild.ui.project.CompactProjectCard
import com.example.cobuild.ui.project.ProjectViewModel
import com.google.firebase.auth.FirebaseAuth

/* -------------------- COLORS -------------------- */

private val PrimaryColor = Color(0xFF4F46E5)
private val BackgroundColor = Color(0xFFF8FAFC)
private val SurfaceColor = Color.White
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val BorderLight = Color(0xFFE2E8F0)


/* -------------------- HOME SCREEN -------------------- */

@Composable
fun HomeScreen(
    navController: NavController,
    onAddProjectClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onProjectClick: (Project) -> Unit,
    onNotificationClick: () -> Unit
) {

    var selectedTab by remember { mutableIntStateOf(0) }
    var showFilters by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val projectViewModel: ProjectViewModel = viewModel()

    val searchQuery by projectViewModel.searchQuery.collectAsState()
    val projects by projectViewModel.filteredProjects.collectAsState()
    val userSkills by projectViewModel.userSkills.collectAsState()

    LaunchedEffect(Unit) {
        projectViewModel.loadProjects()

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            projectViewModel.loadUserSkills(uid)
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = { HomeTopBar(onNotificationClick) },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        0 -> navController.navigate(Destinations.HOME)
                        1 -> navController.navigate(Destinations.PROJECT_LIST)
                        3 -> onMessagesClick()
                        4 -> onProfileClick()
                    }
                },
                onAddClick = onAddProjectClick
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {

            if (showFilters) {
                FilterBottomSheet(
                    onDismiss = { showFilters = false },
                    onApply = {
                        projectViewModel.applyFilters(it)
                        showFilters = false
                    }
                )
            }

            SearchBarVisual(
                query = searchQuery,
                onQueryChange = projectViewModel::onSearchQueryChange,
                onFilterClick = { showFilters = true }
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Build Together.\nGrow Faster.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 34.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Discover ideas and find collaborators",
                fontSize = 15.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(20.dp))

            AddProjectCTA(onAddProjectClick)

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Latest Projects",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(Modifier.height(14.dp))

            if (projects.isEmpty()) {
                Text(
                    text = "No projects yet. Be the first to post!",
                    color = TextSecondary
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    projects.forEach { project ->
                        CompactProjectCard(
                            project = project,
                            userSkills = userSkills,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProjectClick(project) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

/* -------------------- TOP BAR -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(onNotificationClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Welcome back 👋", fontSize = 13.sp, color = TextSecondary)
                Text("CoBuilder", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        actions = {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceColor)
                    .border(1.dp, BorderLight, CircleShape)
                    .size(42.dp)
            ) {
                Icon(Icons.Outlined.Notifications, null, tint = TextPrimary)
            }
            Spacer(Modifier.width(12.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
    )
}

/* -------------------- SEARCH -------------------- */

@Composable
fun SearchBarVisual(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceColor)
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(Icons.Default.Search, null, tint = TextSecondary)

        Spacer(Modifier.width(12.dp))

        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    "Search projects, skills or people",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )
            },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        IconButton(onClick = onFilterClick) {
            Icon(Icons.Default.Tune, null, tint = TextSecondary)
        }
    }
}

/* -------------------- FILTER SHEET -------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    onApply: (ProjectFilters) -> Unit
) {

    var commitment by remember { mutableStateOf<String?>(null) }
    var experience by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf<String?>(null) }

    var selectedSkills by remember { mutableStateOf(setOf<String>()) }

    ModalBottomSheet(onDismissRequest = onDismiss) {

        Column(
            Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text("Filters", fontWeight = FontWeight.Bold, fontSize = 20.sp)

                TextButton(
                    onClick = {

                        commitment = null
                        experience = null
                        status = null
                        selectedSkills = emptySet()

                        onApply(
                            ProjectFilters(
                                commitmentLevel = null,
                                experienceLevel = null,
                                status = null,
                                skills = emptyList()
                            )
                        )

                        onDismiss()
                    }
                ) {
                    Text("Clear All")
                }
            }

            Spacer(Modifier.height(20.dp))

            ChipGroupSingle(
                "Commitment Level",
                listOf("Low", "Medium", "High")
            ) { commitment = it }

            ChipGroupSingle(
                "Experience Level",
                listOf("Beginner", "Intermediate", "Advanced")
            ) { experience = it }

            ChipGroupMulti(
                "Skills",
                listOf("Kotlin", "Firebase", "UI/UX", "React", "ML", "Flutter")
            ) { selectedSkills = it }

            ChipGroupSingle(
                "Status",
                listOf("YET_TO_START", "IN_PROGRESS", "COMPLETED")
            ) { status = it }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    onApply(
                        ProjectFilters(
                            commitmentLevel = commitment,
                            experienceLevel = experience,
                            status = status,
                            skills = selectedSkills.toList()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filters")
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

/* -------------------- CHIP GROUP (SINGLE SELECT) -------------------- */

@Composable
fun ChipGroupSingle(
    title: String,
    options: List<String>,
    onSelected: (String?) -> Unit
) {

    var selected by remember { mutableStateOf<String?>(null) }

    Column {

        Text(title, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            options.forEach { option ->

                FilterChip(
                    selected = selected == option,
                    onClick = {
                        selected = if (selected == option) null else option
                        onSelected(selected)
                    },
                    label = { Text(option) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

/* -------------------- CHIP GROUP (MULTI SELECT) -------------------- */

@Composable
fun ChipGroupMulti(
    title: String,
    options: List<String>,
    onSelectionChanged: (Set<String>) -> Unit
) {

    var selectedItems by remember { mutableStateOf(setOf<String>()) }

    Column {

        Text(title, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            options.forEach { option ->

                val selected = selectedItems.contains(option)

                FilterChip(
                    selected = selected,
                    onClick = {

                        selectedItems = if (selected) {
                            selectedItems - option
                        } else {
                            selectedItems + option
                        }

                        onSelectionChanged(selectedItems)
                    },
                    label = { Text(option) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

fun calculateSkillMatch(
    userSkills: List<String>,
    projectSkills: List<String>
): Int {

    if (projectSkills.isEmpty()) return 0

    val matchCount = projectSkills.count { projectSkill ->
        userSkills.any { userSkill ->
            userSkill.equals(projectSkill, ignoreCase = true)
        }
    }

    return ((matchCount.toFloat() / projectSkills.size) * 100).toInt()
}


/* -------------------- CTA -------------------- */

@Composable
fun AddProjectCTA(onAddProjectClick: () -> Unit) {

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddProjectClick() }
    ) {

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(Modifier.weight(1f)) {
                Text("Have an idea?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Post a project & find collaborators", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
            }

            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

/* -------------------- PROJECT CARD -------------------- */

@Composable
fun CompactProjectCard(
    project: Project,
    modifier: Modifier = Modifier
) {

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        modifier = modifier.height(100.dp)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(Modifier.weight(1f)) {

                Text(project.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Spacer(Modifier.height(4.dp))

                Text("by ${project.ownerName}", color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

/* -------------------- NAV COLORS -------------------- */

@Composable
fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = PrimaryColor,
    unselectedIconColor = Color(0xFF94A3B8),
    indicatorColor = Color.Transparent
)

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onAddClick: () -> Unit
) {

    NavigationBar(
        containerColor = SurfaceColor
    ) {

        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            colors = navColors()
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Default.List, contentDescription = "Projects") },
            colors = navColors()
        )

        NavigationBarItem(
            selected = false,
            onClick = onAddClick,
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Add") },
            colors = navColors()
        )

        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Default.Email, contentDescription = "Messages") },
            colors = navColors()
        )

        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            colors = navColors()
        )
    }
}