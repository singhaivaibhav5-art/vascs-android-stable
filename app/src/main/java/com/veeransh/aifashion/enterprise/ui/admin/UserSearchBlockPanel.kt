package com.veeransh.aifashion.enterprise.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun UserSearchBlockPanel(
    viewModel: UserManagementViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    UserSearchBlockPanelContent(
        users = users,
        searchQuery = searchQuery,
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onUnblock = { viewModel.unblockUser(it) },
        onSuspend = { id, reason, duration -> viewModel.suspendUser(id, reason, duration) }
    )
}

@Composable
fun UserSearchBlockPanelContent(
    users: List<AdminManagedUser>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onUnblock: (String) -> Unit,
    onSuspend: (String, String, Int) -> Unit
) {
    var showSuspendModal by remember { mutableStateOf<AdminManagedUser?>(null) }
    var showBlockModal by remember { mutableStateOf<AdminManagedUser?>(null) }
    
    var localQuery by remember { mutableStateOf(searchQuery) }

    LaunchedEffect(localQuery) {
        if (localQuery != searchQuery) {
            delay(300) // Debounce 300ms
            onSearchQueryChange(localQuery)
        }
    }

    val filteredUsers = remember(users, searchQuery) {
        users.filter { it.name.contains(searchQuery, ignoreCase = true) || 
                      it.phone.contains(searchQuery) ||
                      it.role.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = localQuery,
            onValueChange = { localQuery = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            placeholder = { Text("Search by Phone, Email, or Name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Table Header
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("USER", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text("ROLE", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text("PHONE", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text("STATUS", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Text("ACTION", modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
                
                LazyColumn {
                    items(filteredUsers) { user ->
                        AdminUserRow(
                            user = user,
                            onSuspend = { showSuspendModal = user },
                            onBlock = { showBlockModal = user },
                            onUnblock = { onUnblock(user.id) }
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                    }
                }
            }
        }
    }

    // Modals
    showSuspendModal?.let { user ->
        SuspendUserModal(
            user = user,
            onDismiss = { showSuspendModal = null },
            onConfirm = { reason, duration ->
                onSuspend(user.id, reason, duration)
                showSuspendModal = null
            }
        )
    }

    showBlockModal?.let { user ->
        BlockUserModal(
            user = user,
            onDismiss = { showBlockModal = null },
            onConfirm = {
                onSuspend(user.id, "", 0) // Block without reason
                showBlockModal = null
            }
        )
    }
}

@Composable
fun AdminUserRow(
    user: AdminManagedUser,
    onSuspend: () -> Unit,
    onBlock: () -> Unit,
    onUnblock: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(user.name, modifier = Modifier.weight(2f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Text(user.role, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
        Text(user.phone, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
        
        Box(modifier = Modifier.weight(1.2f)) {
            val statusColor = when (user.status) {
                "Active" -> Color(0xFF10B981)
                "Suspended" -> Color(0xFFF59E0B)
                else -> Color.Red
            }
            Text(user.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        
        Row(modifier = Modifier.weight(2.5f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (user.status == "Active") {
                Button(
                    onClick = onSuspend,
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE9A8)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Suspend", color = Color(0xFF856404), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                
                Surface(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFFB7B7), RoundedCornerShape(8.dp))
                        .clickable { onBlock() },
                    color = Color(0xFFFFE9E9)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
                        Text("Block", color = Color(0xFF7A0C20), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFB7E8C0), RoundedCornerShape(8.dp))
                        .clickable { onUnblock() },
                    color = Color(0xFFE8FFE9)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
                        Text("Unblock", color = Color(0xFF155A2A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SuspendUserModal(
    user: AdminManagedUser,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("7") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Suspend User: ${user.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Suspension Reason") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (Days)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reason, duration.toIntOrNull() ?: 7) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))
            ) {
                Text("Confirm Suspension")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun BlockUserModal(
    user: AdminManagedUser,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Block User without Reason?") },
        text = { Text("This will immediately invalidate the session for ${user.name} and prevent all future access until unblocked.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A0C20))
            ) {
                Text("Block Immediately", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
