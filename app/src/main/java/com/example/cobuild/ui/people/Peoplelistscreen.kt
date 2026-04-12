package com.example.cobuild.ui.people

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/* ── tokens ── */
private val TopBar      = Color(0xFF0F172A)
private val PageBg      = Color(0xFFF8FAFC)
private val CardBg      = Color.White
private val Primary     = Color(0xFF4F46E5)
private val PrimaryBg   = Color(0xFFEDE9FE)
private val TextPrimary = Color(0xFF1E293B)
private val TextSub     = Color(0xFF64748B)
private val TextHint    = Color(0xFF94A3B8)
private val Border      = Color(0xFFE2E8F0)
private val Green       = Color(0xFF16A34A)
private val GreenBg     = Color(0xFFDCFCE7)

data class PersonItem(
    val uid: String              = "",
    val name: String             = "",
    val role: String             = "",
    val location: String         = "",
    val companyOrCollege: String = "",
    val skills: List<String>     = emptyList(),
    val interests: List<String>  = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleListScreen(
    onBackClick: () -> Unit,
    onPersonClick: (String) -> Unit   // passes userId → ProfileViewScreen
) {
    val firestore  = FirebaseFirestore.getInstance()
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var allPeople    by remember { mutableStateOf<List<PersonItem>>(emptyList()) }
    var searchQuery  by remember { mutableStateOf("") }
    var loading      by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        firestore.collection("users").get().addOnSuccessListener { snap ->
            allPeople = snap.documents
                .filter { it.id != currentUid }
                .mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    PersonItem(
                        uid              = doc.id,
                        name             = name,
                        role             = doc.getString("role")             ?: "",
                        location         = doc.getString("location")         ?: "",
                        companyOrCollege = doc.getString("companyOrCollege") ?: "",
                        skills           = (doc.get("skills")    as? List<*>)
                            ?.filterIsInstance<String>() ?: emptyList(),
                        interests        = (doc.get("interests") as? List<*>)
                            ?.filterIsInstance<String>() ?: emptyList()
                    )
                }
            loading = false
        }
    }

    /* search filter */
    val filtered = remember(allPeople, searchQuery) {
        if (searchQuery.isBlank()) allPeople
        else {
            val q = searchQuery.trim().lowercase()
            allPeople.filter { p ->
                p.name.lowercase().contains(q) ||
                        p.role.lowercase().contains(q) ||
                        p.skills.any { it.lowercase().contains(q) } ||
                        p.companyOrCollege.lowercase().contains(q) ||
                        p.location.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("People", color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${allPeople.size} builders",
                            color = Color.White.copy(.65f), fontSize = 12.sp)
                    }
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

        Column(Modifier.fillMaxSize().padding(padding)) {

            /* ── SEARCH BAR ── */
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBg)
                    .border(1.dp, Border, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, null, tint = TextHint,
                    modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                TextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = {
                        Text("Search by name, skill or role…",
                            fontSize = 14.sp, color = TextHint)
                    },
                    singleLine = true,
                    modifier   = Modifier.weight(1f),
                    colors     = TextFieldDefaults.colors(
                        focusedContainerColor   = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor   = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor        = TextPrimary,
                        unfocusedTextColor      = TextPrimary
                    )
                )
            }

            /* ── LIST ── */
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 40.sp)
                        Spacer(Modifier.height(10.dp))
                        Text("No people found", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text("Try a different search term",
                            fontSize = 13.sp, color = TextSub)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.uid }) { person ->
                        PersonListCard(
                            person  = person,
                            onClick = { onPersonClick(person.uid) }
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

/* ── PERSON CARD ── */
@Composable
private fun PersonListCard(person: PersonItem, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        border    = androidx.compose.foundation.BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            /* avatar */
            Box(
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Primary.copy(.15f), Color(0xFF818CF8).copy(.15f))
                        )
                    )
                    .border(1.5.dp, Primary.copy(.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    person.name.firstOrNull()?.uppercase() ?: "?",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Primary
                )
            }

            /* info */
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {

                Text(person.name, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)

                if (person.role.isNotBlank()) {
                    Text(person.role, fontSize = 13.sp, color = Primary,
                        fontWeight = FontWeight.Medium, maxLines = 1)
                }

                if (person.companyOrCollege.isNotBlank()) {
                    Text(person.companyOrCollege, fontSize = 12.sp, color = TextSub,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                } else if (person.location.isNotBlank()) {
                    Text(person.location, fontSize = 12.sp, color = TextSub, maxLines = 1)
                }

                /* top 3 skill chips */
                if (person.skills.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        person.skills.take(3).forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = PrimaryBg
                            ) {
                                Text(
                                    skill,
                                    fontSize   = 10.sp,
                                    color      = Primary,
                                    fontWeight = FontWeight.Medium,
                                    maxLines   = 1,
                                    modifier   = Modifier.padding(
                                        horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (person.skills.size > 3) {
                            Text("+${person.skills.size - 3}",
                                fontSize = 10.sp, color = TextHint,
                                modifier = Modifier.align(Alignment.CenterVertically))
                        }
                    }
                }
            }

            Icon(Icons.Default.ChevronRight, null,
                tint = TextHint, modifier = Modifier.size(18.dp))
        }
    }
}