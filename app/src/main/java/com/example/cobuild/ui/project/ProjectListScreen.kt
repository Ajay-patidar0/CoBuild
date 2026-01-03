package com.example.cobuild.ui.project

import androidx.compose.foundation.background
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
import com.example.cobuild.ui.project.components.ProjectCard
import com.example.cobuild.ui.project.components.ProjectTabSwitcher
import com.google.firebase.auth.FirebaseAuth

// --- Theme Colors ---
private val BackgroundColor = Color(0xFFF8FAFC)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    onBackClick: () -> Unit,
    onProjectClick: (String) -> Unit,
    // Use viewModel() factory for correct lifecycle management
    viewModel: ProjectListViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Joined, 1 = Posted

    // Trigger data load
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
                        "My Projects",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Tab Switcher
            ProjectTabSwitcher(
                selectedTab = selectedTab,
                onTabChange = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content List
            if (projects.isEmpty()) {
                // Empty State
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
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedTab == 0) "You haven't joined any projects yet."
                            else "You haven't posted any projects yet.",
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
                        ProjectCard(
                            project = project,
                            onClick = { onProjectClick(project.id) }
                        )
                    }
                }
            }
        }
    }
}