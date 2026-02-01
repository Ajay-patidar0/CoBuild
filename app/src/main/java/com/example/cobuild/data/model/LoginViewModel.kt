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

    // LiveData to expose the sign-in intent
    private val _signInIntent = MutableLiveData<Intent?>()
    val signInIntent: LiveData<Intent?> = _signInIntent

    val isLoading = MutableLiveData(false)

    /** Initialize GoogleSignInClient */
    fun initGoogleSignIn(activity: Activity, webClientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    /** Trigger Google Sign-In Intent */
    fun signIn() {
        try {
            _signInIntent.value = googleSignInClient.signInIntent
        } catch (e: UninitializedPropertyAccessException) {
            _signInIntent.value = null
        }
    }

    /** Handle Google Sign-In result */
    fun handleGoogleResult(data: Intent?, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        isLoading.value = true

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.result
            val idToken = account?.idToken

            if (idToken != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        saveUserToFirestore(onSuccess)
                    }
                    .addOnFailureListener { e ->
                        isLoading.value = false
                        onFailure(e)
                    }
            } else {
                isLoading.value = false
                onFailure(Exception("Google Sign-In failed: ID Token is null"))
            }
        } catch (e: Exception) {
            isLoading.value = false
            onFailure(e)
        }
    }

    /** Save user to Firestore with stable UID */
    private fun saveUserToFirestore(onSuccess: () -> Unit) {
        val user = auth.currentUser ?: run {
            isLoading.value = false
            return
        }

        val userData = mapOf(
            "uid" to user.uid,
            "name" to (user.displayName ?: "Anonymous"),
            "email" to (user.email ?: ""),
            "photo" to (user.photoUrl?.toString() ?: "")
        )

        firestore.collection("users")
            .document(user.uid) // stable UID
            .set(userData)
            .addOnSuccessListener {
                isLoading.value = false
                onSuccess() // navigate to onboarding/home
            }
            .addOnFailureListener {
                isLoading.value = false
            }
    }

    /** Helper to get current user UID */
    fun getCurrentUserUid(): String? = auth.currentUser?.uid
}
