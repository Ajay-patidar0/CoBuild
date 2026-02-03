package com.example.cobuild.ui.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth

private val PrimaryColor = Color(0xFF4F46E5)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val BackgroundColor = Color(0xFFF8FAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    requesterName: String, // Pass the name from onboarding
    onBackClick: () -> Unit
) {
    val viewModel: ProjectRequestViewModel = viewModel()
    val isRequested by viewModel.isRequested.collectAsState()

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val project by viewModel.getProjectById(projectId).collectAsState(initial = null)
    val proj = project
    val isOwner = proj?.ownerId == currentUserId

    // Check if already requested when screen loads
    LaunchedEffect(projectId) {
        viewModel.checkIfRequested(projectId)
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Project Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            if (proj != null && !isOwner) {
                Surface(tonalElevation = 8.dp) {
                    Button(
                        onClick = { viewModel.requestToJoin(proj, requesterName) },
                        enabled = !isRequested,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor,
                            contentColor = Color.White,
                            disabledContainerColor = PrimaryColor.copy(alpha = 0.6f)
                        )
                    ) {
                        Text(
                            text = if (isRequested) "Request Sent" else "Request to Join Project",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (proj == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Text(proj.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("by ${proj.ownerName}", color = TextSecondary)
            Spacer(Modifier.height(16.dp))
            proj.description?.let { Section("Description", it) }
            proj.goal?.let { Section("Goal", it) }

            if (proj.skills.isNotEmpty()) {
                Text("Required Skills", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    proj.skills.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                }
                Spacer(Modifier.height(16.dp))
            }

            proj.link?.takeIf { it.isNotBlank() }?.let {
                Text("Link", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Link, null, tint = TextSecondary)
                    Spacer(Modifier.width(8.dp))
                    Text(it, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun Section(title: String, value: String) {
    Text(title, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(4.dp))
    Text(value, color = TextSecondary)
    Spacer(Modifier.height(16.dp))
}
