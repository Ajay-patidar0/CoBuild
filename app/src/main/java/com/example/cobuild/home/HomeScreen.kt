package com.example.cobuild.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cobuild.R

// --- Theme Colors ---
private val PrimaryColor = Color(0xFF4F46E5) // Indigo 600
private val BackgroundColor = Color(0xFFF8FAFC) // Slate 50
private val SurfaceColor = Color.White
private val TextPrimary = Color(0xFF1E293B) // Slate 800
private val TextSecondary = Color(0xFF64748B) // Slate 500

@Composable
fun HomeScreen(
    onProjectsClick: () -> Unit = {},
    onAddProjectClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            // Added a proper small header
            HomeTopBar()
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    when (tab) {
                        0 -> {} // Home
                        1 -> onProjectsClick()
                        2 -> onAddProjectClick()
                        3 -> onMessagesClick()
                        4 -> onProfileClick()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {

            // Search Bar Visual
            SearchBarVisual()

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Text
            Text(
                text = "Let's Build Something\nAmazing Together.",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find partners for your next big idea.",
                fontSize = 15.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // --- Featured Projects ---
            SectionHeader(title = "Featured Projects", actionText = "See All")

            Spacer(modifier = Modifier.height(14.dp))

            FeaturedProjectCard(
                title = "AI Note Scanner",
                category = "Machine Learning",
                description = "Smart OCR scanner with auto-tagging.",
                accentColor = Color(0xFF3B82F6) // Blue
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeaturedProjectCard(
                title = "Habit Tracker UI",
                category = "Design System",
                description = "Minimal UI/UX kit for productivity apps.",
                accentColor = Color(0xFF8B5CF6) // Purple
            )

            Spacer(modifier = Modifier.height(28.dp))

            // --- Categories/Collaborators ---
            SectionHeader(title = "Browse Categories")

            Spacer(modifier = Modifier.height(14.dp))

            // Grid Layout for Categories
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryCard(
                        name = "Design",
                        count = "120+ Projects",
                        color = Color(0xFFEC4899), // Pink
                        modifier = Modifier.weight(1f)
                    )
                    CategoryCard(
                        name = "Code",
                        count = "340+ Projects",
                        color = Color(0xFF10B981), // Emerald
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryCard(
                        name = "AI & ML",
                        count = "85+ Projects",
                        color = Color(0xFFF59E0B), // Amber
                        modifier = Modifier.weight(1f)
                    )
                    CategoryCard(
                        name = "Mobile",
                        count = "200+ Projects",
                        color = Color(0xFF6366F1), // Indigo
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/*-----------------------------------------
   COMPONENTS
-----------------------------------------*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Welcome back,",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = "CoBuilder 👋",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        actions = {
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundColor
        )
    )
}

@Composable
fun SearchBarVisual() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceColor)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Search for projects or people...",
            color = Color(0xFF94A3B8),
            fontSize = 14.sp
        )
    }
}

@Composable
fun SectionHeader(title: String, actionText: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        if (actionText != null) {
            Text(
                text = actionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor,
                modifier = Modifier.clickable { }
            )
        }
    }
}

@Composable
fun FeaturedProjectCard(title: String, category: String, description: String, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(accentColor, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Category Tag
                Surface(
                    color = accentColor.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = category.uppercase(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CategoryCard(name: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Text(
                text = count,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

/*-----------------------------------------
   BOTTOM NAVIGATION
-----------------------------------------*/
@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceColor,
        tonalElevation = 0.dp,
        modifier = Modifier.border(
            width = 1.dp,
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        // Home
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            colors = navColors()
        )

        // Projects
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_projects),
                    contentDescription = "Projects",
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = navColors()
        )

        // Add Button
        NavigationBarItem(
            selected = false,
            onClick = { onTabSelected(2) },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = PrimaryColor,
                    shadowElevation = 6.dp,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Create",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )

        // Chats
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chat),
                    contentDescription = "Chats",
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = navColors()
        )

        // Profile
        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            colors = navColors()
        )
    }
}

@Composable
fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = PrimaryColor,
    unselectedIconColor = Color(0xFF94A3B8),
    indicatorColor = Color.Transparent // Removes the pill background for a cleaner look
)