package com.example.cobuild

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
import com.example.cobuild.navigation.Destinations
import com.example.cobuild.profile.ProfileScreen
import com.example.cobuild.ui.notification.NotificationScreen
import com.example.cobuild.ui.profile.ProfileViewScreen
import com.example.cobuild.ui.project.AddProjectScreen
import com.example.cobuild.ui.project.EditProjectScreen
import com.example.cobuild.ui.project.ProjectDetailScreen
import com.example.cobuild.ui.project.ProjectListScreen
import com.example.cobuild.ui.theme.CoBuildTheme
import com.example.cobuild.data.model.Project
import com.example.cobuild.ui.project.HomeProjectDetailScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CoBuildTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {

    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    var startDestination by remember { mutableStateOf(Destinations.LOGIN) }
    var requesterName by remember { mutableStateOf("") }

    /* ---------------- CHECK USER SESSION ---------------- */

    LaunchedEffect(Unit) {

        if (currentUser == null) {

            startDestination = Destinations.LOGIN

        } else {

            firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->

                    if (doc.exists()) {
                        requesterName = doc.getString("name") ?: "User"
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

    /* ---------------- NAVIGATION ---------------- */

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        /* ---------------- AUTH ---------------- */

        composable(Destinations.LOGIN) {
            LoginScreen(navController)
        }

        composable(Destinations.ONBOARDING) {
            OnBoardingScreen(navController)
        }

        /* ---------------- HOME ---------------- */

        composable(Destinations.HOME) {

            HomeScreen(
                navController = navController,

                onAddProjectClick = {
                    navController.navigate(Destinations.ADD_PROJECT)
                },

                onMessagesClick = {
                    navController.navigate(Destinations.CHAT_LIST)
                },

                onProfileClick = {
                    navController.navigate(Destinations.PROFILE)
                },

//                onProjectClick = { project: Project ->
//                    navController.navigate(
//                        Destinations.projectDetailRoute(project.id)
//                    )
//                },
                onProjectClick = { project: Project ->
                    navController.navigate(
                        Destinations.homeProjectDetailRoute(project.id)
                    )
                },

                onNotificationClick = {
                    navController.navigate(Destinations.NOTIFICATIONS)
                }
            )
        }

        /* ---------------- CHAT LIST ---------------- */

        composable(Destinations.CHAT_LIST) {
            ChatListScreen(navController)
        }

        /* ---------------- CHAT SCREEN ---------------- */

        composable(
            route = Destinations.CHAT,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val chatId =
                backStackEntry.arguments?.getString("chatId")
                    ?: return@composable

            ChatScreen(
                chatId = chatId,
                onBackClick = { navController.popBackStack() }
            )
        }

        /* ---------------- NOTIFICATIONS ---------------- */

        composable(Destinations.NOTIFICATIONS) {

            NotificationScreen(
                onBackClick = { navController.popBackStack() },

                onOpenChatClick = { chatId ->
                    navController.navigate(
                        Destinations.chatRoute(chatId)
                    )
                },

                onOpenProjectClick = { projectId ->
                    navController.navigate(
                        Destinations.projectDetailRoute(projectId)
                    )
                },

                onOpenProfileClick = { userId ->
                    navController.navigate(
                        Destinations.profileViewRoute(userId)
                    )
                }
            )
        }

        // ✅ ADD THIS BLOCK — home discovery detail screen
        composable(
            route = Destinations.HOME_PROJECT_DETAIL_ROUTE,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val projectId = backStackEntry.arguments?.getString("projectId")
                ?: return@composable

            HomeProjectDetailScreen(
                projectId   = projectId,
                onBackClick = { navController.popBackStack() }
            )
        }
        /* ---------------- ADD PROJECT ---------------- */

        composable(Destinations.ADD_PROJECT) {

            AddProjectScreen(
                onProjectPosted = {
                    navController.popBackStack()
                }
            )
        }

        /* ---------------- OWN PROFILE ---------------- */

        composable(Destinations.PROFILE) {

            ProfileScreen(
                onHomeClick = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                },

                onProjectsClick = {
                    navController.navigate(Destinations.PROJECT_LIST)
                },

                onAddProjectClick = {
                    navController.navigate(Destinations.ADD_PROJECT)
                },

                onMessagesClick = {
                    navController.navigate(Destinations.CHAT_LIST)
                },

                onProfileClick = {}
            )
        }

        /* ---------------- PROFILE VIEW ---------------- */

        composable(
            route = Destinations.PROFILE_VIEW_ROUTE,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val userId =
                backStackEntry.arguments?.getString("userId")
                    ?: return@composable

            ProfileViewScreen(
                userId = userId,
                onBackClick = { navController.popBackStack() }
            )
        }

        /* ---------------- PROJECT LIST ---------------- */

        composable(Destinations.PROJECT_LIST) {

            ProjectListScreen(
                onBackClick = { navController.popBackStack() },

                onProjectClick = { projectId ->
                    navController.navigate(
                        Destinations.projectDetailRoute(projectId)
                    )
                },

                onEditProject = { projectId ->
                    navController.navigate(
                        Destinations.editProjectRoute(projectId)
                    )
                }
            )
        }

        /* ---------------- PROJECT DETAIL ---------------- */

        composable(
            route = Destinations.PROJECT_DETAIL_ROUTE,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val projectId =
                backStackEntry.arguments?.getString("projectId")
                    ?: return@composable

            ProjectDetailScreen(
                projectId = projectId,
                requesterName = requesterName,
                onBackClick = { navController.popBackStack() }
            )
        }

        /* ---------------- EDIT PROJECT ---------------- */

        composable(
            route = Destinations.EDIT_PROJECT_ROUTE,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val projectId =
                backStackEntry.arguments?.getString("projectId")
                    ?: return@composable

            EditProjectScreen(
                projectId = projectId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}