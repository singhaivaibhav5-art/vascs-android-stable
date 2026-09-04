package com.veeransh.aifashion.enterprise.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ERPToolsGrid(
    modifier: Modifier = Modifier,
    onToolClick: (String) -> Unit
) {
    val tools = listOf(
        ERPTool("Finance", Icons.Default.AccountBalance),
        ERPTool("Inventory", Icons.Default.Inventory2),
        ERPTool("Sales", Icons.Default.PointOfSale),
        ERPTool("Customers", Icons.Default.People),
        ERPTool("Stock", Icons.Default.Checkroom),
        ERPTool("Reports", Icons.Default.Assessment),
        ERPTool("AI Center", Icons.Default.SmartToy),
        ERPTool("Analytics", Icons.Default.BarChart),
        ERPTool("Orders", Icons.Default.ShoppingBag),
        ERPTool("B2B Portal", Icons.Default.Store),
        ERPTool("Style Partner", Icons.Default.Diamond),
        ERPTool("Admin Control", Icons.Default.AdminPanelSettings),
        ERPTool("Dispatch", Icons.Default.LocalShipping)
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.heightIn(max = 1000.dp), // Set a max height or use intrinsic
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false // Let the parent scroll
    ) {
        items(tools) { tool ->
            ToolCard(tool = tool, onClick = { onToolClick(tool.name) })
        }
    }
}

@Composable
fun ToolCard(tool: ERPTool, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.name,
                modifier = Modifier.size(36.dp),
                tint = Color(0xFF0D5C36)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tool.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }
    }
}

data class ERPTool(val name: String, val icon: ImageVector)
