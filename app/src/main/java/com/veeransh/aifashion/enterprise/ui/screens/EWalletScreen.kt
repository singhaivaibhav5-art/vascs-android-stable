package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veeransh.aifashion.enterprise.ui.theme.VeeranshTheme
import com.veeransh.aifashion.enterprise.ui.viewmodel.WalletViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.veeransh.aifashion.enterprise.data.local.entity.WalletTransactionEntity
import com.veeransh.aifashion.enterprise.data.local.entity.DealerWalletEntity

@Composable
fun EWalletScreen(
    viewModel: WalletViewModel = hiltViewModel()
) {
    val wallet by viewModel.wallet.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    
    EWalletScreenContent(
        wallet = wallet,
        transactions = transactions
    )
}

@Composable
fun EWalletScreenContent(
    wallet: DealerWalletEntity?,
    transactions: List<WalletTransactionEntity>
) {
    val brandBg = Color(0xFFF5F1E8)
    val maroon = Color(0xFF7A0C20)
    
    val totalBalance = wallet?.balanceAvailable ?: 0.0
    val isAnyReturnPeriodActive = transactions.any { it.status == "PENDING" }

    Scaffold(
        containerColor = brandBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "Style Partner eWallet • 7-Day Blur Logic",
                fontSize = 28.sp,
                fontFamily = FontFamily.Serif,
                color = Color(0xFF2A1A1D)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // LEFT COLUMN
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    BalanceCard(
                        balance = totalBalance,
                        isBlurred = isAnyReturnPeriodActive,
                        maroon = maroon
                    )
                    
                    TransactionsTable(
                        transactions = transactions
                    )
                }
                
                // RIGHT COLUMN
                Column(
                    modifier = Modifier.width(420.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    BankDetailsCard()
                    CommissionFlowCard()
                }
            }
        }
    }
}

@Composable
fun BalanceCard(
    balance: Double,
    isBlurred: Boolean,
    maroon: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EWALLET BALANCE",
                    fontSize = 11.sp,
                    letterSpacing = 0.2.sp,
                    modifier = Modifier.alpha(0.7f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Veeransh Dealer",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "₹${"%,.2f".format(balance)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2A1A1D)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isBlurred) {
                Surface(
                    color = Color(0xFFFFE9A8),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Redeem button blurred • Return period active",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF856404)
                    )
                }
            } else {
                Surface(
                    color = Color(0xFFE8FFE9),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB7E8C0))
                ) {
                    Text(
                        text = "Available now • Return period over • Ready to redeem",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF155724)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Redeem */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .alpha(if (isBlurred) 0.4f else 1f),
                enabled = !isBlurred,
                colors = ButtonDefaults.buttonColors(
                    containerColor = maroon,
                    disabledContainerColor = maroon
                ),
                shape = CircleShape
            ) {
                Text("Redeem Now", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun TransactionsTable(
    transactions: List<WalletTransactionEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val headers = listOf("Code", "Type", "Value", "Status", "Date")
                headers.forEach { header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
            
            HorizontalDivider(color = Color(0xFFECECEC))
            
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(transactions) { txn ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(txn.orderId, modifier = Modifier.weight(1f), fontSize = 13.sp)
                        Text(txn.type, modifier = Modifier.weight(1f), fontSize = 13.sp)
                        Text("₹${txn.amount}", modifier = Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = if (txn.status == "AVAILABLE") Color(0xFFE8FFE9) else Color(0xFFFFF3CD),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = txn.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (txn.status == "AVAILABLE") Color(0xFF155724) else Color(0xFF856404)
                                )
                            }
                        }
                        
                        val date = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault()).format(java.util.Date(txn.timestamp))
                        Text(date, modifier = Modifier.weight(1f), fontSize = 13.sp)
                    }
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                }
            }
        }
    }
}

@Composable
fun BankDetailsCard() {
    var accountNo by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "BANK DETAILS • KYC",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.1.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Account Holder Name", fontSize = 11.sp, color = Color.Gray)
            Text("Veeransh Dealer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = accountNo,
                onValueChange = { accountNo = it },
                label = { Text("Account No", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = ifsc,
                onValueChange = { ifsc = it },
                label = { Text("IFSC Code", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                supportingText = {
                    if (ifsc.length >= 4) {
                        Text("Bank: HDFC Bank Limited", fontSize = 10.sp, color = Color(0xFF0D5C36))
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text("UPI ID", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Editable but needs re-verification",
                fontSize = 10.sp,
                modifier = Modifier.alpha(0.6f)
            )
        }
    }
}

@Composable
fun CommissionFlowCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6F6)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0E0))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF7A0C20), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("COMMISSION FLOW", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7A0C20))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "If return → pending deducted. Commission credited immediate but PENDING for 7 days. After 7 days, it moves to AVAILABLE balance.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF7A0C20).copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Wireframe Note: Logic ensures funds are only redeemable after the customer return window expires.",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}
