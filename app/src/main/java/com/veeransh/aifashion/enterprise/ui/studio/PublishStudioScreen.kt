package com.veeransh.aifashion.enterprise.ui.studio

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.data.local.entity.PlacementEntity
import com.veeransh.aifashion.enterprise.data.local.entity.ProductEntity
import com.veeransh.aifashion.enterprise.types.PlacementConstants
import com.veeransh.aifashion.enterprise.util.PlacementImageProcessor
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishStudioScreen(
    productId: String,
    viewModel: PublishStudioViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()

    var showForm by remember { mutableStateOf<PlacementEntity?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        viewModel.init(productId)
    }

    LaunchedEffect(operationResult) {
        operationResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, it.getOrNull(), Toast.LENGTH_SHORT).show()
                showForm = null
            } else {
                Toast.makeText(context, "Error: ${it.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
            isSaving = false
            viewModel.resetOperationResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publish & Placement Studio", style = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (uiState is PublishStudioUiState.Success && !isSaving) {
                ExtendedFloatingActionButton(
                    onClick = { 
                        showForm = PlacementEntity(
                            productId = productId,
                            placementType = PlacementConstants.TYPES.first().machineValue,
                            title = (uiState as PublishStudioUiState.Success).product.name
                        ) 
                    },
                    containerColor = Color(0xFF7A0C20),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Add Placement") }
                )
            }
        },
        containerColor = Color(0xFFFFFAFB)
    ) { padding ->
        when (val state = uiState) {
            is PublishStudioUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF7A0C20))
                }
            }
            is PublishStudioUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Color.Red, modifier = Modifier.padding(24.dp))
                }
            }
            is PublishStudioUiState.Success -> {
                PublishStudioContent(
                    padding = padding,
                    product = state.product,
                    placements = state.placements,
                    onEdit = { if(!isSaving) showForm = it },
                    onDelete = { if(!isSaving) viewModel.deletePlacement(it) },
                    onToggleActive = { if(!isSaving) viewModel.toggleActiveState(it) }
                )
            }
        }
        
        if (isSaving) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = Color(0xFF7A0C20))
                        Spacer(Modifier.width(16.dp))
                        Text("Processing optimized image...", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showForm != null && uiState is PublishStudioUiState.Success) {
        PlacementFormDialog(
            placement = showForm!!,
            product = (uiState as PublishStudioUiState.Success).product,
            onDismiss = { if(!isSaving) showForm = null },
            onSave = { 
                isSaving = true
                viewModel.savePlacement(it) 
            },
            onRegenerate = {
                isSaving = true
                viewModel.regenerateImage(it)
            },
            onTypeChange = { newType ->
                if (showForm!!.placementId == 0L) {
                    val defaults = viewModel.getTemplateDefaults(newType)
                    if (defaults != null) {
                        showForm = defaults.copy(title = showForm!!.title, sourceUri = showForm!!.sourceUri)
                    }
                }
            }
        )
    }
}

@Composable
fun PublishStudioContent(
    padding: PaddingValues,
    product: ProductEntity,
    placements: List<PlacementEntity>,
    onEdit: (PlacementEntity) -> Unit,
    onDelete: (PlacementEntity) -> Unit,
    onToggleActive: (PlacementEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProductHeaderCard(product)
        }

        item {
            Text(
                "ACTIVE PLACEMENTS (OPTIMIZED DERIVATIVES)",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Gray, letterSpacing = 1.sp)
            )
        }

        if (placements.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No placements yet. Click + to add.", color = Color.Gray)
                }
            }
        } else {
            items(placements) { placement ->
                PlacementCard(
                    placement = placement,
                    productImage = product.image,
                    onEdit = { onEdit(placement) },
                    onDelete = { onDelete(placement) },
                    onToggleActive = { onToggleActive(placement) }
                )
            }
        }
    }
}

