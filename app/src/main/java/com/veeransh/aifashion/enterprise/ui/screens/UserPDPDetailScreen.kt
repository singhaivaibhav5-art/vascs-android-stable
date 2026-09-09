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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.veeransh.aifashion.enterprise.types.UserPDPCouponItem
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun UserPDPDetailScreen(
    productId: String,
    viewModel: HomeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onRequirementClick: (String) -> Unit = {}
) {
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId }
    val isDealer by viewModel.isDealer.collectAsState()

    if (product != null) {
        // Effective MOQ for state initialization
        val effectiveMin = remember(product, isDealer) {
            if (!product.isMoqEnabled) 1
            else if (isDealer) product.dealerMoq
            else product.moq
        }.coerceAtLeast(1)

        var quantity by remember(product.id, effectiveMin) { mutableIntStateOf(effectiveMin) }

        UserPDPDetailContent(
            product = product,
            onBack = onBack,
            onAddToCart = { p, coupon ->
                viewModel.addToCart(p, quantity, coupon)
            },
            isDealer = isDealer,
            quantity = quantity,
            onQuantityChange = { quantity = it },
            onRequirementClick = onRequirementClick
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
    onAddToCart: (ProductEntity, UserPDPCouponItem?) -> Unit,
    isDealer: Boolean = false,
    quantity: Int = 1,
    onQuantityChange: (Int) -> Unit = {},
    onRequirementClick: (String) -> Unit = {}
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
    val selectedCoupon = remember(selectedCouponCode) {
        availableCoupons.find { it.code == selectedCouponCode }
    }
    
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFFFAFB)
    ) { innerPadding ->
        BoxWithConstraints(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            val isWide = maxWidth > 600.dp
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (isWide) {
                    WidePDPLayout(product, isDealer, quantity, onQuantityChange, basePrice, finalPrice, discountValue, availableCoupons, selectedCouponCode, selectedCoupon, maroon, gold, lightBg, lightMaroon, borderMaroon, brandBorder, darkText, onAddToCart, onRequirementClick, { selectedCouponCode = it })
                } else {
                    MobilePDPLayout(product, isDealer, quantity, onQuantityChange, basePrice, finalPrice, discountValue, availableCoupons, selectedCouponCode, selectedCoupon, maroon, gold, lightBg, lightMaroon, borderMaroon, brandBorder, darkText, onAddToCart, onRequirementClick, { selectedCouponCode = it })
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MobilePDPLayout(
    product: ProductEntity,
    isDealer: Boolean,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    basePrice: Double,
    finalPrice: Double,
    discountValue: Double,
    availableCoupons: List<UserPDPCouponItem>,
    selectedCouponCode: String,
    selectedCoupon: UserPDPCouponItem?,
    maroon: Color,
    gold: Color,
    lightBg: Color,
    lightMaroon: Color,
    borderMaroon: Color,
    brandBorder: Color,
    darkText: Color,
    onAddToCart: (ProductEntity, UserPDPCouponItem?) -> Unit,
    onRequirementClick: (String) -> Unit,
    onCouponSelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Image
        Card(
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 5f),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(model = product.image, contentDescription = product.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Row(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BadgeBox("SKU: ${product.sku}")
                    BadgeBox("${product.size}")
                }
            }
        }
        
        // Content
        PDPContentSection(product, isDealer, quantity, onQuantityChange, basePrice, finalPrice, discountValue, availableCoupons, selectedCouponCode, selectedCoupon, maroon, gold, lightBg, lightMaroon, borderMaroon, brandBorder, darkText, onAddToCart, onRequirementClick, onCouponSelect)
    }
}

@Composable
fun WidePDPLayout(
    product: ProductEntity,
    isDealer: Boolean,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    basePrice: Double,
    finalPrice: Double,
    discountValue: Double,
    availableCoupons: List<UserPDPCouponItem>,
    selectedCouponCode: String,
    selectedCoupon: UserPDPCouponItem?,
    maroon: Color,
    gold: Color,
    lightBg: Color,
    lightMaroon: Color,
    borderMaroon: Color,
    brandBorder: Color,
    darkText: Color,
    onAddToCart: (ProductEntity, UserPDPCouponItem?) -> Unit,
    onRequirementClick: (String) -> Unit,
    onCouponSelect: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        Column(modifier = Modifier.width(400.dp)) {
            Card(modifier = Modifier.fillMaxWidth().aspectRatio(4f / 5f), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(model = product.image, contentDescription = product.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            PDPContentSection(product, isDealer, quantity, onQuantityChange, basePrice, finalPrice, discountValue, availableCoupons, selectedCouponCode, selectedCoupon, maroon, gold, lightBg, lightMaroon, borderMaroon, brandBorder, darkText, onAddToCart, onRequirementClick, onCouponSelect)
        }
    }
}

@Composable
fun PDPContentSection(
    product: ProductEntity,
    isDealer: Boolean,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    basePrice: Double,
    finalPrice: Double,
    discountValue: Double,
    availableCoupons: List<UserPDPCouponItem>,
    selectedCouponCode: String,
    selectedCoupon: UserPDPCouponItem?,
    maroon: Color,
    gold: Color,
    lightBg: Color,
    lightMaroon: Color,
    borderMaroon: Color,
    brandBorder: Color,
    darkText: Color,
    onAddToCart: (ProductEntity, UserPDPCouponItem?) -> Unit,
    onRequirementClick: (String) -> Unit,
    onCouponSelect: (String) -> Unit
) {
    // MOQ Logic
    val effectiveMin = remember(product, isDealer) {
        if (!product.isMoqEnabled) 1
        else if (isDealer) product.dealerMoq
        else product.moq
    }.coerceAtLeast(1)

    val isStockInsufficient = effectiveMin > product.stock

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
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
                    style = TextStyle(fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold, color = Color.Gray.copy(alpha = 0.6f))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.name,
                    style = TextStyle(fontFamily = FontFamily.Serif, fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp),
                    color = darkText
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "₹${finalPrice.toInt()}", fontSize = 28.sp, fontWeight = FontWeight.SemiBold, color = darkText)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "₹${basePrice.toInt()}", fontSize = 14.sp, textDecoration = TextDecoration.LineThrough, color = Color.Gray.copy(alpha = 0.4f))
                    if (discountValue > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(color = lightMaroon, border = BorderStroke(1.dp, borderMaroon), shape = RoundedCornerShape(4.dp)) {
                            Text("Save ₹${discountValue.toInt()}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = maroon)
                        }
                    }
                }
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
                    Text("Available Offers", fontSize = 13.sp, fontWeight = FontWeight.Black, color = darkText)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableCoupons.forEach { coupon ->
                        val isSelected = selectedCouponCode == coupon.code
                        val saving = if (coupon.type == "PERCENT") (basePrice * (coupon.value / 100.0)).roundToInt() else coupon.value.toInt()
                        
                        Surface(
                            modifier = Modifier.fillMaxWidth().clickable { onCouponSelect(if (isSelected) "" else coupon.code) },
                            color = if (isSelected) maroon else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) maroon else borderMaroon),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = isSelected, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = gold, uncheckedColor = Color.LightGray), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(coupon.code, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else darkText)
                                    Text("${coupon.discount} — Save ₹$saving", fontSize = 10.sp, color = if (isSelected) Color.White.copy(alpha = 0.8f) else maroon)
                                }
                            }
                        }
                    }
                }
            }
        }

        // QUANTITY & ACTIONS
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (isStockInsufficient) {
                Surface(
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Error, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Minimum quantity ($effectiveMin) exceeds available stock (${product.stock}).",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Quantity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (product.isMoqEnabled) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = maroon.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                "Min: $effectiveMin",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = maroon
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .border(1.dp, brandBorder, CircleShape)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > effectiveMin) onQuantityChange(quantity - 1) },
                            modifier = Modifier.size(32.dp),
                            enabled = quantity > effectiveMin
                        ) {
                            Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
                        }
                        
                        Text(
                            text = "$quantity",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                        
                        IconButton(
                            onClick = { if (quantity < product.stock) onQuantityChange(quantity + 1) },
                            modifier = Modifier.size(32.dp),
                            enabled = quantity < product.stock
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onAddToCart(product, selectedCoupon) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = maroon),
                    enabled = !isStockInsufficient && quantity <= product.stock && quantity >= effectiveMin
                ) {
                    Text("Add to Cart — ₹${(finalPrice * quantity).toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
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

            // REQUIREMENT ACTION
            OutlinedButton(
                onClick = { onRequirementClick(product.id) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, maroon),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = maroon)
            ) {
                Icon(Icons.Default.EditNote, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("SUBMIT REQUIREMENT", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Features Grid
        Row(
            modifier = Modifier.fillMaxWidth().background(lightBg, RoundedCornerShape(12.dp)).border(1.dp, borderMaroon.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FeatureItem(Icons.Default.Inventory2, "Well Packed")
            FeatureItem(Icons.Default.QrCode, "QR Verified")
            FeatureItem(Icons.Default.VerifiedUser, "QC Hub")
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
