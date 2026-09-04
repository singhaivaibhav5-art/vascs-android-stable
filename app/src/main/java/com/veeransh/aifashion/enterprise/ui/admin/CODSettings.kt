package com.veeransh.aifashion.enterprise.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CODSettings() {
    var isCodEnabled by remember { mutableStateOf(true) }
    var minCod by remember { mutableStateOf("0") }
    var maxCod by remember { mutableStateOf("5000") }
    var codCharge by remember { mutableStateOf("50") }
    val pincodes = remember { mutableStateListOf("482001", "482002", "110001") }
    
    val categories = listOf("Premium Silk", "Cotton Master", "Daily Wear", "Bridal Wear", "Georgette Special")
    val disabledCategories = remember { mutableStateListOf<String>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("COD & TERMS CONFIGURATION", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF0A5C36))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Cash on Delivery", fontWeight = FontWeight.Bold)
                        Switch(
                            checked = isCodEnabled,
                            onCheckedChange = { isCodEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0A5C36), checkedTrackColor = Color(0xFF0A5C36).copy(alpha = 0.5f))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = minCod,
                            onValueChange = { minCod = it },
                            label = { Text("Min COD ₹") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = maxCod,
                            onValueChange = { maxCod = it },
                            label = { Text("Max COD ₹") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = codCharge,
                            onValueChange = { codCharge = it },
                            label = { Text("COD Charge ₹") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                // Pincodes
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Serviceable Pincodes", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            IconButton(onClick = { pincodes.add("") }) { Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF0A5C36)) }
                        }
                        
                        pincodes.forEachIndexed { index, pin ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = pin,
                                    onValueChange = { pincodes[index] = it },
                                    modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                                    placeholder = { Text("Pincode") }
                                )
                                IconButton(onClick = { pincodes.removeAt(index) }) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                            }
                        }
                    }
                }

                // Categories
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Disable COD for Categories", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        categories.forEach { cat ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = disabledCategories.contains(cat),
                                    onCheckedChange = { if (it) disabledCategories.add(cat) else disabledCategories.remove(cat) },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0A5C36))
                                )
                                Text(cat, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("SYSTEM NOTES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "Stock 0 → hidden from gallery (web + Android). Admin filter Out of Stock. COD editable toggle, Max ₹, Charge editable, Pincodes list +/-, Disabled Categories.",
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEC))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("TERMS EDITOR • NO RETURN POLICY", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Mandatory Footer: \"No Return/No Exchange except defect reported within 24h of delivery. Admin can suspend IDs without reason.\"",
                        fontSize = 14.sp,
                        color = Color(0xFF7A0C20),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Button(
                onClick = { /* Save */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Configuration", fontWeight = FontWeight.Bold)
            }
        }
    }
}
