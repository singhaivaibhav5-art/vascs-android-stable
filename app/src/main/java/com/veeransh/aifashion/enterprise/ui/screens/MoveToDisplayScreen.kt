package com.veeransh.aifashion.enterprise.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.theme.VeeranshTheme
import com.veeransh.aifashion.enterprise.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

// Data Models for the Screen
data class DisplayLocation(val id: String, val name: String, val ratio: String, val size: String)
data class CouponItem(val code: String, val discount: String, val type: String)

@Composable
fun MoveToDisplayScreen(
    onNavigateToPdp: (String) -> Unit,
    onBack: () -> Unit,
    homeViewModel: HomeViewModel? = null
) {
    MoveToDisplayContent(
        onSaveDraft = {
            // Save draft logic if any
        },
        onPublish = { product ->
            homeViewModel?.saveProduct(product)
            onNavigateToPdp(product.id)
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveToDisplayContent(
    onSaveDraft: () -> Unit,
    onPublish: (ProductEntity) -> Unit,
    onBack: () -> Unit
) {
    // State Management
    var productId by remember { mutableStateOf("VEER-P-2026-0001") }
    var sku by remember { mutableStateOf("VEER-SKU-BAN-${System.currentTimeMillis().toString().takeLast(6)}") }
    var productName by remember { mutableStateOf("Banarasi Katan Silk - Rose Blush") }
    var category by remember { mutableStateOf("Banarasi") }
    var fabric by remember { mutableStateOf("Pure Katan Silk") }
    var work by remember { mutableStateOf("Zari Butta + Meenakari") }
    var description by remember { mutableStateOf("Handwoven luxury Banarasi Katan Silk saree featuring intricate Meenakari work.") }
    var stockCount by remember { mutableIntStateOf(15) }
    var selectedSize by remember { mutableStateOf("Free Size") }
    var selectedRatio by remember { mutableStateOf("4:5") }
    var selectedFit by remember { mutableStateOf("Cover") }
    var isBlouseIncluded by remember { mutableStateOf(true) }
    var metaTitle by remember { mutableStateOf("Banarasi Katan Silk Rose Blush | Veeransh AI") }
    var metaKeywords by remember { mutableStateOf("banarasi, katan silk, rose, AI drape") }
    
    val displayLocations = listOf(
        DisplayLocation("productGrid", "Product Grid", "4:5", "800x1000"),
        DisplayLocation("homeBanner", "Home Banner", "16:9", "900x506"),
        DisplayLocation("adBanner", "Ad Banner", "1:1", "800x800")
    )
    val selectedLocations = remember { mutableStateListOf("productGrid") }
    
    val coupons = listOf(
        CouponItem("COUP5", "5%", "PERCENT"),
        CouponItem("COUP10", "10%", "PERCENT"),
        CouponItem("FLAT100", "₹100", "FIXED")
    )
    val selectedCoupons = remember { mutableStateListOf<String>() }
    
    val images = remember { mutableStateListOf("https://images.unsplash.com/photo-1610030469915-9a88edc1c29a") }
    var currentImageIndex by remember { mutableIntStateOf(0) }

    val maroon = Color(0xFF7A0C20)
    val gold = Color(0xFFD4AF37)
    val brandBg = Color(0xFFFFFAFB)
    val brandBorder = Color(0xFFECECEC)
    val darkText = Color(0xFF2A1A1D)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Publish to Display",
                        style = TextStyle(fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val stockColor = if (stockCount == 0) Color(0xFFFFE9E9) else Color(0xFFF0FFF4)
                    val stockTextColor = if (stockCount == 0) maroon else Color(0xFF155A2A)
                    Surface(color = stockColor, shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(end = 16.dp)) {
                        Text(
                            if (stockCount == 0) "OOS" else "IN STOCK",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = stockTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = brandBg)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, brandBorder),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onSaveDraft,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, brandBorder)
                    ) {
                        Text("Save Draft", color = darkText, fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { /* Restore Preview Logic - Placeholder */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, brandBorder)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Preview", color = darkText, fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            val newProduct = ProductEntity(
                                id = productId,
                                name = productName,
                                sku = sku,
                                barcode = "8908001123456",
                                category = category,
                                brand = "Veeransh",
                                fabric = fabric,
                                colour = "Rose Blush",
                                size = selectedSize,
                                hsn = "5007",
                                gst = 5.0,
                                purchasePrice = 5000.0,
                                wholesalePrice = 7500.0,
                                retailPrice = 9999.0,
                                mrp = 12500.0,
                                discount = 0.0,
                                stock = stockCount,
                                image = images.getOrNull(0) ?: "",
                                imagesJson = images.joinToString(",", prefix = "[", postfix = "]"),
                                description = description,
                                tags = "festive, bridal, work:$work, coupons:${selectedCoupons.joinToString("|")}",
                                location = selectedLocations.joinToString("|"),
                                createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            )
                            onPublish(newProduct)
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = maroon)
                    ) {
                        Text("Publish", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        },
        containerColor = brandBg
    ) { innerPadding ->
        BoxWithConstraints(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            val isWide = this.maxWidth > 600.dp
            
            if (isWide) {
                WideMoveToDisplayLayout(images, currentImageIndex, displayLocations, selectedLocations, productId, sku, productName, category, fabric, work, description, stockCount, selectedSize, selectedRatio, selectedFit, isBlouseIncluded, metaTitle, metaKeywords, coupons, selectedCoupons, maroon, brandBorder, darkText, 
                    onImageClick = { currentImageIndex = it },
                    onProductIdChange = { productId = it },
                    onSkuChange = { sku = it },
                    onNameChange = { productName = it },
                    onCategoryChange = { category = it },
                    onFabricChange = { fabric = it },
                    onWorkChange = { work = it },
                    onDescChange = { description = it },
                    onStockChange = { stockCount = it },
                    onSizeChange = { selectedSize = it },
                    onRatioChange = { selectedRatio = it },
                    onFitChange = { selectedFit = it },
                    onBlouseChange = { isBlouseIncluded = it },
                    onMetaTitleChange = { metaTitle = it },
                    onMetaKeywordsChange = { metaKeywords = it }
                )
            } else {
                MobileMoveToDisplayLayout(images, currentImageIndex, displayLocations, selectedLocations, productId, sku, productName, category, fabric, work, description, stockCount, selectedSize, selectedRatio, selectedFit, isBlouseIncluded, metaTitle, metaKeywords, coupons, selectedCoupons, maroon, brandBorder, darkText,
                    onImageClick = { currentImageIndex = it },
                    onProductIdChange = { productId = it },
                    onSkuChange = { sku = it },
                    onNameChange = { productName = it },
                    onCategoryChange = { category = it },
                    onFabricChange = { fabric = it },
                    onWorkChange = { work = it },
                    onDescChange = { description = it },
                    onStockChange = { stockCount = it },
                    onSizeChange = { selectedSize = it },
                    onRatioChange = { selectedRatio = it },
                    onFitChange = { selectedFit = it },
                    onBlouseChange = { isBlouseIncluded = it },
                    onMetaTitleChange = { metaTitle = it },
                    onMetaKeywordsChange = { metaKeywords = it }
                )
            }
        }
    }
}

@Composable
fun MobileMoveToDisplayLayout(
    images: List<String>,
    currentImageIndex: Int,
    displayLocations: List<DisplayLocation>,
    selectedLocations: MutableList<String>,
    productId: String,
    sku: String,
    productName: String,
    category: String,
    fabric: String,
    work: String,
    description: String,
    stockCount: Int,
    selectedSize: String,
    selectedRatio: String,
    selectedFit: String,
    isBlouseIncluded: Boolean,
    metaTitle: String,
    metaKeywords: String,
    coupons: List<CouponItem>,
    selectedCoupons: MutableList<String>,
    maroon: Color,
    brandBorder: Color,
    darkText: Color,
    onImageClick: (Int) -> Unit,
    onProductIdChange: (String) -> Unit,
    onSkuChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onFabricChange: (String) -> Unit,
    onWorkChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onStockChange: (Int) -> Unit,
    onSizeChange: (String) -> Unit,
    onRatioChange: (String) -> Unit,
    onFitChange: (String) -> Unit,
    onBlouseChange: (Boolean) -> Unit,
    onMetaTitleChange: (String) -> Unit,
    onMetaKeywordsChange: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ImagePreviewSection(images, currentImageIndex, maroon, onImageClick)
        }
        item {
            QRCodeSection(productId, maroon, brandBorder, darkText)
        }
        item {
            AutoCropPreviewSection(images, selectedLocations, displayLocations, maroon, brandBorder)
        }
        item {
            MoveToDisplayForm(productId, sku, productName, category, fabric, work, description, stockCount, selectedSize, selectedRatio, selectedFit, isBlouseIncluded, metaTitle, metaKeywords, coupons, selectedCoupons, maroon, brandBorder, darkText, onProductIdChange, onSkuChange, onNameChange, onCategoryChange, onFabricChange, onWorkChange, onDescChange, onStockChange, onSizeChange, onRatioChange, onFitChange, onBlouseChange, onMetaTitleChange, onMetaKeywordsChange, displayLocations, selectedLocations)
        }
    }
}

