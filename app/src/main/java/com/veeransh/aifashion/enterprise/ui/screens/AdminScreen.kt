package com.veeransh.aifashion.enterprise.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veeransh.aifashion.enterprise.ui.components.VeeranshLogo
import com.veeransh.aifashion.enterprise.ui.admin.UserSearchBlockPanel
import com.veeransh.aifashion.enterprise.ui.admin.UserSearchBlockPanelContent
import com.veeransh.aifashion.enterprise.ui.admin.AdminManagedUser
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit
) {
    AdminDashboardScreenContent(
        onLogout = onLogout,
        isLive = true
    )
}

@Composable
fun AdminDashboardScreenContent(
    onLogout: () -> Unit,
    isLive: Boolean = false
) {
    var selectedMenuItem by remember { mutableStateOf("Dashboard") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Sample Data
    val usersList = remember {
        mutableStateListOf(
            VeeranshUser("1", "Amit Sharma", "Dealer", true),
            VeeranshUser("2", "Priya Singh", "Style Partner", true),
            VeeranshUser("3", "Rahul Varma", "User", false),
            VeeranshUser("4", "Suresh Kumar", "Dealer", true),
            VeeranshUser("5", "Neha Gupta", "Style Partner", true)
        )
    }

    val pendingApprovals = remember {
        mutableStateListOf(
            VeeranshUser("101", "Rajesh Silk Store", "Dealer", false),
            VeeranshUser("102", "Ethnic Diva Studio", "Style Partner", false),
            VeeranshUser("103", "Modern Saree Hub", "Dealer", false)
        )
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        Column(
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight()
                .background(Color(0xFF0A5C36))
                .padding(16.dp)
        ) {
            VeeranshLogo(showSubtitle = false)
            Spacer(modifier = Modifier.height(32.dp))
            
            val menuItems = listOf(
                AdminMenuItem("Dashboard", Icons.Default.Dashboard),
                AdminMenuItem("User Management", Icons.Default.People),
                AdminMenuItem("Approvals", Icons.Default.VerifiedUser),
                AdminMenuItem("Inventory Master", Icons.Default.Storage),
                AdminMenuItem("Orders", Icons.Default.ShoppingCart),
                AdminMenuItem("Finance", Icons.Default.AccountBalance),
                AdminMenuItem("Reports", Icons.Default.Assessment),
                AdminMenuItem("AI Config", Icons.Default.SettingsSuggest),
                AdminMenuItem("Settings", Icons.Default.Settings)
            )

            menuItems.forEach { item ->
                NavigationItem(
                    item = item,
                    isSelected = selectedMenuItem == item.title,
                    onClick = { selectedMenuItem = item.title }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color.White)
            }
        }

        // Main Content
        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F1E8)) // Brand Beige
                    .padding(24.dp)
            ) {
                Text(
                    text = "ADMIN PANEL - $selectedMenuItem",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0A5C36)
                )
                Spacer(modifier = Modifier.height(24.dp))

                when (selectedMenuItem) {
                    "Dashboard" -> AdminOverview()
                    "User Management" -> {
                        if (isLive) {
                            UserSearchBlockPanel()
                        } else {
                            UserSearchBlockPanelContent(
                                users = listOf(
                                    AdminManagedUser("1", "Mock Rohit", "Dealer", "9876543210", "Active")
                                ),
                                searchQuery = "",
                                onSearchQueryChange = {},
                                onUnblock = {},
                                onSuspend = { _, _, _ -> }
                            )
                        }
                    }
                    "Approvals" -> ApprovalsView(
                        pendingApprovals = pendingApprovals,
                        onApprove = { user ->
                            pendingApprovals.remove(user)
                            usersList.add(user.copy(isActive = true))
                            scope.launch {
                                snackbarHostState.showSnackbar("${user.role} Approved: ${user.name}")
                            }
                        },
                        onReject = { user ->
                            pendingApprovals.remove(user)
                            scope.launch {
                                snackbarHostState.showSnackbar("${user.role} Rejected: ${user.name}")
                            }
                        }
                    )
                    else -> PlaceholderView(selectedMenuItem)
                }
            }
            
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun NavigationItem(
    item: AdminMenuItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun AdminOverview() {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("Total Revenue", "₹12.5M", Icons.Default.TrendingUp, Color(0xFF10B981), Modifier.weight(1f))
            StatCard("Active Dealers", "452", Icons.Default.Groups, Color(0xFF3B82F6), Modifier.weight(1f))
            StatCard("Pending Orders", "86", Icons.Default.HourglassEmpty, Color(0xFFF59E0B), Modifier.weight(1f))
            StatCard("AI Efficiency", "94%", Icons.Default.AutoAwesome, Color(0xFF8B5CF6), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Recent Activity", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                repeat(5) {
                    ActivityItem("New dealer registered: Jaipur Silks", "2 mins ago")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun UserManagementView(users: MutableList<VeeranshUser>) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("User Directory", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF0D5C36))
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New User")
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Table Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("NAME", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("ROLE", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("STATUS", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                Text("ACTIONS", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
            
            LazyColumn {
                itemsIndexed(users) { index, user ->
                    UserRow(
                        user = user,
                        onToggleStatus = { users[index] = user.copy(isActive = !user.isActive) },
                        onDelete = { users.remove(user) }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                }
            }
        }
    }
}

@Composable
fun UserRow(user: VeeranshUser, onToggleStatus: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(user.name, modifier = Modifier.weight(2f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
        
        // Role Badge
        Box(
            modifier = Modifier.weight(1.5f)
        ) {
            val badgeColor = when (user.role) {
                "Dealer" -> Color(0xFFE6F2EC) to Color(0xFF0D5C36)
                "Style Partner" -> Color(0xFFFFF8E7) to Color(0xFF5C4900)
                else -> Color(0xFFF1F5F9) to Color(0xFF475569)
            }
            Surface(
                color = badgeColor.first,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = user.role,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor.second
                )
            }
        }
        
        // Status Toggle
        Box(modifier = Modifier.weight(1f)) {
            Switch(
                checked = user.isActive,
                onCheckedChange = { onToggleStatus() },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0D5C36), checkedTrackColor = Color(0xFF0D5C36).copy(alpha = 0.5f))
            )
        }
        
        // Actions
        Row(modifier = Modifier.weight(1.5f), horizontalArrangement = Arrangement.Center) {
            IconButton(onClick = {}) { Icon(Icons.Default.Block, contentDescription = "Block", tint = Color.Gray, modifier = Modifier.size(18.dp)) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp)) }
        }
    }
}

@Composable
fun ApprovalsView(
    pendingApprovals: List<VeeranshUser>,
    onApprove: (VeeranshUser) -> Unit,
    onReject: (VeeranshUser) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Pending Requests", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF0D5C36))
            Spacer(modifier = Modifier.height(20.dp))
            
            if (pendingApprovals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No pending requests", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(pendingApprovals) { user ->
                        ApprovalItem(user, onApprove, onReject)
                    }
                }
            }
        }
    }
}

@Composable
fun ApprovalItem(user: VeeranshUser, onApprove: (VeeranshUser) -> Unit, onReject: (VeeranshUser) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "Request for ${user.role} Access",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { onReject(user) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = BorderStroke(1.dp, Color(0xFFEF4444))
                ) {
                    Text("Reject")
                }
                
                Button(
                    onClick = { onApprove(user) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))
                ) {
                    Text("Approve")
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.DarkGray)
        }
    }
}

@Composable
fun ActivityItem(title: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, fontSize = 14.sp)
        Text(time, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun PlaceholderView(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name Module is under construction")
    }
}

data class AdminMenuItem(val title: String, val icon: ImageVector)
data class VeeranshUser(val id: String, val name: String, val role: String, var isActive: Boolean)
