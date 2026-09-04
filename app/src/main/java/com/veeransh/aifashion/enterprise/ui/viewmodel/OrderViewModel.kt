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

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    val orders: StateFlow<List<OrderMasterEntity>> = orderRepository.allOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createOrder(order: OrderMasterEntity, items: List<OrderItemEntity>) {
        viewModelScope.launch {
            orderRepository.createOrder(order, items)
        }
    }

    fun updateOrderStatus(order: OrderMasterEntity) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(order)
        }
    }
}
