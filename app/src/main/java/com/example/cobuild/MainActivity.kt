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
import com.example.cobuild.ui.theme.CoBuildTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CoBuildTheme {

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
                        OnBoardingScreen(navController)
                    }

                    composable("home") {
                        HomeScreen()
                    }
                }
            }
        }
    }
}
