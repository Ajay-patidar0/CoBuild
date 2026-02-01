package com.example.cobuild.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cobuild.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    navController: NavController
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // ---------------- GOOGLE CLIENT ----------------
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    var isLoading by remember { mutableStateOf(false) }

    // ---------------- ACTIVITY RESULT LAUNCHER ----------------
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {
                    val account: GoogleSignInAccount? = task.result
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                    isLoading = true
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { authResult ->
                            if (authResult.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null) {

                                    // Check if user already exists in Firestore
                                    val userDocRef = firestore.collection("users").document(user.uid)
                                    userDocRef.get().addOnSuccessListener { document ->
                                        if (document.exists()) {
                                            // User exists → navigate to Home
                                            isLoading = false
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            // New user → save basic info and navigate to Onboarding
                                            val userData = mapOf(
                                                "uid" to user.uid,
                                                "name" to (user.displayName ?: ""),
                                                "email" to (user.email ?: ""),
                                                "photo" to (user.photoUrl?.toString() ?: "")
                                            )
                                            userDocRef.set(userData).addOnCompleteListener {
                                                isLoading = false
                                                navController.navigate("onboarding") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            }
                                        }
                                    }.addOnFailureListener {
                                        isLoading = false
                                    }

                                } else {
                                    isLoading = false
                                }
                            } else {
                                isLoading = false
                            }
                        }
                } catch (e: Exception) {
                    isLoading = false
                }
            } else {
                isLoading = false
            }
        }

    // ---------------- UI COLORS ----------------
    val Primary = Color(0xFF4361EE)
    val PrimaryVariant = Color(0xFF3A0CA3)
    val Background = Color(0xFFF8F9FA)
    val Surface = Color.White
    val TextSecondary = Color(0xFF6C757D)
    val TextTertiary = Color(0xFFADB5BD)

    // ---------------- UI START ----------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(PrimaryVariant, Primary)
                    )
                )
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ---------------- HEADER ----------------
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface)
                        .shadow(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Co",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryVariant
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Welcome to CoBuild",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Surface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Collaborate. Build. Innovate.",
                    fontSize = 16.sp,
                    color = Surface.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            }

            // ---------------- MIDDLE CARD ----------------
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.login_illustration),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ---------------- GOOGLE SIGN-IN BUTTON ----------------
                Button(
                    onClick = { launcher.launch(googleSignInClient.signInIntent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(2.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF6C757D)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Signing In...",
                            fontSize = 16.sp,
                            color = Color(0xFF6C757D),
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                fontSize = 16.sp,
                                color = Color(0xFF3C4043),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FeatureHighlightItem("👥", "Team Collaboration", TextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                FeatureHighlightItem("⚡", "Real-time Updates", TextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                FeatureHighlightItem("🔒", "Secure & Private", TextSecondary)
            }

            // ---------------- FOOTER ----------------
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val annotatedText = buildAnnotatedString {
                    append("By continuing, you agree to our ")
                    withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.SemiBold)) {
                        append("Terms & Conditions")
                    }
                    append(" and ")
                    withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.SemiBold)) {
                        append("Privacy Policy")
                    }
                }

                Text(
                    text = annotatedText,
                    fontSize = 14.sp,
                    color = TextTertiary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ------------------------------------------------------------

@Composable
private fun FeatureHighlightItem(icon: String, text: String, textColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = icon,
            fontSize = 18.sp,
            modifier = Modifier.width(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}
