//package com.example.cobuild
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.runtime.*
//import androidx.navigation.NavType
//import androidx.navigation.compose.*
//import androidx.navigation.navArgument
//import com.example.cobuild.auth.LoginScreen
//import com.example.cobuild.home.HomeScreen
//import com.example.cobuild.messages.ChatListScreen
//import com.example.cobuild.messages.ChatScreen
//import com.example.cobuild.navigation.Destinations
//import com.example.cobuild.profile.ProfileScreen
//import com.example.cobuild.ui.notification.NotificationScreen
//import com.example.cobuild.ui.profile.ProfileViewScreen
//import com.example.cobuild.ui.project.AddProjectScreen
//import com.example.cobuild.ui.project.EditProjectScreen
//import com.example.cobuild.ui.project.ProjectDetailScreen
//import com.example.cobuild.ui.project.ProjectListScreen
//import com.example.cobuild.ui.theme.CoBuildTheme
//import com.example.cobuild.data.model.Project
//import com.example.cobuild.ui.project.HomeProjectDetailScreen
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        setContent {
//            CoBuildTheme {
//                MainApp()
//            }
//        }
//    }
//}
//
//@Composable
//fun MainApp() {
//
//    val navController = rememberNavController()
//    val auth = FirebaseAuth.getInstance()
//    val firestore = FirebaseFirestore.getInstance()
//    val currentUser = auth.currentUser
//
//    var startDestination by remember { mutableStateOf(Destinations.LOGIN) }
//    var requesterName by remember { mutableStateOf("") }
//
//    /* ---------------- CHECK USER SESSION ---------------- */
//
//    LaunchedEffect(Unit) {
//
//        if (currentUser == null) {
//
//            startDestination = Destinations.LOGIN
//
//        } else {
//
//            firestore.collection("users")
//                .document(currentUser.uid)
//                .get()
//                .addOnSuccessListener { doc ->
//
//                    if (doc.exists()) {
//                        requesterName = doc.getString("name") ?: "User"
//                        startDestination = Destinations.HOME
//                    } else {
//                        startDestination = Destinations.ONBOARDING
//                    }
//                }
//                .addOnFailureListener {
//                    startDestination = Destinations.ONBOARDING
//                }
//        }
//    }
//
//    /* ---------------- NAVIGATION ---------------- */
//
//    NavHost(
//        navController = navController,
//        startDestination = startDestination
//    ) {
//
//        /* ---------------- AUTH ---------------- */
//
//        composable(Destinations.LOGIN) {
//            LoginScreen(navController)
//        }
//
//        composable(Destinations.ONBOARDING) {
//            OnBoardingScreen(navController)
//        }
//
//        /* ---------------- HOME ---------------- */
//
//        composable(Destinations.HOME) {
//
//            HomeScreen(
//                navController = navController,
//
//                onAddProjectClick = {
//                    navController.navigate(Destinations.ADD_PROJECT)
//                },
//
//                onMessagesClick = {
//                    navController.navigate(Destinations.CHAT_LIST)
//                },
//
//                onProfileClick = {
//                    navController.navigate(Destinations.PROFILE)
//                },
//
////                onProjectClick = { project: Project ->
////                    navController.navigate(
////                        Destinations.projectDetailRoute(project.id)
////                    )
////                },
//                onProjectClick = { project: Project ->
//                    navController.navigate(
//                        Destinations.homeProjectDetailRoute(project.id)
//                    )
//                },
//
//                onNotificationClick = {
//                    navController.navigate(Destinations.NOTIFICATIONS)
//                }
//            )
//        }
//
//        /* ---------------- CHAT LIST ---------------- */
//
//        composable(Destinations.CHAT_LIST) {
//            ChatListScreen(navController)
//        }
//
//        /* ---------------- CHAT SCREEN ---------------- */
//
//        composable(
//            route = Destinations.CHAT,
//            arguments = listOf(
//                navArgument("chatId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//
//            val chatId =
//                backStackEntry.arguments?.getString("chatId")
//                    ?: return@composable
//
//            ChatScreen(
//                chatId = chatId,
//                onBackClick = { navController.popBackStack() }
//            )
//        }
//
//        /* ---------------- NOTIFICATIONS ---------------- */
//
//        composable(Destinations.NOTIFICATIONS) {
//
//            NotificationScreen(
//                onBackClick = { navController.popBackStack() },
//
//                onOpenChatClick = { chatId ->
//                    navController.navigate(
//                        Destinations.chatRoute(chatId)
//                    )
//                },
//
//                onOpenProjectClick = { projectId ->
//                    navController.navigate(
//                        Destinations.projectDetailRoute(projectId)
//                    )
//                },
//
//                onOpenProfileClick = { userId ->
//                    navController.navigate(
//                        Destinations.profileViewRoute(userId)
//                    )
//                }
//            )
//        }
//
//
//
//        // ✅ ADD THIS BLOCK — home discovery detail screen
//        composable(
//            route = Destinations.HOME_PROJECT_DETAIL_ROUTE,
//            arguments = listOf(
//                navArgument("projectId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//
//            val projectId = backStackEntry.arguments?.getString("projectId")
//                ?: return@composable
//
//            HomeProjectDetailScreen(
//                projectId   = projectId,
//                onBackClick = { navController.popBackStack() }
//            )
//        }
//        /* ---------------- ADD PROJECT ---------------- */
//
//        composable(Destinations.ADD_PROJECT) {
//
//            AddProjectScreen(
//                onProjectPosted = {
//                    navController.popBackStack()
//                }
//            )
//        }
//
//        /* ---------------- OWN PROFILE ---------------- */
//
//        composable(Destinations.PROFILE) {
//
//            ProfileScreen(
//                onHomeClick = {
//                    navController.navigate(Destinations.HOME) {
//                        popUpTo(Destinations.HOME) { inclusive = true }
//                    }
//                },
//
//                onProjectsClick = {
//                    navController.navigate(Destinations.PROJECT_LIST)
//                },
//
//                onAddProjectClick = {
//                    navController.navigate(Destinations.ADD_PROJECT)
//                },
//
//                onMessagesClick = {
//                    navController.navigate(Destinations.CHAT_LIST)
//                },
//
//                onProfileClick = {}
//            )
//        }
//
//        /* ---------------- PROFILE VIEW ---------------- */
//
//        composable(
//            route = Destinations.PROFILE_VIEW_ROUTE,
//            arguments = listOf(
//                navArgument("userId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//
//            val userId =
//                backStackEntry.arguments?.getString("userId")
//                    ?: return@composable
//
//            ProfileViewScreen(
//                userId = userId,
//                onBackClick = { navController.popBackStack() }
//            )
//        }
//
//        /* ---------------- PROJECT LIST ---------------- */
//
//        composable(Destinations.PROJECT_LIST) {
//
//            ProjectListScreen(
//                onBackClick = { navController.popBackStack() },
//
//                onProjectClick = { projectId ->
//                    navController.navigate(
//                        Destinations.projectDetailRoute(projectId)
//                    )
//                },
//
//                onEditProject = { projectId ->
//                    navController.navigate(
//                        Destinations.editProjectRoute(projectId)
//                    )
//                }
//            )
//        }
//
//        /* ---------------- PROJECT DETAIL ---------------- */
//
//        composable(
//            route = Destinations.PROJECT_DETAIL_ROUTE,
//            arguments = listOf(
//                navArgument("projectId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//
//            val projectId =
//                backStackEntry.arguments?.getString("projectId")
//                    ?: return@composable
//
//            ProjectDetailScreen(
//                projectId = projectId,
//                requesterName = requesterName,
//                onBackClick = { navController.popBackStack() }
//            )
//        }
//
//        /* ---------------- EDIT PROJECT ---------------- */
//
//        composable(
//            route = Destinations.EDIT_PROJECT_ROUTE,
//            arguments = listOf(
//                navArgument("projectId") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//
//            val projectId =
//                backStackEntry.arguments?.getString("projectId")
//                    ?: return@composable
//
//            EditProjectScreen(
//                projectId = projectId,
//                onBack = { navController.popBackStack() }
//            )
//        }
//    }
//}

package com.example.cobuild

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.cobuild.auth.LoginScreen
import com.example.cobuild.home.HomeScreen
import com.example.cobuild.messages.ChatListScreen
import com.example.cobuild.messages.ChatScreen
import com.example.cobuild.messages.GroupChatScreen
import com.example.cobuild.navigation.Destinations
import com.example.cobuild.profile.ProfileScreen
import com.example.cobuild.ui.notification.NotificationScreen
import com.example.cobuild.ui.profile.ProfileViewScreen
import com.example.cobuild.ui.project.AddProjectScreen
import com.example.cobuild.ui.project.EditProjectScreen
import com.example.cobuild.ui.project.HomeProjectDetailScreen
import com.example.cobuild.ui.project.ProjectDetailScreen
import com.example.cobuild.ui.project.ProjectListScreen
import com.example.cobuild.ui.theme.CoBuildTheme
import com.example.cobuild.data.model.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.cobuild.ui.people.PeopleListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoBuildTheme { MainApp() }
        }
    }
}

