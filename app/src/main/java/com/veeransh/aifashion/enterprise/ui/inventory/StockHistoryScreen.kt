package com.veeransh.aifashion.enterprise.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.data.local.entity.StockTransactionEntity
import com.veeransh.aifashion.enterprise.ui.screens.StockAdjustmentDialog
import com.veeransh.aifashion.enterprise.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockHistoryScreen(
    productId: String,
    viewModel: HomeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToOrder: (String) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val product = remember(products, productId) { products.find { it.id == productId } }
    
    val transactions by viewModel.observeTransactions(productId).collectAsState(initial = emptyList())
    val balance by viewModel.observeBalance(productId).collectAsState(initial = null)
    
    var showAdjustmentDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Ledger & History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F1E8)
    ) { padding ->
        if (product == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF0D5C36))
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding)) {
                // Product Summary Card
                ProductSummaryCard(
                    product = product,
                    ledgerBalance = balance?.totalStock ?: 0,
                    onAdjust = { showAdjustmentDialog = true }
                )

                if (transactions.isEmpty()) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No stock transactions yet.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(transactions) { tx ->
                            TransactionItemRow(tx, onNavigateToOrder)
                        }
                    }
                }
            }
        }
    }

    if (showAdjustmentDialog && product != null) {
        StockAdjustmentDialog(
            product = product,
            onDismiss = { showAdjustmentDialog = false },
            onSave = { type, qty, reason ->
                viewModel.adjustStock(product.id, type, qty, reason)
                showAdjustmentDialog = false
            }
        )
    }
}

@Composable
fun ProductSummaryCard(
    product: ProductEntity,
    ledgerBalance: Int,
    onAdjust: () -> Unit
) {
    val syncError = product.stock != ledgerBalance
    
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(product.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("SKU: ${product.sku}", fontSize = 12.sp, color = Color.Gray)
            
            Spacer(Modifier.height(16.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Available (Cache)", fontSize = 10.sp, color = Color.Gray)
                    Text("${product.stock}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = if(syncError) Color.Red else Color(0xFF0D5C36))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Ledger Balance", fontSize = 10.sp, color = Color.Gray)
                    Text("$ledgerBalance", fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }
            
            if (syncError) {
                Surface(
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
                ) {
                    Text(
                        "STOCK SYNC ERROR", 
                        modifier = Modifier.padding(8.dp),
                        color = Color.Red, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 11.sp
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = onAdjust,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Adjust Stock")
            }
        }
    }
}

@Composable
fun TransactionItemRow(tx: StockTransactionEntity, onOrderClick: (String) -> Unit) {
    val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(tx.createdAt))
    val isOut = tx.transactionType == "SALE" || tx.transactionType == "ADJUSTMENT_SUB"
    val color = if (isOut) Color(0xFF7A0C20) else Color(0xFF0D5C36)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(tx.transactionType, fontWeight = FontWeight.Black, fontSize = 11.sp, color = color)
                    Text(date, fontSize = 10.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${if(isOut) "-" else "+"}${tx.quantity}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
                    Text("Bal: ${tx.balanceAfter}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text("Ref: ${tx.referenceId}", fontSize = 11.sp, color = Color.Gray)
            
            if (tx.notes.isNotBlank()) {
                Text("Notes: ${tx.notes}", fontSize = 11.sp, color = Color.Gray)
            }
            
            if (tx.unitCost > 0) {
                Text("Unit Cost: ₹${tx.unitCost.toInt()}", fontSize = 10.sp, color = Color.LightGray)
            }
            
            if (tx.referenceType == "ORDER") {
                TextButton(
                    onClick = { onOrderClick(tx.referenceId) },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(20.dp).align(Alignment.End)
                ) {
                    Text("View Order", fontSize = 11.sp)
                }
            }
        }
    }
}
