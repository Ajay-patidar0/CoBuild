////package com.example.cobuild.ui.profile
////
////import androidx.compose.foundation.layout.*
////import androidx.compose.foundation.shape.CircleShape
////import androidx.compose.material.icons.Icons
////import androidx.compose.material.icons.filled.ArrowBack
////import androidx.compose.material.icons.filled.Person
////import androidx.compose.material3.*
////import androidx.compose.runtime.*
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.draw.clip
////import androidx.compose.ui.graphics.Color
////import androidx.compose.ui.unit.dp
////import androidx.lifecycle.viewmodel.compose.viewModel
////
////@OptIn(ExperimentalMaterial3Api::class)
////@Composable
////fun ProfileViewScreen(
////    userId: String,
////    onBackClick: () -> Unit
////) {
////    val viewModel: ProfileViewModel = viewModel()
////    val user by viewModel.user.collectAsState()
////    val loading by viewModel.loading.collectAsState()
////
////    LaunchedEffect(Unit) {
////        viewModel.loadProfile(userId)
////    }
////
////    Scaffold(
////        topBar = {
////            TopAppBar(
////                title = { Text("Profile") },
////                navigationIcon = {
////                    IconButton(onClick = onBackClick) {
////                        Icon(Icons.Default.ArrowBack, null)
////                    }
////                }
////            )
////        }
////    ) { padding ->
////
////        if (loading) {
////            Box(
////                modifier = Modifier
////                    .fillMaxSize()
////                    .padding(padding),
////                contentAlignment = Alignment.Center
////            ) {
////                CircularProgressIndicator()
////            }
////        } else {
////            user?.let { userData ->
////
////                Column(
////                    modifier = Modifier
////                        .padding(padding)
////                        .padding(24.dp),
////                    horizontalAlignment = Alignment.CenterHorizontally
////                ) {
////
////                    Surface(
////                        modifier = Modifier
////                            .size(100.dp)
////                            .clip(CircleShape),
////                        color = Color(0xFFEDE9FE)
////                    ) {
////                        Box(contentAlignment = Alignment.Center) {
////                            Icon(
////                                Icons.Default.Person,
////                                contentDescription = null,
////                                tint = Color(0xFF4F46E5),
////                                modifier = Modifier.size(50.dp)
////                            )
////                        }
////                    }
////
////                    Spacer(Modifier.height(16.dp))
////
////                    Text(
////                        text = userData.name,
////                        style = MaterialTheme.typography.headlineSmall
////                    )
////
////                    Spacer(Modifier.height(6.dp))
////
////                    Text(
////                        text = userData.role,
////                        color = Color.Gray
////                    )
////
////                    Spacer(Modifier.height(20.dp))
////
////                    Text(
////                        text = userData.bio,
////                        style = MaterialTheme.typography.bodyMedium
////                    )
////
////                    Spacer(Modifier.height(20.dp))
////
////                    Text(
////                        text = "Skills",
////                        style = MaterialTheme.typography.titleMedium
////                    )
////
////                    Spacer(Modifier.height(8.dp))
////
////                    userData.skills.forEach { skill ->
////                        AssistChip(
////                            onClick = {},
////                            label = { Text(skill) },
////                            modifier = Modifier.padding(4.dp)
////                        )
////                    }
////                }
////            }
////        }
////    }
////}
//
//
//package com.example.cobuild.ui.profile
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.layout.FlowRow
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Chat
//import androidx.compose.material.icons.filled.Link
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.example.cobuild.data.model.User
//
///* ── tokens ── */
//private val Bg0          = Color(0xFF0A0F1E)
//private val Bg1          = Color(0xFF0F172A)
//private val Bg2          = Color(0xFF1E293B)
//private val Bg3          = Color(0xFF273548)
//private val AccentViolet = Color(0xFF818CF8)
//private val AccentCyan   = Color(0xFF22D3EE)
//private val AccentGreen  = Color(0xFF34D399)
//private val AccentAmber  = Color(0xFFFBBF24)
//private val TextHigh     = Color(0xFFF1F5F9)
//private val TextMid      = Color(0xFF94A3B8)
//private val TextLow      = Color(0xFF475569)
//private val Border       = Color(0xFF1E293B)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileViewScreen(
//    userId: String,
//    onBackClick: () -> Unit,
//    onMessageClick: ((chatId: String) -> Unit)? = null
//) {
//    /* ── use the existing ProfileViewModel ── */
//    val viewModel: ProfileViewModel = viewModel()
//    val user    by viewModel.user.collectAsState()
//    val loading by viewModel.loading.collectAsState()
//
//    val currentUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
//    val firestore  = FirebaseFirestore.getInstance()
//
//    /* shared projects between viewer and this user */
//    var sharedProjects by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
//
//    LaunchedEffect(userId) {
//        viewModel.loadProfile(userId)
//
//        firestore.collection("projects")
//            .whereArrayContains("members", userId)
//            .get()
//            .addOnSuccessListener { snap ->
//                sharedProjects = snap.documents
//                    .filter { doc ->
//                        val members = doc.get("members") as? List<*> ?: emptyList<Any>()
//                        members.contains(currentUid)
//                    }
//                    .mapNotNull { doc -> doc.data?.plus("id" to doc.id) }
//            }
//    }
//
//    /* ── create / get 1-on-1 chat then navigate ── */
//    fun openChat() {
//        val chatId = listOf(currentUid, userId).sorted().joinToString("_")
//        firestore.collection("chats").document(chatId)
//            .get()
//            .addOnSuccessListener { doc ->
//                if (!doc.exists()) {
//                    firestore.collection("chats").document(chatId).set(
//                        mapOf(
//                            "participants"  to listOf(currentUid, userId),
//                            "lastMessage"   to "",
//                            "lastTimestamp" to System.currentTimeMillis()
//                        )
//                    )
//                }
//                onMessageClick?.invoke(chatId)
//            }
//    }
//
//    Scaffold(
//        containerColor = Bg0,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text("Profile", color = TextHigh,
//                        fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, null, tint = TextHigh)
//                    }
//                },
//                actions = {
//                    /* show message button only when viewing someone else's profile */
//                    if (userId != currentUid && onMessageClick != null) {
//                        IconButton(onClick = ::openChat) {
//                            Icon(Icons.Default.Chat, "Message", tint = AccentCyan)
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg1)
//            )
//        }
//    ) { padding ->
//
//        /* ── loading ── */
//        if (loading) {
//            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator(color = AccentViolet)
//            }
//            return@Scaffold
//        }
//
//        val u = user ?: run {
//            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
//                Text("User not found", color = TextMid, fontSize = 16.sp)
//            }
//            return@Scaffold
//        }
//
//        LazyColumn(
//            modifier = Modifier.padding(padding),
//            contentPadding = PaddingValues(bottom = 48.dp)
//        ) {
//
//            /* ── HERO ── */
//            item {
//                Box(
//                    Modifier.fillMaxWidth()
//                        .background(Brush.verticalGradient(listOf(Bg1, Bg0)))
//                        .padding(horizontal = 20.dp, vertical = 32.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        /* avatar */
//                        Box(
//                            Modifier.size(90.dp)
//                                .clip(CircleShape)
//                                .background(
//                                    Brush.linearGradient(listOf(AccentViolet, AccentCyan))
//                                ),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                u.name.firstOrNull()?.uppercase() ?: "?",
//                                fontSize = 38.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = Color.White
//                            )
//                        }
//
//                        /* name */
//                        Text(u.name, fontSize = 22.sp, fontWeight = FontWeight.Bold,
//                            color = TextHigh)
//
//                        /* role */
//                        if (u.role.isNotBlank()) {
//                            Surface(
//                                shape = RoundedCornerShape(20.dp),
//                                color = AccentViolet.copy(.15f)
//                            ) {
//                                Text(u.role, fontSize = 13.sp, color = AccentViolet,
//                                    fontWeight = FontWeight.SemiBold,
//                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp))
//                            }
//                        }
//
//                        /* college */
//                        if (u.college.isNotBlank()) {
//                            Text(u.college, fontSize = 13.sp, color = TextMid)
//                        }
//
//                        /* message button */
//                        if (userId != currentUid && onMessageClick != null) {
//                            Spacer(Modifier.height(4.dp))
//                            Button(
//                                onClick = ::openChat,
//                                shape   = RoundedCornerShape(12.dp),
//                                colors  = ButtonDefaults.buttonColors(containerColor = AccentViolet),
//                                modifier = Modifier.height(42.dp)
//                            ) {
//                                Icon(Icons.Default.Chat, null, modifier = Modifier.size(16.dp))
//                                Spacer(Modifier.width(8.dp))
//                                Text("Message", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
//                            }
//                        }
//                    }
//                }
//            }
//
//            /* ── BIO ── */
//            if (u.bio.isNotBlank()) {
//                item {
//                    ProfileSection("About") {
//                        Text(u.bio, fontSize = 14.sp, color = TextMid,
//                            lineHeight = 22.sp, textAlign = TextAlign.Start)
//                    }
//                }
//            }
//
//            /* ── SKILLS ── */
//            if (u.skills.isNotEmpty()) {
//                item {
//                    ProfileSection("Skills") {
//                        FlowRow(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            verticalArrangement   = Arrangement.spacedBy(8.dp)
//                        ) {
//                            u.skills.forEach { skill: String ->
//                                Surface(
//                                    shape = RoundedCornerShape(20.dp),
//                                    color = AccentViolet.copy(.15f),
//                                    border = androidx.compose.foundation.BorderStroke(
//                                        1.dp, AccentViolet.copy(.3f)
//                                    )
//                                ) {
//                                    Text(skill, fontSize = 13.sp, color = AccentViolet,
//                                        fontWeight = FontWeight.Medium,
//                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            /* ── LINKS ── */
//            val links = buildList<Triple<String, String, ImageVector>> {
//                if (u.github.isNotBlank())    add(Triple("GitHub", u.github, Icons.Default.Link))
//                if (u.linkedin.isNotBlank())  add(Triple("LinkedIn", u.linkedin, Icons.Default.Link))
//                if (u.portfolio.isNotBlank()) add(Triple("Portfolio", u.portfolio, Icons.Default.Link))
//            }
//
//            if (links.isNotEmpty()) {
//                item {
//                    ProfileSection("Links") {
//                        links.forEach { (label, url, icon) ->
//                            Row(
//                                Modifier.fillMaxWidth().padding(vertical = 7.dp),
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(12.dp)
//                            ) {
//                                Box(
//                                    Modifier.size(32.dp)
//                                        .background(AccentCyan.copy(.12f), RoundedCornerShape(8.dp)),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Icon(icon, null, tint = AccentCyan, modifier = Modifier.size(16.dp))
//                                }
//                                Column(Modifier.weight(1f)) {
//                                    Text(label, fontSize = 13.sp, color = TextMid,
//                                        fontWeight = FontWeight.SemiBold)
//                                    Text(url, fontSize = 12.sp, color = TextHigh,
//                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            /* ── SHARED PROJECTS ── */
//            if (sharedProjects.isNotEmpty()) {
//                item {
//                    ProfileSection("Projects in common  ·  ${sharedProjects.size}") { /* header only */ }
//                }
//                items(sharedProjects) { proj ->
//                    Row(
//                        Modifier.fillMaxWidth()
//                            .padding(horizontal = 16.dp)
//                            .padding(bottom = 8.dp)
//                            .background(Bg3, RoundedCornerShape(12.dp))
//                            .border(1.dp, Border, RoundedCornerShape(12.dp))
//                            .padding(12.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(10.dp)
//                    ) {
//                        Box(Modifier.size(8.dp).background(AccentGreen, CircleShape))
//                        Text(proj["title"] as? String ?: "Project",
//                            fontSize = 14.sp, color = TextHigh,
//                            modifier = Modifier.weight(1f),
//                            maxLines = 1, overflow = TextOverflow.Ellipsis)
//                    }
//                }
//            }
//        }
//    }
//}
//
///* ── section card ── */
//@Composable
//private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
//    Column(
//        Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 6.dp)
//            .background(Bg2, RoundedCornerShape(16.dp))
//            .border(1.dp, Border, RoundedCornerShape(16.dp))
//            .padding(16.dp)
//    ) {
//        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextHigh)
//        Spacer(Modifier.height(12.dp))
//        content()
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
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/* ── tokens ── */
private val TopBar      = Color(0xFF0F172A)
private val PageBg      = Color(0xFFF8FAFC)
private val CardBg      = Color.White
private val CardBg2     = Color(0xFFF1F5F9)
private val Primary     = Color(0xFF4F46E5)
private val PrimaryBg   = Color(0xFFEDE9FE)
private val TextPrimary = Color(0xFF1E293B)
private val TextSub     = Color(0xFF64748B)
private val TextHint    = Color(0xFF94A3B8)
private val Border      = Color(0xFFE2E8F0)

