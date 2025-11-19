package com.example.cobuild.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(text = "Welcome to Home Screen!")
    }
}
