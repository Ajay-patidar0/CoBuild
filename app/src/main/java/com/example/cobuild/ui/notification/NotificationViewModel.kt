////package com.example.cobuild.ui.notification
////
////import androidx.lifecycle.ViewModel
////import androidx.lifecycle.viewModelScope
////import com.example.cobuild.data.model.AppNotification
////import com.example.cobuild.data.repository.NotificationRepository
////import com.google.firebase.auth.FirebaseAuth
////import kotlinx.coroutines.flow.MutableStateFlow
////import kotlinx.coroutines.flow.StateFlow
////import kotlinx.coroutines.flow.launchIn
////import kotlinx.coroutines.flow.onEach
////
////class NotificationViewModel : ViewModel() {
////
////    private val repository = NotificationRepository()
////
////    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
////    val notifications: StateFlow<List<AppNotification>> = _notifications
////
////    init {
////        FirebaseAuth.getInstance().currentUser?.uid?.let { ownerId ->
////            repository.observeJoinRequestsForOwner(ownerId)
////                .onEach { _notifications.value = it }
////                .launchIn(viewModelScope)
////        }
////        // If currentUser is null, we do nothing safely
////    }
////}
//
//package com.example.cobuild.ui.notification
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.cobuild.data.model.AppNotification
//import com.example.cobuild.data.repository.NotificationRepository
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FieldValue
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.launchIn
//import kotlinx.coroutines.flow.onEach
//
//class NotificationViewModel : ViewModel() {
//
//    private val repository = NotificationRepository()
//    private val firestore = FirebaseFirestore.getInstance()
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
//    }
//
//    /* -------------------- ACCEPT REQUEST -------------------- */
////    fun acceptRequest(notification: AppNotification) {
////        firestore.collection("project_requests")
////            .document(notification.id)
////            .update("status", "accepted")
////    }
//    fun acceptRequest(notification: AppNotification) {
//
//        // 1️⃣ Accept request
//        firestore.collection("project_requests")
//            .document(notification.id)
//            .update("status", "accepted")
//
//        // 2️⃣ Add member
//        firestore.collection("projects")
//            .document(notification.projectId)
//            .update(
//                mapOf(
//                    "members" to FieldValue.arrayUnion(notification.userId),
//                    "status" to "IN_PROGRESS"
//                )
//            )
//    }
//
//
//
//    /* -------------------- DENY REQUEST -------------------- */
//    fun denyRequest(notification: AppNotification) {
//
//        // 1️⃣ Update request status
//        firestore.collection("project_requests")
//            .document(notification.id)
//            .update("status", "denied")
//
//        // 2️⃣ Delete related chat
//        firestore.collection("chats")
//            .whereEqualTo("projectId", notification.projectId)
//            .whereArrayContains("participants", notification.userId)
//            .get()
//            .addOnSuccessListener { snapshot ->
//                snapshot.documents.forEach {
//                    it.reference.delete()
//                }
//            }
//    }
//    fun openChat(
//        notification: AppNotification,
//        onChatFound: (String) -> Unit
//    ) {
//        firestore.collection("chats")
//            .whereEqualTo("projectId", notification.projectId)
//            .whereArrayContains("participants", notification.userId)
//            .get()
//            .addOnSuccessListener { snapshot ->
//                val chatDoc = snapshot.documents.firstOrNull()
//                chatDoc?.let {
//                    onChatFound(it.id)
//                }
//            }
//    }
//
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.combine
class NotificationViewModel : ViewModel() {

    private val repository = NotificationRepository()
    private val firestore  = FirebaseFirestore.getInstance()

//    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
//    val notifications: StateFlow<List<AppNotification>> = _notifications
private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

//    init {
//        FirebaseAuth.getInstance().currentUser?.uid?.let { ownerId ->
//            repository.observeJoinRequestsForOwner(ownerId)
//                .onEach { _notifications.value = it }
//                .launchIn(viewModelScope)
//        }
//    }

