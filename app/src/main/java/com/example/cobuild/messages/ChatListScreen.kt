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

package com.example.cobuild.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChatListScreen(navController: NavController) {

    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    val firestore = FirebaseFirestore.getInstance()

    var chats by remember { mutableStateOf<List<Chat>>(emptyList()) }

    LaunchedEffect(Unit) {
        firestore.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .addSnapshotListener { snapshot, _ ->
                chats = snapshot?.documents
                    ?.mapNotNull { it.toObject(Chat::class.java)?.copy(chatId = it.id) }
                    ?: emptyList()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .statusBarsPadding()
    ) {

        // Header
        Text(
            text = "Messages",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(chats) { chat ->
                ChatRow(
                    chat = chat,
                    currentUserId = currentUserId
                ) {
                    navController.navigate("chat/${chat.chatId}")
                }
            }
        }
    }
}

/* -------------------- CHAT ROW -------------------- */

@Composable
fun ChatRow(
    chat: Chat,
    currentUserId: String,
    onClick: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val otherUserId = chat.participants.first { it != currentUserId }

    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(otherUserId) {
        firestore.collection("users")
            .document(otherUserId)
            .get()
            .addOnSuccessListener {
                user = it.toObject(User::class.java)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            // Avatar (Clean & soft)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.name?.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4338CA)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = user?.name ?: "User",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = chat.lastMessage.ifBlank { "Say hi 👋" },
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Soft divider (no black box feeling)
        Divider(
            modifier = Modifier.padding(top = 12.dp),
            color = Color(0xFFE5E7EB),
            thickness = 0.6.dp
        )
    }
}

