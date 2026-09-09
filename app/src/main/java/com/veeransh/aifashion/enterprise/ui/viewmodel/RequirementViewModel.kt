package com.veeransh.aifashion.enterprise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.local.entity.CustomerRequirementEntity
import com.veeransh.aifashion.enterprise.data.repository.RequirementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequirementViewModel @Inject constructor(
    private val repository: RequirementRepository,
    private val productRepository: com.veeransh.aifashion.enterprise.data.repository.ProductRepository,
    private val userRepository: com.veeransh.aifashion.enterprise.data.repository.UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RequirementUiState>(RequirementUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _targetProduct = MutableStateFlow<com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity?>(null)
    val targetProduct = _targetProduct.asStateFlow()

    private val _currentUser = MutableStateFlow<com.veeransh.aifashion.enterprise.data.local.entity.UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun loadContext(productId: String, userId: String?) {
        viewModelScope.launch {
            _uiState.value = RequirementUiState.Loading
            
            // Load Product
            val product = productRepository.getProductById(productId)
            _targetProduct.value = product
            
            // Load User (if logged in)
            if (userId != null) {
                _currentUser.value = userRepository.getUserById(userId)
            }

            if (product == null) {
                _uiState.value = RequirementUiState.Error("Authoritative product data not found")
            } else {
                _uiState.value = RequirementUiState.Idle
            }
        }
    }
    fun submitRequirement(requirement: CustomerRequirementEntity) {
        viewModelScope.launch {
            _uiState.value = RequirementUiState.Loading
            try {
                repository.create(requirement)
                _uiState.value = RequirementUiState.Success("Requirement submitted successfully")
            } catch (e: Exception) {
                _uiState.value = RequirementUiState.Error(e.message ?: "Failed to submit requirement")
            }
        }
    }

    // Observe requirements for a specific user
    fun observeMyRequirements(userId: String): StateFlow<List<CustomerRequirementEntity>> {
        return repository.observeByUserId(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // Observe all requirements (Admin)
    fun observeAllRequirements(): StateFlow<List<CustomerRequirementEntity>> {
        return repository.observeAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // Get a specific requirement
    private val _selectedRequirement = MutableStateFlow<CustomerRequirementEntity?>(null)
    val selectedRequirement = _selectedRequirement.asStateFlow()

    fun getRequirement(requirementId: String) {
        viewModelScope.launch {
            _uiState.value = RequirementUiState.Loading
            try {
                val requirement = repository.getById(requirementId)
                _selectedRequirement.value = requirement
                _uiState.value = RequirementUiState.Idle
            } catch (e: Exception) {
                _uiState.value = RequirementUiState.Error(e.message ?: "Failed to load requirement")
            }
        }
    }

    // Update status
    fun updateRequirementStatus(id: String, status: String) {
        viewModelScope.launch {
            _uiState.value = RequirementUiState.Loading
            try {
                repository.updateStatus(id, status)
                _uiState.value = RequirementUiState.Success("Status updated successfully")
            } catch (e: Exception) {
                _uiState.value = RequirementUiState.Error(e.message ?: "Failed to update status")
            }
        }
    }

    // Update admin notes
    fun updateAdminNotes(id: String, notes: String) {
        viewModelScope.launch {
            _uiState.value = RequirementUiState.Loading
            try {
                repository.updateAdminNotes(id, notes)
                _uiState.value = RequirementUiState.Success("Notes updated successfully")
            } catch (e: Exception) {
                _uiState.value = RequirementUiState.Error(e.message ?: "Failed to update notes")
            }
        }
    }

    fun resetState() {
        _uiState.value = RequirementUiState.Idle
    }
}

sealed class RequirementUiState {
    object Idle : RequirementUiState()
    object Loading : RequirementUiState()
    data class Success(val message: String) : RequirementUiState()
    data class Error(val message: String) : RequirementUiState()
}
