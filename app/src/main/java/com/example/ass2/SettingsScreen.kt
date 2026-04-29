package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val bgColor = Color(0xFFF4F5F4)
    val teal = Color(0xFF0D7377)
    val darkText = Color(0xFF1F1F1F)
    val grayText = Color(0xFF5F6368)
    val cardColor = Color(0xFFF8F8F8)

    val pushNotifications = remember { mutableStateOf(true) }
    val soundVibration = remember { mutableStateOf(true) }
    val highContrastMode = remember { mutableStateOf(false) }
    val largerText = remember { mutableStateOf(false) }

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
                Text("Settings", color = teal, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                SettingsSectionHeader("General Preferences", teal)
                Spacer(modifier = Modifier.height(12.dp))

                SettingsGroup(cardColor) {
                    SettingsToggleRow(
                        icon = Icons.Outlined.Notifications,
                        iconBg = Color(0xFFE5F0EE),
                        iconTint = teal,
                        title = "Push Notifications",
                        subtitle = "Stay updated on your daily goals",
                        checked = pushNotifications.value,
                        onCheckedChange = { pushNotifications.value = it },
                        teal = teal,
                        darkText = darkText,
                        grayText = grayText,
                        showDivider = true
                    )
                    SettingsToggleRow(
                        icon = Icons.Outlined.Vibration,
                        iconBg = Color(0xFFE5F0EE),
                        iconTint = teal,
                        title = "Sound & Vibration",
                        subtitle = "Haptic feedback during exercises",
                        checked = soundVibration.value,
                        onCheckedChange = { soundVibration.value = it },
                        teal = teal,
                        darkText = darkText,
                        grayText = grayText,
                        showDivider = false
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
                SettingsSectionHeader("Accessibility", teal)
                Spacer(modifier = Modifier.height(12.dp))

                SettingsGroup(cardColor) {
                    SettingsToggleRow(
                        icon = Icons.Outlined.Contrast,
                        iconBg = Color(0xFFE5E5F0),
                        iconTint = Color(0xFF3D4A8A),
                        title = "High Contrast Mode",
                        subtitle = "Increases visual clarity",
                        checked = highContrastMode.value,
                        onCheckedChange = { highContrastMode.value = it },
                        teal = teal,
                        darkText = darkText,
                        grayText = grayText,
                        showDivider = true
                    )
                    SettingsToggleRow(
                        icon = Icons.Outlined.FormatSize,
                        iconBg = Color(0xFFE5E5F0),
                        iconTint = Color(0xFF3D4A8A),
                        title = "Larger Text",
                        subtitle = "Increases font size throughout the app",
                        checked = largerText.value,
                        onCheckedChange = { largerText.value = it },
                        teal = teal,
                        darkText = darkText,
                        grayText = grayText,
                        showDivider = false
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Need assistance card
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = teal)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.SelfImprovement,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Need assistance?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Our care team is available 24/7 to help you with any movement adjustments.",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Email: support@sitandfit.app",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Phone: +61 3 9000 1234",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, teal: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.VerticalDivider(
            modifier = Modifier
                .height(20.dp)
                .padding(end = 10.dp),
            color = teal,
            thickness = 3.dp
        )
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = teal)
    }
}

@Composable
private fun SettingsGroup(cardColor: Color, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardColor)
    ) {
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    teal: Color,
    darkText: Color,
    grayText: Color,
    showDivider: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = darkText)
            Text(subtitle, fontSize = 13.sp, color = grayText)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = teal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
    if (showDivider) {
        HorizontalDivider(
            color = Color(0xFFEEEEEE),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
