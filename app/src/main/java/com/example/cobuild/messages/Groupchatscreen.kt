package com.example.cobuild.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*

/* ── tokens matching ProjectDetailScreen ── */
private val Bg0          = Color(0xFF0A0F1E)
private val Bg1          = Color(0xFF0F172A)
private val Bg2          = Color(0xFF1E293B)
private val Bg3          = Color(0xFF273548)
private val AccentViolet = Color(0xFF818CF8)
private val AccentCyan   = Color(0xFF22D3EE)
private val TextHigh     = Color(0xFFF1F5F9)
private val TextMid      = Color(0xFF94A3B8)
private val TextLow      = Color(0xFF475569)

data class GroupMessage(
    val id: String      = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String    = "",
    val timestamp: Long = 0L
)

/**
 * One shared chat room per project.
 * Firestore path: group_chats/{projectId}/messages/{msgId}
 * Call this from the 👥 button in ProjectDetailScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatScreen(
    projectId: String,
    projectTitle: String,
    onBackClick: () -> Unit
) {
    val firestore     = FirebaseFirestore.getInstance()
    val currentUid    = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var messages      by remember { mutableStateOf<List<GroupMessage>>(emptyList()) }
    var memberNames   by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var inputText     by remember { mutableStateOf("") }
    var senderName    by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    /* ── load sender name + member names ── */
    LaunchedEffect(projectId) {
        firestore.collection("users").document(currentUid).get()
            .addOnSuccessListener { doc -> senderName = doc.getString("name") ?: "User" }

        firestore.collection("users").get().addOnSuccessListener { snap ->
            memberNames = snap.documents.associate { it.id to (it.getString("name") ?: "User") }
        }

        /* ensure group_chats document exists */
        val chatRef = firestore.collection("group_chats").document(projectId)
        chatRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                chatRef.set(mapOf("projectId" to projectId, "createdAt" to System.currentTimeMillis()))
            }
        }
    }

    /* ── realtime messages ── */
    DisposableEffect(projectId) {
        val listener: ListenerRegistration = firestore
            .collection("group_chats").document(projectId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snap, _ ->
                messages = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(GroupMessage::class.java)?.copy(id = doc.id)
                } ?: emptyList()
            }
        onDispose { listener.remove() }
    }

    /* ── auto scroll ── */
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    fun sendMessage() {
        val text = inputText.trim()
        if (text.isBlank()) return
        firestore.collection("group_chats").document(projectId)
            .collection("messages")
            .add(mapOf(
                "senderId"   to currentUid,
                "senderName" to senderName,
                "text"       to text,
                "timestamp"  to System.currentTimeMillis()
            ))
        inputText = ""
    }

    Scaffold(
        containerColor = Bg0,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(projectTitle, color = TextHigh,
                            fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                            maxLines = 1)
                        Text("Team Chat", color = TextMid, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextHigh)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg1)
            )
        },
        bottomBar = {
            Surface(color = Bg1, tonalElevation = 0.dp) {
                Row(
                    Modifier.fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value         = inputText,
                        onValueChange = { inputText = it },
                        placeholder   = { Text("Message the team…", color = TextLow, fontSize = 14.sp) },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f),
                        shape         = RoundedCornerShape(24.dp),
                        colors        = TextFieldDefaults.colors(
                            focusedContainerColor   = Bg2,
                            unfocusedContainerColor = Bg2,
                            focusedIndicatorColor   = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor        = TextHigh,
                            unfocusedTextColor      = TextHigh
                        )
                    )
                    IconButton(
                        onClick  = ::sendMessage,
                        enabled  = inputText.isNotBlank(),
                        modifier = Modifier.size(46.dp)
                            .background(
                                if (inputText.isNotBlank()) AccentViolet else Bg2,
                                CircleShape
                            )
                    ) {
                        Icon(Icons.Default.Send, null,
                            tint = if (inputText.isNotBlank()) Color.White else TextLow,
                            modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    ) { padding ->

        if (messages.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💬", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("No messages yet", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextHigh)
                    Spacer(Modifier.height(4.dp))
                    Text("Say hi to the team!", fontSize = 14.sp, color = TextMid)
                }
            }
            return@Scaffold
        }

        LazyColumn(
            state         = listState,
            modifier      = Modifier.fillMaxSize().padding(padding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                GroupMsgBubble(msg, isMe = msg.senderId == currentUid)
            }
        }
    }
}

@Composable
private fun GroupMsgBubble(msg: GroupMessage, isMe: Boolean) {
    val fmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment     = Alignment.Bottom
    ) {
        if (!isMe) {
            /* sender avatar */
            Box(
                Modifier.size(30.dp).clip(CircleShape)
                    .background(AccentViolet.copy(.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(msg.senderName.firstOrNull()?.uppercase() ?: "?",
                    color = AccentViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            Modifier.weight(1f, fill = false).widthIn(max = 280.dp),
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            /* name above bubble (others only) */
            if (!isMe) {
                Text(msg.senderName, fontSize = 11.sp, color = AccentViolet,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
            }

            Box(
                Modifier
                    .background(
                        if (isMe) AccentViolet else Bg2,
                        RoundedCornerShape(
                            topStart    = if (isMe) 16.dp else 4.dp,
                            topEnd      = if (isMe) 4.dp else 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd   = 16.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(msg.text, color = if (isMe) Color.White else TextHigh, fontSize = 14.sp)
            }

            Text(fmt.format(Date(msg.timestamp)), fontSize = 10.sp, color = TextLow,
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp))
        }

        if (isMe) Spacer(Modifier.width(8.dp))
    }
}