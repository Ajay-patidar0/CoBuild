//package com.example.cobuild.messages
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.cobuild.model.Chat
//import com.example.cobuild.model.User
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatListScreen(navController: NavController) {
//
//    val auth = FirebaseAuth.getInstance()
//    val firestore = FirebaseFirestore.getInstance()
//    val currentUserId = auth.currentUser!!.uid
//
//    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }
//
//    LaunchedEffect(Unit) {
//        firestore.collection("chats")
//            .whereArrayContains("participants", currentUserId)
//            .addSnapshotListener { snapshot, _ ->
//                chats = snapshot?.documents?.mapNotNull {
//                    it.toObject(Chat::class.java)?.copy(chatId = it.id)
//                } ?: emptyList()
//            }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Messages") })
//        }
//    ) { padding ->
//
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//        ) {
//            items(chats) { chat ->
//                ChatRow(chat, currentUserId) {
//                    navController.navigate("chat/${chat.chatId}")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ChatRow(
//    chat: Chat,
//    currentUserId: String,
//    onClick: () -> Unit
//) {
//    val firestore = FirebaseFirestore.getInstance()
//    val otherUserId = chat.participants.first { it != currentUserId }
//    var user by remember { mutableStateOf<User?>(null) }
//
//    LaunchedEffect(otherUserId) {
//        firestore.collection("users")
//            .document(otherUserId)
//            .get()
//            .addOnSuccessListener {
//                user = it.toObject(User::class.java)
//            }
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() }
//            .padding(16.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//
//        // Avatar
//        Box(
//            modifier = Modifier
//                .size(48.dp)
//                .clip(CircleShape)
//                .background(Color(0xFFE5E7EB))
//        )
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        Column {
//            Text(
//                text = user?.name ?: "User",
//                fontWeight = FontWeight.Bold
//            )
//            Text(
//                text = chat.lastMessage,
//                color = Color.Gray,
//                maxLines = 1
//            )
//        }
//    }
//}

//package com.example.cobuild.messages
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.cobuild.model.Chat
//import com.example.cobuild.model.User
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//@Composable
//fun ChatListScreen(navController: NavController) {
//
//    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
//    val firestore = FirebaseFirestore.getInstance()
//
//    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }
//
//    LaunchedEffect(Unit) {
//        firestore.collection("chats")
//            .whereArrayContains("participants", currentUserId)
//            .addSnapshotListener { snapshot, _ ->
//                chats = snapshot?.documents
//                    ?.mapNotNull { it.toObject(Chat::class.java)?.copy(chatId = it.id) }
//                    ?: emptyList()
//            }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF9FAFB))
//            .statusBarsPadding()
//    ) {
//
//        // Header
//        Text(
//            text = "Messages",
//            fontSize = 22.sp,
//            fontWeight = FontWeight.SemiBold,
//            modifier = Modifier.padding(16.dp)
//        )
//
//        LazyColumn(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(chats) { chat ->
//                ChatRow(
//                    chat = chat,
//                    currentUserId = currentUserId
//                ) {
//                    navController.navigate("chat/${chat.chatId}")
//                }
//            }
//        }
//    }
//}
//
///* -------------------- CHAT ROW -------------------- */
//
//@Composable
//fun ChatRow(
//    chat: Chat,
//    currentUserId: String,
//    onClick: () -> Unit
//) {
//    val firestore = FirebaseFirestore.getInstance()
//    val otherUserId = chat.participants.first { it != currentUserId }
//
//    var user by remember { mutableStateOf<User?>(null) }
//
//    LaunchedEffect(otherUserId) {
//        firestore.collection("users")
//            .document(otherUserId)
//            .get()
//            .addOnSuccessListener {
//                user = it.toObject(User::class.java)
//            }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() }
//            .padding(horizontal = 16.dp, vertical = 12.dp)
//    ) {
//
//        Row(verticalAlignment = Alignment.CenterVertically) {
//
//            // Avatar (Clean & soft)
//            Box(
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFFE0E7FF)),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = user?.name?.firstOrNull()?.uppercase() ?: "?",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFF4338CA)
//                )
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Column(modifier = Modifier.weight(1f)) {
//
//                Text(
//                    text = user?.name ?: "User",
//                    fontSize = 15.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//
//                Spacer(modifier = Modifier.height(3.dp))
//
//                Text(
//                    text = chat.lastMessage.ifBlank { "Say hi 👋" },
//                    fontSize = 13.sp,
//                    color = Color(0xFF6B7280),
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//        }
//
//        // Soft divider (no black box feeling)
//        Divider(
//            modifier = Modifier.padding(top = 12.dp),
//            color = Color(0xFFE5E7EB),
//            thickness = 0.6.dp
//        )
//    }
//}




//package com.example.cobuild.messages
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Groups
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.cobuild.model.Chat
//import com.example.cobuild.model.User
//import com.example.cobuild.navigation.Destinations
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import java.text.SimpleDateFormat
//import java.util.*
//
//private val Primary     = Color(0xFF4F46E5)
//private val PrimaryBg   = Color(0xFFEDE9FE)
//private val PageBg      = Color(0xFFF9FAFB)
//private val CardBg      = Color.White
//private val TextPrimary = Color(0xFF1E293B)
//private val TextSub     = Color(0xFF6B7280)
//private val DividerCol  = Color(0xFFE5E7EB)
//private val TopBarColor = Color(0xFF0F172A)
//
///* ── data class for group chat rows ── */
//data class GroupChatPreview(
//    val projectId: String    = "",
//    val projectTitle: String = "",
//    val lastMessage: String  = "",
//    val lastTimestamp: Long  = 0L
//)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatListScreen(navController: NavController) {
//
//    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
//    val firestore     = FirebaseFirestore.getInstance()
//
//    var chats      by remember { mutableStateOf<List<Chat>>(emptyList()) }
//    var groupChats by remember { mutableStateOf<List<GroupChatPreview>>(emptyList()) }
//
//    /* ── load 1-on-1 chats ── */
//    LaunchedEffect(Unit) {
//        firestore.collection("chats")
//            .whereArrayContains("participants", currentUserId)
//            .addSnapshotListener { snap, _ ->
//                chats = snap?.documents
//                    ?.mapNotNull { it.toObject(Chat::class.java)?.copy(chatId = it.id) }
//                    ?: emptyList()
//            }
//
//        /* ── load group chats where user is a participant ── */
//        firestore.collection("group_chats")
//            .whereArrayContains("participants", currentUserId)
//            .addSnapshotListener { snap, _ ->
//                groupChats = snap?.documents?.mapNotNull { doc ->
//                    val data = doc.data ?: return@mapNotNull null
//                    GroupChatPreview(
//                        projectId    = doc.id,
//                        projectTitle = data["projectTitle"] as? String ?: "Team Chat",
//                        lastMessage  = data["lastMessage"]  as? String ?: "",
//                        lastTimestamp = data["lastTimestamp"] as? Long ?: 0L
//                    )
//                } ?: emptyList()
//            }
//    }
//
//    Scaffold(
//        containerColor = PageBg,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text("Messages", color = Color.White,
//                        fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBarColor)
//            )
//        }
//    ) { padding ->
//
//        LazyColumn(
//            modifier = Modifier.fillMaxSize().padding(padding),
//            contentPadding = PaddingValues(bottom = 24.dp)
//        ) {
//
//            /* ── GROUP CHATS section ── */
//            if (groupChats.isNotEmpty()) {
//                item {
//                    SectionLabel("Team Chats")
//                }
//                items(groupChats) { group ->
//                    GroupChatRow(group) {
//                        navController.navigate(
//                            Destinations.groupChatRoute(group.projectId, group.projectTitle)
//                        )
//                    }
//                }
//            }
//
//            /* ── 1-ON-1 CHATS section ── */
//            if (chats.isNotEmpty()) {
//                item {
//                    SectionLabel(if (groupChats.isNotEmpty()) "Direct Messages" else "Messages")
//                }
//                items(chats) { chat ->
//                    DirectChatRow(chat, currentUserId) {
//                        navController.navigate("chat/${chat.chatId}")
//                    }
//                }
//            }
//
//            /* ── empty state ── */
//            if (chats.isEmpty() && groupChats.isEmpty()) {
//                item {
//                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                            Text("💬", fontSize = 48.sp)
//                            Spacer(Modifier.height(12.dp))
//                            Text("No messages yet", fontSize = 16.sp,
//                                fontWeight = FontWeight.SemiBold, color = TextPrimary)
//                            Spacer(Modifier.height(4.dp))
//                            Text("Start collaborating on a project",
//                                fontSize = 14.sp, color = TextSub)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
///* ── GROUP CHAT ROW ── */
//@Composable
//private fun GroupChatRow(group: GroupChatPreview, onClick: () -> Unit) {
//    val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(CardBg)
//            .clickable { onClick() }
//            .padding(horizontal = 16.dp, vertical = 14.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        /* group icon avatar */
//        Box(
//            modifier = Modifier
//                .size(50.dp)
//                .clip(CircleShape)
//                .background(PrimaryBg),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(Icons.Default.Groups, null,
//                tint = Primary, modifier = Modifier.size(26.dp))
//        }
//
//        Spacer(Modifier.width(14.dp))
//
//        Column(Modifier.weight(1f)) {
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically) {
//                Text(group.projectTitle, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
//                    color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.weight(1f))
//                if (group.lastTimestamp > 0) {
//                    Text(fmt.format(Date(group.lastTimestamp)),
//                        fontSize = 11.sp, color = TextSub)
//                }
//            }
//            Spacer(Modifier.height(3.dp))
//            Text(
//                group.lastMessage.ifBlank { "Team chat • Tap to open" },
//                fontSize = 13.sp, color = TextSub,
//                maxLines = 1, overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//
//    Divider(modifier = Modifier.padding(start = 80.dp),
//        color = DividerCol, thickness = 0.6.dp)
//}
//
///* ── 1-ON-1 CHAT ROW ── */
//@Composable
//fun DirectChatRow(chat: Chat, currentUserId: String, onClick: () -> Unit) {
//    val firestore   = FirebaseFirestore.getInstance()
//    val otherUserId = chat.participants.first { it != currentUserId }
//    var user        by remember { mutableStateOf<User?>(null) }
//
//    LaunchedEffect(otherUserId) {
//        firestore.collection("users").document(otherUserId).get()
//            .addOnSuccessListener { user = it.toObject(User::class.java) }
//    }
//
//    val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(CardBg)
//            .clickable { onClick() }
//            .padding(horizontal = 16.dp, vertical = 14.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Box(
//            modifier = Modifier
//                .size(50.dp)
//                .clip(CircleShape)
//                .background(Color(0xFFE0E7FF)),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                user?.name?.firstOrNull()?.uppercase() ?: "?",
//                fontSize = 19.sp, fontWeight = FontWeight.Bold,
//                color = Color(0xFF4338CA)
//            )
//        }
//
//        Spacer(Modifier.width(14.dp))
//
//        Column(Modifier.weight(1f)) {
//            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically) {
//                Text(user?.name ?: "User", fontSize = 15.sp,
//                    fontWeight = FontWeight.SemiBold, color = TextPrimary,
//                    maxLines = 1, overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.weight(1f))
//                if (chat.lastTimestamp > 0) {
//                    Text(fmt.format(Date(chat.lastTimestamp)),
//                        fontSize = 11.sp, color = TextSub)
//                }
//            }
//            Spacer(Modifier.height(3.dp))
//            Text(
//                chat.lastMessage.ifBlank { "Say hi 👋" },
//                fontSize = 13.sp, color = TextSub,
//                maxLines = 1, overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//
//    Divider(modifier = Modifier.padding(start = 80.dp),
//        color = DividerCol, thickness = 0.6.dp)
//}
//
//@Composable
//private fun SectionLabel(text: String) {
//    Text(
//        text     = text,
//        fontSize = 12.sp,
//        fontWeight = FontWeight.SemiBold,
//        color    = TextSub,
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color(0xFFF1F5F9))
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//    )
//}

