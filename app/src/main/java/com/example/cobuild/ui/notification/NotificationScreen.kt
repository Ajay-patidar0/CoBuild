//package com.example.cobuild.ui.notification
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NotificationScreen(
//    onBackClick: () -> Unit,
//    onJoinRequestClick: (String) -> Unit
//) {
//    val viewModel: NotificationViewModel = viewModel()
//    val notifications by viewModel.notifications.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Notifications", fontWeight = FontWeight.SemiBold) },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//
//        if (notifications.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("No join requests yet")
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .padding(padding)
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
////                items(notifications) { notification ->
////                    NotificationCard(
////                        notification = notification,
////                        onClick = { onJoinRequestClick(notification.projectId) }
////                    )
////                }
//                items(notifications) { notification ->
//                    NotificationCard(
//                        notification = notification,
//                        onAccept = {
//                            viewModel.acceptRequest(notification)
//                        },
//                        onDeny = {
//                            viewModel.denyRequest(notification)
//                        },
//                        onOpenChat = {
//                            onJoinRequestClick(notification.projectId)
//                        }
//                    )
//                }
//
//            }
//        }
//    }
//}
//
//package com.example.cobuild.ui.notification
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NotificationScreen(
//    onBackClick: () -> Unit,
//
//    // 🔥 opens chat/{chatId}
//    onOpenChatClick: (String) -> Unit,
//
//    // 🔥 opens project_detail/{projectId}
//    onOpenProjectClick: (String) -> Unit,
//    onOpenProfileClick: (String) -> Unit
//) {
//    val viewModel: NotificationViewModel = viewModel()
//    val notifications by viewModel.notifications.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Notifications", fontWeight = FontWeight.SemiBold) },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//
//        if (notifications.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("No join requests yet")
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .padding(padding)
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                items(notifications) { notification ->
//                    NotificationCard(
//                        notification = notification,
//
//                        onAccept = {
//                            viewModel.acceptRequest(notification)
//                        },
//
//                        onDeny = {
//                            viewModel.denyRequest(notification)
//                        },
//
//                        onOpenChat = {
//                            viewModel.openChat(
//                                notification = notification,
//                                onChatFound = { chatId ->
//                                    onOpenChatClick(chatId)
//                                }
//                            )
//                        },
//
//                        onOpenProject = {
//                            onOpenProjectClick(notification.projectId)
//                        },
//
//                        onOpenProfile = {
//                            onOpenProfileClick(notification.userId) // 🔥 PASS USER ID
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
package com.example.cobuild.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cobuild.data.model.AppNotification

/* ── tokens ── */
private val TopBar    = Color(0xFF0F172A)
private val PageBg    = Color(0xFFF8FAFC)
private val CardBg    = Color.White
private val Primary   = Color(0xFF4F46E5)
private val PrimaryBg = Color(0xFFEDE9FE)
private val Green     = Color(0xFF16A34A)
private val GreenBg   = Color(0xFFDCFCE7)
private val Red       = Color(0xFFDC2626)
private val RedBg     = Color(0xFFFEE2E2)
private val TextPrimary = Color(0xFF1E293B)
private val TextSub   = Color(0xFF64748B)
private val TextHint  = Color(0xFF94A3B8)
private val Border    = Color(0xFFE2E8F0)

/* ══════════════════ SCREEN ══════════════════════════════════════════════ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    onOpenChatClick: (String) -> Unit,
    onOpenProjectClick: (String) -> Unit,
    onOpenProfileClick: (String) -> Unit
) {
    val viewModel: NotificationViewModel = viewModel()
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notifications",
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBar)
            )
        }
    ) { padding ->

        if (notifications.isEmpty()) {
            /* ── EMPTY STATE ── */
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        Modifier
                            .size(80.dp)
                            .background(PrimaryBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            null,
                            tint     = Primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        "No notifications yet",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary
                    )
                    Text(
                        "Join requests from other builders\nwill appear here",
                        fontSize  = 14.sp,
                        color     = TextSub,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier       = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${notifications.size} pending request${if (notifications.size > 1) "s" else ""}",
                        fontSize = 13.sp,
                        color    = TextSub,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(notifications) { notification ->
                    NotificationCard(
                        notification  = notification,
                        onAccept      = { viewModel.acceptRequest(notification) },
                        onDeny        = { viewModel.denyRequest(notification) },
                        onOpenChat    = {
                            viewModel.openChat(notification) { chatId ->
                                onOpenChatClick(chatId)
                            }
                        },
                        onOpenProject = { onOpenProjectClick(notification.projectId) },
                        onOpenProfile = { onOpenProfileClick(notification.userId) }
                    )
                }
            }
        }
    }
}

/* ══════════════════ CARD ════════════════════════════════════════════════ */

@Composable
fun NotificationCard(
    notification: AppNotification,
    onAccept: () -> Unit,
    onDeny: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenProject: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (notification.isRead) CardBg else Color(0xFFFAF9FF)
        ),
        border    = androidx.compose.foundation.BorderStroke(
            width = if (notification.isRead) 1.dp else 1.5.dp,
            color = if (notification.isRead) Border else Primary.copy(.25f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            /* ── HEADER ── */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                /* avatar — tappable to open profile */
                Box(
                    Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush(
                                Primary.copy(.15f),
                                Color(0xFF818CF8).copy(.15f)
                            )
                        )
                        .clickable { onOpenProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        notification.requesterName
                            .firstOrNull()?.uppercase() ?: "?",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Primary
                    )
                }

                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            notification.requesterName,
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary
                        )
                        /* unread dot */
                        if (!notification.isRead) {
                            Box(
                                Modifier.size(7.dp)
                                    .background(Primary, CircleShape)
                            )
                        }
                    }
//                    Text(
//                        "wants to join your project",
//                        fontSize = 13.sp,
//                        color    = TextSub
//                    )
                    Text(
                        if (notification.isInvite) "invited you to join their project"
                        else "wants to join your project",
                        fontSize = 13.sp,
                        color    = TextSub
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            /* ── PROJECT CHIP ── */
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(PrimaryBg, RoundedCornerShape(10.dp))
                    .clickable { onOpenProject() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(Modifier.size(6.dp).background(Primary, CircleShape))
                Text(
                    notification.projectTitle,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Primary,
                    modifier   = Modifier.weight(1f)
                )
                Text(
                    "View →",
                    fontSize = 12.sp,
                    color    = Primary.copy(.6f)
                )
            }

            Spacer(Modifier.height(14.dp))

            /* ── ACTION BUTTONS ── */
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                /* ACCEPT */
                Button(
                    onClick  = onAccept,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Green,
                        contentColor   = Color.White
                    )
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Accept", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                /* DENY */
                OutlinedButton(
                    onClick  = onDeny,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape    = RoundedCornerShape(12.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, Red.copy(.5f)),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Red)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("Deny", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            /* ── OPEN CHAT ── */
            TextButton(
                onClick  = onOpenChat,
                modifier = Modifier.fillMaxWidth().height(38.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.textButtonColors(contentColor = TextSub)
            ) {
                Icon(Icons.Default.Message, null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text("Open Chat", fontSize = 13.sp)
            }
        }
    }
}

/* ── tiny helper to fake a gradient background without import issues ── */
private fun Brush(start: Color, end: Color) =
    androidx.compose.ui.graphics.Brush.linearGradient(listOf(start, end))
