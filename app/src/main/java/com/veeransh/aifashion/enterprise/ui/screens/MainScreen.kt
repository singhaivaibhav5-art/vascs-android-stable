package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.veeransh.aifashion.enterprise.ui.viewmodel.HomeViewModel
import com.veeransh.aifashion.enterprise.ui.viewmodel.OrderViewModel
import com.veeransh.aifashion.enterprise.ui.viewmodel.WalletViewModel
import com.veeransh.aifashion.enterprise.ui.admin.UserSearchBlockPanel
import com.veeransh.aifashion.enterprise.ui.admin.CODSettings
import com.veeransh.aifashion.enterprise.ui.admin.SuperAdminDashboard
import com.veeransh.aifashion.enterprise.ui.shop.StylePartnerDashboard
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.data.local.entity.OrderItemEntity
import com.veeransh.aifashion.enterprise.data.local.entity.OrderMasterEntity
import com.veeransh.aifashion.enterprise.types.CartItem
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    walletViewModel: WalletViewModel = hiltViewModel()
) {
    val products by homeViewModel.products.collectAsState()
    val orders by orderViewModel.orders.collectAsState()
    val cartItems by homeViewModel.cartItems.collectAsState()
    
    var isAdmin by remember { mutableStateOf(false) }
    var isDealer by remember { mutableStateOf(true) }
    
    MainScreenContent(
        isAdmin = isAdmin,
        isDealer = isDealer,
        products = products,
        orders = orders,
        onAdminPinSuccess = { isAdmin = true },
        onLogout = { isAdmin = false },
        onSaveProduct = { product -> homeViewModel.saveProduct(product) },
        homeViewModel = homeViewModel,
        orderViewModel = orderViewModel,
        walletViewModel = walletViewModel
    )
}

