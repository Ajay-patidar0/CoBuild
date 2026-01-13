package com.example.cobuild.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUserId = auth.currentUser?.uid ?: return

    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var text by remember { mutableStateOf("") }

    // 🔥 Proper realtime listener
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

        onDispose {
            listener.remove()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        isMe = message.senderId == currentUserId
                    )
                }
            }

            MessageInput(
                text = text,
                onTextChange = { text = it },
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
    }
}