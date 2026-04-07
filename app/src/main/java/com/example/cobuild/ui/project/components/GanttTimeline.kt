package com.example.cobuild.ui.project.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cobuild.data.model.Task
import com.example.cobuild.utils.taskProgress

// ── Design tokens ──────────────────────────────────────────────
private val BgCard      = Color(0xFF1E293B)
private val TrackBg     = Color(0xFF0F172A)
private val TextPrimary = Color(0xFFF1F5F9)
private val TextMuted   = Color(0xFF64748B)
private val AccentStart = Color(0xFF6366F1)
private val AccentEnd   = Color(0xFF22D3EE)

private fun barBrush() = Brush.horizontalGradient(listOf(AccentStart, AccentEnd))

private fun statusDotColor(status: String) = when (status) {
    "DONE"        -> Color(0xFF34D399)
    "IN_PROGRESS" -> Color(0xFFFBBF24)
    else          -> Color(0xFF818CF8)
}

@Composable
fun GanttTimeline(tasks: List<Task>) {

    Column {

        // ── Section header ──
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(AccentStart, CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Project Timeline",
                color      = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        tasks.forEachIndexed { index, task ->
            GanttRow(task = task, index = index)
            if (index < tasks.lastIndex) Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun GanttRow(task: Task, index: Int) {

    val progress = taskProgress(task)

    // Animate on first composition
    var triggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { triggered = true }

    val animatedProgress by animateFloatAsState(
        targetValue   = if (triggered) progress else 0f,
        animationSpec = tween(durationMillis = 700, delayMillis = index * 80),
        label         = "gantt_$index"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgCard, RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {

            // ── Title + status dot ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusDotColor(task.status), CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text       = task.title,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize   = 14.sp,
                    modifier   = Modifier.weight(1f)
                )
                // Percentage label
                Text(
                    text     = "${(animatedProgress * 100).toInt()}%",
                    color    = AccentEnd,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(10.dp))

            // ── Progress track ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(TrackBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(8.dp)
                        .background(barBrush())
                )

                // Glow end cap
                if (animatedProgress > 0.02f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(animatedProgress)
                            .wrapContentWidth(Alignment.End)
                            .size(8.dp)
                            .background(Color.White.copy(alpha = 0.6f), CircleShape)
                    )
                }
            }
        }
    }
}