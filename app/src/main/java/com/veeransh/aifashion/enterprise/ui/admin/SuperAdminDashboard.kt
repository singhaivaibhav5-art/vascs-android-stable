package com.veeransh.aifashion.enterprise.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.SuperAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDashboard(
    navController: NavController,
    viewModel: SuperAdminViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isUnlocked by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // FINAL 15 TABS - As per VASCS MASTER Blueprint
    val tabs = listOf(
        "Login", "PIN", "Roles", "Users", "Perms", 
        "COD", "Payment", "Display", "Coupon", "Pincode", 
        "Return", "AI", "B2B", "System", "Logs"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("VASCS MASTER - Super Admin V4.0", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D5C36),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            if(isUnlocked) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("ai_drape_studio") },
                    containerColor = Color(0xFF0D5C36),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    text = { Text("AI Drape Studio") }
                )
            }
        }
    ) { padding ->
        if (!isUnlocked) {
            // TAB 0: Admin Login - PIN 2026 Security
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F1E8)), contentAlignment = Alignment.Center) {
                Card(modifier = Modifier.padding(24.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFF0D5C36))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("VASCS MASTER", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFF0D5C36))
                        Text("Enter Super Admin PIN", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = { if(it.length <= 4) enteredPin = it },
                            label = { Text("PIN - Default 2026") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if(enteredPin == "2026") { isUnlocked = true }
                                else { scope.launch { snackbarHostState.showSnackbar("Wrong PIN! Use 2026") } }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))
                        ) { Text("UNLOCK MASTER") }
                    }
                }
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 8.dp,
                    containerColor = Color.White,
                    contentColor = Color(0xFF0D5C36),
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontSize = 11.sp, maxLines = 1, fontWeight = if(selectedTabIndex==index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F1E8))) {
                    when (selectedTabIndex) {
                        0 -> AdminLoginTab(isUnlocked = true)
                        1 -> PinTab(viewModel, snackbarHostState)
                        2 -> RolesTab()
                        3 -> UsersTab(viewModel)
                        4 -> PermissionsTab()
                        5 -> CODTab(viewModel, snackbarHostState)
                        6 -> PaymentTab()
                        7 -> DisplayTab(viewModel)
                        8 -> CouponTab(snackbarHostState)
                        9 -> PincodeTab(snackbarHostState)
                        10 -> ReturnTab()
                        11 -> AITab(viewModel)
                        12 -> B2BTab()
                        13 -> SystemTab(viewModel, snackbarHostState)
                        14 -> LogsTab(viewModel)
                    }
                }
            }
        }
    }
}

// TAB 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14 implementations
@Composable fun AdminLoginTab(isUnlocked: Boolean) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){ Text(if(isUnlocked) "✓ Unlocked as Super Admin (PIN 2026)" else "Locked", color = Color(0xFF0D5C36), fontWeight = FontWeight.Bold) } }

@Composable
fun PinTab(viewModel: SuperAdminViewModel, snackbarHostState: SnackbarHostState) {
    val adminPin by viewModel.adminPin.collectAsState()
    var pinText by remember(adminPin) { mutableStateOf(adminPin) }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Change Security PIN", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                OutlinedTextField(value = pinText, onValueChange = { pinText = it }, label = { Text("New PIN") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Button(onClick = { viewModel.updateAdminPin(pinText); scope.launch { snackbarHostState.showSnackbar("PIN Updated: $pinText") } }, modifier = Modifier.align(Alignment.End).padding(top=8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))) { Text("Save PIN") }
            }
        }
    }
}

@Composable fun RolesTab() {
    var selected by remember { mutableStateOf("Super Admin") }
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Roles Management", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                listOf("Super Admin", "Admin", "Dealer", "Staff", "Customer").forEach { role ->
                    Row(verticalAlignment = Alignment.CenterVertically){ RadioButton(selected = selected==role, onClick = {selected=role}); Text(role) }
                }
            }
        }
    }
}

@Composable fun UsersTab(viewModel: SuperAdminViewModel) {
    val users = listOf("Dealer - Jaipur (Approved)", "Dealer - Surat (Pending)", "Staff - Ramesh (Active)")
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(users) { u ->
            Card(modifier = Modifier.padding(vertical=4.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(u, modifier = Modifier.weight(1f))
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Block") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))) { Text("Approve") }
                }
            }
        }
    }
}

@Composable fun PermissionsTab() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Permissions by Role", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                SystemSwitch("Dealer can see Price", true) {}
                SystemSwitch("Staff can Edit Product", false) {}
                SystemSwitch("Customer can see Stock", false) {}
                SystemSwitch("Dealer Wallet Visible", true) {}
            }
        }
    }
}