data class UserProjectPreview(
    val id: String       = "",
    val title: String    = "",
    val status: String   = "",
    val isOwner: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileViewScreen(
    userId: String,
    onBackClick: () -> Unit,
    onMessageClick: ((chatId: String) -> Unit)? = null
) {
    val viewModel: ProfileViewModel = viewModel()
    val user    by viewModel.user.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val currentUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val firestore  = FirebaseFirestore.getInstance()

    var userProjects by remember { mutableStateOf<List<UserProjectPreview>>(emptyList()) }

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)

        /* load all projects this user is a member of */
        firestore.collection("projects")
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { snap ->
                userProjects = snap.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    UserProjectPreview(
                        id      = doc.id,
                        title   = data["title"]   as? String ?: "Untitled",
                        status  = data["status"]  as? String ?: "",
                        isOwner = data["ownerId"] as? String == userId
                    )
                }
            }
    }

    fun openChat() {
        val chatId = listOf(currentUid, userId).sorted().joinToString("_")
        firestore.collection("chats").document(chatId).get()
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
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White,
                    fontWeight = FontWeight.SemiBold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    if (userId != currentUid && onMessageClick != null) {
                        IconButton(onClick = ::openChat) {
                            Icon(Icons.Default.Chat, "Message", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TopBar)
            )
        }
    ) { padding ->

        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        /* user == null means Firestore returned no doc for this userId */
        val u = user ?: run {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("😕", fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Profile not found", fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text("This user may have deleted their account",
                        fontSize = 13.sp, color = TextSub, textAlign = TextAlign.Center)
                }
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
                        .background(Brush.verticalGradient(listOf(TopBar, Color(0xFF1E1B4B))))
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        /* avatar with gradient */
                        Box(
                            Modifier.size(88.dp).clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(Primary, Color(0xFF818CF8)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                u.name.firstOrNull()?.uppercase() ?: "?",
                                fontSize   = 38.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                        }

                        /* name */
                        Text(u.name, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                            color = Color.White)

                        /* role badge */
                        if (u.role.isNotBlank()) {
                            Surface(shape = RoundedCornerShape(20.dp), color = PrimaryBg) {
                                Text(u.role, fontSize = 13.sp, color = Primary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp))
                            }
                        }

                        /* location + college row */
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (u.location.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(Icons.Default.LocationOn, null,
                                        tint = Color.White.copy(.7f), modifier = Modifier.size(14.dp))
                                    Text(u.location, fontSize = 13.sp, color = Color.White.copy(.7f))
                                }
                            }
                            if (u.companyOrCollege.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(Icons.Default.School, null,
                                        tint = Color.White.copy(.7f), modifier = Modifier.size(14.dp))
                                    Text(u.companyOrCollege, fontSize = 12.sp,
                                        color = Color.White.copy(.7f),
                                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 200.dp))
                                }
                            }
                        }

                        /* message button */
                        if (userId != currentUid && onMessageClick != null) {
                            Spacer(Modifier.height(4.dp))
                            Button(
                                onClick = ::openChat,
                                shape   = RoundedCornerShape(12.dp),
                                colors  = ButtonDefaults.buttonColors(containerColor = Primary),
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

            /* ── GOALS / BIO ── */
            if (u.goals.isNotBlank()) {
                item {
                    ProfileCard("About") {
                        Text(u.goals, fontSize = 14.sp, color = TextSub, lineHeight = 22.sp)
                    }
                }
            }

            /* ── SKILLS ── */
            if (u.skills.isNotEmpty()) {
                item {
                    ProfileCard("Skills") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(8.dp)
                        ) {
                            u.skills.forEach { skill: String ->
                                Surface(
                                    shape  = RoundedCornerShape(20.dp),
                                    color  = PrimaryBg,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, Primary.copy(.3f)
                                    )
                                ) {
                                    Text(skill, fontSize = 13.sp, color = Primary,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
                                }
                            }
                        }
                    }
                }
            }

            /* ── INTERESTS ── */
            if (u.interests.isNotEmpty()) {
                item {
                    ProfileCard("Interests") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(8.dp)
                        ) {
                            u.interests.forEach { interest: String ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFF0FDF4),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, Color(0xFF16A34A).copy(.3f)
                                    )
                                ) {
                                    Text(interest, fontSize = 13.sp, color = Color(0xFF16A34A),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
                                }
                            }
                        }
                    }
                }
            }

            /* ── LINKS ── */
            val links = buildList {
                if (u.github.isNotBlank())   add(Triple("GitHub",   u.github,   Icons.Default.OpenInNew))
                if (u.linkedin.isNotBlank()) add(Triple("LinkedIn", u.linkedin, Icons.Default.Link))
                u.portfolio.filter { it.isNotBlank() }.forEachIndexed { i, url ->
                    add(Triple(if (i == 0) "Portfolio" else "Portfolio ${i + 1}", url, Icons.Default.Link))
                }
            }
            if (links.isNotEmpty()) {
                item {
                    ProfileCard("Links") {
                        links.forEach { (label, url, icon) ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    Modifier.size(32.dp)
                                        .background(Color(0xFFE0F2FE), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, null, tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(16.dp))
                                }
                                Column(Modifier.weight(1f)) {
                                    Text(label, fontSize = 12.sp, color = TextSub,
                                        fontWeight = FontWeight.SemiBold)
                                    Text(url, fontSize = 13.sp, color = Primary,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }

            /* ── PROJECTS ── */
            if (userProjects.isNotEmpty()) {
                item {
                    ProfileCard("Projects  ·  ${userProjects.size}") {
                        val owned  = userProjects.filter { it.isOwner }
                        val joined = userProjects.filter { !it.isOwner }

                        if (owned.isNotEmpty()) {
                            Text("Owns", fontSize = 12.sp, color = TextSub,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 6.dp))
                            owned.forEach { proj ->
                                ProjectRow(proj)
                                Spacer(Modifier.height(6.dp))
                            }
                        }

                        if (joined.isNotEmpty()) {
                            if (owned.isNotEmpty()) Spacer(Modifier.height(8.dp))
                            Text("Joined", fontSize = 12.sp, color = TextSub,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 6.dp))
                            joined.forEach { proj ->
                                ProjectRow(proj)
                                Spacer(Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

/* ── PROJECT ROW ── */
@Composable
private fun ProjectRow(proj: UserProjectPreview) {
    val (statusBg, statusFg) = when (proj.status.uppercase()) {
        "IN_PROGRESS"  -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        "COMPLETED"    -> Color(0xFFDCFCE7) to Color(0xFF16A34A)
        else           -> Color(0xFFF1F5F9) to Color(0xFF64748B)
    }

    Row(
        Modifier.fillMaxWidth()
            .background(CardBg2, RoundedCornerShape(12.dp))
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        /* colored dot */
        Box(Modifier.size(8.dp).background(statusFg, CircleShape))

        Text(proj.title, fontSize = 14.sp, color = TextPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1, overflow = TextOverflow.Ellipsis)

        /* owner badge */
        if (proj.isOwner) {
            Surface(shape = RoundedCornerShape(6.dp), color = PrimaryBg) {
                Text("Owner", fontSize = 10.sp, color = Primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }

        /* status badge */
        if (proj.status.isNotBlank()) {
            Surface(shape = RoundedCornerShape(6.dp), color = statusBg) {
                Text(proj.status.replace("_", " "), fontSize = 10.sp, color = statusFg,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }
    }
}

/* ── PROFILE CARD ── */
@Composable
private fun ProfileCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(CardBg, RoundedCornerShape(16.dp))
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(12.dp))
        content()
    }
}