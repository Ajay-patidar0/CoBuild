package com.example.cobuild.ui.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cobuild.data.model.ProjectStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val Primary      = Color(0xFF4F46E5)
private val PrimaryLight = Color(0xFFEDE9FE)
private val BgColor      = Color(0xFFF8FAFC)
private val Surface1     = Color.White
private val TextPrimary  = Color(0xFF1E293B)
private val TextMuted    = Color(0xFF64748B)
private val Border       = Color(0xFFE2E8F0)
private val DarkBar      = Color(0xFF0F172A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeProjectDetailScreen(
    projectId: String,
    onBackClick: () -> Unit
) {
    val projectVm: ProjectViewModel         = viewModel()
    val requestVm: ProjectRequestViewModel  = viewModel()
    val firestore  = FirebaseFirestore.getInstance()
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    var requesterName by remember { mutableStateOf("") }

    val projects    by projectVm.projects.collectAsState()
    val isRequested by requestVm.isRequested.collectAsState()

    val proj = projects.firstOrNull { it.id == projectId }

    LaunchedEffect(projectId) {
        projectVm.loadProjects()
        requestVm.checkIfRequested(projectId)
        firestore.collection("users").document(currentUid).get()
            .addOnSuccessListener { doc -> requesterName = doc.getString("name") ?: "User" }
    }

    Scaffold(
        containerColor = BgColor,

        /* ── DARK TOP BAR ── */
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Project Details",
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White        // white arrow on dark bar
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBar)
            )
        },

        /* ── FIXED BOTTOM JOIN BUTTON ── */
        bottomBar = {
            if (proj != null) {
                val isOwner  = proj.ownerId == currentUid
                val isMember = proj.members.contains(currentUid)

                Surface(shadowElevation = 12.dp, color = Surface1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        when {
                            isOwner  -> BottomLabel(
                                text = "👑 You own this project",
                                bg = Color(0xFFF0FDF4), fg = Color(0xFF166534)
                            )
                            isMember -> BottomLabel(
                                text = "✅ You are a member",
                                bg = PrimaryLight, fg = Primary
                            )
                            isRequested -> BottomLabel(
                                text = "🕐 Join request sent",
                                bg = Color(0xFFFEF9C3), fg = Color(0xFF854D0E)
                            )
                            else -> Button(
                                onClick = { requestVm.requestToJoin(proj, requesterName) },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape    = RoundedCornerShape(14.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Text(
                                    "Request to Join",
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

    ) { padding ->

        if (proj == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            /* ── HERO BANNER (gradient continuing from top bar) ── */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(DarkBar, Color(0xFF1E1B4B)))
                    )
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        proj.title,
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Text(
                        "Posted by ${proj.ownerName}",
                        fontSize = 14.sp,
                        color    = Color.White.copy(alpha = 0.65f)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusBadge(proj.status)
                        if (proj.commitmentLevel.isNotBlank()) CommitmentBadge(proj.commitmentLevel)
                    }
                }
            }

            /* ── BODY ── */
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                if (proj.description.isNotBlank()) InfoSection("Description", proj.description)
                if (proj.goal.isNotBlank())        InfoSection("Goal", proj.goal)

                /* SKILLS */
                if (proj.skills.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionLabel("Skills Needed")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(8.dp)
                        ) {
                            proj.skills.forEach { skill: String -> SkillChip(skill) }
                        }
                    }
                }

                /* STATS GRID — 2 columns */
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionLabel("Project Info")

                    val stats = buildList {
                        add("Members"    to proj.members.size.toString())
                        if (proj.experienceLevel.isNotBlank()) add("Experience" to proj.experienceLevel)
                        if (proj.timeline.isNotBlank())        add("Timeline"   to proj.timeline)
                        if (proj.projectType.isNotBlank())     add("Type"       to proj.projectType)
                        if (proj.teamSize.isNotBlank())        add("Team Size"  to proj.teamSize)
                    }

                    stats.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { (label, value) ->
                                StatCard(label = label, value = value, modifier = Modifier.weight(1f))
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

/* ─── PRIVATE HELPERS ─────────────────────────────────────────────────────── */

@Composable
private fun SectionLabel(text: String) =
    Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)

@Composable
private fun InfoSection(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(label)
        Text(value, fontSize = 14.sp, color = TextMuted, lineHeight = 22.sp)
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = Surface1),
        border   = BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = TextMuted)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun SkillChip(skill: String) {
    Surface(
        shape  = RoundedCornerShape(20.dp),
        color  = PrimaryLight,
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.25f))
    ) {
        Text(
            text       = skill,
            fontSize   = 13.sp,
            color      = Primary,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun StatusBadge(status: ProjectStatus) {
    val (bg, fg) = when (status) {
        ProjectStatus.IN_PROGRESS  -> Color(0xFF6EE7B7) to Color(0xFF065F46)
        ProjectStatus.COMPLETED    -> Color(0xFFA5B4FC) to Color(0xFF3730A3)
        ProjectStatus.YET_TO_START -> Color(0xFFE2E8F0) to Color(0xFF475569)
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            text       = status.name.replace("_", " "),
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color      = fg,
            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun CommitmentBadge(level: String) {
    val (bg, fg) = when (level.lowercase()) {
        "high"   -> Color(0xFFFDE68A) to Color(0xFF92400E)
        "medium" -> Color(0xFFBAE6FD) to Color(0xFF0C4A6E)
        else     -> Color(0xFFBBF7D0) to Color(0xFF14532D)
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            text       = level,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color      = fg,
            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun BottomLabel(text: String, bg: Color, fg: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        color    = bg
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = fg)
        }
    }
}