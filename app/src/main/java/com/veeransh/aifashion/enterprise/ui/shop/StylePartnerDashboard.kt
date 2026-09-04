package com.veeransh.aifashion.enterprise.ui.shop

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.veeransh.aifashion.enterprise.R
import com.veeransh.aifashion.enterprise.data.local.entity.DealerWalletEntity
import com.veeransh.aifashion.enterprise.data.local.entity.WalletTransactionEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.WalletViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StylePartnerDashboard(
    viewModel: WalletViewModel = hiltViewModel()
) {
    val wallet by viewModel.wallet.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    StylePartnerDashboardContent(wallet = wallet, transactions = transactions)
}

@Composable
fun StylePartnerDashboardContent(
    wallet: DealerWalletEntity?,
    transactions: List<WalletTransactionEntity> = emptyList()
) {
    val isAnyReturnPeriodActive = transactions.any { it.status == "PENDING" }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFEFEFE))) {
        // Watermark background
        Image(
            painter = painterResource(id = R.drawable.brand_name),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.08f),
            contentScale = ContentScale.Inside
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Style Partner eWallet",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0A5C36)
                )
            }

            item {
                BalanceCard(
                    available = wallet?.balanceAvailable ?: 0.0,
                    isBlurred = isAnyReturnPeriodActive,
                    onRedeem = { /* Redeem logic */ }
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        TransactionsTable(transactions)
                    }
                    Column(modifier = Modifier.width(360.dp)) {
                        BankDetailsCard()
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceCard(
    available: Double,
    isBlurred: Boolean,
    onRedeem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EWALLET BALANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text = "Veeransh Dealer",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "₹${"%,.2f".format(available)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2A1A1D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = if (isBlurred) Color(0xFFFFE9A8) else Color(0xFFE8FFE9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isBlurred) "Redeem button blurred • Return period active" 
                           else "Available now • Ready to redeem",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = if (isBlurred) Color(0xFF856404) else Color(0xFF155724)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRedeem,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .alpha(if (isBlurred) 0.4f else 1f),
                enabled = !isBlurred,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                shape = CircleShape
            ) {
                Text("Redeem Now", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun TransactionsTable(transactions: List<WalletTransactionEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("TRANSACTIONS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text("Code", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("Type", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("Value", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("Status", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("Date", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
            }
            
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            
            transactions.forEach { txn ->
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(txn.orderId, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
                    Text(txn.type.replace("_", " ").uppercase(), modifier = Modifier.weight(1.5f), fontSize = 11.sp)
                    Text("₹${txn.amount}", modifier = Modifier.weight(1.2f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    
                    Box(modifier = Modifier.weight(1.5f)) {
                        val statusBg = if (txn.status == "AVAILABLE") Color(0xFFE8FFE9) else Color(0xFFFFF3CD)
                        val statusText = if (txn.status == "AVAILABLE") Color(0xFF155724) else Color(0xFF856404)
                        Surface(color = statusBg, shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = txn.status,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusText
                            )
                        }
                    }
                    Text(sdf.format(Date(txn.timestamp)), modifier = Modifier.weight(1.5f), fontSize = 12.sp)
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
            Text("BANK DETAILS • KYC", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Account Holder Name", fontSize = 11.sp, color = Color.Gray)
            Text("Veeransh Dealer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = accountNo,
                onValueChange = { accountNo = it },
                label = { Text("Account No") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = ifsc,
                onValueChange = { ifsc = it },
                label = { Text("IFSC Code") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if (ifsc.length >= 4) {
                        Text("Bank: Veeransh State Bank", color = Color(0xFF0D5C36), fontSize = 10.sp)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text("UPI ID") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("Editable but needs re-verification", fontSize = 10.sp, color = Color.Gray)
        }
    }
}
