package com.veeransh.aifashion.enterprise.ui.requirements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.veeransh.aifashion.enterprise.data.local.entity.CustomerRequirementEntity
import com.veeransh.aifashion.enterprise.ui.viewmodel.RequirementViewModel
import com.veeransh.aifashion.enterprise.ui.viewmodel.RequirementUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRequirementListScreen(
    viewModel: RequirementViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onRequirementClick: (String) -> Unit
) {
    val maroon = Color(0xFF7A0C20)
    val brandBg = Color(0xFFFFFAFB)
    val requirements by viewModel.observeAllRequirements().collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Requirements", style = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)) },
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is RequirementUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = maroon)
                }
                is RequirementUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ErrorOutline, null, tint = Color.Red, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(state.message, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { viewModel.resetState() /* Or re-trigger observation if needed */ }, colors = ButtonDefaults.buttonColors(containerColor = maroon)) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    if (requirements.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Inbox, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(Modifier.height(16.dp))
                            Text("No requirements found", color = Color.Gray, fontSize = 16.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(requirements) { req ->
                                RequirementAdminCard(req, maroon) { onRequirementClick(req.requirementId) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequirementAdminCard(
    requirement: CustomerRequirementEntity,
    maroon: Color,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val dateStr = dateFormat.format(Date(requirement.createdAt))

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECECEC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = requirement.requirementId,
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Black, color = maroon, letterSpacing = 1.sp)
                )
                RequirementStatusBadge(requirement.status)
            }
            
            Spacer(Modifier.height(12.dp))
            
            Text(requirement.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("SKU: ${requirement.productSku}", fontSize = 12.sp, color = Color.Gray)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(requirement.userName, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            
            Spacer(Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(requirement.userPhone, fontSize = 13.sp, color = Color.Gray)
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Qty: ${requirement.quantity}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = maroon)
                Text(dateStr, fontSize = 11.sp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun RequirementStatusBadge(status: String) {
    val colors = when (status) {
        "PENDING" -> Color(0xFFE9C46A) to Color(0xFF917400)
        "IN_REVIEW" -> Color(0xFF3B82F6) to Color.White
        "QUOTED" -> Color(0xFF8B5CF6) to Color.White
        "APPROVED" -> Color(0xFF10B981) to Color.White
        "COMPLETED" -> Color(0xFF0D5C36) to Color.White
        "REJECTED" -> Color(0xFFEF4444) to Color.White
        "CANCELLED" -> Color.Gray to Color.White
        else -> Color.LightGray to Color.DarkGray
    }
    
    Surface(
        color = colors.first,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = colors.second
        )
    }
}
