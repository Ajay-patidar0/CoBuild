package com.example.cobuild.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileViewScreen(
    userId: String,
    onBackClick: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel()
    val user by viewModel.user.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            user?.let { userData ->

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        color = Color(0xFFEDE9FE)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = userData.name,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = userData.role,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = userData.bio,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Skills",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(8.dp))

                    userData.skills.forEach { skill ->
                        AssistChip(
                            onClick = {},
                            label = { Text(skill) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}