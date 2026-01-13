package com.example.cobuild.ui.notification

import androidx.lifecycle.ViewModel
import com.example.cobuild.data.model.AppNotification
import com.example.cobuild.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationViewModel : ViewModel() {

    private val repository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    fun loadNotifications() {
        val userId = auth.currentUser?.uid ?: return
        repository.getNotifications(userId) {
            _notifications.value = it
        }
    }

    fun markRead(notificationId: String) {
        repository.markAsRead(notificationId)
    }
}
