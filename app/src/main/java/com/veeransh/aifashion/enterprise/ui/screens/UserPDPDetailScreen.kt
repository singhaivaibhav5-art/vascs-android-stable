package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.theme.VeeranshTheme
import com.veeransh.aifashion.enterprise.ui.viewmodel.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun UserPDPDetailScreen(
    productId: String,
    viewModel: HomeViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId }

    if (product != null) {
        UserPDPDetailContent(
            product = product,
            onBack = onBack,
            onAddToCart = { p, finalPrice ->
                // Logic to add to cart
            }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF7A0C20))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPDPDetailContent(
    product: ProductEntity,
    onBack: () -> Unit,
    onAddToCart: (ProductEntity, Double) -> Unit
) {
    val maroon = Color(0xFF7A0C20)
    val gold = Color(0xFFD4AF37)
    val lightBg = Color(0xFFFFF6F6)
    val lightMaroon = Color(0xFFFFF0F1)
    val borderMaroon = Color(0xFFE8B4B8)
    val brandBorder = Color(0xFFECECEC)
    val darkText = Color(0xFF2A1A1D)

    // Parse Coupons from tags (Expected format in tags: "coupons:COUP5|COUP10")
    val allowedCouponCodes = remember(product.tags) {
        val couponTag = product.tags.split(",").find { it.trim().startsWith("coupons:") }
        couponTag?.substringAfter("coupons:")?.split("|")?.filter { it.isNotBlank() } ?: emptyList()
    }

    val availableCoupons = listOf(
        UserPDPCouponItem("COUP5", "5% OFF", "PERCENT", 5.0),
        UserPDPCouponItem("COUP10", "10% OFF", "PERCENT", 10.0),
        UserPDPCouponItem("FLAT100", "₹100 OFF", "FIXED", 100.0)
    ).filter { allowedCouponCodes.contains(it.code) }

    var selectedCouponCode by remember { mutableStateOf("") }
    
    // Price Logic
    val basePrice = product.retailPrice.takeIf { it > 0 } ?: 1000.0
    val discountValue = remember(selectedCouponCode, basePrice) {
        val coupon = availableCoupons.find { it.code == selectedCouponCode }
        when (coupon?.type) {
            "PERCENT" -> (basePrice * (coupon.value / 100.0)).roundToInt().toDouble()
            "FIXED" -> coupon.value
            else -> 0.0
        }
    }
    val finalPrice = basePrice - discountValue

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details", style = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFFFAFB)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // MAIN LAYOUT: Grid-like 2 columns on large screens
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // LEFT COLUMN (Fixed Width 480dp approx)
                Column(modifier = Modifier.width(480.dp)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 5f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.White),
                        colors = CardDefaults.cardColors(containerColor = lightBg),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = product.image,
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            
                            // Badges Row at bottom
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BadgeBox("ID: ${product.id}")
                                BadgeBox("SKU: ${product.sku}")
                                BadgeBox("${product.size} • 4:5")
                            }
                        }
                    }
                }

                // RIGHT COLUMN
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    // Title & Price Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.White),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "${product.category.uppercase()} • ${product.colour.uppercase()}",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray.copy(alpha = 0.6f)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.name,
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 32.sp
                                ),
                                color = darkText
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "₹${finalPrice.toInt()}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = darkText
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "₹${basePrice.toInt()}",
                                    fontSize = 14.sp,
                                    textDecoration = TextDecoration.LineThrough,
                                    color = Color.Gray.copy(alpha = 0.4f)
                                )
                                if (discountValue > 0) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Surface(
                                        color = lightMaroon,
                                        border = BorderStroke(1.dp, borderMaroon),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "Save ₹${discountValue.toInt()}",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = maroon
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Inclusive of taxes • Well Packed • No Return/No Exchange except defect 24h",
                                fontSize = 11.sp,
                                color = Color.Gray.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // COUPON TIER 1 LOGIC
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, brandBorder),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFafB))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Percent, null, modifier = Modifier.size(14.dp), tint = maroon)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Available Offers (Product-Level) • Tick to Apply", fontSize = 13.sp, fontWeight = FontWeight.Black, color = darkText)
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (availableCoupons.isEmpty()) {
                                Text(
                                    "No coupons allowed for this product (admin unchecked all).",
                                    fontSize = 11.sp,
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    availableCoupons.forEach { coupon ->
                                        val isSelected = selectedCouponCode == coupon.code
                                        val saving = if (coupon.type == "PERCENT") (basePrice * (coupon.value / 100.0)).roundToInt() else coupon.value.toInt()
                                        
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { selectedCouponCode = if (isSelected) "" else coupon.code },
                                            color = if (isSelected) maroon else Color.White,
                                            border = BorderStroke(1.dp, if (isSelected) maroon else borderMaroon),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = isSelected,
                                                    onCheckedChange = null,
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = gold,
                                                        uncheckedColor = Color.LightGray
                                                    ),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        coupon.code,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else darkText
                                                    )
                                                    Text(
                                                        "${coupon.discount} — Save ₹$saving",
                                                        fontSize = 10.sp,
                                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else maroon
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, modifier = Modifier.size(12.dp), tint = Color.Gray.copy(alpha = 0.7f))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Price animates: ₹${basePrice.toInt()} line-through → ₹${finalPrice.toInt()} when ticked. Add to Cart shows ₹${finalPrice.toInt()}.",
                                    fontSize = 11.sp,
                                    color = Color.Gray.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // ACTIONS
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { onAddToCart(product, finalPrice) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = maroon)
                        ) {
                            Text("Add to Cart — ₹${finalPrice.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                        
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            border = BorderStroke(1.dp, brandBorder)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = "Bag", modifier = Modifier.size(18.dp), tint = darkText)
                        }
                    }

                    // Features Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(lightBg, RoundedCornerShape(12.dp))
                            .border(1.dp, borderMaroon.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FeatureItem(Icons.Default.Inventory2, "Well Packed")
                        FeatureItem(Icons.Default.QrCode, "QR Verified")
                        FeatureItem(Icons.Default.VerifiedUser, "QC Hub")
                    }
                }
            }

            // INFO FOOTER
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White),
                color = Color.White.copy(alpha = 0.5f)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = maroon)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Ratio auto applied per location: Home Banner 16:9 900x506, Grid 4:5 800x1000, Ad 1:1 800x800. Admin checked productGrid, homeBanner → auto crops preview.",
                        fontSize = 11.sp,
                        color = Color.Gray.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTTOM BAR
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .fillMaxWidth(),
                color = maroon,
                shape = RoundedCornerShape(20.dp)
            ) {
                FlowRow(
                    modifier = Modifier.padding(20.dp),
                    mainAxisSpacing = 16.dp,
                    crossAxisSpacing = 12.dp
                ) {
                    MetaItem("Product ID", product.id)
                    MetaItem("SKU", product.sku)
                    MetaItem("QR", "VERIFIED")
                    MetaItem("Size", product.size)
                    MetaItem("Ratio", "4:5")
                    MetaItem("Fit", "Cover")
                    MetaItem("Stock", "${product.stock}")
                    MetaItem("Display", product.location.replace("|", ", "))
                }
            }
        }
    }
}

@Composable
fun BadgeBox(text: String) {
    Surface(
        color = Color.Black.copy(alpha = 0.5f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = Color.White
        )
    }
}

@Composable
fun FeatureItem(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(14.dp), tint = Color(0xFF7A0C20))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}

@Composable
fun MetaItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.6f))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(mainAxisSpacing),
        verticalArrangement = Arrangement.spacedBy(crossAxisSpacing),
        content = { content() }
    )
}

data class UserPDPCouponItem(val code: String, val discount: String, val type: String, val value: Double)
