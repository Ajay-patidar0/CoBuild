package com.example.cobuild.ui.project.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cobuild.data.model.Task
import com.example.cobuild.utils.taskProgress
import com.google.firebase.firestore.FirebaseFirestore

// ── Design tokens ──────────────────────────────────────────────
private val Surface0    = Color(0xFF0F172A)
private val Surface1    = Color(0xFF1E293B)
private val Surface2    = Color(0xFF273548)
private val AccentViolet = Color(0xFF818CF8)
private val AccentCyan  = Color(0xFF22D3EE)
private val AccentGreen = Color(0xFF34D399)
private val TextPrimary = Color(0xFFF1F5F9)
private val TextMuted   = Color(0xFF64748B)
private val DoneBadge   = Color(0xFF14532D)
private val DoneText    = Color(0xFF4ADE80)
private val ProgressBg  = Color(0xFF1E293B)

private fun statusColor(status: String) = when (status) {
    "DONE"        -> Color(0xFF34D399)
    "IN_PROGRESS" -> Color(0xFFFBBF24)
    else          -> Color(0xFF818CF8)
}

private fun statusLabel(status: String) = when (status) {
    "DONE"        -> "✓  Done"
    "IN_PROGRESS" -> "⟳  In Progress"
    else          -> "○  To Do"
}

@Composable
fun TaskCard(
    task: Task,
    projectId: String,
    currentUser: String?,
    memberNames: Map<String, String>
) {
    val firestore = FirebaseFirestore.getInstance()
    val progress  = taskProgress(task)

    val animatedProgress by animateFloatAsState(
        targetValue  = progress,
        animationSpec = tween(800),
        label        = "progress"
    )

    val isAssignedToMe = task.assignedTo == currentUser
    val isDone         = task.status == "DONE"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(Surface1, Surface2)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.07f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {

                // ── Title row ──
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Colored left accent bar
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(20.dp)
                            .background(
                                color = statusColor(task.status),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text       = task.title,
                        color      = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp,
                        modifier   = Modifier.weight(1f)
                    )

                    // Status pill
                    Surface(
                        color = statusColor(task.status).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text     = statusLabel(task.status),
                            color    = statusColor(task.status),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Assignee row ──
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(AccentViolet.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint    = AccentViolet,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text     = memberNames[task.assignedTo] ?: task.assignedTo,
                        color    = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(14.dp))

                // ── Progress bar ──
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progress", color = TextMuted, fontSize = 11.sp)
                        Text(
                            "${(animatedProgress * 100).toInt()}%",
                            color    = AccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress     = animatedProgress,
                        modifier     = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color        = AccentCyan,
                        trackColor   = ProgressBg,
                        strokeCap    = StrokeCap.Round
                    )
                }

                // ── Mark Done button ──
                if (isAssignedToMe && !isDone) {
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = {
                            firestore.collection("projects")
                                .document(projectId)
                                .collection("tasks")
                                .document(task.id)
                                .update("status", "DONE")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen.copy(alpha = 0.15f),
                            contentColor   = AccentGreen
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Mark as Done", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}