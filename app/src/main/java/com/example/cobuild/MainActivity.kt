package com.example.cobuild

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cobuild.auth.LoginScreen
import com.example.cobuild.ui.theme.CoBuildTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CoBuildTheme {
                val navController = rememberNavController()

                // Check if user is logged in
                val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                val startDest = if (isLoggedIn) "onboarding" else "login"

                NavHost(
                    navController = navController,
                    startDestination = startDest
                ) {

                    // LOGIN SCREEN
//                    composable("login") {
//                        LoginScreen(
//                            onLaunchGoogleSignIn = { intent: Intent ->
//                                // Launch Google Sign-In intent here if needed
//                                startActivity(intent)
//
//                                // After sign-in, navigate to onboarding (you can also do this after Firebase callback)
//                                navController.navigate("onboarding") {
//                                    popUpTo("login") { inclusive = true }
//                                }
//                            }
//                        )
//                    }
                    composable("login") {
                        LoginScreen(navController)
                    }

                    // ONBOARDING SCREEN
                    composable("onboarding") {
                        OnBoardingScreen()
                    }
                }
            }
        }
    }
}
