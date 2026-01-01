package com.example.cobuild.ui.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cobuild.data.model.Project

private val BorderLight = Color(0xFFE2E8F0)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val PrimaryColor = Color(0xFF4F46E5)

@Composable
fun CompactProjectCard(
    project: Project
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderLight),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = project.title.ifBlank { "Untitled Project" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "by ${project.ownerName.ifBlank { "Unknown" }}",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = project.description
                    ?.takeIf { it.isNotBlank() }
                    ?: "No description provided",
                fontSize = 14.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )


            Spacer(modifier = Modifier.height(12.dp))

            if (project.skills.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    project.skills
                        .take(3)
                        .forEach { skill ->
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = skill,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = PrimaryColor.copy(alpha = 0.12f),
                                    labelColor = PrimaryColor
                                )
                            )
                        }
                }
            }
        }
    }
}
