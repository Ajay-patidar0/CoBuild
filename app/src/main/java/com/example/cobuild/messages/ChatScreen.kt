//package com.example.cobuild.messages
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.cobuild.model.Message
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ListenerRegistration
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatScreen(
//    chatId: String,
//    onBackClick: () -> Unit
//) {
//    val auth = FirebaseAuth.getInstance()
//    val firestore = FirebaseFirestore.getInstance()
//    val currentUserId = auth.currentUser?.uid ?: return
//
//    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
//    var text by remember { mutableStateOf("") }
//
//    // 🔥 Proper realtime listener
//    DisposableEffect(chatId) {
//        val listener: ListenerRegistration =
//            firestore.collection("chats")
//                .document(chatId)
//                .collection("messages")
//                .orderBy("timestamp")
//                .addSnapshotListener { snapshot, _ ->
//                    messages = snapshot?.documents
//                        ?.mapNotNull { it.toObject(Message::class.java) }
//                        ?: emptyList()
//                }
//
//        onDispose {
//            listener.remove()
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Chat") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//
//            LazyColumn(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(8.dp)
//            ) {
//                items(messages) { message ->
//                    MessageBubble(
//                        message = message,
//                        isMe = message.senderId == currentUserId
//                    )
//                }
//            }
//
//            MessageInput(
//                text = text,
//                onTextChange = { text = it },
//                onSend = {
//                    if (text.isBlank()) return@MessageInput
//
//                    val msg = Message(
//                        senderId = currentUserId,
//                        text = text.trim(),
//                        timestamp = System.currentTimeMillis()
//                    )
//
//                    firestore.collection("chats")
//                        .document(chatId)
//                        .collection("messages")
//                        .add(msg)
//
//                    firestore.collection("chats")
//                        .document(chatId)
//                        .update(
//                            mapOf(
//                                "lastMessage" to msg.text,
//                                "lastTimestamp" to msg.timestamp
//                            )
//                        )
//
//                    text = ""
//                }
//            )
//        }
//    }
//}

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cobuild.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    onBackClick: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var text by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    // 🔥 Realtime messages
    DisposableEffect(chatId) {
        val listener: ListenerRegistration =
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, _ ->
                    messages = snapshot?.documents
                        ?.mapNotNull { it.toObject(Message::class.java) }
                        ?: emptyList()
                }

        onDispose { listener.remove() }
    }

    // 🔥 Auto-scroll to bottom
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chat", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            MessageInput(
                text = text,
                onTextChange = { text = it }, // ✅ inference fixed
                onSend = {
                    if (text.isBlank()) return@MessageInput

                    val msg = Message(
                        senderId = currentUserId,
                        text = text.trim(),
                        timestamp = System.currentTimeMillis()
                    )

                    firestore.collection("chats")
                        .document(chatId)
                        .collection("messages")
                        .add(msg)

                    firestore.collection("chats")
                        .document(chatId)
                        .update(
                            mapOf(
                                "lastMessage" to msg.text,
                                "lastTimestamp" to msg.timestamp
                            )
                        )

                    text = ""
                }
            )
        }
    ) { padding ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // ✅ correct inset handling
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    isMe = message.senderId == currentUserId
                )
            }
        }
    }

}

/* -------------------- MESSAGE BUBBLE -------------------- */
/* ✅ KEEP ONLY THIS ONE IN PROJECT */

@Composable
fun MessageBubble(
    message: Message,
    isMe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isMe) Color(0xFF4F46E5) else Color(0xFFE5E7EB),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            )
        ) {
            Text(
                text = message.text,
                color = if (isMe) Color.White else Color.Black,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

/* -------------------- MESSAGE INPUT -------------------- */
@Composable
fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // ✅ FIXES 3-button nav
            .imePadding()            // ✅ FIXES keyboard
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message…") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (text.isNotBlank()) Color(0xFF4F46E5)
                        else Color(0xFFCBD5E1),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}
