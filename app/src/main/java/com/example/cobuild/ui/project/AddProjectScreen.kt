package com.example.cobuild.ui.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

// ---------- Colors ----------
private val PrimaryColor = Color(0xFF4F46E5)
private val BackgroundColor = Color(0xFFF8FAFC)
private val SurfaceColor = Color.White
private val TextSecondary = Color(0xFF64748B)
private val ErrorColor = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    onBackClick: () -> Unit = {},
    onProjectPosted: () -> Unit
) {
    val viewModel: ProjectViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // ---------- Form State ----------
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    var timeline by remember { mutableStateOf("") }
    var teamSize by remember { mutableStateOf("") }
    var projectType by remember { mutableStateOf("") }
    var commitment by remember { mutableStateOf("") }
    var experienceLevel by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            snackbarHostState.showSnackbar("Project posted successfully")
            onProjectPosted()
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("New Project", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {

            Text(
                text = "Share Your Idea",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) titleError = false
                },
                label = { Text("Project Title *") },
                isError = titleError,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                colors = formFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    if (it.isNotBlank()) descError = false
                },
                label = { Text("Description *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                isError = descError,
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it },
                label = { Text("Project Goal") },
                leadingIcon = { Icon(Icons.Default.Star, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = skills,
                onValueChange = { skills = it },
                label = { Text("Required Skills") },
                placeholder = { Text("Kotlin, Firebase, UI/UX") },
                leadingIcon = { Icon(Icons.Default.List, null) },
                supportingText = { Text("Comma separated") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = timeline,
                onValueChange = { timeline = it },
                label = { Text("Expected Timeline") },
                placeholder = { Text("1–3 months") },
                leadingIcon = { Icon(Icons.Default.Timer, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = teamSize,
                onValueChange = { teamSize = it },
                label = { Text("Team Size Needed") },
                placeholder = { Text("2–4 people") },
                leadingIcon = { Icon(Icons.Default.Groups, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = projectType,
                onValueChange = { projectType = it },
                label = { Text("Project Type") },
                placeholder = { Text("Startup / Open Source / College") },
                leadingIcon = { Icon(Icons.Default.Build, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = commitment,
                onValueChange = { commitment = it },
                label = { Text("Commitment Level") },
                placeholder = { Text("Low / Medium / High") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = experienceLevel,
                onValueChange = { experienceLevel = it },
                label = { Text("Experience Level") },
                placeholder = { Text("Beginner / Intermediate / Advanced") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                label = { Text("Link (Optional)") },
                leadingIcon = { Icon(Icons.Default.Share, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    var valid = true

                    if (title.isBlank()) {
                        titleError = true
                        valid = false
                    }

                    if (description.isBlank()) {
                        descError = true
                        valid = false
                    }

                    if (valid) {
                        viewModel.addProject(
                            title.trim(),
                            description.trim(),
                            goal.trim(),
                            skills.trim(),
                            timeline.trim(),
                            teamSize.trim(),
                            projectType.trim(),
                            commitment.trim(),
                            experienceLevel.trim(),
                            link.trim()
                        )
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Fill required fields")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Post Project",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = SurfaceColor,
    unfocusedContainerColor = SurfaceColor,
    focusedBorderColor = PrimaryColor,
    unfocusedBorderColor = Color(0xFFE2E8F0),
    focusedLabelColor = PrimaryColor,
    unfocusedLabelColor = TextSecondary,
    errorBorderColor = ErrorColor
)
