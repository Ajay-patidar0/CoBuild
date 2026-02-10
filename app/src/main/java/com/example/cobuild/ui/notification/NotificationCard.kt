package com.example.cobuild.ui.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cobuild.data.model.AppNotification
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.filled.ChatBubbleOutline

private val PrimaryColor = Color(0xFF4F46E5)
private val TextSecondary = Color(0xFF64748B)

//@Composable
//fun NotificationCard(
//    notification: AppNotification,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//        shape = RoundedCornerShape(14.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = if (notification.isRead)
//                Color.White else Color(0xFFF1F5FF)
//        )
//    ) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//
//            Icon(
//                imageVector = Icons.Default.Notifications,
//                contentDescription = null,
//                tint = PrimaryColor
//            )
//
//            Spacer(Modifier.width(12.dp))
//
//            Column {
//                Text(
//                    text = "${notification.requesterName} requested to join",
//                    fontWeight = FontWeight.SemiBold
//                )
//                Spacer(Modifier.height(4.dp))
//                Text(
//                    text = notification.projectTitle,
//                    color = TextSecondary,
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//        }
//    }
//}
@Composable
fun NotificationCard(
    notification: AppNotification,
    onAccept: () -> Unit,
    onDeny: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenProject: () -> Unit
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

            /* -------- Title -------- */
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

            Spacer(Modifier.height(14.dp))

            /* -------- ACTION BUTTONS -------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ✅ ACCEPT (GREEN)
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A), // rich green
                        contentColor = Color.White
                    )
                ) {
                    Text("Accept", fontWeight = FontWeight.Medium)
                }

                // ❌ DENY (RED OUTLINED)
                OutlinedButton(
                    onClick = onDeny,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC2626) // red
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(Color(0xFFDC2626))
                    )
                ) {
                    Text("Deny", fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(10.dp))

            /* -------- OPEN CHAT -------- */
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
