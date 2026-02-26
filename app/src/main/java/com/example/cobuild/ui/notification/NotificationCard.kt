package com.example.cobuild.ui.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cobuild.data.model.AppNotification

private val PrimaryColor = Color(0xFF4F46E5)
private val TextSecondary = Color(0xFF64748B)

@Composable
fun NotificationCard(
    notification: AppNotification,
    onAccept: () -> Unit,
    onDeny: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenProject: () -> Unit,
    onOpenProfile: () -> Unit // 🔥 NEW
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenProject() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                Color.White else Color(0xFFF1F5FF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            /* -------- HEADER ROW -------- */
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 🔥 Profile Icon
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onOpenProfile() },
                    color = PrimaryColor.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "View Profile",
                            tint = PrimaryColor
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = "${notification.requesterName} wants to join",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = notification.projectTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            /* -------- ACTION BUTTONS -------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A),
                        contentColor = Color.White
                    )
                ) {
                    Text("Accept", fontWeight = FontWeight.Medium)
                }

                OutlinedButton(
                    onClick = onDeny,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC2626)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(Color(0xFFDC2626))
                    )
                ) {
                    Text("Deny", fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(10.dp))

            TextButton(
                onClick = onOpenChat,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = PrimaryColor
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Open Chat")
            }
        }
    }
}