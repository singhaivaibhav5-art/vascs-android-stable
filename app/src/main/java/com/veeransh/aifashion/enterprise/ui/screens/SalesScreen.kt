package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.HomeViewModel
import com.veeransh.aifashion.enterprise.types.CartItem
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SalesScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onGenerateBill: (String, List<CartItem>, Double) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val products by viewModel.products.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    var showSuccessDialog by remember { mutableStateOf<String?>(null) }
    
    val subtotal = cartItems.sumOf { it.product.retailPrice * it.qty }
    val gst = subtotal * 0.05
    val total = subtotal + gst

    Row(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F1E8))) {
        // Left: Product List
        Column(modifier = Modifier.weight(1.5f).padding(16.dp)) {
            Text("BILLING - SELECT SAREES", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D5C36))
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(products) { product ->
                    val cartItem = cartItems.find { it.product.id == product.id }
                    val qty = cartItem?.qty ?: 0
                    
                    BillingProductCard(
                        product = product,
                        qtyInCart = qty,
                        onAdd = {
                            viewModel.addToCart(product, 1)
                        },
                        onRemove = {
                            if (cartItem != null) {
                                viewModel.updateCartQty(product.id, cartItem.qty - 1)
                            }
                        }
                    )
                }
            }
        }

        // Right: Bill Summary
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Text("BILL SUMMARY", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D5C36))
            Spacer(modifier = Modifier.height(24.dp))

            // Cart Items
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.product.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Qty: ${item.qty} x ₹${item.product.retailPrice}", fontSize = 10.sp, color = Color.Gray)
                        }
                        Text("₹${item.product.retailPrice * item.qty}", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        
                        IconButton(onClick = {
                            viewModel.updateCartQty(item.product.id, item.qty - 1)
                        }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFF1F5F9))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Calculations
            BillRow("Subtotal", "₹$subtotal")
            BillRow("GST (5%)", "₹$gst")
            Spacer(modifier = Modifier.height(8.dp))
            BillRow("NET TOTAL", "₹$total", isTotal = true)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (cartItems.isNotEmpty()) {
                        val orderId = "ORD-2026-${(1000..9999).random()}"
                        onGenerateBill(orderId, cartItems, total)
                        showSuccessDialog = orderId
                        // Note: In a real app, clear cart after order success
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36)),
                shape = RoundedCornerShape(12.dp),
                enabled = cartItems.isNotEmpty()
            ) {
                Text("GENERATE BILL", fontWeight = FontWeight.Black)
            }
        }
    }

    if (showSuccessDialog != null) {
        OrderSuccessDialog(
            orderId = showSuccessDialog!!,
            total = total,
            onDismiss = { showSuccessDialog = null }
        )
    }
}

@Composable
fun BillingProductCard(
    product: ProductEntity,
    qtyInCart: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("SKU: ${product.sku} | Stock: ${product.stock}", fontSize = 10.sp, color = Color.Gray)
                Text("₹${product.retailPrice}", fontWeight = FontWeight.Black, color = Color(0xFF0D5C36))
            }
            
            if (qtyInCart == 0) {
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    enabled = product.stock > 0
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = Color.Red)
                    }
                    Text(
                        text = qtyInCart.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    IconButton(
                        onClick = onAdd, 
                        modifier = Modifier.size(32.dp),
                        enabled = qtyInCart < product.stock
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color(0xFF0D5C36))
                    }
                }
            }
        }
    }
}

@Composable
fun BillRow(label: String, value: String, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = if (isTotal) FontWeight.Black else FontWeight.Normal, fontSize = if (isTotal) 16.sp else 12.sp)
        Text(value, fontWeight = if (isTotal) FontWeight.Black else FontWeight.Bold, fontSize = if (isTotal) 16.sp else 12.sp, color = if (isTotal) Color(0xFF0D5C36) else Color.DarkGray)
    }
}

@Composable
fun OrderSuccessDialog(orderId: String, total: Double, onDismiss: () -> Unit) {
    val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Order Placed Successfully", fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(24.dp))
                
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp)).padding(16.dp)) {
                    Text("Order ID: $orderId", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Date: $date", fontSize = 12.sp, color = Color.Gray)
                    Text("Total Amount: ₹$total", fontWeight = FontWeight.Black, color = Color(0xFF0D5C36), fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Close")
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                }
            }
        }
    }
}
