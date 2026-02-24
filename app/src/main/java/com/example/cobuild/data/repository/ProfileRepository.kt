package com.example.cobuild.data.repository

import com.example.cobuild.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUserProfile(userId: String): User? {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}