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
import com.example.cobuild.data.model.Project
import com.google.firebase.auth.FirebaseAuth

private val PrimaryColor = Color(0xFF4F46E5)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val BackgroundColor = Color(0xFFF8FAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    onBackClick: () -> Unit
) {
    val viewModel: ProjectRequestViewModel = viewModel()
    val isRequested by viewModel.isRequested.collectAsState(initial = false)

    // ✅ Get current user safely
    val currentUserId = remember {
        FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    }

    val project by viewModel
        .getProjectById(projectId)
        .collectAsState(initial = null)

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Project Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        if (project == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
            return@Scaffold
        }

        val proj = project!!

        /**
         * ✅ FINAL OWNER CHECK (FAILSAFE)
         * Hide button IF:
         * - user is owner
         * - OR ownerId missing (old data)
         * - OR user not logged in
         */
        val isOwner = remember(proj.ownerId, currentUserId) {
            proj.ownerId.isNullOrBlank() ||
                    currentUserId.isBlank() ||
                    proj.ownerId == currentUserId
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Text(
                text = proj.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "by ${proj.ownerName}",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(16.dp))

            proj.description?.takeIf { it.isNotBlank() }?.let {
                Section("Description", it)
            }

            proj.goal?.takeIf { it.isNotBlank() }?.let {
                Section("Goal", it)
            }

            if (proj.skills.isNotEmpty()) {
                Text("Required Skills", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    proj.skills.forEach {
                        AssistChip(onClick = {}, label = { Text(it) })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            proj.timeline?.takeIf { it.isNotBlank() }?.let {
                Section("Expected Timeline", it)
            }

            proj.teamSize?.takeIf { it.isNotBlank() }?.let {
                Section("Team Size Needed", it)
            }

            proj.projectType?.takeIf { it.isNotBlank() }?.let {
                Section("Project Type", it)
            }

            proj.commitmentLevel?.takeIf { it.isNotBlank() }?.let {
                Section("Commitment Level", it)
            }

            proj.experienceLevel?.takeIf { it.isNotBlank() }?.let {
                Section("Experience Level", it)
            }

            proj.link?.takeIf { it.isNotBlank() }?.let {
                Text("Link", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Link, null, tint = TextSecondary)
                    Spacer(Modifier.width(8.dp))
                    Text(it, color = TextSecondary)
                }
                Spacer(Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // ✅ BUTTON SHOWN ONLY FOR NON-OWNERS
            if (!isOwner) {
                Button(
                    onClick = { viewModel.requestToJoin(proj) },
                    enabled = !isRequested,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Icon(Icons.Default.Send, null)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (isRequested) "Request Sent" else "Request to Join Project",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
