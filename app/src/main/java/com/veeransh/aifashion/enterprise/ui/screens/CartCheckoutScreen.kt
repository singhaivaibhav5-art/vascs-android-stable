package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.HomeViewModel
import com.veeransh.aifashion.enterprise.ui.viewmodel.OrderViewModel
import com.veeransh.aifashion.enterprise.ui.viewmodel.OrderState
import com.veeransh.aifashion.enterprise.types.CartItem
import com.veeransh.aifashion.enterprise.types.UserPDPCouponItem
import com.veeransh.aifashion.enterprise.util.FinancialCalculator
import com.veeransh.aifashion.enterprise.ui.components.OrderSuccessDialog
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun CartCheckoutScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val orderState by orderViewModel.orderState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf<String?>(null) }
    var lastTotal by remember { mutableDoubleStateOf(0.0) }

    LaunchedEffect(orderState) {
        if (orderState is OrderState.Success) {
            val orderId = (orderState as OrderState.Success).orderId.toString()
            showSuccessDialog = orderId
            viewModel.clearCart()
            orderViewModel.resetOrderState()
        }
    }

    CartCheckoutContent(
        cartItems = cartItems,
        orderState = orderState,
        onNavigateBack = onNavigateBack,
        onPlaceOrder = { total, cartCoupon ->
             if (cartItems.isNotEmpty() && orderState !is OrderState.Loading) {
                 lastTotal = total
                 val timestamp = SimpleDateFormat("HHmmss", Locale.getDefault()).format(Date())
                 val orderNumber = "ORD-2026-$timestamp-${(10..99).random()}"
                 orderViewModel.placeOrder(orderNumber, cartItems, cartCoupon)
             }
        }
    )

    if (showSuccessDialog != null) {
        OrderSuccessDialog(
            orderId = showSuccessDialog!!,
            total = lastTotal,
            onDismiss = { 
                showSuccessDialog = null
                onOrderPlaced() // Navigate home
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutContent(
    cartItems: List<CartItem>,
    orderState: OrderState,
    onNavigateBack: () -> Unit,
    onPlaceOrder: (Double, UserPDPCouponItem?) -> Unit
) {
    val maroon = Color(0xFF7A0C20)
    val brandBg = Color(0xFFFFFAFB)
    val brandBorder = Color(0xFFECECEC)
    val darkText = Color(0xFF2A1A1D)

    // Tier 2 - Cart Level Coupon State
    var selectedTier2CouponCode by remember { mutableStateOf<String?>(null) }
    val tier2Coupons = listOf(
        UserPDPCouponItem("EXTRA5", "5% Extra OFF", "PERCENT", 5.0),
        UserPDPCouponItem("OFFER500", "₹500 OFF", "FIXED", 500.0)
    )
    val selectedTier2Coupon = remember(selectedTier2CouponCode) {
        tier2Coupons.find { it.code == selectedTier2CouponCode }
    }

    // Calculations using Authoritative Calculator
    val calc = remember(cartItems, selectedTier2Coupon) {
        FinancialCalculator.calculate(cartItems, selectedTier2Coupon)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Your Cart & Checkout",
                        style = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = brandBg
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // LEFT - CART ITEMS (1.1fr)
            Column(modifier = Modifier.weight(1.1f)) {
                Text(
                    "CART ITEMS (AFTER PRODUCT COUPONS)",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = maroon.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (cartItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Cart is Empty", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(cartItems) { item ->
                            CartItemCard(item, maroon, brandBorder, darkText)
                        }
                        
                        item {
                            OutlinedButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.padding(top = 8.dp),
                                border = BorderStroke(1.dp, brandBorder),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = maroon)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add More Items", color = darkText, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // RIGHT - CHECKOUT SUMMARY (0.9fr)
            Column(modifier = Modifier.weight(0.9f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // Tier 2 Cart-Level Coupons
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, brandBorder),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ConfirmationNumber, null, modifier = Modifier.size(14.dp), tint = maroon)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cart-Level Offers (Tier 2)", fontSize = 13.sp, fontWeight = FontWeight.Black, color = darkText)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        tier2Coupons.forEach { coupon ->
                            val isSelected = selectedTier2CouponCode == coupon.code
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedTier2CouponCode = if (isSelected) null else coupon.code },
                                color = if (isSelected) maroon.copy(alpha = 0.05f) else Color.Transparent,
                                border = BorderStroke(1.dp, if (isSelected) maroon else brandBorder),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null,
                                        colors = RadioButtonDefaults.colors(selectedColor = maroon)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(coupon.code, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(coupon.discount, fontSize = 10.sp, color = maroon)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, null, tint = maroon, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Summary Bill
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, brandBorder),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("ORDER SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        
                        SummaryRow("Subtotal", "₹${calc.subtotal.toInt()}")
                        if (calc.productDiscount > 0) {
                            SummaryRow("Product Discounts", "-₹${calc.productDiscount.toInt()}", color = maroon)
                        }
                        
                        if (calc.cartDiscount > 0) {
                            SummaryRow("Cart Discount (${selectedTier2CouponCode})", "-₹${calc.cartDiscount.toInt()}", color = maroon)
                        }
                        
                        SummaryRow("Taxable Amount", "₹${calc.taxableAmount.toInt()}")
                        SummaryRow("GST", "₹${calc.gstAmount.toInt()}")
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = brandBorder)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("NET TOTAL", fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("₹${calc.netAmount.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = maroon)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = { onPlaceOrder(calc.netAmount, selectedTier2Coupon) },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = maroon),
                            enabled = cartItems.isNotEmpty() && orderState !is OrderState.Loading
                        ) {
                            if (orderState is OrderState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text("PROCEED TO PAY — ₹${calc.netAmount.toInt()}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                // Features
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF6F6),
                    border = BorderStroke(1.dp, maroon.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FeatureItemSmall(Icons.Default.Security, "Secure Pay")
                        FeatureItemSmall(Icons.Default.LocalShipping, "Tracked")
                        FeatureItemSmall(Icons.Default.Verified, "QC Hub")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, maroon: Color, border: Color, darkText: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, border),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFafB))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Tiny thumbnail
            AsyncImage(
                model = item.product.image,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = darkText)
                if (item.appliedCoupon != null) {
                    val itemDiscount = when (item.appliedCoupon.type) {
                        "PERCENT" -> (item.product.retailPrice * (item.appliedCoupon.value / 100.0)).roundToInt().toDouble()
                        "FIXED" -> item.appliedCoupon.value
                        else -> 0.0
                    }
                    Text(
                        "${item.appliedCoupon.code} applied • Sale ₹${item.product.retailPrice.toInt()} → ₹${(item.product.retailPrice - itemDiscount).toInt()}",
                        fontSize = 10.sp,
                        color = Color.Gray.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val itemDiscount = when (item.appliedCoupon?.type) {
                        "PERCENT" -> (item.product.retailPrice * (item.appliedCoupon.value / 100.0)).roundToInt().toDouble()
                        "FIXED" -> item.appliedCoupon.value
                        else -> 0.0
                    }
                    val discountedPrice = item.product.retailPrice - itemDiscount
                    
                    Text("₹${discountedPrice.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = darkText)
                    if (item.appliedCoupon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "₹${item.product.retailPrice.toInt()}",
                            fontSize = 11.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray.copy(alpha = 0.4f)
                        )
                    }
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                if (item.appliedCoupon != null) {
                    Surface(
                        border = BorderStroke(1.dp, border),
                        shape = RoundedCornerShape(6.dp),
                        color = maroon.copy(alpha = 0.1f)
                    ) {
                        Text(
                            item.appliedCoupon.code, 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 9.sp, 
                            fontWeight = FontWeight.Bold,
                            color = maroon
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Qty: ${item.qty}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, color: Color = Color.DarkGray) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun FeatureItemSmall(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(12.dp), tint = Color(0xFF7A0C20))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}
