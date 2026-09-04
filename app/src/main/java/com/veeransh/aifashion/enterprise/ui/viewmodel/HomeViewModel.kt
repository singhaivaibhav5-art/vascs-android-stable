package com.veeransh.aifashion.enterprise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.repository.ProductRepository
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
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
    private val productRepository: ProductRepository
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
                productRepository.insertProduct(sampleProduct)
            }
        }
    }

    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.insertProduct(product)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.updateProduct(product)
        }
    }
}
