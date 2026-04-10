//package com.example.cobuild.ui.profile
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileViewScreen(
//    userId: String,
//    onBackClick: () -> Unit
//) {
//    val viewModel: ProfileViewModel = viewModel()
//    val user by viewModel.user.collectAsState()
//    val loading by viewModel.loading.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.loadProfile(userId)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Profile") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, null)
//                    }
//                }
//            )
//        }
//    ) { padding ->
//
//        if (loading) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        } else {
//            user?.let { userData ->
//
//                Column(
//                    modifier = Modifier
//                        .padding(padding)
//                        .padding(24.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//
//                    Surface(
//                        modifier = Modifier
//                            .size(100.dp)
//                            .clip(CircleShape),
//                        color = Color(0xFFEDE9FE)
//                    ) {
//                        Box(contentAlignment = Alignment.Center) {
//                            Icon(
//                                Icons.Default.Person,
//                                contentDescription = null,
//                                tint = Color(0xFF4F46E5),
//                                modifier = Modifier.size(50.dp)
//                            )
//                        }
//                    }
//
//                    Spacer(Modifier.height(16.dp))
//
//                    Text(
//                        text = userData.name,
//                        style = MaterialTheme.typography.headlineSmall
//                    )
//
//                    Spacer(Modifier.height(6.dp))
//
//                    Text(
//                        text = userData.role,
//                        color = Color.Gray
//                    )
//
//                    Spacer(Modifier.height(20.dp))
//
//                    Text(
//                        text = userData.bio,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//
//                    Spacer(Modifier.height(20.dp))
//
//                    Text(
//                        text = "Skills",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//
//                    Spacer(Modifier.height(8.dp))
//
//                    userData.skills.forEach { skill ->
//                        AssistChip(
//                            onClick = {},
//                            label = { Text(skill) },
//                            modifier = Modifier.padding(4.dp)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}


package com.example.cobuild.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cobuild.data.model.User

/* ── tokens ── */
private val Bg0          = Color(0xFF0A0F1E)
private val Bg1          = Color(0xFF0F172A)
private val Bg2          = Color(0xFF1E293B)
private val Bg3          = Color(0xFF273548)
private val AccentViolet = Color(0xFF818CF8)
private val AccentCyan   = Color(0xFF22D3EE)
private val AccentGreen  = Color(0xFF34D399)
private val AccentAmber  = Color(0xFFFBBF24)
private val TextHigh     = Color(0xFFF1F5F9)
private val TextMid      = Color(0xFF94A3B8)
private val TextLow      = Color(0xFF475569)
private val Border       = Color(0xFF1E293B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileViewScreen(
    userId: String,
    onBackClick: () -> Unit,
    onMessageClick: ((chatId: String) -> Unit)? = null
) {
    /* ── use the existing ProfileViewModel ── */
    val viewModel: ProfileViewModel = viewModel()
    val user    by viewModel.user.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val currentUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val firestore  = FirebaseFirestore.getInstance()

    /* shared projects between viewer and this user */
    var sharedProjects by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)

        firestore.collection("projects")
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { snap ->
                sharedProjects = snap.documents
                    .filter { doc ->
                        val members = doc.get("members") as? List<*> ?: emptyList<Any>()
                        members.contains(currentUid)
                    }
                    .mapNotNull { doc -> doc.data?.plus("id" to doc.id) }
            }
    }

    /* ── create / get 1-on-1 chat then navigate ── */
    fun openChat() {
        val chatId = listOf(currentUid, userId).sorted().joinToString("_")
        firestore.collection("chats").document(chatId)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    firestore.collection("chats").document(chatId).set(
                        mapOf(
                            "participants"  to listOf(currentUid, userId),
                            "lastMessage"   to "",
                            "lastTimestamp" to System.currentTimeMillis()
                        )
                    )
                }
                onMessageClick?.invoke(chatId)
            }
    }

    Scaffold(
        containerColor = Bg0,
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile", color = TextHigh,
                        fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextHigh)
                    }
                },
                actions = {
                    /* show message button only when viewing someone else's profile */
                    if (userId != currentUid && onMessageClick != null) {
                        IconButton(onClick = ::openChat) {
                            Icon(Icons.Default.Chat, "Message", tint = AccentCyan)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg1)
            )
        }
    ) { padding ->

        /* ── loading ── */
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentViolet)
            }
            return@Scaffold
        }

        val u = user ?: run {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("User not found", color = TextMid, fontSize = 16.sp)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 48.dp)
        ) {

            /* ── HERO ── */
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Bg1, Bg0)))
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        /* avatar */
                        Box(
                            Modifier.size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(AccentViolet, AccentCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                u.name.firstOrNull()?.uppercase() ?: "?",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        /* name */
                        Text(u.name, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = TextHigh)

                        /* role */
                        if (u.role.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = AccentViolet.copy(.15f)
                            ) {
                                Text(u.role, fontSize = 13.sp, color = AccentViolet,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp))
                            }
                        }

                        /* college */
                        if (u.college.isNotBlank()) {
                            Text(u.college, fontSize = 13.sp, color = TextMid)
                        }

                        /* message button */
                        if (userId != currentUid && onMessageClick != null) {
                            Spacer(Modifier.height(4.dp))
                            Button(
                                onClick = ::openChat,
                                shape   = RoundedCornerShape(12.dp),
                                colors  = ButtonDefaults.buttonColors(containerColor = AccentViolet),
                                modifier = Modifier.height(42.dp)
                            ) {
                                Icon(Icons.Default.Chat, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Message", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            /* ── BIO ── */
            if (u.bio.isNotBlank()) {
                item {
                    ProfileSection("About") {
                        Text(u.bio, fontSize = 14.sp, color = TextMid,
                            lineHeight = 22.sp, textAlign = TextAlign.Start)
                    }
                }
            }

            /* ── SKILLS ── */
            if (u.skills.isNotEmpty()) {
                item {
                    ProfileSection("Skills") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(8.dp)
                        ) {
                            u.skills.forEach { skill: String ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = AccentViolet.copy(.15f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, AccentViolet.copy(.3f)
                                    )
                                ) {
                                    Text(skill, fontSize = 13.sp, color = AccentViolet,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
                                }
                            }
                        }
                    }
                }
            }

            /* ── LINKS ── */
            val links = buildList<Triple<String, String, ImageVector>> {
                if (u.github.isNotBlank())    add(Triple("GitHub", u.github, Icons.Default.Link))
                if (u.linkedin.isNotBlank())  add(Triple("LinkedIn", u.linkedin, Icons.Default.Link))
                if (u.portfolio.isNotBlank()) add(Triple("Portfolio", u.portfolio, Icons.Default.Link))
            }

            if (links.isNotEmpty()) {
                item {
                    ProfileSection("Links") {
                        links.forEach { (label, url, icon) ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    Modifier.size(32.dp)
                                        .background(AccentCyan.copy(.12f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(label, fontSize = 13.sp, color = TextMid,
                                        fontWeight = FontWeight.SemiBold)
                                    Text(url, fontSize = 12.sp, color = TextHigh,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }

            /* ── SHARED PROJECTS ── */
            if (sharedProjects.isNotEmpty()) {
                item {
                    ProfileSection("Projects in common  ·  ${sharedProjects.size}") { /* header only */ }
                }
                items(sharedProjects) { proj ->
                    Row(
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                            .background(Bg3, RoundedCornerShape(12.dp))
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(Modifier.size(8.dp).background(AccentGreen, CircleShape))
                        Text(proj["title"] as? String ?: "Project",
                            fontSize = 14.sp, color = TextHigh,
                            modifier = Modifier.weight(1f),
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

/* ── section card ── */
@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(Bg2, RoundedCornerShape(16.dp))
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextHigh)
        Spacer(Modifier.height(12.dp))
        content()
    }
}