package com.example.ass2.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandTeal
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.viewmodel.ProfileViewModel

@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit,
    onSaveAndContinue: () -> Unit,
    showBackButton: Boolean = true,
    viewModel: ProfileViewModel = viewModel()
)  {
    val bgColor = Color(0xFFF4F5F4)
    val teal = BrandTeal
    val darkText = Color(0xFF1F1F1F)
    val grayText = Color(0xFF5F6368)
    val cardColor = Color(0xFFF0F2F1)
    val whiteCard = Color(0xFFFCFCFC)
    // below code error because overwrite profile page with talent code
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsState()

    var displayName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var mobilitySupport by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var painLevel by remember { mutableFloatStateOf(4f) }
    var restingHeartRate by remember { mutableStateOf("") }

    var hasLoadedExistingProfile by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        if (profile != null && !hasLoadedExistingProfile) {
            displayName = profile?.displayName.orEmpty()
            age = profile?.age?.toString().orEmpty()
            mobilitySupport = profile?.mobilitySupport.orEmpty()
            activityLevel = profile?.activityLevel.orEmpty()
            conditions = profile?.conditions.orEmpty()
            painLevel = profile?.painBaseline?.toFloat() ?: 4f
            restingHeartRate = profile?.restingHeartRate?.toString().orEmpty()

            hasLoadedExistingProfile = true
        }
    }
    val mobilityOptions = listOf("Wheelchair", "Cane / Walker", "Independent", "Other")
    val activityOptions = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active")

    // Block the system back gesture when arriving from registration
    BackHandler(enabled = !showBackButton) {}

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBackButton) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = teal)
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("Sit&Fit", color = teal, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "Let's set up\nyour profile",
                    color = teal,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "We'll use this information to safely customize your wellness journey.",
                    color = grayText,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(26.dp))

                // Personal Profile
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        CardSectionTitle(
                            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = grayText) },
                            title = "Personal Profile",
                            teal = teal
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Display Name",
                            color = grayText,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            placeholder = { Text("Enter your name", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = whiteCard,
                                focusedContainerColor = whiteCard,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = teal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Age", color = grayText, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it.filter { c -> c.isDigit() } },
                            placeholder = { Text("Enter your age", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = whiteCard,
                                focusedContainerColor = whiteCard,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = teal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mobility & Exercise
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        CardSectionTitle(
                            icon = { Icon(Icons.AutoMirrored.Filled.Accessible, contentDescription = null, tint = grayText) },
                            title = "Mobility & Exercise",
                            teal = teal
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Primary Mobility Support", color = grayText, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                        RadioGroup(
                            options = mobilityOptions,
                            selected = mobilitySupport,
                            onSelected = { mobilitySupport = it },
                            teal = teal
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Current Activity Level", color = grayText, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                        RadioGroup(
                            options = activityOptions,
                            selected = activityLevel,
                            onSelected = { activityLevel = it },
                            teal = teal
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Related Conditions (Optional)", color = grayText, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                        OutlinedTextField(
                            value = conditions,
                            onValueChange = { conditions = it },
                            placeholder = { Text("e.g., Arthritis, MS, post-surgery", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = whiteCard,
                                focusedContainerColor = whiteCard,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = teal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "Helps us suggest appropriate modifications.",
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Baseline Vitals
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        CardSectionTitle(
                            icon = { Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = grayText) },
                            title = "Baseline Vitals",
                            teal = teal
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Typical Daily Pain Level", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = darkText)
                            Text("${painLevel.toInt()} / 10", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = teal)
                        }
                        Slider(
                            value = painLevel,
                            onValueChange = { painLevel = it },
                            valueRange = 0f..10f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = teal,
                                activeTrackColor = teal,
                                inactiveTrackColor = teal.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("0 (None)", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                            Text("5 (Moderate)", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                            Text("10 (Severe)", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Resting Heart Rate (bpm)", color = grayText, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                        OutlinedTextField(
                            value = restingHeartRate,
                            onValueChange = { restingHeartRate = it.filter { c -> c.isDigit() } },
                            placeholder = { Text("e.g., 72", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = whiteCard,
                                focusedContainerColor = whiteCard,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = teal
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Leave blank if unknown.", fontSize = 12.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(34.dp))

                Button(
                    onClick = {
                        viewModel.saveProfile(
                            com.example.ass2.data.local.Profile(
                                userId = viewModel.uid,
                                displayName = displayName,
                                conditions = conditions.takeIf { it.isNotBlank() },
                                painBaseline = painLevel.toInt(),
                                mobilitySupport = mobilitySupport.takeIf { it.isNotBlank() },
                                activityLevel = activityLevel.takeIf { it.isNotBlank() },
                                age = age.toIntOrNull(),
                                restingHeartRate = restingHeartRate.toIntOrNull()
                            )
                        )
                        onSaveAndContinue()
                    },
                    enabled = displayName.isNotBlank() &&
                            mobilitySupport.isNotBlank() &&
                            activityLevel.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = teal,
                        disabledContainerColor = teal.copy(alpha = 0.35f)
                    )
                ) {
                    Text("Save and Continue", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(22.dp))
            }
        }
    }
}

@Composable
private fun CardSectionTitle(icon: @Composable () -> Unit, title: String, teal: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Text(" $title", color = teal, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RadioGroup(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    teal: Color
) {
    Column {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(option) }
            ) {
                RadioButton(
                    selected = option == selected,
                    onClick = { onSelected(option) },
                    colors = RadioButtonDefaults.colors(selectedColor = teal)
                )
                Text(option, fontSize = 14.sp, color = Color(0xFF1F1F1F))
            }
        }
    }
}

