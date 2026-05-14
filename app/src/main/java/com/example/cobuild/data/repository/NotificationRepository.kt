package com.example.cobuild.data.repository

import android.util.Log
import com.example.cobuild.data.model.AppNotification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun observeJoinRequestsForOwner(ownerId: String): Flow<List<AppNotification>> = callbackFlow {

        val listener = firestore.collection("project_requests")
            .whereEqualTo("ownerId", ownerId)
            .whereEqualTo("status", "pending")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotificationRepo", "Error fetching notifications: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        AppNotification(
                            id = doc.getString("requestId") ?: doc.id,
                            userId = doc.getString("requesterId") ?: "", // <-- use requesterId as userId
                            type = "join_request",
                            requesterId = doc.getString("requesterId") ?: "",
                            requesterName = doc.getString("requesterName") ?: "User",
                            projectId = doc.getString("projectId") ?: "",
                            projectTitle = doc.getString("projectTitle") ?: "",
                            isRead = doc.getBoolean("isRead") ?: false,
                            createdAt = (doc.get("createdAt") as? Timestamp)?.toDate()?.time
                                ?: System.currentTimeMillis()
                        )
                    }
                    Log.d("NotificationRepo", "Fetched ${list.size} notifications")
                    trySend(list)
                } else {
                    Log.d("NotificationRepo", "No notifications found")
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }
    fun observeInvitesForUser(userId: String): Flow<List<AppNotification>> = callbackFlow {

        val listener = firestore.collection("project_requests")
            .whereEqualTo("requesterId", userId)
            .whereEqualTo("isInvite", true)
            .whereEqualTo("status", "invited")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotificationRepo", "Error fetching invites: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        AppNotification(
                            id            = doc.getString("requestId") ?: doc.id,
                            userId        = doc.getString("ownerId")      ?: "",
                            type          = "invite",
                            requesterId   = doc.getString("ownerId")      ?: "",
                            requesterName = doc.getString("ownerName")    ?: "Project Owner",
                            projectId     = doc.getString("projectId")    ?: "",
                            projectTitle  = doc.getString("projectTitle") ?: "",
                            isRead        = false,
                            isInvite      = true,
                            createdAt     = (doc.get("createdAt") as? Timestamp)?.toDate()?.time
                                ?: System.currentTimeMillis()
                        )
                    }
                    Log.d("NotificationRepo", "Fetched ${list.size} invites")
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }
}
