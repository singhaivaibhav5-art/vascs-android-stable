package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FinanceScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "FINANCE OVERVIEW",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0D5C36)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { FinanceStatCard("Total Revenue", "₹12,50,000", Icons.Default.TrendingUp, Color(0xFF10B981)) }
            item { FinanceStatCard("Expense", "₹4,20,000", Icons.Default.TrendingDown, Color(0xFFEF4444)) }
            item { FinanceStatCard("Profit", "₹8,30,000", Icons.Default.Payments, Color(0xFF3B82F6)) }
            item { FinanceStatCard("GST Payable", "₹62,500", Icons.Default.AccountBalance, Color(0xFFF59E0B)) }
        }
    }
}

@Composable
fun FinanceStatCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.DarkGray)
        }
    }
}
