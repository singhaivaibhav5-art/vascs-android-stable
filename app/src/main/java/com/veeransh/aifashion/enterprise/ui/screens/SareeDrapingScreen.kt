package com.veeransh.aifashion.enterprise.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.veeransh.aifashion.enterprise.data.local.entity.AiDrapeResultEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.DrapeUiState
import com.veeransh.aifashion.enterprise.ui.viewmodel.DrapeViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SareeDrapingScreen(
    navController: NavController,
    viewModel: DrapeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val results by viewModel.drapeResults.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()

    val permissions = mutableListOf(Manifest.permission.CAMERA)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val permissionState = rememberMultiplePermissionsState(permissions)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Drape Studio V1.0", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (uiState is DrapeUiState.Idle) navController.popBackStack() 
                        else viewModel.clearSelection() 
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedIds.isNotEmpty()) {
                        Text("${selectedIds.size} Selected", modifier = Modifier.padding(end = 16.dp), color = Color(0xFF0D5C36), fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (selectedIds.isNotEmpty() && uiState is DrapeUiState.Idle) {
                Surface(tonalElevation = 8.dp, shadowElevation = 8.dp, color = Color.White) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(onClick = { /* Save to gallery logic */ }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                            Text("Save to Gallery")
                        }
                        Button(
                            onClick = { navController.navigate("add_saree?selectedIds=${selectedIds.joinToString(",")}") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))
                        ) {
                            Text("Next: Add Product")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (permissionState.allPermissionsGranted) {
                when (uiState) {
                    is DrapeUiState.Idle -> GalleryGrid(results, selectedIds) { viewModel.toggleSelection(it) }
                    is DrapeUiState.Capturing -> CameraCaptureView(
                        onCaptured = { b, u -> viewModel.onImageCaptured(b, u) },
                        onCancel = { viewModel.clearSelection() }
                    )
                    is DrapeUiState.Draping -> DrapingProgress((uiState as DrapeUiState.Draping).message)
                    is DrapeUiState.Error -> ErrorState((uiState as DrapeUiState.Error).message) { viewModel.startCapture() }
                }
                
                if (uiState is DrapeUiState.Idle) {
                    FloatingActionButton(
                        onClick = { viewModel.startCapture() },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                        containerColor = Color(0xFF0D5C36),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                    }
                }
            } else {
                PermissionRequest(permissionState)
            }
        }
    }
}

@Composable
fun GalleryGrid(results: List<AiDrapeResultEntity>, selectedIds: Set<Long>, onToggle: (Long) -> Unit) {
    val columns = if (LocalConfiguration.current.screenWidthDp >= 600) 4 else 2
    if (results.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No images yet. Start capturing!", color = Color.Gray)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(results) { item ->
                Box(
                    modifier = Modifier
                        .aspectRatio(0.8f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(if (selectedIds.contains(item.id)) 3.dp else 0.dp, Color(0xFF0D5C36), RoundedCornerShape(12.dp))
                        .clickable { onToggle(item.id) }
                ) {
                    AsyncImage(model = item.drapedUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    if (selectedIds.contains(item.id)) {
                        Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color(0xFF0D5C36), CircleShape).padding(4.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(
                        item.status, 
                        modifier = Modifier.align(Alignment.BottomStart).background(Color.Black.copy(0.6f)).padding(horizontal = 4.dp), 
                        color = Color.White, fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CameraCaptureView(onCaptured: (Bitmap, String) -> Unit, onCancel: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch (e: Exception) {
                error = e.message
            }
        }, ContextCompat.getMainExecutor(context))
    }

    if (error != null) {
        ErrorState("Camera Error: $error", onCancel)
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
            
            Box(modifier = Modifier.fillMaxSize().padding(32.dp).border(1.dp, Color.White.copy(0.2f)), contentAlignment = Alignment.Center) {
                Text("Align saree in frame", color = Color.White.copy(0.5f), fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancel, modifier = Modifier.background(Color.White.copy(0.2f), CircleShape)) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
                Box(
                    modifier = Modifier.size(72.dp).border(4.dp, Color.White, CircleShape).padding(4.dp).background(Color.White, CircleShape)
                        .clickable { 
                            val file = File(context.cacheDir, "cap_${System.currentTimeMillis()}.jpg")
                            imageCapture?.takePicture(
                                ImageCapture.OutputFileOptions.Builder(file).build(),
                                cameraExecutor,
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                        onCaptured(bitmap, Uri.fromFile(file).toString())
                                    }
                                    override fun onError(ex: ImageCaptureException) { Log.e("Camera", "Fail", ex) }
                                }
                            )
                        }
                )
                IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(0.2f), CircleShape)) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun DrapingProgress(msg: String) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator(color = Color(0xFF0D5C36))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Banana AI Processing...", fontWeight = FontWeight.Bold)
        Text(msg, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(32.dp))
        var progress by remember { mutableStateOf(0f) }
        LaunchedEffect(Unit) { while(true) { progress += 0.02f; if(progress > 1f) progress = 0f; kotlinx.coroutines.delay(30) } }
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.width(200.dp).height(8.dp).clip(CircleShape), color = Color(0xFF0D5C36))
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequest(state: com.google.accompanist.permissions.MultiplePermissionsState) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFF0D5C36))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Camera Access Required", fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text("We need camera to scan sarees and AI to drape them.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { state.launchMultiplePermissionRequest() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))) {
            Text("Grant Permissions")
        }
    }
}

@Composable
fun ErrorState(msg: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Something went wrong", fontWeight = FontWeight.Bold)
        Text(msg, textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) { Text("Try Again") }
    }
}
