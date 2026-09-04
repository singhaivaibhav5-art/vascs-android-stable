package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veeransh.aifashion.enterprise.data.local.entity.OrderMasterEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.OrderViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OrdersScreen(
    viewModel: OrderViewModel = hiltViewModel(),
    onOrderClick: (OrderMasterEntity) -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    OrdersScreenContent(
        orders = orders,
        onOrderClick = onOrderClick
    )
}

@Composable
fun OrdersScreenContent(
    orders: List<OrderMasterEntity>,
    onOrderClick: (OrderMasterEntity) -> Unit
) {
    var selectedOrder by remember { mutableStateOf<OrderMasterEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1E8))
            .padding(16.dp)
    ) {
        Text(
            text = "ORDER HISTORY",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0D5C36)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Text("No orders placed yet.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(orders) { order ->
                    OrderSummaryCard(order = order, onClick = { 
                        selectedOrder = order 
                        onOrderClick(order)
                    })
                }
            }
        }
    }

    selectedOrder?.let {
        OrderDetailDialog(order = it, onDismiss = { selectedOrder = null })
    }
}

@Composable
fun OrderSummaryCard(order: OrderMasterEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.orderNumber,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0D5C36),
                    fontSize = 16.sp
                )
                Text(
                    text = order.orderDate,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${order.netAmount}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                val statusColor = when (order.status) {
                    "PENDING" -> Color(0xFFF59E0B)
                    "DISPATCHED", "SHIPPED" -> Color(0xFF3B82F6)
                    "DELIVERED" -> Color(0xFF10B981)
                    else -> Color.Gray
                }
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }
        }
    }
}

@Composable
fun OrderDetailDialog(
    order: OrderMasterEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Order Details: ${order.orderNumber}",
                fontWeight = FontWeight.Black,
                color = Color(0xFF0D5C36)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailRow("Date", order.orderDate)
                DetailRow("Status", order.status)
                DetailRow("Dealer", order.dealerName)
                DetailRow("Items Count", order.totalItems.toString())
                DetailRow("Total Quantity", order.totalQty.toString())
                HorizontalDivider()
                DetailRow("Subtotal", "₹${order.totalAmount}")
                DetailRow("GST", "₹${order.gstAmount}")
                DetailRow("Net Amount", "₹${order.netAmount}", isBold = true)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))
            ) {
                Text("Close")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { /* Mock Print */ }) {
                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Print Invoice")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
fun DetailRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(
            text = value,
            fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isBold) Color(0xFF0D5C36) else Color.DarkGray
        )
    }
}
