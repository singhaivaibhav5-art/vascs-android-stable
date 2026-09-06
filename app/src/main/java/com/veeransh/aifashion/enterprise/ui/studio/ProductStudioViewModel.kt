package com.veeransh.aifashion.enterprise.ui.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductStudioViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductStudioUiState>(ProductStudioUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _saveResult = MutableStateFlow<SaveOperationResult?>(null)
    val saveResult = _saveResult.asStateFlow()

    private var isNewProduct: Boolean = true

    fun loadProduct(productId: String?) {
        viewModelScope.launch {
            if (productId == null || productId.isBlank()) {
                isNewProduct = true
                _uiState.value = ProductStudioUiState.Success(createNewProduct())
            } else {
                val product = productRepository.getProductById(productId)
                if (product != null) {
                    isNewProduct = false
                    _uiState.value = ProductStudioUiState.Success(product)
                } else {
                    // Treat as new if ID provided but not found, or show error?
                    // User rules say "Do not silently overwrite".
                    _uiState.value = ProductStudioUiState.Error("Product with ID $productId not found.")
                }
            }
        }
    }

    private fun createNewProduct(): ProductEntity {
        return ProductEntity(
            id = UUID.randomUUID().toString(),
            name = "",
            sku = "", // Force user to enter
            barcode = "",
            category = "Silk",
            brand = "Veeransh",
            createdAt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        )
    }

    fun saveDraft(product: ProductEntity, andNavigate: Boolean = false) {
        viewModelScope.launch {
            try {
                if (isNewProduct) {
                    productRepository.insertProduct(product)
                    isNewProduct = false // Now it exists
                } else {
                    productRepository.updateProduct(product)
                }
                _saveResult.value = SaveOperationResult.Success(product.id, andNavigate)
            } catch (e: Exception) {
                _saveResult.value = SaveOperationResult.Failure(e.message ?: "Database error")
            }
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }
}

sealed class ProductStudioUiState {
    object Loading : ProductStudioUiState()
    data class Success(val product: ProductEntity) : ProductStudioUiState()
    data class Error(val message: String) : ProductStudioUiState()
}

sealed class SaveOperationResult {
    data class Success(val productId: String, val shouldNavigate: Boolean) : SaveOperationResult()
    data class Failure(val message: String) : SaveOperationResult()
}
