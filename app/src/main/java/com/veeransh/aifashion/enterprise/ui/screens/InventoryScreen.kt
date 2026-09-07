package com.veeransh.aifashion.enterprise.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun InventoryScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToHistory: (String) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val stockOpResult by viewModel.stockOpResult.collectAsState()
    val context = LocalContext.current

    var selectedProductForAdjustment by remember { mutableStateOf<ProductEntity?>(null) }
    
    val filteredProducts = remember(products, searchQuery) {
        products.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.sku.contains(searchQuery, ignoreCase = true) ||
            it.barcode.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(stockOpResult) {
        stockOpResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "Stock adjusted successfully", Toast.LENGTH_SHORT).show()
                selectedProductForAdjustment = null
            } else {
                Toast.makeText(context, "Error: ${it.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
            viewModel.clearStockOpResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1E8)) // Beige BG
            .padding(16.dp)
    ) {
        Text(
            text = "INVENTORY MASTER", 
            fontSize = 14.sp, 
            fontWeight = FontWeight.Black, 
            color = Color(0xFF0D5C36)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by Name, SKU, Barcode...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { viewModel.updateSearchQuery("") }) { Icon(Icons.Default.Close, contentDescription = null) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF0D5C36)
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for bottom nav
        ) {
            items(filteredProducts) { product ->
                InventoryItemRow(
                    product = product,
                    onAdjust = { selectedProductForAdjustment = product },
                    onHistory = { onNavigateToHistory(product.id) }
                )
            }
        }
    }

    if (selectedProductForAdjustment != null) {
        StockAdjustmentDialog(
            product = selectedProductForAdjustment!!,
            onDismiss = { selectedProductForAdjustment = null },
            onSave = { type, qty, reason ->
                viewModel.adjustStock(selectedProductForAdjustment!!.id, type, qty, reason)
            }
        )
    }
}

@Composable
fun InventoryItemRow(
    product: ProductEntity,
    onAdjust: () -> Unit,
    onHistory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image (4:5)
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .size(width = 80.dp, height = 100.dp) // 4:5 ratio approx
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // SKU Badge
                Surface(
                    color = Color(0xFFE9C46A), // Yellow Accent
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = product.sku,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stock: ${product.stock}",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = if (product.stock < 5) Color.Red else Color(0xFF0D5C36)
                    )
                    
                    if (product.stock < 5) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                    }
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = onAdjust,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Adjust", fontSize = 11.sp)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onHistory,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text("History", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun StockAdjustmentDialog(
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSave: (String, Int, String) -> Unit
) {
    var type by remember { mutableStateOf("ADJUSTMENT_ADD") }
    var qtyText by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adjust Stock: ${product.sku}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Current Stock: ${product.stock}", fontWeight = FontWeight.Bold)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == "ADJUSTMENT_ADD", onClick = { type = "ADJUSTMENT_ADD" })
                    Text("Add Stock", modifier = Modifier.padding(end = 16.dp))
                    RadioButton(selected = type == "ADJUSTMENT_SUB", onClick = { type = "ADJUSTMENT_SUB" })
                    Text("Remove Stock")
                }
                
                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason (Required)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = qtyText.toIntOrNull() ?: 0
                    if (qty > 0 && reason.isNotBlank()) {
                        onSave(type, qty, reason)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A0C20))
            ) {
                Text("Save Adjustment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