@Composable
fun MainScreenContent(
    isAdmin: Boolean,
    isDealer: Boolean,
    products: List<ProductEntity>,
    orders: List<OrderMasterEntity>,
    onAdminPinSuccess: () -> Unit,
    onLogout: () -> Unit,
    onSaveProduct: (ProductEntity) -> Unit,
    homeViewModel: HomeViewModel? = null,
    orderViewModel: OrderViewModel? = null,
    walletViewModel: WalletViewModel? = null
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showAdminPinDialog by remember { mutableStateOf(false) }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    if (showAdminPinDialog) {
        AdminPinDialog(
            onDismiss = { showAdminPinDialog = false },
            onSuccess = {
                onAdminPinSuccess()
                showAdminPinDialog = false
                navController.navigate("super_admin")
            }
        )
    }

    Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF0D5C36),
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        BottomNavItem("Home", "home", Icons.Default.Home),
                        BottomNavItem("Categories", "categories", Icons.Default.GridView),
                        BottomNavItem("AI Center", "ai_center", Icons.Default.SmartToy),
                        BottomNavItem("Analytics", "analytics", Icons.Default.BarChart),
                        BottomNavItem("Profile", "profile", Icons.Default.Person)
                    )
                    
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0D5C36),
                                selectedTextColor = Color(0xFF0D5C36),
                                indicatorColor = Color(0xFF0D5C36).copy(alpha = 0.1f),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            },
            containerColor = Color(0xFFF5F1E8)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F1E8))
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        if (homeViewModel != null) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onProductClick = { /* Navigate to Details */ },
                                onAdminAccess = { showAdminPinDialog = true },
                                onToolClick = { toolName ->
                                    val route = when (toolName) {
                                        "Finance" -> "finance"
                                        "Inventory" -> "inventory"
                                        "Sales" -> "sales"
                                        "Customers" -> "customers"
                                        "Stock" -> "stock"
                                        "Reports" -> "reports"
                                        "AI Center" -> "ai_brain"
                                        "Analytics" -> "analytics"
                                        "Orders" -> "orders"
                                        "B2B Portal" -> "b2b"
                                        "Style Partner" -> "ewallet"
                                        "Admin Control" -> {
                                            showAdminPinDialog = true
                                            "home"
                                        }
                                        "Dispatch" -> "dispatch"
                                        else -> "home"
                                    }
                                    if (route != "home") navController.navigate(route)
                                },
                                onAddSareeClick = { navController.navigate("moveToDisplay") },
                                isAdmin = isAdmin,
                                isDealer = isDealer
                            )
                        }
                    }
                    composable("moveToDisplay") {
                        if (homeViewModel != null) {
                            MoveToDisplayScreen(
                                onNavigateToPdp = { productId ->
                                    navController.navigate("userPdp/$productId")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Published to Display!")
                                    }
                                },
                                onBack = { navController.popBackStack() },
                                homeViewModel = homeViewModel
                            )
                        }
                    }
                    composable("userPdp/{productId}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId") ?: ""
                        if (homeViewModel != null) {
                            UserPDPDetailScreen(
                                productId = productId,
                                viewModel = homeViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                    composable("cart") {
                        if (homeViewModel != null && orderViewModel != null) {
                            CartCheckoutScreen(
                                viewModel = homeViewModel,
                                orderViewModel = orderViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onOrderPlaced = {
                                    navController.navigate("home")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Order Placed Successfully!")
                                    }
                                }
                            )
                        }
                    }
                    composable("finance") { FinanceScreen() }
                    composable("inventory") { 
                        if (homeViewModel != null) {
                            InventoryScreen(
                                viewModel = homeViewModel
                            ) 
                        }
                    }
                    composable("sales") { 
                        if (homeViewModel != null && orderViewModel != null) {
                            SalesScreen(
                                viewModel = homeViewModel,
                                orderViewModel = orderViewModel,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                    composable("orders") { 
                        if (orderViewModel != null) {
                            OrdersScreen(
                                viewModel = orderViewModel,
                                onOrderClick = { /* Show details */ }
                            )
                        }
                    }
                    composable("customers") { PlaceholderScreen("Customer Relationship Management") }
                    composable("stock") { PlaceholderScreen("Live Stock Monitoring") }
                    composable("reports") { PlaceholderScreen("Business Reports") }
                    composable("ai_center") { PlaceholderScreen("AI Fashion Center") }
                    composable("ai_brain") {
                        PlaceholderScreen("VASCS AI Brain - FROZEN")
                    }
                    composable("analytics") { PlaceholderScreen("Business Analytics") }
                    composable(
                        route = "add_saree?selectedIds={selectedIds}",
                        arguments = listOf(navArgument("selectedIds") { nullable = true })
                    ) { backStackEntry ->
                        val selectedIds = backStackEntry.arguments?.getString("selectedIds")
                        AddSareeScreen(
                            onSave = { product ->
                                onSaveProduct(product)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Product ${product.name} saved to ERP")
                                }
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() },
                            selectedIds = selectedIds
                        )
                    }
                    
                    composable("categories") { PlaceholderScreen("Categories") }
                    composable("profile") { PlaceholderScreen("User Profile") }
                    composable("ewallet") { StylePartnerDashboard() }
                    composable("codSettings") { CODSettings() }
                    composable("adminControl") { 
                        navController.navigate("super_admin")
                    }
                    composable("adminSearchBlock") {
                        UserSearchBlockPanel()
                    }
                    composable("super_admin") {
                        SuperAdminDashboard(navController = navController)
                    }
                    composable("ai_drape_studio") {
                        SareeDrapingScreen(navController = navController)
                    }
                    composable("admin-veeransh-2026") {
                        navController.navigate("super_admin")
                    }
                }
            }
        }
}

@Composable
fun AdminPinDialog(onDismiss: () -> Unit, onSuccess: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF0D5C36), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Admin Access Control", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Enter Secret PIN to unlock ERP Ultima", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it; error = false },
                    label = { Text("PIN") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error,
                    singleLine = true
                )
                
                if (error) {
                    Text("Incorrect PIN. Please try again.", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        if (pin == "2026") onSuccess()
                        else error = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Unlock Dashboard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Construction, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(name, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.LightGray)
        }
    }
}

data class BottomNavItem(val label: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
