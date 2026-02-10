//package com.example.cobuild.ui.notification
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.cobuild.data.model.AppNotification
//import com.example.cobuild.data.repository.NotificationRepository
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.launchIn
//import kotlinx.coroutines.flow.onEach
//
//class NotificationViewModel : ViewModel() {
//
//    private val repository = NotificationRepository()
//
//    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
//    val notifications: StateFlow<List<AppNotification>> = _notifications
//
//    init {
//        FirebaseAuth.getInstance().currentUser?.uid?.let { ownerId ->
//            repository.observeJoinRequestsForOwner(ownerId)
//                .onEach { _notifications.value = it }
//                .launchIn(viewModelScope)
//        }
//        // If currentUser is null, we do nothing safely
//    }
//}

package com.example.cobuild.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobuild.data.model.AppNotification
import com.example.cobuild.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NotificationViewModel : ViewModel() {

    private val repository = NotificationRepository()
    private val firestore = FirebaseFirestore.getInstance()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    init {
        FirebaseAuth.getInstance().currentUser?.uid?.let { ownerId ->
            repository.observeJoinRequestsForOwner(ownerId)
                .onEach { _notifications.value = it }
                .launchIn(viewModelScope)
        }
    }

    /* -------------------- ACCEPT REQUEST -------------------- */
//    fun acceptRequest(notification: AppNotification) {
//        firestore.collection("project_requests")
//            .document(notification.id)
//            .update("status", "accepted")
//    }
    fun acceptRequest(notification: AppNotification) {

        // 1️⃣ Accept request
        firestore.collection("project_requests")
            .document(notification.id)
            .update("status", "accepted")

        // 2️⃣ Add member
        firestore.collection("projects")
            .document(notification.projectId)
            .update(
                mapOf(
                    "members" to FieldValue.arrayUnion(notification.userId),
                    "status" to "IN_PROGRESS"
                )
            )
    }



    /* -------------------- DENY REQUEST -------------------- */
    fun denyRequest(notification: AppNotification) {

        // 1️⃣ Update request status
        firestore.collection("project_requests")
            .document(notification.id)
            .update("status", "denied")

        // 2️⃣ Delete related chat
        firestore.collection("chats")
            .whereEqualTo("projectId", notification.projectId)
            .whereArrayContains("participants", notification.userId)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach {
                    it.reference.delete()
                }
            }
    }
    fun openChat(
        notification: AppNotification,
        onChatFound: (String) -> Unit
    ) {
        firestore.collection("chats")
            .whereEqualTo("projectId", notification.projectId)
            .whereArrayContains("participants", notification.userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val chatDoc = snapshot.documents.firstOrNull()
                chatDoc?.let {
                    onChatFound(it.id)
                }
            }
    }

}
