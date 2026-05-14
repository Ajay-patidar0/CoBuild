//package com.example.cobuild.ui.project.components
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ChevronRight
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.cobuild.data.model.Project
//
//private val BorderLight = Color(0xFFE2E8F0)
//private val TextPrimary = Color(0xFF1E293B)
//private val TextSecondary = Color(0xFF64748B)
//private val PrimaryColor = Color(0xFF4F46E5)
//
//@Composable
//fun ProjectCard(
//    project: Project,
//    onClick: () -> Unit,
//) {
//    Card(
//        onClick = onClick,
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        border = BorderStroke(1.dp, BorderLight),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            // Header: Title and Status
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Status Chip (Small)
//                ProjectStatusChip(project.status)
//            }
//
//            Spacer(modifier = Modifier.height(10.dp))
//
//            // Title
//            Text(
//                text = project.title.ifBlank { "Untitled Project" },
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = TextPrimary,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//
//            // Owner
//            Text(
//                text = "by ${project.ownerName.ifBlank { "Unknown" }}",
//                fontSize = 13.sp,
//                color = TextSecondary,
//                fontWeight = FontWeight.Medium
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Description
//            Text(
//                text = project.description
//                    ?.takeIf { it.isNotBlank() }
//                    ?: "No description provided.",
//                fontSize = 14.sp,
//                color = TextSecondary,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis,
//                lineHeight = 20.sp
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Footer: Skills and Action Icon
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                // Skills Row (Limit to 2 to prevent overflow, add +X if more)
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(6.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.weight(1f)
//                ) {
//                    if (project.skills.isNotEmpty()) {
//                        project.skills.take(2).forEach { skill ->
//                            SkillChip(text = skill)
//                        }
//                        if (project.skills.size > 2) {
//                            Text(
//                                text = "+${project.skills.size - 2}",
//                                fontSize = 12.sp,
//                                color = TextSecondary,
//                                fontWeight = FontWeight.SemiBold
//                            )
//                        }
//                    } else {
//                        Text(
//                            text = "No specific skills",
//                            fontSize = 12.sp,
//                            color = TextSecondary.copy(alpha = 0.7f),
//                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
//                        )
//                    }
//                }
//
//                // Chevron indicating interaction
//                Icon(
//                    imageVector = Icons.Default.ChevronRight,
//                    contentDescription = "View Details",
//                    tint = TextSecondary.copy(alpha = 0.5f)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun SkillChip(text: String) {
//    Surface(
//        color = PrimaryColor.copy(alpha = 0.08f),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Text(
//            text = text,
//            fontSize = 12.sp,
//            color = PrimaryColor,
//            fontWeight = FontWeight.Medium,
//            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//        )
//    }
//}

package com.example.cobuild.ui.project.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
fun ProjectCard(
    project: Project,
    onClick: () -> Unit,
    onMembersClick: (List<String>) -> Unit,
    canEdit: Boolean = false,
    onEditClick: (() -> Unit)? = null,
    topMatches: List<TopMatch> = emptyList(),   // ← ADD
    ownerId: String = ""                         // ← ADD
) {

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

                Row(verticalAlignment = Alignment.CenterVertically) {

                    ProjectStatusChip(project.status)

                    Spacer(Modifier.width(8.dp))

                    // Edit icon only for posted projects
                    if (canEdit && onEditClick != null) {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Project",
                                tint = PrimaryColor
                            )
                        }
                    }
                }

                // 👥 MEMBERS COUNT
                TextButton(
                    onClick = { onMembersClick(project.members) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {

                    Text(
                        text = "👥 ${project.members.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryColor
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

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
                color = TextSecondary
            )

            Spacer(Modifier.height(12.dp))

            /* ---------- DESCRIPTION ---------- */

            Text(
                text = project.description?.takeIf { it.isNotBlank() }
                    ?: "No description provided.",
                fontSize = 14.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            /* ---------- FOOTER ---------- */

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Skills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {

                    if (project.skills.isNotEmpty()) {

                        project.skills.take(2).forEach {
                            SkillChip(it)
                        }

                        if (project.skills.size > 2) {

                            Text(
                                "+${project.skills.size - 2}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                    } else {

                        Text(
                            "No skills",
                            fontSize = 12.sp,
                            color = TextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.5f)
                )
            }
        }
    }
    // Show AI matches only for owner on posted projects
    if (canEdit && topMatches.isNotEmpty()) {
        TopMatchesSection(
            projectId    = project.id,
            projectTitle = project.title,
            ownerId      = ownerId,
            topMatches   = topMatches
        )
    }
}

/* ---------- SKILL CHIP ---------- */

@Composable
private fun SkillChip(text: String) {

    Surface(
        color = PrimaryColor.copy(alpha = 0.08f),
        shape = RoundedCornerShape(8.dp)
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
