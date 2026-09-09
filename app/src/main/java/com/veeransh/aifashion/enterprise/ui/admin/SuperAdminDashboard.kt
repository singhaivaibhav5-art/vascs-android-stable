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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.SuperAdminViewModel
import com.veeransh.aifashion.enterprise.types.PlacementConstants
import com.veeransh.aifashion.enterprise.types.PlacementTemplate
import com.veeransh.aifashion.enterprise.types.DefaultTemplates
import com.veeransh.aifashion.enterprise.util.PlacementTemplateHelper
import com.veeransh.aifashion.enterprise.ui.studio.PlacementDropdown

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

    // FINAL 16 TABS - Added Templates for Phase 2.6.3
    val tabs = listOf(
        "Login", "PIN", "Roles", "Users", "Perms", 
        "COD", "Payment", "Display", "Templates", "Coupon", 
        "Pincode", "Return", "AI", "B2B", "Requirements", "System", "Logs"
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
                        8 -> TemplatesTab(viewModel, snackbarHostState)
                        9 -> CouponTab(snackbarHostState)
                        10 -> PincodeTab(snackbarHostState)
                        11 -> ReturnTab()
                        12 -> AITab(viewModel)
                        13 -> B2BTab()
                        14 -> com.veeransh.aifashion.enterprise.ui.requirements.AdminRequirementListScreen(
                            onBack = { selectedTabIndex = 0 },
                            onRequirementClick = { /* detail later */ }
                        )
                        15 -> SystemTab(viewModel, snackbarHostState)
                        16 -> LogsTab(viewModel)
                    }
                }
            }
        }
    }
}

// TAB implementations
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

@Composable
fun TemplatesTab(viewModel: SuperAdminViewModel, snackbarHostState: SnackbarHostState) {
    val config by viewModel.adminConfig.collectAsState()
    val scope = rememberCoroutineScope()
    var editingTemplate by remember { mutableStateOf<PlacementTemplate?>(null) }
    var showResetAllConfirm by remember { mutableStateOf(false) }

    val templates = remember(config?.templatesJson) {
        PlacementTemplateHelper.parseTemplates(config?.templatesJson ?: "[]")
    }

    // Ensure we show all types even if missing from JSON
    val displayTemplates = remember(templates) {
        PlacementConstants.TYPES.map { type ->
            templates.find { it.placementType == type.machineValue }
                ?: DefaultTemplates.DEFAULTS.find { it.placementType == type.machineValue }
                ?: DefaultTemplates.DEFAULTS.first()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Placement Templates", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFF0D5C36))
            Button(
                onClick = { showResetAllConfirm = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Reset All", fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(displayTemplates) { template ->
                TemplateCard(
                    template = template,
                    onEdit = { editingTemplate = template },
                    onReset = {
                        val dflt = DefaultTemplates.DEFAULTS.find { it.placementType == template.placementType }
                        if (dflt != null) {
                            viewModel.updatePlacementTemplate(dflt)
                            scope.launch { snackbarHostState.showSnackbar("Reset ${template.placementType} to default") }
                        }
                    }
                )
            }
        }
    }

    if (editingTemplate != null) {
        TemplateEditDialog(
            template = editingTemplate!!,
            onDismiss = { editingTemplate = null },
            onSave = { 
                viewModel.updatePlacementTemplate(it)
                editingTemplate = null
                scope.launch { snackbarHostState.showSnackbar("Template Updated") }
            }
        )
    }

    if (showResetAllConfirm) {
        AlertDialog(
            onDismissRequest = { showResetAllConfirm = false },
            title = { Text("Reset All Templates?") },
            text = { Text("This will restore all placement standards to default. Continue?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetAllTemplates(DefaultTemplates.getSerializedDefaults())
                    showResetAllConfirm = false
                    scope.launch { snackbarHostState.showSnackbar("All templates reset to defaults") }
                }) { Text("Reset All", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showResetAllConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun TemplateCard(template: PlacementTemplate, onEdit: () -> Unit, onReset: () -> Unit) {
    val displayName = PlacementConstants.TYPES.find { it.machineValue == template.placementType }?.displayName ?: template.placementType
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text(displayName.uppercase(), fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFF7A0C20))
                    Text(template.placementType, fontSize = 10.sp, color = Color.Gray)
                }
                IconButton(onClick = onReset, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Refresh, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TemplateInfoItem("Ratio", template.aspectRatio, Modifier.weight(1f))
                TemplateInfoItem("Target Size", "${template.targetWidthPx}×${template.targetHeightPx}", Modifier.weight(1f))
                TemplateInfoItem("Crop", template.cropMode, Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0D5C36))
            ) {
                Text("Edit Template", color = Color(0xFF0D5C36))
            }
        }
    }
}

@Composable
fun TemplateInfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 9.sp, color = Color.Gray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TemplateEditDialog(template: PlacementTemplate, onDismiss: () -> Unit, onSave: (PlacementTemplate) -> Unit) {
    var ratio by remember { mutableStateOf(template.aspectRatio) }
    var crop by remember { mutableStateOf(template.cropMode) }
    var width by remember { mutableStateOf(template.targetWidthPx.toString()) }
    var height by remember { mutableStateOf(template.targetHeightPx.toString()) }

    val displayName = PlacementConstants.TYPES.find { it.machineValue == template.placementType }?.displayName ?: template.placementType

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Standard: $displayName") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PlacementDropdown("Aspect Ratio", ratio, PlacementConstants.ASPECT_RATIOS.map { it to it }, { ratio = it })
                PlacementDropdown("Crop Mode", crop, PlacementConstants.CROP_MODES.map { it to it }, { crop = it })
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = width, onValueChange = { width = it }, label = { Text("Width Px") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height Px") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = width.toIntOrNull() ?: 0
                    val h = height.toIntOrNull() ?: 0
                    if (w > 0 && h > 0) {
                        onSave(template.copy(aspectRatio = ratio, cropMode = crop, targetWidthPx = w, targetHeightPx = h))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A0C20))
            ) { Text("Save Standard") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
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