@Composable
fun CODTab(viewModel: SuperAdminViewModel, snackbarHostState: SnackbarHostState) {
    val enableCod by viewModel.enableCod.collectAsState()
    val minCod by viewModel.minCod.collectAsState()
    val maxCod by viewModel.maxCod.collectAsState()
    val codCharge by viewModel.codCharge.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Enable COD Service", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Switch(checked = enableCod, onCheckedChange = { viewModel.updateCodSettings(it, minCod, maxCod, codCharge) })
                }
                OutlinedTextField(value = minCod.toString(), onValueChange = {}, label = { Text("Min ₹") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = maxCod.toString(), onValueChange = {}, label = { Text("Max ₹") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = codCharge.toString(), onValueChange = {}, label = { Text("COD Charge ₹50") }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable fun PaymentTab() {
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Razorpay & Payment", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                OutlinedTextField(value = "rzp_live_xxxx", onValueChange = {}, label = { Text("Key ID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "****", onValueChange = {}, label = { Text("Key Secret") }, modifier = Modifier.fillMaxWidth())
                SystemSwitch("Prepaid Only", false) {}
                SystemSwitch("Allow COD + Prepaid", true) {}
            }
        }
    }
}

@Composable fun DisplayTab(viewModel: SuperAdminViewModel) {
    val products by viewModel.products.collectAsState()
    LazyColumn { items(products) { p -> Card(modifier = Modifier.padding(8.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)){ Row(modifier = Modifier.padding(16.dp)){ Column(modifier = Modifier.weight(1f)){ Text(p.name, fontWeight = FontWeight.Bold); Text("SKU: ${p.sku} | Stock: ${p.stock}") } } } } }
}

@Composable fun CouponTab(snackbarHostState: SnackbarHostState) {
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Coupons & Offers", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                Row(verticalAlignment = Alignment.CenterVertically){ Text("Flat 5% Off", modifier = Modifier.weight(1f)); Switch(checked = true, onCheckedChange = {}) }
                OutlinedTextField(value = "999", onValueChange = {}, label = { Text("Min Cart Value") }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable fun PincodeTab(snackbarHostState: SnackbarHostState) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Add Pincode - 6 digits") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {}, modifier = Modifier.fillMaxWidth().padding(top=8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))) { Text("Add") }
        Text("Serviceable: 302001, 302020, 110001", modifier = Modifier.padding(top=16.dp))
    }
}

@Composable fun ReturnTab() {
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Return & Refund Policy", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                SystemSwitch("No Return - Saree", true) {}
                SystemSwitch("7 Days Return - Defective Only", true) {}
                OutlinedTextField(value = "3", onValueChange = {}, label = { Text("Return Window Days") }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable fun AITab(viewModel: SuperAdminViewModel) {
    val aiEnabled by viewModel.bananaAiEnabled.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AI Configuration - Gemini + Catalogue", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                SystemSwitch("Enable Banana AI", aiEnabled) { viewModel.updateBananaAiEnabled(it) }
                SystemSwitch("Enable Gemini Drape", true) {}
                SystemSwitch("Enable Catalogue Factory", true) {}
                SystemSwitch("Auto Generate QR + Price", true) {}
            }
        }
    }
}

@Composable fun B2BTab() {
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("B2B Dealer Network", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                SystemSwitch("Special B2B Pricing", true) {}
                SystemSwitch("Wallet Blur - 7 Days", true) {}
                SystemSwitch("Dealer Approval Needed", true) {}
                SystemSwitch("Show Margin to Dealer", false) {}
            }
        }
    }
}

@Composable fun SystemTab(viewModel: SuperAdminViewModel, snackbarHostState: SnackbarHostState) {
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("System", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                SystemSwitch("Well Packed Badge", true) {}
                SystemSwitch("OTP Login", true) {}
                SystemSwitch("Omega Intelligence", true) {}
                SystemSwitch("Autonomous Commerce", false) {}
            }
        }
    }
}

@Composable fun LogsTab(viewModel: SuperAdminViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Activity Logs - AdminConfig + Audit", fontWeight = FontWeight.Bold, color = Color(0xFF0D5C36))
                Text("• 01 Sep - PIN changed to 2026", fontSize = 12.sp)
                Text("• 01 Sep - Dealer Jaipur Approved", fontSize = 12.sp)
                Text("• 01 Sep - COD Enabled ₹500-20000", fontSize = 12.sp)
                Text("• 01 Sep - AI Catalogue Generated 15", fontSize = 12.sp)
            }
        }
    }
}

@Composable fun SystemSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical=4.dp)) {
        Text(label, modifier = Modifier.weight(1f), fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
