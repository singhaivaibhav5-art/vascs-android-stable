package com.veeransh.aifashion.enterprise.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.veeransh.aifashion.enterprise.ui.theme.VeeranshTheme
import com.veeransh.aifashion.enterprise.ui.components.VeeranshLogo

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onAuthSuccess()
        }
    }

    Scaffold(
        containerColor = Color(0xFFFEFEFE)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VeeranshLogo(showSubtitle = true)
            
            Spacer(modifier = Modifier.height(40.dp))
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF0A5C36),
                divider = {}
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("OTP Login", modifier = Modifier.padding(vertical = 12.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Password", modifier = Modifier.padding(vertical = 12.dp))
                }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                    Text("Signup", modifier = Modifier.padding(vertical = 12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            when (selectedTab) {
                0 -> OTPLoginView(viewModel)
                1 -> PasswordLoginView(viewModel)
                2 -> SignupView(viewModel)
            }
            
            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 16.dp),
                    color = Color(0xFF0A5C36)
                )
            }
        }
    }
}

@Composable
fun OTPLoginView(viewModel: AuthViewModel) {
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    val otpSent by viewModel.otpSent.collectAsState()
    val context = LocalContext.current
    val activity = context as android.app.Activity

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = phone,
            onValueChange = { newValue -> phone = newValue },
            label = { Text("Phone Number (+91)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
        )
        
        if (otpSent) {
            OutlinedTextField(
                value = otp,
                onValueChange = { newValue -> otp = newValue },
                label = { Text("6-Digit OTP") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) }
            )
            
            Button(
                onClick = { viewModel.verifyOtp(otp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Verify & Login", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = { viewModel.signInWithPhone(phone, activity) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Send OTP", fontWeight = FontWeight.Bold)
            }
        }
        
        Text(
            "OTP expires in 5 minutes • Rate limit 5/min",
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun PasswordLoginView(viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showReset by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = email,
            onValueChange = { newValue -> email = newValue },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { newValue -> password = newValue },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
        )
        
        Button(
            onClick = { viewModel.signInWithEmail(email, password) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login with Password", fontWeight = FontWeight.Bold)
        }
        
        TextButton(
            onClick = { showReset = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Forgot Password?", color = Color(0xFF0A5C36))
        }

        if (showReset) {
            AlertDialog(
                onDismissRequest = { showReset = false },
                title = { Text("Reset Password") },
                text = { Text("An email will be sent to $email with a reset link.") },
                confirmButton = {
                    TextButton(onClick = { 
                        viewModel.resetPassword(email)
                        showReset = false 
                    }) { Text("Send Link") }
                }
            )
        }
    }
}

@Composable
fun SignupView(viewModel: AuthViewModel) {
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var step by remember { mutableIntStateOf(0) } // 0: Phone/OTP, 1: Details

    val otpSent by viewModel.otpSent.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (step == 0) {
            Text("Step 1: Phone Verification (Compulsory)", fontWeight = FontWeight.Bold)
            OTPLoginView(viewModel)
            
            val authState by viewModel.authState.collectAsState()
            if (authState is AuthState.Authenticated) {
                step = 1
            }
        } else {
            Text("Step 2: Account Details", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = email,
                onValueChange = { newValue -> email = newValue },
                label = { Text("Email (Recovery)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { newValue -> password = newValue },
                label = { Text("Create Password (Optional Backup)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            
            Button(
                onClick = { viewModel.signup(phone, email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Complete Signup", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TwoFactorSetupView(onComplete: () -> Unit) {
    val backupCodes = remember { List(10) { (100000..999999).random().toString() } }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("2FA SETUP", fontSize = 20.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(24.dp))
        
        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(200.dp))
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Save these backup codes (Single-use, AES-256 Encrypted):", fontSize = 12.sp)
        
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            backupCodes.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    pair.forEach { code -> Text(code, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace) }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A5C36))
        ) {
            Text("Enable 2FA")
        }
    }
}