@Composable
fun WideMoveToDisplayLayout(
    images: List<String>,
    currentImageIndex: Int,
    displayLocations: List<DisplayLocation>,
    selectedLocations: MutableList<String>,
    productId: String,
    sku: String,
    productName: String,
    category: String,
    fabric: String,
    work: String,
    description: String,
    stockCount: Int,
    selectedSize: String,
    selectedRatio: String,
    selectedFit: String,
    isBlouseIncluded: Boolean,
    metaTitle: String,
    metaKeywords: String,
    coupons: List<CouponItem>,
    selectedCoupons: MutableList<String>,
    maroon: Color,
    brandBorder: Color,
    darkText: Color,
    onImageClick: (Int) -> Unit,
    onProductIdChange: (String) -> Unit,
    onSkuChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onFabricChange: (String) -> Unit,
    onWorkChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onStockChange: (Int) -> Unit,
    onSizeChange: (String) -> Unit,
    onRatioChange: (String) -> Unit,
    onFitChange: (String) -> Unit,
    onBlouseChange: (Boolean) -> Unit,
    onMetaTitleChange: (String) -> Unit,
    onMetaKeywordsChange: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        Column(modifier = Modifier.width(400.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            ImagePreviewSection(images, currentImageIndex, maroon, onImageClick)
            QRCodeSection(productId, maroon, brandBorder, darkText)
            AutoCropPreviewSection(images, selectedLocations, displayLocations, maroon, brandBorder)
        }
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            item {
                MoveToDisplayForm(productId, sku, productName, category, fabric, work, description, stockCount, selectedSize, selectedRatio, selectedFit, isBlouseIncluded, metaTitle, metaKeywords, coupons, selectedCoupons, maroon, brandBorder, darkText, onProductIdChange, onSkuChange, onNameChange, onCategoryChange, onFabricChange, onWorkChange, onDescChange, onStockChange, onSizeChange, onRatioChange, onFitChange, onBlouseChange, onMetaTitleChange, onMetaKeywordsChange, displayLocations, selectedLocations)
            }
        }
    }
}

