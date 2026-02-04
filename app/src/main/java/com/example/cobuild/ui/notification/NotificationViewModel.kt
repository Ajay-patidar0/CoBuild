package com.example.cobuild.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cobuild.data.model.AppNotification
import com.example.cobuild.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NotificationViewModel : ViewModel() {

    private val repository = NotificationRepository()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    init {
        FirebaseAuth.getInstance().currentUser?.uid?.let { ownerId ->
            repository.observeJoinRequestsForOwner(ownerId)
                .onEach { _notifications.value = it }
                .launchIn(viewModelScope)
        }
        // If currentUser is null, we do nothing safely
    }
}