    init {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->

//            // Existing: join requests where YOU are the owner
//            repository.observeJoinRequestsForOwner(uid)
//                .onEach { _notifications.value = it }
//                .launchIn(viewModelScope)
            combine(
                repository.observeJoinRequestsForOwner(uid),
                repository.observeInvitesForUser(uid)
            ) { joins, invites -> joins + invites }
                .onEach { _notifications.value = it }
                .launchIn(viewModelScope)

            // New: AI match notifications from recommended_projects scoring
            firestore.collection("users").document(uid)
                .collection("notifications")
                .whereEqualTo("read", false)
                .addSnapshotListener { snap, _ ->
                    val aiUnread   = snap?.size() ?: 0
                    val joinUnread = _notifications.value.count { !it.isRead }
                    _unreadCount.value = aiUnread + joinUnread
                }
        }
    }

    /* ── ACCEPT ── */
//    fun acceptRequest(notification: AppNotification) {
//        val batch = firestore.batch()
//
//        /* 1. mark request accepted */
//        batch.update(
//            firestore.collection("project_requests").document(notification.id),
//            "status", "accepted"
//        )
//
//        /* 2. add user to project members + flip status to IN_PROGRESS */
//        batch.update(
//            firestore.collection("projects").document(notification.projectId),
//            mapOf(
//                "members" to FieldValue.arrayUnion(notification.userId),
//                "status"  to "IN_PROGRESS"
//            )
//        )
//
//        /* 3. add user to group chat participants so they see the team chat */
//        batch.update(
//            firestore.collection("group_chats").document(notification.projectId),
//            "participants", FieldValue.arrayUnion(notification.userId)
//        )
//
//        batch.commit()
//    }

    fun acceptRequest(notification: AppNotification) {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val batch = firestore.batch()

        batch.update(
            firestore.collection("project_requests").document(notification.id),
            "status", "accepted"
        )

        // if it's an invite, the CURRENT user joins; if join-request, the requester joins
        val joiningUser = if (notification.isInvite) currentUid else notification.userId

        batch.update(
            firestore.collection("projects").document(notification.projectId),
            mapOf(
                "members" to FieldValue.arrayUnion(joiningUser),
                "status"  to "IN_PROGRESS"
            )
        )

        batch.update(
            firestore.collection("group_chats").document(notification.projectId),
            "participants", FieldValue.arrayUnion(joiningUser)
        )

        batch.commit()
    }
    /* ── DENY ── */
    fun denyRequest(notification: AppNotification) {
        /* 1. mark denied */
        firestore.collection("project_requests")
            .document(notification.id)
            .update("status", "denied")

        /* 2. delete the 1-on-1 chat that was auto-created on join request */
        val chatId = listOf(
            FirebaseAuth.getInstance().currentUser?.uid ?: "",
            notification.userId
        ).sorted().joinToString("_")

        firestore.collection("chats").document(chatId).delete()
    }

    /* ── OPEN CHAT ── */
    fun openChat(
        notification: AppNotification,
        onChatFound: (String) -> Unit
    ) {
        /* use deterministic chat ID (same as createDirectChatIfNotExists) */
        val chatId = listOf(
            FirebaseAuth.getInstance().currentUser?.uid ?: "",
            notification.userId
        ).sorted().joinToString("_")

        val chatRef = firestore.collection("chats").document(chatId)

        chatRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                onChatFound(chatId)
            } else {
                /* create it if somehow missing, then navigate */
                chatRef.set(
                    mapOf(
                        "participants"  to listOf(
                            FirebaseAuth.getInstance().currentUser?.uid ?: "",
                            notification.userId
                        ),
                        "projectId"     to notification.projectId,
                        "lastMessage"   to "",
                        "lastTimestamp" to System.currentTimeMillis()
                    )
                ).addOnSuccessListener { onChatFound(chatId) }
            }
        }
    }
}
