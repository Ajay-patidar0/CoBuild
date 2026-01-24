package com.example.cobuild.ui.project.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cobuild.data.model.Project

/* ---------------- COLORS ---------------- */

private val BorderLight = Color(0xFFE2E8F0)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF64748B)
private val PrimaryColor = Color(0xFF4F46E5)

/* ---------------- PROJECT CARD ---------------- */


@Composable
fun ProjectCard(
    project: Project,
    onClick: () -> Unit,
    onJoinClick: (() -> Unit)? = null
)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderLight),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            /* ---------- HEADER ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProjectStatusChip(project.status)

                // + JOIN BUTTON
                IconButton(
                    onClick = { onJoinClick?.invoke() },
                    enabled = onJoinClick != null
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryColor.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Join Project",
                            tint = PrimaryColor,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            /* ---------- TITLE ---------- */
            Text(
                text = project.title.ifBlank { "Untitled Project" },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            /* ---------- OWNER ---------- */
            Text(
                text = "by ${project.ownerName.ifBlank { "Unknown" }}",
                fontSize = 13.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            /* ---------- DESCRIPTION ---------- */
            Text(
                text = project.description
                    ?.takeIf { it.isNotBlank() }
                    ?: "No description provided.",
                fontSize = 14.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------- FOOTER ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // SKILLS
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (project.skills.isNotEmpty()) {
                        project.skills.take(2).forEach { skill ->
                            SkillChip(text = skill)
                        }
                        if (project.skills.size > 2) {
                            Text(
                                text = "+${project.skills.size - 2}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Text(
                            text = "No specific skills",
                            fontSize = 12.sp,
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                // CHEVRON
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Details",
                    tint = TextSecondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/* ---------------- SKILL CHIP ---------------- */

@Composable
private fun SkillChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = PrimaryColor.copy(alpha = 0.08f)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = PrimaryColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
