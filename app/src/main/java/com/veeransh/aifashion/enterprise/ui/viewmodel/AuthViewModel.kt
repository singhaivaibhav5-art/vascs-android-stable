package com.veeransh.aifashion.enterprise.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _otpSent = MutableStateFlow(false)
    val otpSent = _otpSent.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)

    // Rate limiting logic (simplified for seed/mock)
    private var lastOtpTime = 0L
    private var otpCount = 0

    fun signInWithPhone(phoneNumber: String, activity: android.app.Activity) {
        val now = System.currentTimeMillis()
        if (now - lastOtpTime < 60000 && otpCount >= 5) {
            _authState.value = AuthState.Error("Rate limit exceeded. Try again in 1 minute.")
            return
        }
        
        if (now - lastOtpTime > 60000) {
            otpCount = 0
        }
        
        lastOtpTime = now
        otpCount++

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    _authState.value = AuthState.Error(e.message ?: "Verification failed")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _verificationId.value = verificationId
                    _otpSent.value = true
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(code: String) {
        val vId = _verificationId.value ?: return
        val credential = PhoneAuthProvider.getCredential(vId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: com.google.firebase.auth.PhoneAuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    _authState.value = AuthState.Authenticated
                    checkUserRecord(it.user?.uid ?: "")
                }
                .addOnFailureListener {
                    _authState.value = AuthState.Error(it.message ?: "Login failed")
                }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // In a real app, we'd fetch the hashed password from Firestore and verify
            // For now, using standard Firebase Auth which handles its own hashing
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _authState.value = AuthState.Authenticated
                }
                .addOnFailureListener {
                    _authState.value = AuthState.Error("Invalid credentials")
                }
        }
    }

    fun signup(phone: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            // Hash password with 10 rounds
            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10))
            
            // In V4, OTP is compulsory before this step normally, but here's the save logic
            val user = hashMapOf(
                "phone" to phone,
                "email" to email,
                "password" to hashedPassword,
                "twoFactorEnabled" to false,
                "createdAt" to System.currentTimeMillis()
            )
            
            auth.currentUser?.uid?.let { uid ->
                firestore.collection("users").document(uid).set(user)
                    .addOnSuccessListener {
                        _authState.value = AuthState.Authenticated
                    }
            }
        }
    }

    private fun checkUserRecord(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    // Navigate to profile completion if record missing
                }
            }
    }

    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}
