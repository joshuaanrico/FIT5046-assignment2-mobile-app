package com.example.ass2.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandBackground
import com.example.ass2.ui.theme.BrandTeal

@Composable
fun HelpScreen(onBack: () -> Unit) {
    val grayText = Color(0xFF4A4A4A)
    val cardColor = Color(0xFFF8F8F8)
    val sectionTeal = BrandTeal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = BrandTeal)
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Help", color = BrandTeal, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = "How to Use the Camera",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = sectionTeal
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The live session screen uses your camera to track your posture and movement in real time. Follow these steps for best results.",
                fontSize = 14.sp,
                color = grayText,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    CameraStepRow(
                        icon = Icons.Outlined.Straighten,
                        title = "1. Position your device",
                        body = "Place your phone or tablet 1–2 metres away, propped up so the camera faces you directly. Your full upper body should be visible in frame.",
                        showDivider = true
                    )
                    CameraStepRow(
                        icon = Icons.Outlined.LightMode,
                        title = "2. Ensure good lighting",
                        body = "Sit facing a window or light source. Avoid strong backlighting — if the camera can't see you clearly, pose detection will be less accurate.",
                        showDivider = true
                    )
                    CameraStepRow(
                        icon = Icons.Outlined.PersonOutline,
                        title = "3. Keep your body in frame",
                        body = "During the session, stay within the camera's view. A green outline will appear when your pose is detected correctly.",
                        showDivider = true
                    )
                    CameraStepRow(
                        icon = Icons.Outlined.CameraAlt,
                        title = "4. Grant camera permission",
                        body = "The app requires camera access to analyse movement. If prompted, tap Allow. You can manage this in your device Settings → Apps → Sit&Fit → Permissions.",
                        showDivider = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Frequently Asked Questions",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = sectionTeal
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    FaqRow(
                        question = "Is my camera footage stored or sent anywhere?",
                        answer = "No. All camera processing happens on-device in real time. No video is recorded, stored, or transmitted.",
                        showDivider = true
                    )
                    FaqRow(
                        question = "Why is my pose not being detected?",
                        answer = "Check that your full upper body is visible, the room is well lit, and no one else is in the frame. Re-entering the session can also reset detection.",
                        showDivider = true
                    )
                    FaqRow(
                        question = "Can I do a session without the camera?",
                        answer = "Yes. Tap the camera icon during a session to disable pose tracking. You can still follow the guided exercises manually.",
                        showDivider = true
                    )
                    FaqRow(
                        question = "What does the Pose Quality score mean?",
                        answer = "It reflects how closely your movement matched the target pose during the session. A score above 80% is considered good form.",
                        showDivider = true
                    )
                    FaqRow(
                        question = "How do I pause or end a session?",
                        answer = "Use the Pause button to rest. If you feel any discomfort, tap End Session — your progress up to that point will still be saved.",
                        showDivider = true
                    )
                    FaqRow(
                        question = "Who can I contact for support?",
                        answer = "Reach our care team at support@sitandfit.app. We're available 24/7 for any questions about your exercises or account.",
                        showDivider = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CameraStepRow(
    icon: ImageVector,
    title: String,
    body: String,
    showDivider: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE5F0EE), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.height(4.dp))
            Text(body, fontSize = 13.sp, color = Color(0xFF4A4A4A), lineHeight = 20.sp)
        }
    }
    if (showDivider) {
        HorizontalDivider(color = Color(0xFFEEEEEE))
    }
}

@Composable
private fun FaqRow(
    question: String,
    answer: String,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Outlined.HelpOutline,
                contentDescription = null,
                tint = BrandTeal,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = " $question",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(answer, fontSize = 13.sp, color = Color(0xFF4A4A4A), lineHeight = 20.sp)
    }
    if (showDivider) {
        HorizontalDivider(color = Color(0xFFEEEEEE))
    }
}
