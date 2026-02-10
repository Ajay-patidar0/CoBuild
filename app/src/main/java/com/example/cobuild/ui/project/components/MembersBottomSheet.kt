package com.example.cobuild.ui.project.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersBottomSheet(
    members: List<String>, // userIds
    onDismiss: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    var memberNames by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    // 🔥 Fetch user names from Firestore
    LaunchedEffect(members) {
        if (members.isEmpty()) {
            loading = false
            return@LaunchedEffect
        }

        firestore.collection("users")
            .whereIn(FieldPath.documentId(), members.take(10)) // Firestore limit
            .get()
            .addOnSuccessListener { snapshot ->
                memberNames = snapshot.documents.mapNotNull {
                    it.getString("name")
                }
                loading = false
            }
            .addOnFailureListener {
                loading = false
            }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = "Project Members",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            when {
                loading -> {
                    CircularProgressIndicator()
                }

                memberNames.isEmpty() -> {
                    Text("No members yet")
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(memberNames) { name ->
                            Text(
                                text = name,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
