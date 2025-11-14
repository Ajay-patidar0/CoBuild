package com.example.cobuild.auth

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cobuild.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    // callback to activity to launch the Google sign-in Intent
    onLaunchGoogleSignIn: (Intent) -> Unit
) {
    // Observe LiveData<Boolean> from ViewModel safely in Compose
    val isLoading by viewModel.isLoading.observeAsState(false)

    // Colors
    val Primary = Color(0xFF4361EE)
    val PrimaryVariant = Color(0xFF3A0CA3)
    val Background = Color(0xFFF8F9FA)
    val Surface = Color.White
    val TextSecondary = Color(0xFF6C757D)
    val TextTertiary = Color(0xFFADB5BD)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Gradient header background
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

            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // App Logo/Icon (kept small and simple)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
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

            // Main content (illustration + actions)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.login_illustration),
                        contentDescription = "Team collaboration illustration",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Google Sign-in flow: ask ViewModel for intent then launch via callback
                Button(
                    onClick = {
                        if (!isLoading) {
                            viewModel.signIn()
                            // signInIntent is a LiveData<Intent> updated in ViewModel
                            viewModel.signInIntent.value?.let { intent ->
                                onLaunchGoogleSignIn(intent)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
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

                // Feature highlights
                FeatureHighlightItem("👥", "Team Collaboration", TextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                FeatureHighlightItem("⚡", "Real-time Updates", TextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                FeatureHighlightItem("🔒", "Secure & Private", TextSecondary)
            }

            // Footer
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
