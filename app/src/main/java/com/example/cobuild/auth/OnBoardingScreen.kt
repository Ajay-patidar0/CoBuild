package com.example.cobuild

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.FlowRow

@Composable
fun OnBoardingScreen() {

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    // ------- FORM STATES -------
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }
    var portfolio by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }

    // ------- SUGGESTIONS -------
    val roleSuggestions = listOf("Designer", "Developer", "Product Manager", "Researcher", "Content Writer")
    val skillSuggestions = listOf("UI/UX", "Figma", "Kotlin", "Java", "Jetpack Compose", "Firebase", "ML")
    val interestSuggestions = listOf("App Design", "Prototyping", "AI", "Android Dev", "UI Animation")

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {

        // ---------- HEADER ----------
        Text(
            text = "Complete Your Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3A0CA3)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tell the community about your role, skills and goals!",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ---------- CARD CONTAINER ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // NAME
                CustomInputField("Your Name", name) { name = it }
                Spacer(Modifier.height(12.dp))

                // ROLE
                Text("Select Role", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                FlowRow(maxItemsInEachRow = 3) {
                    roleSuggestions.forEach { suggestion ->
                        SuggestionChip(
                            text = suggestion,
                            isSelected = role == suggestion,
                            onClick = { role = suggestion }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                CustomInputField(
                    label = "Or type custom role",
                    value = role,
                    onChange = { role = it }
                )

                Spacer(Modifier.height(16.dp))

                // SKILLS
                Text("Skills", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                FlowRow(maxItemsInEachRow = 3) {
                    skillSuggestions.forEach { suggestion ->
                        SuggestionChip(
                            text = suggestion,
                            isSelected = skills.contains(suggestion),
                            onClick = {
                                skills = if (skills.contains(suggestion)) {
                                    skills.replace(suggestion, "")
                                        .replace(",,", ",")
                                        .trim(',')
                                } else {
                                    if (skills.isBlank()) suggestion else "$skills, $suggestion"
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                CustomInputField(
                    label = "Add custom skills (comma separated)",
                    value = skills,
                    onChange = { skills = it }
                )

                Spacer(Modifier.height(16.dp))

                // INTERESTS
                Text("Interests", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                FlowRow(maxItemsInEachRow = 3) {
                    interestSuggestions.forEach { suggestion ->
                        SuggestionChip(
                            text = suggestion,
                            isSelected = interests.contains(suggestion),
                            onClick = {
                                interests = if (interests.contains(suggestion)) {
                                    interests.replace(suggestion, "")
                                        .replace(",,", ",")
                                        .trim(',')
                                } else {
                                    if (interests.isBlank()) suggestion else "$interests, $suggestion"
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                CustomInputField(
                    label = "Add custom interests (comma separated)",
                    value = interests,
                    onChange = { interests = it }
                )

                Spacer(Modifier.height(16.dp))

                // OTHER FIELDS
                CustomInputField("Your Goals", goals) { goals = it }
                Spacer(Modifier.height(12.dp))
                CustomInputField("Portfolio Link", portfolio) { portfolio = it }

                Spacer(Modifier.height(24.dp))

                // ---------- SAVE BUTTON ----------
                Button(
                    onClick = {

                        if (name.isBlank() || role.isBlank()) {
                            Toast.makeText(context, "Name & Role are required!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSaving = true

                        val userMap = hashMapOf(
                            "name" to name,
                            "role" to role,
                            "skills" to skills.split(",").map { it.trim() }
                                .filter { it.isNotEmpty() },
                            "interests" to interests.split(",").map { it.trim() }
                                .filter { it.isNotEmpty() },
                            "goals" to goals,
                            "portfolio" to portfolio.split(",").map { it.trim() }
                                .filter { it.isNotEmpty() },
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )

                        // No login → random document for demonstration
                        firestore.collection("users")
                            .add(userMap)
                            .addOnSuccessListener {
                                isSaving = false
                                Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
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

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SuggestionChip(text: String, isSelected: Boolean, onClick: () -> Unit) {

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF3A86FF) else Color(0xFFE8EAF0),
        label = ""
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Black,
        label = ""
    )

    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = textColor, fontSize = 14.sp)
    }
}

@Composable
fun CustomInputField(label: String, value: String, onChange: (String) -> Unit) {
    Column {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    OnBoardingScreen()
}