package com.example.cobuild.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cobuild.model.Chat
import com.example.cobuild.model.User
import com.example.cobuild.navigation.Destinations
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

/* ── design tokens ── */
private val TopBar     = Color(0xFF0F172A)
private val PageBg     = Color(0xFFF8FAFC)
private val CardBg     = Color.White
private val Primary    = Color(0xFF4F46E5)
private val PrimaryBg  = Color(0xFFEDE9FE)
private val TextPrimary= Color(0xFF1E293B)
private val TextSub    = Color(0xFF64748B)
private val TextHint   = Color(0xFF94A3B8)
private val Border     = Color(0xFFE2E8F0)

data class GroupChatPreview(
    val projectId: String    = "",
    val projectTitle: String = "",
    val lastMessage: String  = "",
    val lastTimestamp: Long  = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    onBackClick: (() -> Unit)? = null   // optional — hoisted so it works from any nav setup
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    val firestore     = FirebaseFirestore.getInstance()

    var chats      by remember { mutableStateOf<List<Chat>>(emptyList()) }
    var groupChats by remember { mutableStateOf<List<GroupChatPreview>>(emptyList()) }

    LaunchedEffect(Unit) {
        /* 1-on-1 chats */
        firestore.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snap, _ ->
                chats = snap?.documents
                    ?.mapNotNull { it.toObject(Chat::class.java)?.copy(chatId = it.id) }
                    ?: emptyList()
            }

        /* group / team chats */
        firestore.collection("group_chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snap, _ ->
                groupChats = snap?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    GroupChatPreview(
                        projectId    = doc.id,
                        projectTitle = data["projectTitle"] as? String ?: "Team Chat",
                        lastMessage  = data["lastMessage"]  as? String ?: "",
                        lastTimestamp = data["lastTimestamp"] as? Long ?: 0L
                    )
                } ?: emptyList()
            }
    }

    Scaffold(
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Messages",
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick?.invoke() ?: navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBar)
            )
        }
    ) { padding ->

        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            /* ── TEAM CHATS ── */
            if (groupChats.isNotEmpty()) {
                item { SectionHeader("Team Chats") }
                items(groupChats, key = { it.projectId }) { group ->
                    TeamChatRow(group) {
                        navController.navigate(
                            Destinations.groupChatRoute(group.projectId, group.projectTitle)
                        )
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }

            /* ── DIRECT MESSAGES ── */
            if (chats.isNotEmpty()) {
                item {
                    SectionHeader(
                        if (groupChats.isNotEmpty()) "Direct Messages" else "Direct Messages"
                    )
                }
                items(chats, key = { it.chatId }) { chat ->
                    DirectChatRow(chat, currentUserId) {
                        navController.navigate("chat/${chat.chatId}")
                    }
                }
            }

            /* ── EMPTY STATE ── */
            if (chats.isEmpty() && groupChats.isEmpty()) {
                item {
                    Box(
                        Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💬", fontSize = 52.sp)
                            Spacer(Modifier.height(14.dp))
                            Text(
                                "No messages yet",
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Join a project to start chatting with your team",
                                fontSize = 14.sp,
                                color    = TextSub
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ── SECTION HEADER ── */
@Composable
private fun SectionHeader(title: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            title,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Bold,
            color      = TextHint,
            letterSpacing = 0.8.sp
        )
    }
}

/* ── TEAM CHAT ROW ── */
@Composable
private fun TeamChatRow(group: GroupChatPreview, onClick: () -> Unit) {
    val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        border    = androidx.compose.foundation.BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /* group avatar */
            Box(
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(PrimaryBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Groups,
                    null,
                    tint     = Primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        group.projectTitle,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f)
                    )
                    if (group.lastTimestamp > 0) {
                        Text(
                            fmt.format(Date(group.lastTimestamp)),
                            fontSize = 11.sp,
                            color    = TextHint
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    /* "Team" badge */
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = PrimaryBg
                    ) {
                        Text(
                            "Team",
                            fontSize = 10.sp,
                            color    = Primary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                    Text(
                        group.lastMessage.ifBlank { "Tap to open team chat" },
                        fontSize = 13.sp,
                        color    = TextSub,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/* ── DIRECT CHAT ROW ── */
@Composable
fun DirectChatRow(chat: Chat, currentUserId: String, onClick: () -> Unit) {
    val firestore   = FirebaseFirestore.getInstance()
    val otherUserId = chat.participants.firstOrNull { it != currentUserId } ?: return
    var user        by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(otherUserId) {
        firestore.collection("users").document(otherUserId).get()
            .addOnSuccessListener { user = it.toObject(User::class.java) }
    }

    val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        border    = androidx.compose.foundation.BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /* avatar */
            Box(
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    user?.name?.firstOrNull()?.uppercase() ?: "?",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF4338CA)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        user?.name ?: "User",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f)
                    )
                    if (chat.lastTimestamp > 0) {
                        Text(
                            fmt.format(Date(chat.lastTimestamp)),
                            fontSize = 11.sp,
                            color    = TextHint
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    chat.lastMessage.ifBlank { "Say hi 👋" },
                    fontSize = 13.sp,
                    color    = TextSub,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}