@Composable
fun ProductHeaderCard(product: ProductEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECECEC))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("SKU: ${product.sku}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PlacementCard(
    placement: PlacementEntity,
    productImage: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (placement.isActive) Color(0xFF0D5C36).copy(alpha = 0.3f) else Color(0xFFECECEC))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF1F5F9))
                ) {
                    AsyncImage(
                        model = placement.imageUri.ifBlank { productImage },
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = PlacementConstants.TYPES.find { it.machineValue == placement.placementType }?.displayName?.uppercase() ?: placement.placementType.uppercase(),
                        fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF7A0C20)
                    )
                    Text(placement.title.ifBlank { "Untitled Placement" }, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("${placement.aspectRatio} • ${placement.cropMode}", fontSize = 11.sp, color = Color.Gray)
                }
                Switch(checked = placement.isActive, onCheckedChange = { onToggleActive() })
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Sort, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(" Priority: ${placement.priority}", fontSize = 12.sp, color = Color.Gray)
                }
                Row {
                    TextButton(onClick = onEdit) { Text("Edit") }
                    TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) { Text("Delete") }
                }
            }
        }
    }
}

@Composable
fun PlacementFormDialog(
    placement: PlacementEntity,
    product: ProductEntity,
    onDismiss: () -> Unit,
    onSave: (PlacementEntity) -> Unit,
    onRegenerate: (PlacementEntity) -> Unit,
    onTypeChange: (String) -> Unit
) {
    var type by remember(placement.placementId) { mutableStateOf(placement.placementType) }
    var title by remember(placement.placementId) { mutableStateOf(placement.title) }
    var sourceUri by remember(placement.placementId) { mutableStateOf(placement.sourceUri) }
    var aspectRatio by remember(placement.placementId) { mutableStateOf(placement.aspectRatio) }
    var cropMode by remember(placement.placementId) { mutableStateOf(placement.cropMode) }
    var priority by remember(placement.placementId) { mutableStateOf(placement.priority.toString()) }
    var sortOrder by remember(placement.placementId) { mutableStateOf(placement.sortOrder.toString()) }
    var isActive by remember(placement.placementId) { mutableStateOf(placement.isActive) }
    
    // Normalized Crop State
    var scale by remember(placement.placementId) { mutableFloatStateOf(placement.cropScale) }
    var nOffsetX by remember(placement.placementId) { mutableFloatStateOf(placement.cropOffsetX) }
    var nOffsetY by remember(placement.placementId) { mutableFloatStateOf(placement.cropOffsetY) }

    val isStale = remember(type, sourceUri, aspectRatio, cropMode, scale, nOffsetX, nOffsetY) {
        val currentSource = if(sourceUri.isBlank()) product.image else sourceUri
        val tempPlacement = placement.copy(
            placementType = type,
            aspectRatio = aspectRatio,
            cropMode = cropMode,
            cropScale = scale,
            cropOffsetX = nOffsetX,
            cropOffsetY = nOffsetY,
            targetWidthPx = placement.targetWidthPx,
            targetHeightPx = placement.targetHeightPx
        )
        PlacementImageProcessor.isStale(tempPlacement, currentSource)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            if (uri != null) {
                sourceUri = uri.toString()
                // Reset crop when source changes
                scale = 1.0f
                nOffsetX = 0f
                nOffsetY = 0f
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (placement.placementId == 0L) "New Placement" else "Edit Placement") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    // INTERACTIVE PREVIEW
                    Text("PLACEMENT PREVIEW (ZOOM & PAN)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                    
                    val frameRatio = when (aspectRatio) {
                        "16:9" -> 16f / 9f
                        "1:1" -> 1f
                        "4:5" -> 4f / 5f
                        "3:4" -> 3f / 4f
                        "9:16" -> 9f / 16f
                        else -> 1f
                    }

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(frameRatio)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .pointerInput(cropMode) {
                                if (cropMode == "CenterCrop") {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        scale = (scale * zoom).coerceIn(1.0f, 4.0f)
                                        nOffsetX += pan.x / size.width
                                        nOffsetY += pan.y / size.width
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val widthPx = this.constraints.maxWidth.toFloat()
                        AsyncImage(
                            model = sourceUri.ifBlank { product.image },
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = nOffsetX * widthPx,
                                    translationY = nOffsetY * widthPx
                                ),
                            contentScale = when (cropMode) {
                                "CenterCrop" -> ContentScale.Crop
                                "Fit" -> ContentScale.Fit
                                else -> ContentScale.FillBounds
                            }
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Zoom: ${(scale * 100).toInt()}%", fontSize = 11.sp, color = Color.Gray)
                            Text("Target: ${placement.targetWidthPx} × ${placement.targetHeightPx} px", fontSize = 10.sp, color = Color.Gray)
                        }
                        TextButton(
                            onClick = {
                                scale = 1.0f
                                nOffsetX = 0f
                                nOffsetY = 0f
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("Reset Crop", fontSize = 11.sp)
                        }
                    }
                }

                item {
                    val statusText = if (isStale) "Needs regeneration" else "Processed"
                    val statusColor = if (isStale) Color(0xFF7A0C20) else Color(0xFF0D5C36)
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = statusText.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = statusColor
                            )
                        }
                        if (isStale && placement.placementId != 0L) {
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = {
                                onRegenerate(placement.copy(
                                    placementType = type,
                                    sourceUri = sourceUri,
                                    aspectRatio = aspectRatio,
                                    cropMode = cropMode,
                                    cropScale = scale,
                                    cropOffsetX = nOffsetX,
                                    cropOffsetY = nOffsetY
                                ))
                            }) {
                                Text("Regenerate Now", fontSize = 11.sp)
                            }
                        }
                    }
                }

                item {
                    val types = PlacementConstants.TYPES.map { it.machineValue to it.displayName }
                    PlacementDropdown("Placement Type", type, types, { 
                        type = it
                        onTypeChange(it)
                    })
                }

                item {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Display Title") }, modifier = Modifier.fillMaxWidth())
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = if (sourceUri.isBlank()) "Product Main Image" else "Override Image Selected",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Source Image") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                            Icon(Icons.Default.Collections, null, tint = Color(0xFF7A0C20))
                        }
                        if (sourceUri.isNotBlank()) {
                            IconButton(onClick = { sourceUri = "" }) {
                                Icon(Icons.Default.Clear, null, tint = Color.Red)
                            }
                        }
                    }
                }

                item {
                    val ratios = PlacementConstants.ASPECT_RATIOS.map { it to it }
                    PlacementDropdown("Aspect Ratio", aspectRatio, ratios, { aspectRatio = it })
                }

                item {
                    val modes = PlacementConstants.CROP_MODES.map { it to it }
                    PlacementDropdown("Crop Mode", cropMode, modes, { cropMode = it })
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = priority, onValueChange = { priority = it }, label = { Text("Priority") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = sortOrder, onValueChange = { sortOrder = it }, label = { Text("Sort Order") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }
                }
                
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Active State", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Switch(checked = isActive, onCheckedChange = { isActive = it })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(placement.copy(
                        placementType = type,
                        title = title,
                        imageUri = placement.imageUri,
                        sourceUri = sourceUri,
                        aspectRatio = aspectRatio,
                        cropMode = cropMode,
                        priority = priority.toIntOrNull() ?: 0,
                        sortOrder = sortOrder.toIntOrNull() ?: 0,
                        isActive = isActive,
                        updatedAt = System.currentTimeMillis(),
                        cropScale = scale,
                        cropOffsetX = nOffsetX,
                        cropOffsetY = nOffsetY
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A0C20))
            ) { Text("Save & Process") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PlacementDropdown(label: String, value: String, options: List<Pair<String, String>>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = options.find { it.first == value }?.second ?: value
    
    Box {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { IconButton(onClick = { expanded = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
            modifier = Modifier.fillMaxWidth().clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt.second) }, onClick = { onSelect(opt.first); expanded = false })
            }
        }
    }
}
