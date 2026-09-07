package com.veeransh.aifashion.enterprise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.repository.ProductRepository
import com.veeransh.aifashion.enterprise.data.repository.PlacementRepository
import com.veeransh.aifashion.enterprise.data.repository.StockRepository
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.data.local.entity.PlacementEntity
import com.veeransh.aifashion.enterprise.types.CartItem
import com.veeransh.aifashion.enterprise.types.UserPDPCouponItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val placementRepository: PlacementRepository,
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val products: StateFlow<List<ProductEntity>> = productRepository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(products, _searchQuery) { list, query ->
        if (query.isEmpty()) list
        else list.filter {
            it.name.contains(query, ignoreCase = true) || it.sku.contains(query, ignoreCase = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Placement Data
    val activePlacements: StateFlow<List<PlacementWithProduct>> = combine(
        placementRepository.observeAllActivePlacements(),
        products
    ) { placements, productList ->
        val currentTime = System.currentTimeMillis()
        placements
            .filter { it.status == "Published" }
            .filter { isEligible(it, currentTime) }
            .mapNotNull { placement ->
                productList.find { it.id == placement.productId }?.let { product ->
                    PlacementWithProduct(placement, product)
                }
            }
            .sortedWith(compareBy({ it.placement.priority }, { it.placement.sortOrder }, { it.placement.placementId }))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private fun isEligible(placement: PlacementEntity, currentTime: Long): Boolean {
        val startAt = placement.startAt
        val endAt = placement.endAt
        
        if (startAt == null && endAt == null) return true
        if (startAt != null && currentTime < startAt) return false
        if (endAt != null && currentTime > endAt) return false
        return true
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    fun addToCart(product: ProductEntity, qty: Int, coupon: UserPDPCouponItem? = null) {
        val current = _cartItems.value.toMutableList()
        val existing = current.find { it.product.id == product.id }
        if (existing != null) {
            val index = current.indexOf(existing)
            current[index] = existing.copy(qty = existing.qty + qty, appliedCoupon = coupon ?: existing.appliedCoupon)
        } else {
            current.add(CartItem(product, qty, coupon))
        }
        _cartItems.value = current
    }

    fun updateCartQty(productId: String, newQty: Int) {
        val current = _cartItems.value.toMutableList()
        val item = current.find { it.product.id == productId }
        if (item != null) {
            val index = current.indexOf(item)
            if (newQty <= 0) current.removeAt(index)
            else current[index] = item.copy(qty = newQty)
        }
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun addSampleIfEmpty() {
        viewModelScope.launch {
            val currentList = productRepository.allProducts.first()
            if (currentList.isEmpty()) {
                val sampleProduct = ProductEntity(
                    id = "VEER-P-2026-0001",
                    name = "Pure Banarasi Silk Saree",
                    sku = "VEER-SKU-BAN-0001",
                    barcode = "8908001123456",
                    category = "Banarasi",
                    brand = "Veeransh",
                    fabric = "Pure Katan Silk",
                    colour = "Royal Blue",
                    size = "Free Size",
                    hsn = "5007",
                    gst = 5.0,
                    purchasePrice = 1500.0,
                    wholesalePrice = 2000.0,
                    retailPrice = 2499.0,
                    mrp = 3500.0,
                    discount = 0.0,
                    stock = 10,
                    image = "",
                    createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
                saveProduct(sampleProduct)
            }
        }
    }

    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            val initialStock = product.stock
            // Insert with 0 stock first, then adjust to create ledger entry
            productRepository.insertProduct(product.copy(stock = 0))
            if (initialStock > 0) {
                stockRepository.adjustStock(
                    productId = product.id,
                    adjustmentType = "ADJUSTMENT_ADD",
                    quantity = initialStock,
                    reason = "Opening stock"
                )
            }
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.updateProduct(product)
        }
    }

    private val _stockOpResult = MutableStateFlow<Result<Unit>?>(null)
    val stockOpResult = _stockOpResult.asStateFlow()

    fun adjustStock(
        productId: String,
        type: String,
        qty: Int,
        reason: String
    ) {
        viewModelScope.launch {
            val result = stockRepository.adjustStock(productId, type, qty, reason)
            _stockOpResult.value = result
        }
    }

    fun clearStockOpResult() {
        _stockOpResult.value = null
    }

    fun observeTransactions(productId: String) = stockRepository.observeTransactionsByProduct(productId)
    
    fun observeBalance(productId: String) = stockRepository.observeBalance(productId)
}

data class PlacementWithProduct(
    val placement: PlacementEntity,
    val product: ProductEntity
)
