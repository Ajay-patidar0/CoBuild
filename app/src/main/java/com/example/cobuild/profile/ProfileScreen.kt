package com.example.cobuild.profile


import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import com.example.cobuild.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// --- Theme Colors ---
private val ProfilePrimaryColor = Color(0xFF4F46E5) // Indigo 600
private val ProfileBackgroundColor = Color(0xFFF8FAFC) // Slate 50
private val ProfileSurfaceColor = Color.White
private val ProfileTextPrimary = Color(0xFF1E293B) // Slate 800
private val ProfileTextSecondary = Color(0xFF64748B) // Slate 500

@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit = {},
    onProjectsClick: () -> Unit = {},
    onAddProjectClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    // State Variables
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }
    var portfolio by remember { mutableStateOf("") }

    // We keep this variable to store the URL, even if we don't display it without Coil
    var photoUrl by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var saveStatus by remember { mutableStateOf("") } // "Success", "Error", or ""

    val scrollState = rememberScrollState()

    // 🔥 Load data from Firestore
    LaunchedEffect(Unit) {
        if (uid != null) {
            try {
                val snapshot = db.collection("users").document(uid).get().await()
                if (snapshot.exists()) {
                    name = snapshot.getString("name") ?: ""
                    role = snapshot.getString("role") ?: ""
                    location = snapshot.getString("location") ?: ""
                    goals = snapshot.getString("goals") ?: ""
                    skills = (snapshot.get("skills") as? List<*>)?.joinToString(", ") ?: ""
                    interests = (snapshot.get("interests") as? List<*>)?.joinToString(", ") ?: ""
                    portfolio = (snapshot.get("portfolio") as? List<*>)?.joinToString(", ") ?: ""
                    photoUrl = snapshot.getString("photoUrl")
                }
            } catch (e: Exception) {
                // Handle error silently
            }
        }
        isLoading = false
    }

    Scaffold(
        containerColor = ProfileBackgroundColor,
        bottomBar = {
            ProfileBottomBar(
                selectedTab = 4,
                onTabSelected = { tab ->
                    when (tab) {
                        0 -> onHomeClick()
                        1 -> onProjectsClick()
                        2 -> onAddProjectClick()
                        3 -> onMessagesClick()
                        4 -> onProfileClick()
                    }
                }
            )
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = ProfilePrimaryColor) }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Edit Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfileTextPrimary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- Profile Photo Section ---
                Box(
                    modifier = Modifier.clickable { /* TODO: Implement Image Picker */ },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Image Container
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        border = BorderStroke(3.dp, ProfileSurfaceColor),
                        shadowElevation = 6.dp
                    ) {
                        // Standard Placeholder (Since Coil is not available)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(ProfilePrimaryColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = ProfilePrimaryColor
                            )
                        }
                    }

                    // Edit Badge
                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ProfilePrimaryColor)
                            .border(2.dp, ProfileSurfaceColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- Form Fields ---
                ProfileTextField(label = "Full Name", value = name, onValueChange = { name = it })
                ProfileTextField(label = "Role / Job Title", value = role, onValueChange = { role = it })
                ProfileTextField(label = "Location", value = location, onValueChange = { location = it })

                Spacer(modifier = Modifier.height(8.dp))
                SectionDivider("Details")

                ProfileTextField(label = "Goals", value = goals, onValueChange = { goals = it }, singleLine = false)
                ProfileTextField(label = "Skills (comma separated)", value = skills, onValueChange = { skills = it }, singleLine = false)
                ProfileTextField(label = "Interests", value = interests, onValueChange = { interests = it })
                ProfileTextField(label = "Portfolio Links", value = portfolio, onValueChange = { portfolio = it })

                Spacer(modifier = Modifier.height(24.dp))

                // --- Save Button ---
                Button(
                    onClick = {
                        if (uid != null) {
                            val userMap = hashMapOf(
                                "name" to name,
                                "role" to role,
                                "location" to location,
                                "goals" to goals,
                                "skills" to skills.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                "interests" to interests.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                "portfolio" to portfolio.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            )
                            db.collection("users").document(uid).update(userMap as Map<String, Any>)
                                .addOnSuccessListener { saveStatus = "Success" }
                                .addOnFailureListener { saveStatus = "Error" }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfilePrimaryColor)
                ) {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                if (saveStatus.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (saveStatus == "Success") "Profile updated successfully!" else "Failed to update profile.",
                        color = if (saveStatus == "Success") Color(0xFF10B981) else Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- Logout Button ---
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.85f))
                ) {
                    Text(
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }


                Spacer(modifier = Modifier.height(40.dp))

            }
        }
    }
}


@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ProfileTextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

            textStyle = androidx.compose.ui.text.TextStyle(
                color = ProfileTextPrimary,
                fontSize = 16.sp
            ),

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProfilePrimaryColor,
                unfocusedBorderColor = Color(0xFFE2E8F0),

                focusedContainerColor = ProfileSurfaceColor,
                unfocusedContainerColor = ProfileSurfaceColor,

                focusedTextColor = ProfileTextPrimary,
                unfocusedTextColor = ProfileTextPrimary,

                cursorColor = ProfilePrimaryColor
            ),

            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 4
        )
    }
}

@Composable
fun SectionDivider(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
        Text(
            text = text.uppercase(),
            fontSize = 12.sp,
            color = ProfileTextSecondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
    }
}

/*-----------------------------------------
   BOTTOM NAVIGATION
-----------------------------------------*/
@Composable
fun ProfileBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = ProfileSurfaceColor,
        tonalElevation = 8.dp
    ) {
        // Home
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = profileNavColors()
        )

        // Projects
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_projects),
                    contentDescription = "Projects",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Projects") },
            colors = profileNavColors()
        )

        // Add Button
        NavigationBarItem(
            selected = false,
            onClick = { onTabSelected(2) },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = ProfilePrimaryColor,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Create",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            label = { Text("") },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )

        // Chat
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chat),
                    contentDescription = "Chats",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Chats") },
            colors = profileNavColors()
        )

        // Profile
        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = profileNavColors()
        )
    }
}

@Composable
fun profileNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = ProfilePrimaryColor,
    selectedTextColor = ProfilePrimaryColor,
    indicatorColor = ProfilePrimaryColor.copy(alpha = 0.1f)
)