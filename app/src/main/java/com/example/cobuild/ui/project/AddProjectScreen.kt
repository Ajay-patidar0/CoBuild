package com.example.cobuild.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

// --- Theme Colors ---
private val PrimaryColor = Color(0xFF4F46E5) // Indigo 600
private val BackgroundColor = Color(0xFFF8FAFC) // Slate 50
private val SurfaceColor = Color.White
private val TextPrimary = Color(0xFF1E293B) // Slate 800
private val TextSecondary = Color(0xFF64748B) // Slate 500
private val ErrorColor = Color(0xFFEF4444) // Red 500

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

    // Form State
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    // Validation State
    var titleError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }

    // ViewModel State
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    // Effect: Handle Success
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            snackbarHostState.showSnackbar("Project posted successfully!")
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header Section
            // Switched to Icons.Default.Edit (Core Icon)
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Share Your Idea",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Connect with collaborators and build something amazing.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // --- Form Inputs ---

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (titleError && it.isNotEmpty()) titleError = false
                },
                label = { Text("Project Title *") },
                placeholder = { Text("e.g. AI Study Assistant") },
                isError = titleError,
                supportingText = { if (titleError) Text("Title is required", color = ErrorColor) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    if (descError && it.isNotEmpty()) descError = false
                },
                label = { Text("Description *") },
                placeholder = { Text("Describe what you are building...") },
                isError = descError,
                supportingText = { if (descError) Text("Description is required", color = ErrorColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp), // Taller for multiline
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default // Allow new lines
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Goal Field
            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it },
                label = { Text("Project Goal") },
                placeholder = { Text("e.g. Launch on Play Store") },
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Skills Field
            OutlinedTextField(
                value = skills,
                onValueChange = { skills = it },
                label = { Text("Required Skills") },
                placeholder = { Text("e.g. Kotlin, Figma, Python") },
                leadingIcon = { Icon(Icons.Default.List, contentDescription = null, tint = TextSecondary) },
                supportingText = { Text("Comma separated") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Link Field
            // Switched to Icons.Default.Share (Core Icon) to avoid 'Unresolved reference Link'
            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                label = { Text("Link (Optional)") },
                placeholder = { Text("https://github.com/...") },
                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    // Validation Logic
                    focusManager.clearFocus()
                    var isValid = true
                    if (title.isBlank()) {
                        titleError = true
                        isValid = false
                    }
                    if (description.isBlank()) {
                        descError = true
                        isValid = false
                    }

                    if (isValid) {
                        viewModel.addProject(
                            title.trim(),
                            description.trim(),
                            goal.trim(),
                            skills.trim(),
                            link.trim()
                        )
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please fill in all required fields.")
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = PrimaryColor.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Posting...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Post Project", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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
    errorBorderColor = ErrorColor,
    errorLabelColor = ErrorColor
)