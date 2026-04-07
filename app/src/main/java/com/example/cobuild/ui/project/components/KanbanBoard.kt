package com.example.cobuild.ui.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cobuild.data.model.Task

// ── Design tokens ──────────────────────────────────────────────
private val BgBase      = Color(0xFF0F172A)
private val ColTodo     = Color(0xFF818CF8)   // violet
private val ColProgress = Color(0xFFFBBF24)   // amber
private val ColDone     = Color(0xFF34D399)   // emerald
private val ColBg       = Color(0xFF1E293B)
private val ColBorder   = Color(0xFF334155)
private val TextPrimary = Color(0xFFF1F5F9)
private val TextMuted   = Color(0xFF64748B)
private val AccentViolet = Color(0xFF818CF8)

data class ColumnConfig(
    val label : String,
    val emoji : String,
    val accent: Color
)

private val columns = listOf(
    ColumnConfig("To Do",       "○", ColTodo),
    ColumnConfig("In Progress", "⟳", ColProgress),
    ColumnConfig("Done",        "✓", ColDone)
)

/* ── BOARD ───────────────────────────────────────────────────── */
@Composable
fun KanbanBoard(
    tasks        : List<Task>,
    projectId    : String,
    currentUserId: String,
    memberNames  : Map<String, String>
) {
    val todo     = tasks.filter { it.status == "TODO" }
    val progress = tasks.filter { it.status == "IN_PROGRESS" }

    // ✅ DONE only your tasks
    val done = tasks.filter {
        it.status == "DONE" && it.assignedTo == currentUserId
    }

    val taskBuckets = listOf(todo, progress, done)

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        columns.forEachIndexed { index, config ->
            KanbanColumn(
                config        = config,
                tasks         = taskBuckets[index],
                projectId     = projectId,
                currentUserId = currentUserId,
                memberNames   = memberNames
            )
        }
    }
}

/* ── COLUMN ──────────────────────────────────────────────────── */
@Composable
fun KanbanColumn(
    config       : ColumnConfig,
    tasks        : List<Task>,
    projectId    : String,
    currentUserId: String,
    memberNames  : Map<String, String>
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .background(ColBg, RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        config.accent.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {

        // ── Column header ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dot badge
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(config.accent, shape = RoundedCornerShape(5.dp))
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text       = config.label,
                color      = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                modifier   = Modifier.weight(1f)
            )

            // Count chip
            Surface(
                color = config.accent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text     = "${tasks.size}",
                    color    = config.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }

        // Top accent line
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(config.accent.copy(alpha = 0.6f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(1.dp)
                )
        )
        Spacer(Modifier.height(12.dp))

        // ── Tasks or empty state ──
        if (tasks.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        color = config.accent.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = config.accent.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = "No tasks yet",
                    color    = TextMuted,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    // 🔥 TODO: Add Task Dialog
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = config.accent.copy(alpha = 0.15f),
                    contentColor   = config.accent
                ),
                shape  = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Text("+ Add Task", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }

        } else {

            tasks.forEach { task ->
                TaskCard(
                    task          = task,
                    currentUser   = currentUserId,
                    projectId     = projectId,
                    memberNames   = memberNames
                )
            }
        }
    }
}