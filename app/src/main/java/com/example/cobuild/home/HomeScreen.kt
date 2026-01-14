package com.example.cobuild.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cobuild.R
import com.example.cobuild.data.model.Project
import com.example.cobuild.navigation.Destinations
import com.example.cobuild.ui.project.ProjectViewModel

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
    val scrollState = rememberScrollState()

    val projectViewModel: ProjectViewModel = viewModel()
    val projects by projectViewModel.projects.collectAsState()

    LaunchedEffect(Unit) {
        projectViewModel.loadProjects()
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            HomeTopBar(onNotificationClick)
        },
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

            SearchBarVisual()

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
                Text(
                    "CoBuilder",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
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
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = TextPrimary
                )
            }
            Spacer(Modifier.width(12.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
    )
}

/* -------------------- SEARCH -------------------- */

@Composable
fun SearchBarVisual() {
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
        Text(
            text = "Search projects, skills or people",
            fontSize = 14.sp,
            color = Color(0xFF94A3B8)
        )
    }
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
                Text(
                    "Post a project & find collaborators",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
    }
}

/* -------------------- BOTTOM NAV (FIXED) -------------------- */

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    NavigationBar(
        containerColor = SurfaceColor,
        modifier = Modifier.border(1.dp, BorderLight)
    ) {

        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Home, null, Modifier.size(24.dp)) },
            colors = navColors()
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_projects),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = navColors()
        )

        // 🔥 CENTER ADD (NOT SELECTABLE)
        NavigationBarItem(
            selected = false,
            onClick = onAddClick,
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )

        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_chat),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = navColors()
        )

        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            icon = { Icon(Icons.Default.Person, null, Modifier.size(24.dp)) },
            colors = navColors()
        )
    }
}

@Composable
fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = PrimaryColor,
    unselectedIconColor = Color(0xFF94A3B8),
    indicatorColor = Color.Transparent
)

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
