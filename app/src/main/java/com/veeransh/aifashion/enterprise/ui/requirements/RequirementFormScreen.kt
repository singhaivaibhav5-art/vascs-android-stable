package com.veeransh.aifashion.enterprise.ui.requirements

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.veeransh.aifashion.enterprise.data.local.entity.CustomerRequirementEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.RequirementViewModel
import com.veeransh.aifashion.enterprise.ui.viewmodel.RequirementUiState
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequirementFormScreen(
    productId: String,
    userId: String?,
    viewModel: RequirementViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val maroon = Color(0xFF7A0C20)
    val brandBg = Color(0xFFFFFAFB)
    val brandBorder = Color(0xFFECECEC)
    
    val uiState by viewModel.uiState.collectAsState()
    val product by viewModel.targetProduct.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    
    LaunchedEffect(productId, userId) {
        viewModel.loadContext(productId, userId)
    }

    LaunchedEffect(uiState) {
        if (uiState is RequirementUiState.Success) {
            Toast.makeText(context, (uiState as RequirementUiState.Success).message, Toast.LENGTH_LONG).show()
            onBack()
            viewModel.resetState()
        }
    }

    // Form State
    var quantity by remember { mutableStateOf("1") }
    var description by remember { mutableStateOf("") }
    val images = remember { mutableStateListOf<String>() }
    val supportMedia = remember { mutableStateListOf<String>() }
    
    // Media Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5),
        onResult = { uris ->
            val availableSlots = 5 - images.size
            val toAdd = uris.take(availableSlots).map { it.toString() }
            images.addAll(toAdd)
            if (uris.size > availableSlots) {
                Toast.makeText(context, "Maximum 5 reference images allowed", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val supportMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(3),
        onResult = { uris ->
            val availableSlots = 3 - supportMedia.size
            val toAdd = uris.take(availableSlots).map { it.toString() }
            supportMedia.addAll(toAdd)
            if (uris.size > availableSlots) {
                Toast.makeText(context, "Maximum 3 support files allowed", Toast.LENGTH_SHORT).show()
            }
        }
    )

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempImageUri != null) {
                try {
                    val fileName = "req_ref_${System.currentTimeMillis()}.jpg"
                    val persistentDir = File(context.filesDir, "requirements")
                    if (!persistentDir.exists()) persistentDir.mkdirs()
                    val persistentFile = File(persistentDir, fileName)
                    
                    context.contentResolver.openInputStream(tempImageUri!!)?.use { input ->
                        FileOutputStream(persistentFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    images.add(Uri.fromFile(persistentFile).toString())
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to save camera image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    fun takePhoto() {
        if (images.size >= 5) {
            Toast.makeText(context, "Maximum 5 images allowed", Toast.LENGTH_SHORT).show()
            return
        }
        val file = File(context.cacheDir, "temp_req_capture.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        tempImageUri = uri
        cameraLauncher.launch(uri)
    }

    // Validation
    val isFormValid = description.isNotBlank() && 
                    (quantity.toIntOrNull() ?: 0) >= 1 && 
                    product != null && 
                    user != null && 
                    uiState !is RequirementUiState.Loading

    fun handleSubmit() {
        if (!isFormValid || product == null || user == null) return

        val now = System.currentTimeMillis()
        val datePrefix = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date(now))
        val randomSuffix = (1000..9999).random()
        val reqId = "REQ-$datePrefix-$randomSuffix"

        val entity = CustomerRequirementEntity(
            requirementId = reqId,
            userId = user!!.uid,
            userName = user!!.name,
            userPhone = user!!.phone,
            userEmail = user!!.email,
            productId = product!!.id,
            productName = product!!.name,
            productSku = product!!.sku,
            quantity = quantity.toIntOrNull() ?: 1,
            description = description,
            imageUrisJson = images.toList().toJsonArray(),
            supportMediaUrisJson = supportMedia.toList().toJsonArray(),
            status = "PENDING",
            adminNotes = "",
            createdAt = now,
            updatedAt = now
        )

        viewModel.submitRequirement(entity)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Custom Saree Request", style = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = brandBg
    ) { padding ->
        when (val state = uiState) {
            is RequirementUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = maroon)
                }
            }
            is RequirementUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(state.message, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(24.dp))
                        Button(onClick = { viewModel.loadContext(productId, userId) }, colors = ButtonDefaults.buttonColors(containerColor = maroon)) {
                            Text("Retry")
                        }
                    }
                }
            }
            else -> {
                if (product == null || user == null) {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text("Verifying authentication and product context...")
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // 1. PRODUCT INFORMATION (DISPLAY ONLY)
                        RequirementFormSection("PRODUCT DETAILS") {
                            ReadOnlyField("Product Name", product?.name ?: "N/A")
                            ReadOnlyField("SKU", product?.sku ?: "N/A")
                            ReadOnlyField("Internal ID", productId)
                        }

                        // 2. USER INFORMATION (DISPLAY ONLY)
                        RequirementFormSection("YOUR IDENTITY") {
                            ReadOnlyField("Name", user?.name ?: "Guest")
                            ReadOnlyField("Phone", user?.phone ?: "N/A")
                            ReadOnlyField("Email", user?.email ?: "N/A")
                        }

                        // 3. REQUIREMENTS (EDITABLE)
                        RequirementFormSection("CUSTOMIZATION DETAILS") {
                            OutlinedTextField(
                                value = quantity,
                                onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) quantity = it },
                                label = { Text("Target Quantity") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = maroon)
                            )

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Requirement Description *") },
                                modifier = Modifier.fillMaxWidth().height(160.dp),
                                placeholder = { Text("Describe specific colors, patterns, or fabric changes required...") },
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = maroon)
                            )
                        }

                        // 4. REFERENCE MEDIA
                        RequirementFormSection("REFERENCE IMAGES (MAX 5)") {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { takePhoto() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = images.size < 5
                                ) {
                                    Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Camera", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = images.size < 5
                                ) {
                                    Icon(Icons.Default.Collections, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Gallery", fontSize = 12.sp)
                                }
                            }

                            if (images.isNotEmpty()) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(images) { uri ->
                                        Box(Modifier.size(100.dp)) {
                                            AsyncImage(
                                                model = uri,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).border(1.dp, brandBorder, RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                            Surface(
                                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                                color = Color.Black.copy(alpha = 0.5f),
                                                shape = CircleShape
                                            ) {
                                                IconButton(
                                                    onClick = { images.remove(uri) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 5. SUPPORT MEDIA
                        RequirementFormSection("SUPPORT MEDIA (IMAGES/VIDEOS - MAX 3)") {
                            Button(
                                onClick = { supportMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                                shape = RoundedCornerShape(8.dp),
                                enabled = supportMedia.size < 3
                            ) {
                                Icon(Icons.Default.VideoCall, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Attach Media", fontSize = 12.sp)
                            }

                            if (supportMedia.isNotEmpty()) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(supportMedia) { uri ->
                                        Box(Modifier.size(100.dp)) {
                                            val isVideo = uri.contains("video", ignoreCase = true) || uri.endsWith(".mp4") || uri.endsWith(".mov")
                                            
                                            if (isVideo) {
                                                Box(
                                                    Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(Color.Black).border(1.dp, brandBorder, RoundedCornerShape(8.dp)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.PlayCircle, null, tint = Color.White, modifier = Modifier.size(40.dp))
                                                }
                                            } else {
                                                AsyncImage(
                                                    model = uri,
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).border(1.dp, brandBorder, RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            
                                            Surface(
                                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                                color = Color.Black.copy(alpha = 0.5f),
                                                shape = CircleShape
                                            ) {
                                                IconButton(
                                                    onClick = { supportMedia.remove(uri) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 6. SUBMIT ACTION
                        Button(
                            onClick = { handleSubmit() },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(27.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = maroon),
                            enabled = isFormValid
                        ) {
                            Text("SUBMIT CUSTOM REQUEST", fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RequirementFormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF7A0C20), letterSpacing = 2.sp)
        )
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
fun ReadOnlyField(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
    }
}

// Extension helper for manual JSON array (matching ProductStudio pattern)
fun List<String>.toJsonArray(): String {
    if (isEmpty()) return "[]"
    return joinToString(separator = "\",\"", prefix = "[\"", postfix = "\"]")
}