@Composable
fun MainApp() {

    val navController  = rememberNavController()
    val auth           = FirebaseAuth.getInstance()
    val firestore      = FirebaseFirestore.getInstance()
    val currentUser    = auth.currentUser

    var startDestination by remember { mutableStateOf(Destinations.LOGIN) }
    var requesterName    by remember { mutableStateOf("") }

    /* ── session check ── */
    LaunchedEffect(Unit) {
        if (currentUser == null) {
            startDestination = Destinations.LOGIN
        } else {
            firestore.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        requesterName    = doc.getString("name") ?: "User"
                        startDestination = Destinations.HOME
                    } else {
                        startDestination = Destinations.ONBOARDING
                    }
                }
                .addOnFailureListener {
                    startDestination = Destinations.ONBOARDING
                }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        /* ── AUTH ── */
        composable(Destinations.LOGIN) {
            LoginScreen(navController)
        }

        composable(Destinations.ONBOARDING) {
            OnBoardingScreen(navController)
        }

        /* ── HOME ── */
        composable(Destinations.HOME) {
            HomeScreen(
                navController     = navController,
                onAddProjectClick = { navController.navigate(Destinations.ADD_PROJECT) },
                onMessagesClick   = { navController.navigate(Destinations.CHAT_LIST) },
                onProfileClick    = { navController.navigate(Destinations.PROFILE) },
                // home feed tap → discovery/join view (NOT the management view)
                onProjectClick    = { project: Project ->
                    navController.navigate(Destinations.homeProjectDetailRoute(project.id))
                },
                onNotificationClick = { navController.navigate(Destinations.NOTIFICATIONS) }
            )
        }

        /* ── HOME PROJECT DETAIL (discovery / join) ── */
        composable(
            route     = Destinations.HOME_PROJECT_DETAIL_ROUTE,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { back ->
            val projectId = back.arguments?.getString("projectId") ?: return@composable
            HomeProjectDetailScreen(
                projectId   = projectId,
                onBackClick = { navController.popBackStack() }
            )
        }

        /* ── PROJECT LIST (My Projects) ── */
        composable(Destinations.PROJECT_LIST) {
            ProjectListScreen(
                onBackClick   = { navController.popBackStack() },
                // my-projects tap → full management view
                onProjectClick = { projectId ->
                    navController.navigate(Destinations.projectDetailRoute(projectId))
                },
                onEditProject = { projectId ->
                    navController.navigate(Destinations.editProjectRoute(projectId))
                }
            )
        }

        /* ── PROJECT DETAIL (member / owner management view) ── */
        composable(
            route     = Destinations.PROJECT_DETAIL_ROUTE,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { back ->
            val projectId = back.arguments?.getString("projectId") ?: return@composable
            ProjectDetailScreen(
                projectId     = projectId,
                requesterName = requesterName,
                onBackClick   = { navController.popBackStack() },
                // edit pencil — only visible to owner inside the screen
                onEditClick   = { id ->
                    navController.navigate(Destinations.editProjectRoute(id))
                },
                // group chat button in top bar / hero
                onOpenGroupChat = { id ->
                    // fetch title first from firestore, or pass a placeholder
                    navController.navigate(Destinations.groupChatRoute(id, "Team Chat"))
                },
                // tap a member row → their profile
                onMemberClick = { uid ->
                    navController.navigate(Destinations.profileViewRoute(uid))
                }
            )
        }

        /* ── EDIT PROJECT ── */
        composable(
            route     = Destinations.EDIT_PROJECT_ROUTE,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { back ->
            val projectId = back.arguments?.getString("projectId") ?: return@composable
            EditProjectScreen(
                projectId = projectId,
                onBack    = { navController.popBackStack() }
            )
        }

        /* ── ADD PROJECT ── */
        composable(Destinations.ADD_PROJECT) {
            AddProjectScreen(
                onProjectPosted = { navController.popBackStack() }
            )
        }

        /* ── GROUP CHAT ── */
        composable(
            route = Destinations.GROUP_CHAT_ROUTE,
            arguments = listOf(
                navArgument("projectId")    { type = NavType.StringType },
                navArgument("projectTitle") { type = NavType.StringType }
            )
        ) { back ->
            val projectId    = back.arguments?.getString("projectId")    ?: return@composable
            val projectTitle = back.arguments?.getString("projectTitle") ?: "Team Chat"
            GroupChatScreen(
                projectId    = projectId,
                projectTitle = Uri.decode(projectTitle),
                onBackClick  = { navController.popBackStack() }
            )
        }

        /* ── 1-ON-1 CHAT LIST ── */
//        composable(Destinations.CHAT_LIST) {
//            ChatListScreen(navController)
//        }
        composable(Destinations.CHAT_LIST) {
            ChatListScreen(
                navController = navController,
                onBackClick   = { navController.popBackStack() }
            )
        }


        composable(Destinations.PEOPLE_LIST) {
            PeopleListScreen(
                onBackClick   = { navController.popBackStack() },
                onPersonClick = { uid ->
                    navController.navigate(Destinations.profileViewRoute(uid))
                }
            )
        }

        /* ── 1-ON-1 CHAT ── */
        composable(
            route     = Destinations.CHAT,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { back ->
            val chatId = back.arguments?.getString("chatId") ?: return@composable
            ChatScreen(
                chatId      = chatId,
                onBackClick = { navController.popBackStack() }
            )
        }

        /* ── NOTIFICATIONS ── */
        composable(Destinations.NOTIFICATIONS) {
            NotificationScreen(
                onBackClick      = { navController.popBackStack() },
                onOpenChatClick  = { chatId   -> navController.navigate(Destinations.chatRoute(chatId)) },
                onOpenProjectClick = { pid    -> navController.navigate(Destinations.projectDetailRoute(pid)) },
                onOpenProfileClick = { userId -> navController.navigate(Destinations.profileViewRoute(userId)) }
            )
        }

        /* ── OWN PROFILE ── */
        composable(Destinations.PROFILE) {
            ProfileScreen(
                onHomeClick = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                },
                onProjectsClick   = { navController.navigate(Destinations.PROJECT_LIST) },
                onAddProjectClick = { navController.navigate(Destinations.ADD_PROJECT) },
                onMessagesClick   = { navController.navigate(Destinations.CHAT_LIST) },
                onProfileClick    = {}
            )
        }

        /* ── PROFILE VIEW (other user) ── */
        composable(
            route     = Destinations.PROFILE_VIEW_ROUTE,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { back ->
            val userId = back.arguments?.getString("userId") ?: return@composable
            ProfileViewScreen(
                userId         = userId,
                onBackClick    = { navController.popBackStack() },
                // message icon → open / create 1-on-1 chat
                onMessageClick = { chatId ->
                    navController.navigate(Destinations.chatRoute(chatId))
                }
            )
        }
    }
}