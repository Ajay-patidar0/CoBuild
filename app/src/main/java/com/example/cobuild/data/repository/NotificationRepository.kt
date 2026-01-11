package com.example.cobuild.data.repository

import com.example.cobuild.data.model.AppNotification
import com.google.firebase.firestore.FirebaseFirestore

class NotificationRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getNotifications(
        userId: String,
        onResult: (List<AppNotification>) -> Unit
    ) {
        firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AppNotification::class.java)
                        ?.copy(id = doc.id)
                }

                onResult(list)
            }
    }

    fun markAsRead(notificationId: String) {
        firestore.collection("notifications")
            .document(notificationId)
            .update("isRead", true)
    }
}