@Composable
fun ImagePreviewSection(images: List<String>, currentImageIndex: Int, maroon: Color, onImageClick: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 5f),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(model = images.getOrNull(currentImageIndex), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Surface(modifier = Modifier.padding(12.dp).align(Alignment.TopStart), color = Color.White.copy(alpha = 0.9f), shape = RoundedCornerShape(4.dp)) {
                    Text("AI Draped • NanoBanana", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = maroon)
                }
                Surface(modifier = Modifier.padding(12.dp).align(Alignment.TopEnd), color = maroon, shape = RoundedCornerShape(4.dp)) {
                    Text("4:5 Cover", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, color = Color.White)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            images.forEachIndexed { index, img ->
                Box(
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))
                        .border(if (index == currentImageIndex) 2.dp else 1.dp, if (index == currentImageIndex) maroon else Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { onImageClick(index) }
                ) {
                    AsyncImage(model = img, contentDescription = null, contentScale = ContentScale.Crop)
                }
            }
        }
    }
}

@Composable
fun QRCodeSection(productId: String, maroon: Color, brandBorder: Color, darkText: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, brandBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(64.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.DarkGray)
                    Text("VEER-P", fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("QR Code Auto • SKU linked", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = darkText)
                Text("$productId • Scans to PDP", fontSize = 10.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = maroon), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), modifier = Modifier.height(28.dp), shape = RoundedCornerShape(4.dp)) {
                        Text("Download QR", fontSize = 10.sp)
                    }
                    OutlinedButton(onClick = {}, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), modifier = Modifier.height(28.dp), shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, brandBorder)) {
                        Text("Barcode Info", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun AutoCropPreviewSection(images: List<String>, selectedLocations: List<String>, displayLocations: List<DisplayLocation>, maroon: Color, brandBorder: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, brandBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("AUTO CROP PREVIEW BY LOCATION", fontSize = 12.sp, fontWeight = FontWeight.Black, color = maroon, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                displayLocations.filter { selectedLocations.contains(it.id) }.forEach { loc ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF8FAFC)), contentAlignment = Alignment.Center) {
                            AsyncImage(model = images.getOrNull(0), contentDescription = null, contentScale = ContentScale.Crop)
                            Surface(color = maroon, shape = RoundedCornerShape(2.dp), modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)) {
                                Text("Auto", color = Color.White, fontSize = 7.sp, modifier = Modifier.padding(2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(loc.name, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("${loc.ratio} • ${loc.size}", fontSize = 8.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun MoveToDisplayForm(
    productId: String, sku: String, productName: String, category: String, fabric: String, work: String, description: String, stockCount: Int, selectedSize: String, selectedRatio: String, selectedFit: String, isBlouseIncluded: Boolean, metaTitle: String, metaKeywords: String, coupons: List<CouponItem>, selectedCoupons: MutableList<String>, maroon: Color, brandBorder: Color, darkText: Color,
    onProductIdChange: (String) -> Unit, onSkuChange: (String) -> Unit, onNameChange: (String) -> Unit, onCategoryChange: (String) -> Unit, onFabricChange: (String) -> Unit, onWorkChange: (String) -> Unit, onDescChange: (String) -> Unit, onStockChange: (Int) -> Unit, onSizeChange: (String) -> Unit, onRatioChange: (String) -> Unit, onFitChange: (String) -> Unit, onBlouseChange: (Boolean) -> Unit, onMetaTitleChange: (String) -> Unit, onMetaKeywordsChange: (String) -> Unit,
    displayLocations: List<DisplayLocation>, selectedLocations: MutableList<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        SectionTitle("Product Identity")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IdentityField(label = "ID", value = productId, onValueChange = onProductIdChange, modifier = Modifier.weight(1f))
            IdentityField(label = "SKU", value = sku, onValueChange = onSkuChange, modifier = Modifier.weight(1f))
        }

        SectionTitle("Details")
        DetailTextField(label = "Name", value = productName, onValueChange = onNameChange)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DropdownField(label = "Category", value = category, options = listOf("Banarasi", "Silk", "Cotton", "Kanjivaram", "Chanderi", "Paithani", "Organza", "Georgette", "Tussar", "Designer"), onSelect = onCategoryChange, modifier = Modifier.weight(1f))
            DetailTextField(label = "Fabric", value = fabric, onValueChange = onFabricChange, modifier = Modifier.weight(1f))
        }
        DetailTextField(label = "Work", value = work, onValueChange = onWorkChange)
        
        Column {
            Text("Tags", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("festive", "bridal").forEach { tag ->
                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(tag, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        }
                    }
                }
                Box(modifier = Modifier.drawDashedBorder(Color.LightGray, 4.dp).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("+ Add", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }

        DetailTextField(label = "Description", value = description, onValueChange = onDescChange, minHeight = 80.dp)

        SectionTitle("Inventory & Layout")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Card(modifier = Modifier.weight(1f), border = BorderStroke(1.dp, brandBorder), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Stock Count", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        IconButton(onClick = { if (stockCount > 0) onStockChange(stockCount - 1) }, modifier = Modifier.size(24.dp).background(Color(0xFFF1F5F9), CircleShape)) { Icon(Icons.Default.Remove, null, modifier = Modifier.size(14.dp)) }
                        Text("$stockCount", modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Black)
                        IconButton(onClick = { onStockChange(stockCount + 1) }, modifier = Modifier.size(24.dp).background(Color(0xFFF1F5F9), CircleShape)) { Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp)) }
                    }
                }
            }
            DropdownField(label = "Size", value = selectedSize, options = listOf("Free Size", "S", "M", "L", "Custom"), onSelect = onSizeChange, modifier = Modifier.weight(1f))
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DropdownField(label = "Ratio", value = selectedRatio, options = listOf("Original", "1:1", "4:5", "16:9", "9:16", "Auto AI"), onSelect = onRatioChange, modifier = Modifier.weight(1f))
            DropdownField(label = "Fit", value = selectedFit, options = listOf("Cover", "Contain"), onSelect = onFitChange, modifier = Modifier.weight(1f))
        }

        SectionTitle("Display Settings & Priority")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                displayLocations.forEach { loc ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = selectedLocations.contains(loc.id), onCheckedChange = { if (it) selectedLocations.add(loc.id) else selectedLocations.remove(loc.id) }, colors = CheckboxDefaults.colors(checkedColor = maroon))
                        Text(loc.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6F6)), border = BorderStroke(1.dp, brandBorder)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Priority", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = maroon)
                    Spacer(modifier = Modifier.height(8.dp))
                    selectedLocations.forEach { id ->
                        val locName = displayLocations.find { it.id == id }?.name ?: ""
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(locName, fontSize = 10.sp)
                            Box(modifier = Modifier.size(width = 32.dp, height = 24.dp).border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)).background(Color.White, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                                Text("${(1..10).random()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = maroon)
                            }
                        }
                    }
                }
            }
        }

        SectionTitle("SEO")
        DetailTextField(label = "Meta Title", value = metaTitle, onValueChange = onMetaTitleChange)
        DetailTextField(label = "Meta Keywords", value = metaKeywords, onValueChange = onMetaKeywordsChange)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF7A0C20),
            letterSpacing = 2.sp
        ),
        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
    )
}

@Composable
fun IdentityField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, bgColor: Color = Color.White) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            textStyle = TextStyle(fontSize = 12.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7A0C20),
                unfocusedBorderColor = Color(0xFFECECEC),
                unfocusedContainerColor = bgColor,
                focusedContainerColor = bgColor
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

@Composable
fun DetailTextField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, minHeight: androidx.compose.ui.unit.Dp = 48.dp) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().heightIn(min = minHeight),
            textStyle = TextStyle(fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7A0C20),
                unfocusedBorderColor = Color(0xFFECECEC),
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun DropdownField(label: String, value: String, options: List<String>, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, Color(0xFFECECEC), RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(value, fontSize = 13.sp, maxLines = 1)
                Icon(Icons.Default.ArrowDropDown, null, tint = Color.Gray)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false })
                }
            }
        }
    }
}

@Composable
fun CustomSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, color: Color) {
    Box(
        modifier = Modifier
            .size(width = 32.dp, height = 14.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(if (checked) color else Color.LightGray)
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

fun Modifier.drawDashedBorder(color: Color, cornerRadius: androidx.compose.ui.unit.Dp): Modifier = this.drawBehind {
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
    )
}
