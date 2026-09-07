package com.veeransh.aifashion.enterprise.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.veeransh.aifashion.enterprise.data.repository.OrderRepository
import com.veeransh.aifashion.enterprise.data.local.entity.OrderItemEntity
import com.veeransh.aifashion.enterprise.data.local.entity.OrderMasterEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import com.veeransh.aifashion.enterprise.types.CartItem
import com.veeransh.aifashion.enterprise.types.UserPDPCouponItem
import com.veeransh.aifashion.enterprise.util.FinancialCalculator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val orderState = _orderState.asStateFlow()

    val orders: StateFlow<List<OrderMasterEntity>> = orderRepository.allOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun placeOrder(
        orderNumber: String, 
        cartItems: List<CartItem>, 
        cartCoupon: UserPDPCouponItem? = null,
        onComplete: (Result<Long>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _orderState.value = OrderState.Loading
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateStr = dateFormat.format(Date())

            val calc = FinancialCalculator.calculate(cartItems, cartCoupon)

            val order = OrderMasterEntity(
                orderNumber = orderNumber,
                dealerId = "DLR-001", // Default for now
                dealerName = "Self Retail",
                mobile = "9876543210",
                whatsapp = "9876543210",
                orderDate = dateStr,
                totalItems = cartItems.size,
                totalQty = cartItems.sumOf { it.qty },
                totalAmount = calc.taxableAmount, // Authorsitative Taxable Amount
                gstAmount = calc.gstAmount,
                netAmount = calc.netAmount,
                createdDate = dateStr,
                updatedDate = dateStr
            )

            // Distribute cart-level discount proportionally across items to keep records consistent
            val totalGrossSubtotal = calc.grossSubtotal
            
            val orderItems = cartItems.map { item ->
                val itemSubtotalAfterProductDiscount = (item.product.retailPrice - when (item.appliedCoupon?.type) {
                    "PERCENT" -> (item.product.retailPrice * (item.appliedCoupon.value / 100.0)).roundToInt().toDouble()
                    "FIXED" -> item.appliedCoupon.value
                    else -> 0.0
                }) * item.qty
                
                // Proportion of cart discount for this item
                val itemCartDiscount = if (totalGrossSubtotal > 0) {
                    (itemSubtotalAfterProductDiscount / totalGrossSubtotal) * calc.cartDiscount
                } else 0.0
                
                val itemTaxableAmount = itemSubtotalAfterProductDiscount - itemCartDiscount
                
                // Use Product's GST rate for snapshotting (Phase 3.2)
                val itemGstRate = max(0.0, item.product.gst)
                val itemGstAmount = (itemTaxableAmount * (itemGstRate / 100.0)).roundToInt().toDouble()
                val itemNetAmount = itemTaxableAmount + itemGstAmount
                
                OrderItemEntity(
                    orderId = 0L,
                    productId = item.product.id,
                    productName = item.product.name,
                    sku = item.product.sku,
                    qty = item.qty,
                    rate = if (item.qty > 0) (itemTaxableAmount / item.qty) else 0.0, // Fully discounted rate
                    amount = itemTaxableAmount,
                    taxRate = itemGstRate, // Snapshotted rate
                    gst = itemGstAmount,
                    netAmount = itemNetAmount
                )
            }

            val result = orderRepository.createOrder(order, orderItems)
            if (result.isSuccess) {
                _orderState.value = OrderState.Success(result.getOrNull() ?: 0L)
            } else {
                _orderState.value = OrderState.Error(result.exceptionOrNull()?.message ?: "Order placement failed")
            }
            onComplete(result)
        }
    }

    fun resetOrderState() {
        _orderState.value = OrderState.Idle
    }

    fun updateOrderStatus(order: OrderMasterEntity) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(order)
        }
    }
}

sealed class OrderState {
    object Idle : OrderState()
    object Loading : OrderState()
    data class Success(val orderId: Long) : OrderState()
    data class Error(val message: String) : OrderState()
}
