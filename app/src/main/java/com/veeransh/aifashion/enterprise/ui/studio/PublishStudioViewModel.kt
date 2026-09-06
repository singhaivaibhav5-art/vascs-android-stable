package com.veeransh.aifashion.enterprise.ui.studio

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.local.dao.AdminConfigDao
import com.veeransh.aifashion.enterprise.data.local.entity.PlacementEntity
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.data.repository.PlacementRepository
import com.veeransh.aifashion.enterprise.data.repository.ProductRepository
import com.veeransh.aifashion.enterprise.util.PlacementImageProcessor
import com.veeransh.aifashion.enterprise.util.PlacementTemplateHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublishStudioViewModel @Inject constructor(
    private val application: Application,
    private val productRepository: ProductRepository,
    private val placementRepository: PlacementRepository,
    private val adminConfigDao: AdminConfigDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<PublishStudioUiState>(PublishStudioUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _operationResult = MutableStateFlow<Result<String>?>(null)
    val operationResult = _operationResult.asStateFlow()

    val adminConfig = adminConfigDao.getConfig().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private var currentProductId: String? = null

    fun init(productId: String) {
        if (currentProductId == productId) return
        currentProductId = productId
        
        viewModelScope.launch {
            _uiState.value = PublishStudioUiState.Loading
            
            val product = productRepository.getProductById(productId)
            if (product == null) {
                _uiState.value = PublishStudioUiState.Error("Product not found")
                return@launch
            }

            placementRepository.observeProductPlacements(productId)
                .onEach { placements ->
                    _uiState.value = PublishStudioUiState.Success(product, placements)
                }
                .catch { e ->
                    _uiState.value = PublishStudioUiState.Error(e.message ?: "Unknown error")
                }
                .launchIn(viewModelScope)
        }
    }

    fun getTemplateDefaults(placementType: String): PlacementEntity? {
        val config = adminConfig.value ?: return null
        val productId = currentProductId ?: return null
        
        val template = PlacementTemplateHelper.getTemplate(placementType, config.templatesJson)
        
        return PlacementEntity(
            productId = productId,
            placementType = placementType,
            aspectRatio = template.aspectRatio,
            cropMode = template.cropMode,
            targetWidthPx = template.targetWidthPx,
            targetHeightPx = template.targetHeightPx,
            title = ""
        )
    }

    fun savePlacement(placement: PlacementEntity) {
        viewModelScope.launch {
            try {
                _operationResult.value = null
                
                val product = (uiState.value as? PublishStudioUiState.Success)?.product
                val sourceToProcess = if (placement.sourceUri.isNotBlank()) {
                    placement.sourceUri
                } else if (product != null && product.image.isNotBlank()) {
                    product.image
                } else {
                    ""
                }

                var finalPlacement = placement
                val isStale = PlacementImageProcessor.isStale(placement, sourceToProcess)
                
                if (sourceToProcess.isNotBlank() && isStale) {
                    val processedUri = PlacementImageProcessor.processImage(
                        context = application,
                        sourceUri = sourceToProcess,
                        placement = placement
                    )
                    
                    // Cleanup old asset if it changed and is not shared
                    if (placement.imageUri.isNotBlank() && placement.imageUri != processedUri) {
                        checkAndCleanupAsset(placement.imageUri, processedUri)
                    }
                    
                    finalPlacement = placement.copy(imageUri = processedUri, sourceUri = sourceToProcess)
                }

                if (finalPlacement.placementId == 0L) {
                    placementRepository.insertPlacement(finalPlacement)
                } else {
                    placementRepository.updatePlacement(finalPlacement)
                }
                
                _operationResult.value = Result.success("Placement saved successfully")
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            }
        }
    }

    fun regenerateImage(placement: PlacementEntity) {
        viewModelScope.launch {
            try {
                val product = (uiState.value as? PublishStudioUiState.Success)?.product
                val sourceToProcess = if (placement.sourceUri.isNotBlank()) placement.sourceUri 
                                     else product?.image ?: ""
                
                if (sourceToProcess.isBlank()) {
                    _operationResult.value = Result.failure(Exception("No source image available"))
                    return@launch
                }

                val processedUri = PlacementImageProcessor.processImage(
                    context = application,
                    sourceUri = sourceToProcess,
                    placement = placement
                )

                if (placement.imageUri.isNotBlank() && placement.imageUri != processedUri) {
                    checkAndCleanupAsset(placement.imageUri, processedUri)
                }

                val finalPlacement = placement.copy(imageUri = processedUri, sourceUri = sourceToProcess)
                placementRepository.updatePlacement(finalPlacement)
                
                _operationResult.value = Result.success("Image regenerated successfully")
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            }
        }
    }

    private suspend fun checkAndCleanupAsset(oldUri: String, newUri: String) {
        // Shared asset protection: verify if other placements use the same oldUri
        val allPlacements = placementRepository.getAllPlacements()
        val isShared = allPlacements.any { it.imageUri == oldUri && it.placementId != 0L }
        if (!isShared) {
            PlacementImageProcessor.cleanupOldAsset(application, oldUri, newUri)
        }
    }

    fun deletePlacement(placement: PlacementEntity) {
        viewModelScope.launch {
            try {
                // Identify processed asset before deletion
                val assetToDelete = placement.imageUri
                placementRepository.deletePlacement(placement)
                
                // Cleanup physical file if not shared
                if (assetToDelete.isNotBlank()) {
                    val allPlacements = placementRepository.getAllPlacements()
                    val isStillReferenced = allPlacements.any { it.imageUri == assetToDelete }
                    if (!isStillReferenced) {
                        PlacementImageProcessor.cleanupOldAsset(application, assetToDelete)
                    }
                }
                
                _operationResult.value = Result.success("Placement deleted")
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            }
        }
    }

    fun toggleActiveState(placement: PlacementEntity) {
        viewModelScope.launch {
            try {
                placementRepository.updateActiveState(placement.placementId, !placement.isActive)
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            }
        }
    }

    fun updateOrdering(placement: PlacementEntity, priority: Int, sortOrder: Int) {
        viewModelScope.launch {
            try {
                placementRepository.updateOrdering(placement.placementId, priority, sortOrder)
            } catch (e: Exception) {
                _operationResult.value = Result.failure(e)
            }
        }
    }

    fun resetOperationResult() {
        _operationResult.value = null
    }
}

sealed class PublishStudioUiState {
    object Loading : PublishStudioUiState()
    data class Success(val product: ProductEntity, val placements: List<PlacementEntity>) : PublishStudioUiState()
    data class Error(val message: String) : PublishStudioUiState()
}
