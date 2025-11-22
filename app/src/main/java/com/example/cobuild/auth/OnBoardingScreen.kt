package com.example.cobuild

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnBoardingScreen(navController: NavController? = null) {

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // User fields
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var companyOrCollege by remember { mutableStateOf("") }
    var linkedin by remember { mutableStateOf("") }
    var github by remember { mutableStateOf("") }
    var portfolio by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }

    val roles = listOf("Designer", "Developer", "Product Manager", "Researcher", "Data Analyst")
    val skillsList = listOf("UI/UX", "Figma", "Kotlin", "Java", "Python", "ML", "Jetpack Compose", "Firebase")
    val interestList = listOf("AI", "Android Dev", "Open Source", "UI Animation", "Product Design")
    val indiaLocations = listOf("Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Pune", "Indore")

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {

        Text("Complete Your Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3A86FF))
        Spacer(Modifier.height(8.dp))
        Text("Tell more about yourself to help others connect better", fontSize = 16.sp, color = Color.Gray)
        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(18.dp)) {

                CustomInputField("Your Name", name, onChange = { name = it })
                Spacer(Modifier.height(16.dp))

                CustomInputField("Your Role", role, onChange = { role = it })
                AnimatedSuggestionList(query = role, items = roles) { selected -> role = selected }
                Spacer(Modifier.height(16.dp))

                CustomInputField("Skills (comma-separated)", skills, onChange = { skills = it })
                AnimatedSuggestionList(
                    query = getLastWord(skills),
                    items = skillsList
                ) { selected -> skills = replaceLastWord(skills, selected) }
                Spacer(Modifier.height(16.dp))

                CustomInputField("Interests (comma-separated)", interests, onChange = { interests = it })
                AnimatedSuggestionList(
                    query = getLastWord(interests),
                    items = interestList
                ) { selected -> interests = replaceLastWord(interests, selected) }
                Spacer(Modifier.height(16.dp))

                CustomInputField("Location", location, onChange = { location = it })
                AnimatedSuggestionList(query = location, items = indiaLocations) { selected -> location = selected }
                Spacer(Modifier.height(16.dp))

                CustomInputField("Company / College", companyOrCollege, onChange = { companyOrCollege = it })
                Spacer(Modifier.height(16.dp))

                CustomInputField("LinkedIn URL", linkedin, keyboardType = KeyboardType.Uri, onChange = { linkedin = it })
                Spacer(Modifier.height(16.dp))

                CustomInputField("GitHub URL", github, keyboardType = KeyboardType.Uri, onChange = { github = it })
                Spacer(Modifier.height(16.dp))

                CustomInputField("Portfolio Link (Optional)", portfolio, keyboardType = KeyboardType.Uri, onChange = { portfolio = it })
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (currentUser == null) {
                            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (name.isBlank() || role.isBlank()) {
                            Toast.makeText(context, "Name & Role required!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSaving = true

                        val userMap = hashMapOf(
                            "name" to name,
                            "role" to role,
                            "skills" to splitCsv(skills),
                            "interests" to splitCsv(interests),
                            "location" to location,
                            "companyOrCollege" to companyOrCollege,
                            "linkedin" to linkedin,
                            "github" to github,
                            "portfolio" to portfolio,
                            "createdAt" to Timestamp.now()
                        )

                        firestore.collection("users")
                            .document(currentUser.uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                isSaving = false
                                Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()

                                // MOVE TO HOME SCREEN
                                navController?.navigate("home") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                            .addOnFailureListener {
                                isSaving = false
                                Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A86FF))
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text("Save Profile", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit
) {
    Column {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedSuggestionList(
    query: String,
    items: List<String>,
    onClick: (String) -> Unit
) {

    val filtered = remember(query) {
        if (query.isBlank()) emptyList()
        else items.filter { it.contains(query, ignoreCase = true) }.take(5)
    }

    AnimatedVisibility(
        visible = filtered.isNotEmpty(),
        enter = fadeIn(tween(180)) + slideInVertically(),
        exit = fadeOut(tween(180)) + slideOutVertically()
    ) {
        Column(
            modifier = Modifier
                .padding(top = 6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF1F1F1))
        ) {
            filtered.forEach { suggestion ->
                SuggestionRow(text = suggestion) { onClick(suggestion) }
            }
        }
    }
}

@Composable
fun SuggestionRow(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, fontSize = 15.sp, color = Color.DarkGray)
    }
}

// -------- Helper Functions ----------
fun replaceLastWord(current: String, new: String): String {
    val parts = current.split(",").map { it.trim() }.toMutableList()
    if (parts.isEmpty()) return new
    parts[parts.size - 1] = new
    return parts.joinToString(", ")
}

fun getLastWord(text: String): String =
    if (text.isBlank()) "" else text.split(",").last().trim()

fun splitCsv(text: String): List<String> =
    text.split(",").map { it.trim() }.filter { it.isNotEmpty() }
