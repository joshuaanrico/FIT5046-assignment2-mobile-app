package com.example.ass2.ui.auth
import com.example.ass2.R

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.*

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLegal: (String) -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val passwordStrength = computePasswordStrength(password)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 60.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand header — accessibility icon + app name, left-aligned
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.accessibility),
                    contentDescription = null,
                    tint = BrandTeal,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Sit&Fit",
                    color = BrandTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Start your accessible fitness journey today.",
                fontSize = 14.sp,
                color = Color(0xFF6B6B6B),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 24.dp)
            )

            // Full Name
            FieldLabel("Full Name")
            AuthTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "John Doe",
                imeAction = ImeAction.Next,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF9E9E9E))
                }
            )

            Spacer(Modifier.height(16.dp))

            // Email
            FieldLabel("Email Address")
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "student@university.edu",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF9E9E9E))
                }
            )

            Spacer(Modifier.height(16.dp))

            // Password
            FieldLabel("Password")
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "••••••••",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9E9E9E))
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF9E9E9E)
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Confirm Password
            FieldLabel("Confirm Password")
            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "••••••••",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF9E9E9E))
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF9E9E9E)
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            // Password strength
            PasswordStrengthBar(strength = passwordStrength)

            Spacer(Modifier.height(8.dp))

            Text(
                text = "✓ At least 8 characters, 1 uppercase letter, and 1 unique symbol.",
                fontSize = 12.sp,
                color = if (passwordStrength >= 3) BrandTeal else Color(0xFF9E9E9E),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(28.dp))

            // Create Account button
            Button(
                onClick = { onRegisterSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
            ) {
                Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(Modifier.height(20.dp))

            // Divider
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
                Text(
                    text = "  OR CONTINUE WITH  ",
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E),
                    fontWeight = FontWeight.Medium
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDDDDD))
            }

            Spacer(Modifier.height(16.dp))

            // Google sign-in with actual Google icon
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1A1A1A))
            ) {
                Icon(
                    painter = painterResource(R.drawable.google_icon),
                    contentDescription = "Google",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Continue with Google", fontSize = 15.sp)
            }

            Spacer(Modifier.height(24.dp))

            // Already have account
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = Color(0xFF6B6B6B), fontSize = 14.sp)
                TextButton(
                    onClick = onNavigateToLogin,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Sign in", color = BrandTeal, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            val legalText = buildAnnotatedString {
                append("By joining, you agree to our ")
                pushStringAnnotation(tag = "terms", annotation = "terms")
                withStyle(SpanStyle(color = BrandTeal, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Medium)) {
                    append("Terms of Service")
                }
                pop()
                append(" and ")
                pushStringAnnotation(tag = "privacy", annotation = "privacy")
                withStyle(SpanStyle(color = BrandTeal, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Medium)) {
                    append("Privacy Policy")
                }
                pop()
                append(". Wellness is a commitment to yourself.")
            }

            val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
            Text(
                text = legalText,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color(0xFF6B6B6B),
                    textAlign = TextAlign.Center
                ),
                onTextLayout = { layoutResult.value = it },
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures { pos ->
                        layoutResult.value?.let { layout ->
                            val offset = layout.getOffsetForPosition(pos)
                            legalText.getStringAnnotations("terms", offset, offset).firstOrNull()?.let { onNavigateToLegal("terms") }
                            legalText.getStringAnnotations("privacy", offset, offset).firstOrNull()?.let { onNavigateToLegal("privacy") }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF3A3A3A),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFBBBBBB), fontSize = 14.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = FieldBackground,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = BrandTeal
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PasswordStrengthBar(strength: Int) {
    val segments = 4
    val labels = listOf("Weak", "Fair", "Good", "Strong")
    val colors = listOf(StrengthWeak, StrengthFair, StrengthGood, StrengthStrong)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("PASSWORD STRENGTH", fontSize = 10.sp, color = Color(0xFF9E9E9E), fontWeight = FontWeight.Medium)
            if (strength > 0) {
                Text(
                    text = labels[strength - 1],
                    fontSize = 12.sp,
                    color = colors[strength - 1],
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..segments) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .background(
                            color = if (i <= strength) colors[strength - 1] else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}

/** Returns 0 (empty) . 4 (strong) */
private fun computePasswordStrength(password: String): Int {
    if (password.isEmpty()) return 0
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return score
}
