package com.veeransh.aifashion.enterprise.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.veeransh.aifashion.enterprise.data.local.entity.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    user: UserEntity?,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val maroon = Color(0xFF7A0C20)
    val brandBg = Color(0xFFFFFAFB)
    val brandBorder = Color(0xFFECECEC)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", style = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)) },
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
        if (user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = maroon)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading profile details...", color = Color.Gray)
                }
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
                // Header / Avatar Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(maroon.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = maroon
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = user.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Text(
                        text = user.role.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = maroon,
                        letterSpacing = 1.sp
                    )
                }

                // 1. IDENTITY SECTION
                ProfileSection("IDENTITY") {
                    ProfileInfoRow(Icons.Default.Person, "Full Name", user.name)
                    ProfileInfoRow(Icons.Default.Phone, "Phone Number", user.phone)
                    ProfileInfoRow(Icons.Default.Email, "Email Address", user.email)
                }

                // 2. ACCOUNT STATUS SECTION
                ProfileSection("ACCOUNT STATUS") {
                    ProfileInfoRow(Icons.Default.Shield, "Role", user.role.replaceFirstChar { it.uppercase() })
                    ProfileInfoRow(Icons.Default.VerifiedUser, "Account Status", user.status.replaceFirstChar { it.uppercase() })
                    ProfileInfoRow(Icons.Default.FactCheck, "KYC Verification", user.kycStatus)
                }

                // 3. REFERRAL SECTION
                ProfileSection("REFERRAL PROGRAM") {
                    ProfileInfoRow(Icons.Default.Share, "Your Referral Code", user.referralCode.ifEmpty { "None" })
                    ProfileInfoRow(Icons.Default.Group, "Referred By", user.referredBy.ifEmpty { "Direct Signup" })
                }

                // 4. ACTIONS
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { /* Placeholder for editing */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D5C36))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EDIT PROFILE", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        border = BorderStroke(1.dp, maroon),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = maroon)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LOGOUT SESSION", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        }
    }
}
