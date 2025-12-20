package com.example.cobuild

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cobuild.auth.LoginScreen
import com.example.cobuild.home.HomeScreen
import com.example.cobuild.profile.ProfileScreen
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

    var startDestination by remember { mutableStateOf("login") }

    LaunchedEffect(Unit) {
        if (currentUser == null) {
            startDestination = "login"
        } else {
            firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { doc ->
                    startDestination =
                        if (doc.exists()) "home" else "onboarding"
                }
                .addOnFailureListener {
                    startDestination = "onboarding"
                }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(navController)
        }

        composable("onboarding") {
            // Make sure you have OnBoardingScreen in your project with navController parameter
            // If not, you can create it or remove this composable
            OnBoardingScreen(navController)
        }

        composable("home") {
            HomeScreen(
                onProfileClick = { navController.navigate("profile") },
                onProjectsClick = { /* Handle projects click */ },
                onAddProjectClick = { /* Handle add project click */ },
                onMessagesClick = { /* Handle messages click */ }
            )
        }

        composable("profile") {
            ProfileScreen(
                onHomeClick = { navController.navigate("home") },
                onProjectsClick = { /* Handle projects click */ },
                onAddProjectClick = { /* Handle add project click */ },
                onMessagesClick = { /* Handle messages click */ },
                onProfileClick = { /* Already on profile */ }
            )
        }
    }
}