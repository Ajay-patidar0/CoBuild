package com.example.cobuild

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cobuild.auth.LoginScreen
import com.example.cobuild.home.HomeScreen
import com.example.cobuild.navigation.Destinations
import com.example.cobuild.profile.ProfileScreen
import com.example.cobuild.ui.project.AddProjectScreen
import com.example.cobuild.ui.project.ProjectDetailScreen
import com.example.cobuild.ui.project.ProjectListScreen
import com.example.cobuild.ui.theme.CoBuildTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 🔥 ADD THESE CHAT IMPORTS
import com.example.cobuild.messages.ChatListScreen
import com.example.cobuild.messages.ChatScreen

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

    LaunchedEffect(Unit) {
        if (currentUser == null) {
            startDestination = Destinations.LOGIN
        } else {
            firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    startDestination =
                        if (doc.exists()) Destinations.HOME else Destinations.ONBOARDING
                }
                .addOnFailureListener {
                    startDestination = Destinations.ONBOARDING
                }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Destinations.LOGIN) {
            LoginScreen(navController)
        }

        composable(Destinations.ONBOARDING) {
            OnBoardingScreen(navController)
        }

        composable(Destinations.HOME) {
            HomeScreen(
                navController = navController,
                onAddProjectClick = {
                    navController.navigate(Destinations.ADD_PROJECT)
                },
                onMessagesClick = {
                    // 🔥 CHAT LIST NAVIGATION
                    navController.navigate("chat_list")
                },
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE)
                },
                onProjectClick = { project ->
                    navController.navigate(
                        "${Destinations.PROJECT_DETAIL}/${project.id}"
                    )
                }
            )
        }

        composable(Destinations.ADD_PROJECT) {
            AddProjectScreen(
                onProjectPosted = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.PROFILE) {
            ProfileScreen(
                onHomeClick = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                },
                onProjectsClick = { navController.navigate(Destinations.PROJECT_LIST) },
                onAddProjectClick = { navController.navigate(Destinations.ADD_PROJECT) },
                onMessagesClick = {
                    // 🔥 GO TO CHAT LIST
                    navController.navigate("chat_list")
                },
                onProfileClick = {},

                onLogout = {
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.PROJECT_LIST) {
            ProjectListScreen(
                onBackClick = { navController.popBackStack() },
                onProjectClick = { projectId ->
                    navController.navigate("${Destinations.PROJECT_DETAIL}/${projectId}")
                }
            )
        }

        composable(
            route = Destinations.PROJECT_DETAIL_ROUTE,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId =
                backStackEntry.arguments?.getString("projectId") ?: return@composable

            ProjectDetailScreen(
                projectId = projectId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 🔥 CHAT LIST SCREEN
        composable("chat_list") {
            ChatListScreen(navController)
        }

        // 🔥 CHAT SCREEN
        composable(
            route = "chat/{chatId}",
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable

            ChatScreen(
                chatId = chatId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
