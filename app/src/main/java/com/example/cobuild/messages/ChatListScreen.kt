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
import androidx.navigation.NavController
import com.example.cobuild.model.Chat
import com.example.cobuild.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Messages",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(chats) { chat ->
                ChatRow(
                    chat = chat,
                    currentUserId = currentUserId,
                    onClick = {
                        navController.navigate("chat/${chat.chatId}")
                    }
                )
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

    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.name?.firstOrNull()?.uppercase() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4F46E5)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user?.name ?: "User",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = chat.lastMessage.ifBlank { "Say hi 👋" },
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
