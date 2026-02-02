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
import com.example.cobuild.ui.notification.NotificationScreen
import com.example.cobuild.ui.project.AddProjectScreen
import com.example.cobuild.ui.project.ProjectDetailScreen
import com.example.cobuild.ui.project.ProjectListScreen
import com.example.cobuild.ui.theme.CoBuildTheme
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
    var requesterName by remember { mutableStateOf("") } // To store onboarding name

    // Fetch start destination and requester name
    LaunchedEffect(Unit) {
        if (currentUser == null) {
            startDestination = Destinations.LOGIN
        } else {
            firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        requesterName = doc.getString("name") ?: "User" // Get name from onboarding
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

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        /* -------------------- AUTH -------------------- */
        composable(Destinations.LOGIN) { LoginScreen(navController) }
        composable(Destinations.ONBOARDING) { OnBoardingScreen(navController) }

        /* -------------------- HOME -------------------- */
        composable(Destinations.HOME) {
            HomeScreen(
                navController = navController,
                onAddProjectClick = { navController.navigate(Destinations.ADD_PROJECT) },
                onMessagesClick = {},
                onProfileClick = { navController.navigate(Destinations.PROFILE) },
                onProjectClick = { project ->
                    navController.navigate("${Destinations.PROJECT_DETAIL}/${project.id}")
                },
                onNotificationClick = { navController.navigate(Destinations.NOTIFICATIONS) }
            )
        }

        /* -------------------- NOTIFICATIONS -------------------- */
        composable(Destinations.NOTIFICATIONS) {
            NotificationScreen(
                onBackClick = { navController.popBackStack() },
                onJoinRequestClick = { projectId ->
                    navController.navigate("${Destinations.PROJECT_DETAIL}/${projectId}")
                }
            )
        }

        /* -------------------- ADD PROJECT -------------------- */
        composable(Destinations.ADD_PROJECT) {
            AddProjectScreen(onProjectPosted = { navController.popBackStack() })
        }

        /* -------------------- PROFILE -------------------- */
        composable(Destinations.PROFILE) {
            ProfileScreen(
                onHomeClick = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                },
                onProjectsClick = { navController.navigate(Destinations.PROJECT_LIST) },
                onAddProjectClick = { navController.navigate(Destinations.ADD_PROJECT) },
                onMessagesClick = {},
                onProfileClick = {}
            )
        }

        /* -------------------- PROJECT LIST -------------------- */
        composable(Destinations.PROJECT_LIST) {
            ProjectListScreen(
                onBackClick = { navController.popBackStack() },
                onProjectClick = { projectId ->
                    navController.navigate("${Destinations.PROJECT_DETAIL}/${projectId}")
                }
            )
        }

        /* -------------------- PROJECT DETAIL -------------------- */
        composable(
            route = Destinations.PROJECT_DETAIL_ROUTE,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable

            // Pass requesterName fetched from Firestore
            ProjectDetailScreen(
                projectId = projectId,
                requesterName = requesterName,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
