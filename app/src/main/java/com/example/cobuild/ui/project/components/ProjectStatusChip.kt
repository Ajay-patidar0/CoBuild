package com.example.cobuild.ui.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cobuild.data.model.ProjectStatus

@Composable
fun ProjectStatusChip(status: ProjectStatus) {

    val (label, color, bgColor) = when (status) {
        ProjectStatus.YET_TO_START -> Triple("Pending", Color(0xFF64748B), Color(0xFFF1F5F9)) // Gray
        ProjectStatus.IN_PROGRESS -> Triple("Active", Color(0xFF3B82F6), Color(0xFFEFF6FF)) // Blue
        ProjectStatus.COMPLETED -> Triple("Done", Color(0xFF22C55E), Color(0xFFF0FDF4)) // Green
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}