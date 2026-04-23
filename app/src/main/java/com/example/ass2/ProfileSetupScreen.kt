package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandTeal

@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit,
    onSaveAndContinue: () -> Unit
) {
    val bgColor = Color(0xFFF4F5F4)
    val teal = BrandTeal
    val darkText = Color(0xFF1F1F1F)
    val grayText = Color(0xFF5F6368)
    val cardColor = Color(0xFFF0F2F1)
    val whiteCard = Color(0xFFFCFCFC)

    var age by remember { mutableStateOf("") }
    var mobilitySupport by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var painLevel by remember { mutableFloatStateOf(4f) }
    var restingHeartRate by remember { mutableStateOf("") }
    var mobilityExpanded by remember { mutableStateOf(false) }
    var activityExpanded by remember { mutableStateOf(false) }

    val mobilityOptions = listOf("Wheelchair", "Cane / Walker", "Independent", "Other")
    val activityOptions = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active")

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
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = teal)
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

                // ── Personal Profile ──────────────────────────────────
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

                // ── Mobility & Exercise ───────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        CardSectionTitle(
                            icon = { Icon(Icons.Default.Accessible, contentDescription = null, tint = grayText) },
                            title = "Mobility & Exercise",
                            teal = teal
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Primary Mobility Support", color = grayText, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                        DropdownRow(
                            value = mobilitySupport,
                            placeholder = "Select an option",
                            expanded = mobilityExpanded,
                            options = mobilityOptions,
                            whiteCard = whiteCard,
                            teal = teal,
                            onExpandChange = { mobilityExpanded = it },
                            onOptionSelected = { mobilitySupport = it; mobilityExpanded = false }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Current Activity Level", color = grayText, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                        DropdownRow(
                            value = activityLevel,
                            placeholder = "Select an option",
                            expanded = activityExpanded,
                            options = activityOptions,
                            whiteCard = whiteCard,
                            teal = teal,
                            onExpandChange = { activityExpanded = it },
                            onOptionSelected = { activityLevel = it; activityExpanded = false }
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

                // ── Baseline Vitals ───────────────────────────────────
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
                    onClick = onSaveAndContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = teal)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownRow(
    value: String,
    placeholder: String,
    expanded: Boolean,
    options: List<String>,
    whiteCard: Color,
    teal: Color,
    onExpandChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = onExpandChange) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(whiteCard)
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (value.isEmpty()) placeholder else value,
                color = if (value.isEmpty()) Color(0xFFBBBBBB) else Color(0xFF1F1F1F),
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF5F6368))
        }
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { onExpandChange(false) }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 14.sp) },
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

