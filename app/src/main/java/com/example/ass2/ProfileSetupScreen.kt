package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Person
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
import com.example.ass2.ui.theme.BrandBackground
import com.example.ass2.ui.theme.BrandTeal
import com.example.ass2.ui.theme.FieldBackground

@Composable
fun ProfileSetupScreen(
    onBack: () -> Unit,
    onSaveAndContinue: () -> Unit
) {
    var age by remember { mutableStateOf("") }
    var mobilitySupport by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var painLevel by remember { mutableFloatStateOf(4f) }
    var restingHeartRate by remember { mutableStateOf("") }
    var screenReaderOptimized by remember { mutableStateOf(false) }
    var highContrastText by remember { mutableStateOf(true) }
    var voiceCommands by remember { mutableStateOf(false) }
    var dailyReminders by remember { mutableStateOf(true) }

    var mobilityExpanded by remember { mutableStateOf(false) }
    var activityExpanded by remember { mutableStateOf(false) }

    val mobilityOptions = listOf("Wheelchair", "Cane / Walker", "Independent", "Other")
    val activityOptions = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active")

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Button(
                    onClick = onSaveAndContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D3D40))
                ) {
                    Text("Save and Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        },
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1A1A))
                }
                Text(
                    text = "SmartFit Fuel",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandTeal,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Let's set up your profile",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 32.sp
                )
                Text(
                    text = "We'll use this information to safely customize your wellness journey.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B6B6B),
                    modifier = Modifier.padding(top = 6.dp, bottom = 24.dp),
                    lineHeight = 20.sp
                )

                // ── Personal Profile ──────────────────────────────────
                SectionHeader(icon = { Icon(Icons.Default.Person, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(20.dp)) }, title = "Personal Profile")

                Spacer(Modifier.height(12.dp))

                FieldLabel("Age")
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter { c -> c.isDigit() } },
                    placeholder = { Text("Enter your age", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = BrandTeal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // ── Mobility & Exercise ───────────────────────────────
                Text(
                    text = "Mobility & Exercise",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandTeal,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                FieldLabel("Primary Mobility Support")
                DropdownField(
                    value = mobilitySupport,
                    placeholder = "Select an option",
                    expanded = mobilityExpanded,
                    options = mobilityOptions,
                    onExpandChange = { mobilityExpanded = it },
                    onOptionSelected = {
                        mobilitySupport = it
                        mobilityExpanded = false
                    }
                )

                Spacer(Modifier.height(12.dp))

                FieldLabel("Current Activity Level")
                DropdownField(
                    value = activityLevel,
                    placeholder = "Select an option",
                    expanded = activityExpanded,
                    options = activityOptions,
                    onExpandChange = { activityExpanded = it },
                    onOptionSelected = {
                        activityLevel = it
                        activityExpanded = false
                    }
                )

                Spacer(Modifier.height(12.dp))

                FieldLabel("Related Conditions (Optional)")
                OutlinedTextField(
                    value = conditions,
                    onValueChange = { conditions = it },
                    placeholder = { Text("e.g., Arthritis, MS, post-surgery", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = BrandTeal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Helps us suggest appropriate modifications.",
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(24.dp))

                // ── Baseline Vitals ───────────────────────────────────
                SectionHeader(
                    icon = { Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(20.dp)) },
                    title = "Baseline Vitals"
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Typical Daily Pain Level", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A3A3A))
                    Text("${painLevel.toInt()} / 10", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandTeal)
                }

                Slider(
                    value = painLevel,
                    onValueChange = { painLevel = it },
                    valueRange = 0f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandTeal,
                        activeTrackColor = BrandTeal,
                        inactiveTrackColor = BrandTeal.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0 (None)", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                    Text("5 (Moderate)", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                    Text("10 (Severe)", fontSize = 11.sp, color = Color(0xFF9E9E9E))
                }

                Spacer(Modifier.height(16.dp))

                FieldLabel("Resting Heart Rate (bpm)")
                OutlinedTextField(
                    value = restingHeartRate,
                    onValueChange = { restingHeartRate = it.filter { c -> c.isDigit() } },
                    placeholder = { Text("e.g., 72", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = BrandTeal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Leave blank if unknown.", fontSize = 12.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))

                Spacer(Modifier.height(24.dp))

                // ── Experience Settings ───────────────────────────────
                SectionHeader(
                    icon = {
                        Text("T", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandTeal)
                    },
                    title = "Experience Settings"
                )

                Spacer(Modifier.height(14.dp))

                Text("App Accessibility", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                Spacer(Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        AccessibilityCheckbox(
                            checked = screenReaderOptimized,
                            onCheckedChange = { screenReaderOptimized = it },
                            title = "Optimize for Screen Reader",
                            subtitle = "Enhances audio descriptions"
                        )
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))
                        AccessibilityCheckbox(
                            checked = highContrastText,
                            onCheckedChange = { highContrastText = it },
                            title = "High Contrast Text",
                            subtitle = "Increases visual clarity"
                        )
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))
                        AccessibilityCheckbox(
                            checked = voiceCommands,
                            onCheckedChange = { voiceCommands = it },
                            title = "Enable Voice Commands",
                            subtitle = "Navigate hands-free"
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("Notifications", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                Spacer(Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Daily Reminders", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
                            Text("Gentle nudges to move", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                        }
                        Switch(
                            checked = dailyReminders,
                            onCheckedChange = { dailyReminders = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = BrandTeal)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: @Composable () -> Unit, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(Modifier.width(8.dp))
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandTeal)
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF3A3A3A),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    value: String,
    placeholder: String,
    expanded: Boolean,
    options: List<String>,
    onExpandChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandChange
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder, color = Color(0xFFBBBBBB), fontSize = 14.sp) },
            trailingIcon = {
                Icon(Icons.Default.ExpandMore, contentDescription = null, tint = Color(0xFF9E9E9E))
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = BrandTeal
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 14.sp) },
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun AccessibilityCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = BrandTeal)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF9E9E9E))
        }
    }
}
