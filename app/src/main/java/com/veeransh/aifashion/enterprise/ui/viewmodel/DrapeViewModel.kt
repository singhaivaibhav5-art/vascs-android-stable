package com.veeransh.aifashion.enterprise.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.local.entity.AiDrapeResultEntity
import com.veeransh.aifashion.enterprise.data.repository.DrapeRepository
import com.veeransh.aifashion.enterprise.data.repository.DrapeRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DrapeUiState {
    object Idle : DrapeUiState()
    object Capturing : DrapeUiState()
    data class Draping(val message: String) : DrapeUiState()
    data class Error(val message: String) : DrapeUiState()
}

@HiltViewModel
class DrapeViewModel @Inject constructor(
    private val drapeRepository: DrapeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DrapeUiState>(DrapeUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    val drapeResults = drapeRepository.getAllDrapeResults().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun startCapture() {
        _uiState.value = DrapeUiState.Capturing
    }

    fun onImageCaptured(bitmap: Bitmap, originalUri: String) {
        viewModelScope.launch {
            _uiState.value = DrapeUiState.Draping("Analyzing border, pallu...")
            try {
                val drapedBitmap = drapeRepository.generateDrapedImage(bitmap)
                val repoImpl = drapeRepository as? DrapeRepositoryImpl
                
                val finalUri = if (drapedBitmap != null && repoImpl != null) {
                    repoImpl.saveBitmapToCache(drapedBitmap, "draped_${System.currentTimeMillis()}.jpg")
                } else {
                    originalUri
                }

                val status = if (drapedBitmap != null) "DRAPED" else "DRAFT - AI Pending"
                
                val result = AiDrapeResultEntity(
                    originalUri = originalUri,
                    drapedUri = finalUri,
                    status = status
                )
                drapeRepository.saveDrapeResult(result)
                _uiState.value = DrapeUiState.Idle
            } catch (e: Exception) {
                _uiState.value = DrapeUiState.Error("Capture failed: ${e.message}")
            }
        }
    }

    fun toggleSelection(id: Long) {
        val current = _selectedIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id)
        else current.add(id)
        _selectedIds.value = current
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
        _uiState.value = DrapeUiState.Idle
    }
}
