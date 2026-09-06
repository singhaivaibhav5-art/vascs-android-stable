package com.veeransh.aifashion.enterprise.ui.studio

import android.Manifest
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.types.UserPDPCouponItem
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProductStudioScreen(
    productId: String? = null,
    viewModel: ProductStudioViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToPlacement: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val saveResult by viewModel.saveResult.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(saveResult) {
        saveResult?.let { result ->
            when (result) {
                is SaveOperationResult.Success -> {
                    Toast.makeText(context, "Product Saved Successfully", Toast.LENGTH_SHORT).show()
                    if (result.shouldNavigate) {
                        onNavigateToPlacement(result.productId)
                    }
                    viewModel.resetSaveResult()
                }
                is SaveOperationResult.Failure -> {
                    Toast.makeText(context, "Save Failed: ${result.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetSaveResult()
                }
            }
        }
    }

    when (val state = uiState) {
        is ProductStudioUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF7A0C20))
            }
        }
        is ProductStudioUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = Color.Red, modifier = Modifier.padding(24.dp))
            }
        }
        is ProductStudioUiState.Success -> {
            ProductStudioContent(
                initialProduct = state.product,
                onBack = onBack,
                onSave = { p, andNav -> viewModel.saveDraft(p, andNav) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductStudioContent(
    initialProduct: ProductEntity,
    onBack: () -> Unit,
    onSave: (ProductEntity, Boolean) -> Unit
) {
    val context = LocalContext.current
    
    // Form State
    var name by remember { mutableStateOf(initialProduct.name) }
    var sku by remember { mutableStateOf(initialProduct.sku) }
    var barcode by remember { mutableStateOf(initialProduct.barcode) }
    var category by remember { mutableStateOf(initialProduct.category) }
    var subCategory by remember { mutableStateOf(initialProduct.subCategory) }
    var brand by remember { mutableStateOf(initialProduct.brand) }
    var fabric by remember { mutableStateOf(initialProduct.fabric) }
    var colour by remember { mutableStateOf(initialProduct.colour) }
    var size by remember { mutableStateOf(initialProduct.size) }
    var hsn by remember { mutableStateOf(initialProduct.hsn) }
    var gst by remember { mutableDoubleStateOf(initialProduct.gst) }
    var purchasePrice by remember { mutableStateOf(if(initialProduct.purchasePrice > 0) initialProduct.purchasePrice.toString() else "") }
    var wholesalePrice by remember { mutableStateOf(if(initialProduct.wholesalePrice > 0) initialProduct.wholesalePrice.toString() else "") }
    var retailPrice by remember { mutableStateOf(if(initialProduct.retailPrice > 0) initialProduct.retailPrice.toString() else "") }
    var dealerPrice by remember { mutableStateOf(if(initialProduct.dealerPrice > 0) initialProduct.dealerPrice.toString() else "") }
    var partnerPrice by remember { mutableStateOf(if(initialProduct.partnerPrice > 0) initialProduct.partnerPrice.toString() else "") }
    var mrp by remember { mutableStateOf(if(initialProduct.mrp > 0) initialProduct.mrp.toString() else "") }
    var discount by remember { mutableDoubleStateOf(initialProduct.discount) }
    var stock by remember { mutableIntStateOf(initialProduct.stock) }
    var lowStockAlert by remember { mutableIntStateOf(initialProduct.lowStockAlert) }
    var location by remember { mutableStateOf(initialProduct.location) }
    var weight by remember { mutableStateOf(initialProduct.weight) }
    var description by remember { mutableStateOf(initialProduct.description) }
    
    // Images State
    val images = remember { mutableStateListOf<String>().apply { 
        addAll(initialProduct.imagesJson.parseJsonArray())
        if (isEmpty() && initialProduct.image.isNotBlank()) {
            add(initialProduct.image)
        }
    } }
    var mainImageUri by remember { mutableStateOf(initialProduct.image) }
    
    // Ensure consistency
    if (mainImageUri.isBlank() && images.isNotEmpty()) {
        mainImageUri = images[0]
    } else if (mainImageUri.isNotBlank() && !images.contains(mainImageUri)) {
        mainImageUri = if (images.isNotEmpty()) images[0] else ""
    }

    // Coupons State
    val allPossibleCoupons = listOf(
        UserPDPCouponItem("COUP5", "5% OFF", "PERCENT", 5.0),
        UserPDPCouponItem("COUP10", "10% OFF", "PERCENT", 10.0),
        UserPDPCouponItem("FLAT100", "₹100 OFF", "FIXED", 100.0)
    )
    val initialCoupons = initialProduct.tags.split(",").find { it.trim().startsWith("coupons:") }
        ?.substringAfter("coupons:")?.split("|")?.filter { it.isNotBlank() } ?: emptyList()
    val selectedCouponCodes = remember { mutableStateListOf<String>().apply { addAll(initialCoupons) } }

    // Helper functions
    fun validate(): String? {
        if (name.isBlank()) return "Product Name is required"
        if (sku.isBlank()) return "SKU is required"
        if (retailPrice.toDoubleOrNull() == null || retailPrice.toDouble() < 0) return "Invalid Retail Price"
        if (stock < 0) return "Stock cannot be negative"
        return null
    }

    fun buildProduct(): ProductEntity {
        val couponTag = if (selectedCouponCodes.isNotEmpty()) "coupons:${selectedCouponCodes.joinToString("|")}" else ""
        val existingTags = initialProduct.tags.split(",").filter { !it.trim().startsWith("coupons:") }.joinToString(",")
        val finalTags = if (couponTag.isNotEmpty()) "$existingTags,$couponTag".trim(',') else existingTags
        
        return initialProduct.copy(
            name = name, sku = sku, barcode = barcode, category = category, subCategory = subCategory,
            brand = brand, fabric = fabric, colour = colour, size = size, hsn = hsn, gst = gst,
            purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
            wholesalePrice = wholesalePrice.toDoubleOrNull() ?: 0.0,
            retailPrice = retailPrice.toDoubleOrNull() ?: 0.0,
            dealerPrice = dealerPrice.toDoubleOrNull() ?: 0.0,
            partnerPrice = partnerPrice.toDoubleOrNull() ?: 0.0,
            mrp = mrp.toDoubleOrNull() ?: 0.0,
            discount = discount, stock = stock, lowStockAlert = lowStockAlert,
            weight = weight, location = location, description = description, 
            tags = finalTags,
            image = mainImageUri,
            imagesJson = images.toJsonArray()
        )
    }

    // Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            images.addAll(uris.map { it.toString() })
            if (mainImageUri.isBlank() && images.isNotEmpty()) mainImageUri = images[0]
        }
    )

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempImageUri != null) {
                try {
                    // Copy to persistent storage
                    val fileName = "prod_${initialProduct.id}_${System.currentTimeMillis()}.jpg"
                    val persistentDir = File(context.filesDir, "products")
                    if (!persistentDir.exists()) persistentDir.mkdirs()
                    val persistentFile = File(persistentDir, fileName)
                    
                    context.contentResolver.openInputStream(tempImageUri!!)?.use { input ->
                        FileOutputStream(persistentFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    val persistentUri = Uri.fromFile(persistentFile).toString()
                    images.add(persistentUri)
                    if (mainImageUri.isBlank()) mainImageUri = persistentUri
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to save camera image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    fun takePhoto() {
        val file = File(context.cacheDir, "temp_capture.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        tempImageUri = uri
        cameraLauncher.launch(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Studio", style = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val err = validate()
                            if (err != null) Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            else onSave(buildProduct(), false)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Draft")
                    }
                    Button(
                        onClick = {
                            val err = validate()
                            if (err != null) Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                            else onSave(buildProduct(), true)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A0C20))
                    ) {
                        Text("Next: Placement")
                    }
                }
            }
        },
        containerColor = Color(0xFFFFFAFB)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // IMAGE SECTION
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("PRODUCT IMAGES", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color(0xFF7A0C20))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { takePhoto() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Camera", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Collections, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Gallery", fontSize = 12.sp)
                        }
                    }

                    if (mainImageUri.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().aspectRatio(4f/5f),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Box(Modifier.fillMaxSize()) {
                                AsyncImage(model = mainImageUri, contentDescription = "Main Image", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                Badge(Modifier.align(Alignment.BottomStart).padding(12.dp), containerColor = Color(0xFFE9C46A)) {
                                    Text("MAIN IMAGE", modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                                Surface(
                                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = CircleShape
                                ) {
                                    IconButton(onClick = { 
                                        images.remove(mainImageUri)
                                        mainImageUri = images.firstOrNull() ?: ""
                                    }) {
                                        Icon(Icons.Default.Delete, null, tint = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        itemsIndexed(images) { index, uri ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).border(if (uri == mainImageUri) 2.dp else 1.dp, if (uri == mainImageUri) Color(0xFFE9C46A) else Color.LightGray, RoundedCornerShape(8.dp)).clickable { mainImageUri = uri }) {
                                    AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    
                                    // Small delete for thumbnails that are not main
                                    if (uri != mainImageUri) {
                                        IconButton(
                                            onClick = { images.remove(uri) },
                                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                                
                                // Reorder controls
                                Row {
                                    IconButton(
                                        onClick = { if (index > 0) { val item = images.removeAt(index); images.add(index - 1, item) } },
                                        modifier = Modifier.size(28.dp),
                                        enabled = index > 0
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { if (index < images.size - 1) { val item = images.removeAt(index); images.add(index + 1, item) } },
                                        modifier = Modifier.size(28.dp),
                                        enabled = index < images.size - 1
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTIONS
            item {
                ProductStudioFormSection("PRODUCT IDENTITY") {
                    StudioTextField("Product ID (Auto)", initialProduct.id, {}, readOnly = true)
                    StudioTextField("SKU *", sku, { sku = it })
                    StudioTextField("Barcode", barcode, { barcode = it })
                }
            }

            item {
                ProductStudioFormSection("PRODUCT INFORMATION") {
                    StudioTextField("Product Name *", name, { name = it })
                    DropdownField("Category", category, listOf("Banarasi", "Silk", "Cotton", "Kanjivaram", "Chanderi", "Paithani", "Organza", "Georgette", "Tussar", "Designer"), { category = it })
                    StudioTextField("Sub Category", subCategory, { subCategory = it })
                    StudioTextField("Brand", brand, { brand = it })
                    StudioTextField("Fabric", fabric, { fabric = it })
                    StudioTextField("Colour", colour, { colour = it })
                    DropdownField("Size", size, listOf("Free Size", "S", "M", "L", "Custom"), { size = it })
                }
            }

            item {
                ProductStudioFormSection("COMMERCIAL") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StudioTextField("Purchase Price", purchasePrice, { purchasePrice = it }, Modifier.weight(1f), KeyboardType.Number)
                        StudioTextField("MRP", mrp, { mrp = it }, Modifier.weight(1f), KeyboardType.Number)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StudioTextField("Retail Price *", retailPrice, { retailPrice = it }, Modifier.weight(1f), KeyboardType.Number)
                        StudioTextField("Wholesale Price", wholesalePrice, { wholesalePrice = it }, Modifier.weight(1f), KeyboardType.Number)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StudioTextField("Dealer Price", dealerPrice, { dealerPrice = it }, Modifier.weight(1f), KeyboardType.Number)
                        StudioTextField("Partner Price", partnerPrice, { partnerPrice = it }, Modifier.weight(1f), KeyboardType.Number)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StudioTextField("Discount %", discount.toString(), { discount = it.toDoubleOrNull() ?: 0.0 }, Modifier.weight(1f), KeyboardType.Number)
                        DropdownField("GST %", "${gst.toInt()}%", listOf("0%", "5%", "12%", "18%", "28%"), { gst = it.removeSuffix("%").toDoubleOrNull() ?: 5.0 }, Modifier.weight(1f))
                    }
                }
            }

            item {
                ProductStudioFormSection("INVENTORY") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Stock: $stock", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if(stock > 0) stock-- }) { Icon(Icons.Default.Remove, null) }
                        IconButton(onClick = { stock++ }) { Icon(Icons.Default.Add, null) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Low Stock Alert: $lowStockAlert", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if(lowStockAlert > 0) lowStockAlert-- }) { Icon(Icons.Default.Remove, null) }
                        IconButton(onClick = { lowStockAlert++ }) { Icon(Icons.Default.Add, null) }
                    }
                    StudioTextField("Location", location, { location = it })
                }
            }

            item {
                ProductStudioFormSection("PRODUCT CONTENT") {
                    StudioTextField("Description", description, { description = it }, minLines = 3)
                }
            }

            item {
                ProductStudioFormSection("COUPONS") {
                    Text("Select Allowed Coupons", fontSize = 12.sp, color = Color.Gray)
                    allPossibleCoupons.forEach { coupon ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {
                            if (selectedCouponCodes.contains(coupon.code)) selectedCouponCodes.remove(coupon.code)
                            else selectedCouponCodes.add(coupon.code)
                        }.padding(vertical = 4.dp)) {
                            Checkbox(checked = selectedCouponCodes.contains(coupon.code), onCheckedChange = null)
                            Spacer(Modifier.width(8.dp))
                            Text(coupon.code, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Text(coupon.discount, color = Color(0xFF7A0C20), fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Text(
                    "NEXT PHASE: PUBLISH & PLACEMENT STUDIO\n(Hero, Banners, Grids, Aspect Ratios, Schedule)",
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Gray, textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ProductStudioFormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECECEC)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                content()
            }
        }
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
fun StudioTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines = minLines,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF7A0C20),
            unfocusedBorderColor = Color(0xFFECECEC)
        )
    )
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

// Extension helpers for manual JSON array (since GSON is not added)
fun List<String>.toJsonArray(): String {
    if (isEmpty()) return "[]"
    return joinToString(separator = "\",\"", prefix = "[\"", postfix = "\"]")
}

fun String.parseJsonArray(): List<String> {
    if (this == "[]" || isBlank()) return emptyList()
    return removePrefix("[\"").removeSuffix("\"]").split("\",\"").filter { it.isNotBlank() }
}
