package com.example.cobuild.ui.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

private val Primary      = Color(0xFF4F46E5)
private val PrimaryLight = Color(0xFFEDE9FE)
private val TextPrimary  = Color(0xFF1E293B)
private val TextSub      = Color(0xFF64748B)
private val Border       = Color(0xFFE2E8F0)

data class TopMatch(
    val userId: String = "",
    val score:  Double = 0.0
)

@Composable
fun TopMatchesSection(
    projectId:    String,
    projectTitle: String,
    ownerId:      String,
    topMatches:   List<TopMatch>
) {
    if (topMatches.isEmpty()) return

    val firestore = FirebaseFirestore.getInstance()

    // Fetch user names for the matched user IDs
    var userNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var invitedIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(topMatches) {
        firestore.collection("users").get().addOnSuccessListener { snap ->
            userNames = snap.documents.associate {
                it.id to (it.getString("name") ?: "User")
            }
        }
        // Check already invited
        firestore.collection("project_requests")
            .whereEqualTo("projectId", projectId)
            .whereEqualTo("isInvite", true)
            .get().addOnSuccessListener { snap ->
                invitedIds = snap.documents
                    .mapNotNull { it.getString("requesterId") }
                    .toSet()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "🤖 AI Top Matches",
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = Primary
        )

        topMatches.forEach { match ->
            val name      = userNames[match.userId] ?: "..."
            val isInvited = invitedIds.contains(match.userId)
            val isOwner   = match.userId == ownerId

            if (isOwner) return@forEach  // skip owner themselves

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryLight.copy(0.5f), RoundedCornerShape(12.dp))
                    .border(1.dp, Primary.copy(0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment      = Alignment.CenterVertically,
                    horizontalArrangement  = Arrangement.spacedBy(10.dp),
                    modifier               = Modifier.weight(1f)
                ) {
                    // Avatar
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            name.firstOrNull()?.uppercase() ?: "?",
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Primary
                        )
                    }

                    Column {
                        Text(
                            name,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary
                        )
                        Text(
                            "${match.score.toInt()}% match",
                            fontSize = 11.sp,
                            color    = Primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Invite button
                if (isInvited) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            "✓ Invited",
                            fontSize   = 11.sp,
                            color      = Color(0xFF166534),
                            fontWeight = FontWeight.SemiBold,
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            val docRef = firestore.collection("project_requests").document()
                            docRef.set(mapOf(
                                "requestId"     to docRef.id,
                                "projectId"     to projectId,
                                "projectTitle"  to projectTitle,
                                "ownerId"       to ownerId,
                                "requesterId"   to match.userId,
                                "requesterName" to name,
                                "status"        to "invited",
                                "isInvite"      to true,
                                "createdAt"     to com.google.firebase.Timestamp.now()
                            ))
                            invitedIds = invitedIds + match.userId
                        },
                        shape          = RoundedCornerShape(20.dp),
                        colors         = ButtonDefaults.buttonColors(containerColor = Primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier       = Modifier.height(32.dp)
                    ) {
                        Text("Invite", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}