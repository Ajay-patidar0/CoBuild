package com.example.cobuild.auth

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var googleSignInClient: GoogleSignInClient

    // LiveData to send the SignIn Intent to the Activity
    private val _signInIntent = MutableLiveData<Intent?>()
    val signInIntent: LiveData<Intent?> = _signInIntent

    // Loading state as LiveData (works with observeAsState in Compose)
    val isLoading = MutableLiveData(false)

    /**
     * Initialize GoogleSignInClient from Activity (call from Activity onCreate)
     * Replace "YOUR_WEB_CLIENT_ID_HERE" with your Firebase Web client ID (OAuth 2.0 client).
     */
    fun initGoogleSignIn(activity: Activity, webClientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    /**
     * Prepare sign-in by exposing the sign-in intent through LiveData.
     * Activity should observe signInIntent (or check value after calling this).
     */
    fun signIn() {
        // ensure client is initialized
        try {
            val intent = googleSignInClient.signInIntent
            _signInIntent.value = intent
        } catch (e: UninitializedPropertyAccessException) {
            // client not initialized; set null or handle error
            _signInIntent.value = null
        }
    }

    /**
     * Handle result from Google SignIn activity (Activity.onActivityResult)
     * Pass the returned Intent data into this method.
     */
    fun handleGoogleResult(data: Intent?) {
        isLoading.value = true

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.result
            val idToken = account?.idToken

            if (idToken != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        saveUserToFirestore()
                    }
                    .addOnFailureListener {
                        isLoading.value = false
                    }
            } else {
                isLoading.value = false
            }
        } catch (e: Exception) {
            isLoading.value = false
        }
    }

    private fun saveUserToFirestore() {
        val user = auth.currentUser ?: run {
            isLoading.value = false
            return
        }

        val userData = mapOf(
            "name" to (user.displayName ?: ""),
            "email" to (user.email ?: ""),
            "photo" to (user.photoUrl?.toString() ?: "")
        )

        firestore.collection("users")
            .document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                isLoading.value = false
            }
            .addOnFailureListener {
                isLoading.value = false
            }
    }
}
