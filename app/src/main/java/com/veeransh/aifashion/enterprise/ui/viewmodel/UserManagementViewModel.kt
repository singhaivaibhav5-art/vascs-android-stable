package com.veeransh.aifashion.enterprise.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _users = MutableStateFlow<List<AdminManagedUser>>(emptyList())
    val users = _users.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var lastSearchTime = 0L
    private var searchCount = 0

    init {
        loadUsers()
    }

    private fun loadUsers() {
        // Mock data for Phase 4
        _users.value = listOf(
            AdminManagedUser("1", "Rohit Dealer", "Dealer", "9876543210", "Active"),
            AdminManagedUser("2", "Meena S", "Style Partner", "9876543211", "Active"),
            AdminManagedUser("3", "Rahul Varma", "Customer", "9876543212", "Suspended"),
            AdminManagedUser("4", "Jaipur Silks", "Dealer", "9876543213", "Blocked"),
            AdminManagedUser("5", "Neha Gupta", "Style Partner", "9876543214", "Active")
        )
    }

    fun updateSearchQuery(query: String) {
        val now = System.currentTimeMillis()
        if (now - lastSearchTime < 60000 && searchCount >= 30) {
            // Rate limit search 30/min
            return
        }
        
        if (now - lastSearchTime > 60000) {
            searchCount = 0
        }
        
        lastSearchTime = now
        searchCount++
        
        _searchQuery.value = query
        // In a real app, this would trigger a Firestore search
    }

    fun suspendUser(userId: String, reason: String?, durationDays: Int? = null) {
        viewModelScope.launch {
            val status = if (reason == null) "Blocked" else "Suspended"
            _users.value = _users.value.map {
                if (it.id == userId) it.copy(status = status) else it
            }
            
            // Firebase Logic: In a real app, we would use Firebase Admin SDK to revoke tokens.
            // Since we're in the client app, we'd update a Firestore flag that security rules check.
            firestore.collection("users").document(userId).update(
                mapOf(
                    "status" to status,
                    "suspensionReason" to reason,
                    "suspendedUntil" to durationDays?.let { System.currentTimeMillis() + it * 24 * 60 * 60 * 1000 }
                )
            )
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            _users.value = _users.value.map {
                if (it.id == userId) it.copy(status = "Active") else it
            }
            firestore.collection("users").document(userId).update("status", "Active")
        }
    }
}

data class AdminManagedUser(
    val id: String,
    val name: String,
    val role: String,
    val phone: String,
    val status: String
)
