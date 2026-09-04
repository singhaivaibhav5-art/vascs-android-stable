package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.DrapeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSareeScreen(
    onSave: (ProductEntity) -> Unit,
    onBack: () -> Unit,
    selectedIds: String? = null,
    drapeViewModel: DrapeViewModel = hiltViewModel()
) {
    val results by drapeViewModel.drapeResults.collectAsState()
    val idList = remember(selectedIds) { selectedIds?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList() }
    val selectedResults = results.filter { idList.contains(it.id) }

    // Section 1 - Basic Details
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("VEER-SKU-BAN-${System.currentTimeMillis()}") }
    var barcode by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Silk") }
    var subCategory by remember { mutableStateOf("") }
    
    // Section 2 - Fabric & Design
    var fabric by remember { mutableStateOf("Pure Silk") }
    var colour by remember { mutableStateOf("Red") }
    var designPattern by remember { mutableStateOf("") }
    var workType by remember { mutableStateOf("Zari") }
    var length by remember { mutableStateOf("6.3m") }
    var blouseIncluded by remember { mutableStateOf(true) }

    // Section 3 - Pricing
    var retailPrice by remember { mutableStateOf("") }
    var primaryImageUri by remember { mutableStateOf(selectedResults.firstOrNull()?.drapedUri ?: "") }
    var wholesalePrice by remember { mutableStateOf("") }
    var dealerPrice by remember { mutableStateOf("") }
    var partnerPrice by remember { mutableStateOf("") }
    var purchasePrice by remember { mutableStateOf("") }
    var mrp by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("0") }
    var gst by remember { mutableStateOf("5") }
    var hsn by remember { mutableStateOf("5407") }

    // Section 4 - Inventory
    var stock by remember { mutableStateOf("") }
    var moq by remember { mutableStateOf("1") }
    var lowStockAlert by remember { mutableStateOf("5") }
    var weight by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Section 5 - Media
    var imageUri by remember { mutableStateOf("") }

    // Section 6 - Supplier & Other
    var supplierName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedGst by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1E8))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0D5C36),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "ADD NEW SAREE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedResults.isNotEmpty()) {
                Text("AI DRAPED PREVIEW", fontWeight = FontWeight.Black, color = Color(0xFF0D5C36), fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedResults.forEach { result ->
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(0.75f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (primaryImageUri == result.drapedUri) 3.dp else 1.dp,
                                    color = if (primaryImageUri == result.drapedUri) Color(0xFF0D5C36) else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { primaryImageUri = result.drapedUri }
                        ) {
                            AsyncImage(
                                model = result.drapedUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (primaryImageUri == result.drapedUri) {
                                Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color(0xFF0D5C36), CircleShape).padding(4.dp)) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Section 1: Basic Details
            FormSection(title = "Basic Details") {
                ERPTexField(value = name, onValueChange = { name = it }, label = "Product Name *")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ERPTexField(value = sku, onValueChange = { sku = it }, label = "SKU Code *", modifier = Modifier.weight(1f))
                    ERPTexField(
                        value = barcode, 
                        onValueChange = { barcode = it }, 
                        label = "Barcode", 
                        modifier = Modifier.weight(1f),
                        trailingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color(0xFF0D5C36)) }
                    )
                }
                
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        listOf("Silk", "Cotton", "Banarasi", "Georgette", "Daily Wear").forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    category = selection
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }
                ERPTexField(value = subCategory, onValueChange = { subCategory = it }, label = "Sub-Category")
            }

            // Section 2: Fabric & Design
            FormSection(title = "Fabric & Design") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ERPTexField(value = fabric, onValueChange = { fabric = it }, label = "Fabric Type", modifier = Modifier.weight(1f))
                    ERPTexField(value = colour, onValueChange = { colour = it }, label = "Color", modifier = Modifier.weight(1f))
                }
                ERPTexField(value = designPattern, onValueChange = { designPattern = it }, label = "Design Pattern")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ERPTexField(value = workType, onValueChange = { workType = it }, label = "Work Type", modifier = Modifier.weight(1f))
                    ERPTexField(value = length, onValueChange = { length = it }, label = "Length", modifier = Modifier.weight(1f))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Blouse Included", modifier = Modifier.weight(1f))
                    Switch(checked = blouseIncluded, onCheckedChange = { blouseIncluded = it })
                }
            }

            // Section 3: Pricing
            FormSection(title = "Pricing & GST") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ERPTexField(value = purchasePrice, onValueChange = { purchasePrice = it }, label = "Purchase Price", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                    ERPTexField(value = mrp, onValueChange = { mrp = it }, label = "MRP", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ERPTexField(value = retailPrice, onValueChange = { retailPrice = it }, label = "Retail Price *", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                    ERPTexField(value = wholesalePrice, onValueChange = { wholesalePrice = it }, label = "Wholesale Price *", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ERPTexField(value = dealerPrice, onValueChange = { dealerPrice = it }, label = "B2B Dealer Price *", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                    ERPTexField(value = partnerPrice, onValueChange = { partnerPrice = it }, label = "Style Partner Price *", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    ExposedDropdownMenuBox(
                        expanded = expandedGst,
                        onExpandedChange = { expandedGst = !expandedGst },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "$gst%",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("GST %") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGst) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = expandedGst, onDismissRequest = { expandedGst = false }) {
                            listOf("5", "12", "18").forEach { selection ->
                                DropdownMenuItem(text = { Text("$selection%") }, onClick = { gst = selection; expandedGst = false })
                            }
                        }
                    }
                    ERPTexField(value = hsn, onValueChange = { hsn = it }, label = "HSN Code", modifier = Modifier.weight(1f))
                }
            }

            // Section 4: Inventory
            FormSection(title = "Inventory Control") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ERPTexField(value = stock, onValueChange = { stock = it }, label = "Opening Stock *", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                    ERPTexField(value = moq, onValueChange = { moq = it }, label = "MOQ", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ERPTexField(value = lowStockAlert, onValueChange = { lowStockAlert = it }, label = "Low Stock Alert", modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
                    ERPTexField(value = weight, onValueChange = { weight = it }, label = "Weight (gm)", modifier = Modifier.weight(1f))
                }
                ERPTexField(value = location, onValueChange = { location = it }, label = "Rack / Location")
            }

            // Section 5: Media
            FormSection(title = "Product Media") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F5F9))
                        .clickable { /* Handle upload */ },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri.isEmpty()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                            Text("Upload Product Images (Max 5)", color = Color.Gray, fontSize = 12.sp)
                        }
                    } else {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
            }

            // Section 6: Supplier & Other
            FormSection(title = "Supplier & Description") {
                ERPTexField(value = supplierName, onValueChange = { supplierName = it }, label = "Supplier Name")
                ERPTexField(value = description, onValueChange = { description = it }, label = "Description", minLines = 3)
                ERPTexField(value = tags, onValueChange = { tags = it }, label = "Tags (comma separated)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isBlank() || stock.isBlank() || retailPrice.isBlank()) {
                        // Show error or toast
                        return@Button
                    }
                    
                    val newProduct = ProductEntity(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        sku = sku,
                        barcode = barcode,
                        category = category,
                        subCategory = subCategory,
                        brand = "Veeransh",
                        fabric = fabric,
                        colour = colour,
                        size = length,
                        hsn = hsn,
                        gst = gst.toDoubleOrNull() ?: 5.0,
                        purchasePrice = purchasePrice.toDoubleOrNull() ?: 0.0,
                        wholesalePrice = wholesalePrice.toDoubleOrNull() ?: 0.0,
                        retailPrice = retailPrice.toDoubleOrNull() ?: 0.0,
                        dealerPrice = dealerPrice.toDoubleOrNull() ?: 0.0,
                        partnerPrice = partnerPrice.toDoubleOrNull() ?: 0.0,
                        mrp = mrp.toDoubleOrNull() ?: 0.0,
                        discount = discount.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0,
                        moq = moq.toIntOrNull() ?: 1,
                        lowStockAlert = lowStockAlert.toIntOrNull() ?: 5,
                        weight = weight,
                        location = location,
                        image = imageUri,
                        supplierName = supplierName,
                        description = description,
                        tags = tags,
                        createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    )
                    onSave(newProduct)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SAVE PRODUCT TO ERP", fontWeight = FontWeight.Black)
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom nav
        }
    }
}

@Composable
fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D5C36))
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun ERPTexField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines = minLines,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0D5C36),
            unfocusedBorderColor = Color.LightGray
        )
    )
}
