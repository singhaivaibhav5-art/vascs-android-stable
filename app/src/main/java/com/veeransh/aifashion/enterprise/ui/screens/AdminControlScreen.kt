package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veeransh.aifashion.enterprise.ui.theme.VeeranshTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminControlScreen(
    onBack: () -> Unit
) {
    val maroon = Color(0xFF7A0C20)
    val lightMaroon = Color(0xFFFFF6F6)
    val borderMaroon = Color(0xFFFFE0E0)
    val softBg = Color(0xFFF5F1E8)
    
    var showSuspendToast by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Admin • COD • Suspend + Policies", fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Badge(containerColor = Color(0xFFFEFEFE), contentColor = Color.Black) { Text("LIGHT THEME #FEFEFE", modifier = Modifier.padding(4.dp)) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(containerColor = Color(0xFF0D5C36), contentColor = Color.White) { Text("LIVE BUILD", modifier = Modifier.padding(4.dp)) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(containerColor = Color(0xFF5B4CFF), contentColor = Color.White) { Text("Well Packed Flow", modifier = Modifier.padding(4.dp)) }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = softBg)
            )
        },
        containerColor = softBg,
        bottomBar = {
            AdminControlBottomBar()
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                
                item {
                    CodSettingsCard(maroon)
                }
                
                item {
                    UserSuspendSection(
                        maroon = maroon,
                        onSuspend = { name ->
                            showSuspendToast = "$name removed → Well Packed"
                        }
                    )
                }
                
                item {
                    SecurityPolicyCard(maroon)
                }
            }
            
            // Suspend Toast
            showSuspendToast?.let { msg ->
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(3000)
                    showSuspendToast = null
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp),
                    color = maroon,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 8.dp
                ) {
                    Text(
                        text = msg,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CodSettingsCard(maroon: Color) {
    var isCodEnabled by remember { mutableStateOf(true) }
    var minCod by remember { mutableStateOf("0") }
    var maxCod by remember { mutableStateOf("5000") }
    var codCharge by remember { mutableStateOf("50") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("COD SETTINGS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable COD", fontSize = 14.sp)
                    Switch(
                        checked = isCodEnabled,
                        onCheckedChange = { isCodEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = maroon, checkedTrackColor = maroon.copy(alpha = 0.5f))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = minCod,
                    onValueChange = { minCod = it },
                    label = { Text("Min COD Amount") },
                    modifier = Modifier.weight(1f),
                    prefix = { Text("₹") }
                )
                OutlinedTextField(
                    value = maxCod,
                    onValueChange = { maxCod = it },
                    label = { Text("Max COD Amount") },
                    modifier = Modifier.weight(1f),
                    prefix = { Text("₹") }
                )
                OutlinedTextField(
                    value = codCharge,
                    onValueChange = { codCharge = it },
                    label = { Text("COD Charge") },
                    modifier = Modifier.weight(0.8f),
                    prefix = { Text("₹") }
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text("RESTRICTED LISTS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Premium Deals", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Pincodes list +/-", fontSize = 14.sp)
                IconButton(onClick = {}) { Icon(Icons.Default.AddCircle, contentDescription = null, tint = maroon) }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Disabled Categories:", fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = true, onCheckedChange = {}, colors = CheckboxDefaults.colors(checkedColor = maroon))
                    Text("Premium Silk", fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = false, onCheckedChange = {}, colors = CheckboxDefaults.colors(checkedColor = maroon))
                    Text("Bridal Wear", fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Stock 0 → hidden from gallery (web + Android). Admin filter Out of Stock. COD toggle ON, Max ₹, Charge editable, Pincodes list +/-, Disabled Categories.",
                fontSize = 11.sp,
                color = Color.Gray,
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = maroon),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save COD Configuration", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UserSuspendSection(maroon: Color, onSuspend: (String) -> Unit) {
    val users = listOf(
        AdminUser("Rohit Dealer", "Dealer", "Active"),
        AdminUser("Meena S", "Style Partner", "Active"),
        AdminUser("Jaipur Silks", "Dealer", "Suspended")
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("USER SUSPENSION MANAGEMENT", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Table Header
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC)).padding(12.dp)) {
                Text("User", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("Role", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("Status", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("Action", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
            
            users.forEach { user ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(user.name, modifier = Modifier.weight(2f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(user.role, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
                    Text(
                        text = user.status,
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        color = if (user.status == "Active") Color(0xFF10B981) else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Box(modifier = Modifier.weight(2f), contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFFFB7B7), RoundedCornerShape(8.dp))
                                .clickable { onSuspend(user.name) },
                            color = Color(0xFFFFE9E9)
                        ) {
                            Text(
                                text = "Suspend/Block without reason",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = maroon,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                Divider(color = Color(0xFFF1F5F9))
            }
        }
    }
}

@Composable
fun SecurityPolicyCard(maroon: Color) {
    var agreeTerms by remember { mutableStateOf(false) }
    val policies = listOf(
        "Login via OTP • No Password",
        "Firebase Rules strict",
        "App Check enabled",
        "Razorpay webhook verify signature",
        "PII encryption at rest",
        "Suspension Policy",
        "Style Partner Commission Policy",
        "No Return/No Exchange except defect 24h",
        "OTP 4829 at delivery"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("LOGIN SCREEN • OTP ONLY + MANDATORY TERMS CHECKBOX", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            policies.forEach { policy ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(policy, fontSize = 13.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { agreeTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = maroon)
                )
                Text(
                    text = "I agree that admin can suspend or block my ID without reason as per Terms + No Return No Exchange policy • OTP login",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun AdminControlBottomBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "VEERANSH AI FASHION V3 — Move to Display = Full Product Detail • Product Coupon checkbox ₹1000→₹950 • Cart coupon radio EXTRA5 min 999 • Stock 0 hidden • COD editable • Suspend/Block without reason • Well Packed • eWallet 7-day blur • OTP-only • No Return/No Exchange",
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Razorpay • Firebase • NanoBanana AI Drape", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(modifier = Modifier.weight(1f))
                Text("Admin Console V3.2", fontSize = 10.sp, color = Color.LightGray)
            }
        }
    }
}

data class AdminUser(val name: String, val role: String, val status: String)

@Preview(name = "Admin Control Preview", device = "spec:width=1280dp,height=800dp", showBackground = true)
@Composable
fun AdminControlPreview() {
    VeeranshTheme {
        AdminControlScreen(onBack = {})
    }
}
