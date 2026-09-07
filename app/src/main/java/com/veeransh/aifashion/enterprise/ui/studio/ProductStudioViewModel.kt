package com.veeransh.aifashion.enterprise.ui.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.data.repository.ProductRepository
import com.veeransh.aifashion.enterprise.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProductStudioViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository
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
                val newProd = createNewProduct()
                _uiState.value = ProductStudioUiState.Success(newProd, true)
            } else {
                val product = productRepository.getProductById(productId)
                if (product != null) {
                    isNewProduct = false
                    _uiState.value = ProductStudioUiState.Success(product, false)
                } else {
                    _uiState.value = ProductStudioUiState.Error("Product with ID $productId not found.")
                }
            }
        }
    }

    private suspend fun createNewProduct(): ProductEntity {
        val datePrefix = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        
        var generatedSku = ""
        var collision = true
        while (collision) {
            val suffix = (1000..9999).random().toString()
            generatedSku = "VS-$datePrefix-$suffix"
            collision = productRepository.existsBySku(generatedSku)
        }

        var generatedBarcode = ""
        collision = true
        while (collision) {
            // 13-digit business barcode (e.g. 890 + random)
            val randomBody = (1000000000L..9999999999L).random().toString()
            generatedBarcode = "890$randomBody"
            collision = productRepository.existsByBarcode(generatedBarcode)
        }

        return ProductEntity(
            id = UUID.randomUUID().toString(),
            name = "",
            sku = generatedSku,
            barcode = generatedBarcode,
            category = "Silk",
            brand = "Veeransh",
            createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
    }

    fun saveDraft(product: ProductEntity, andNavigate: Boolean = false) {
        viewModelScope.launch {
            try {
                if (isNewProduct) {
                    val initialStock = product.stock
                    // Insert with 0 stock first, then adjust to create ledger entry
                    productRepository.insertProduct(product.copy(stock = 0))
                    if (initialStock > 0) {
                        stockRepository.adjustStock(
                            productId = product.id,
                            adjustmentType = "ADJUSTMENT_ADD",
                            quantity = initialStock,
                            reason = "Initial opening stock"
                        )
                    }
                    isNewProduct = false
                } else {
                    productRepository.updateProduct(product)
                }
                _saveResult.value = SaveOperationResult.Success(product.id, andNavigate)
            } catch (e: Exception) {
                _saveResult.value = SaveOperationResult.Failure(e.message ?: "Database error")
            }
        }
    }

    fun adjustStock(productId: String, type: String, qty: Int, reason: String) {
        viewModelScope.launch {
            val result = stockRepository.adjustStock(productId, type, qty, reason)
            if (result.isSuccess) {
                _saveResult.value = SaveOperationResult.Success(productId, false)
                // Reload to reflect stock change
                loadProduct(productId)
            } else {
                _saveResult.value = SaveOperationResult.Failure(result.exceptionOrNull()?.message ?: "Adjustment failed")
            }
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }
}

sealed class ProductStudioUiState {
    object Loading : ProductStudioUiState()
    data class Success(val product: ProductEntity, val isNew: Boolean) : ProductStudioUiState()
    data class Error(val message: String) : ProductStudioUiState()
}

sealed class SaveOperationResult {
    data class Success(val productId: String, val shouldNavigate: Boolean) : SaveOperationResult()
    data class Failure(val message: String) : SaveOperationResult()
}
