package com.example.ass2.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.*

@Composable
fun LegalScreen(
    initialTab: String = "terms",
    onBack: () -> Unit
) {
    var selectedTab by remember {
        mutableIntStateOf(if (initialTab == "privacy") 1 else 0)
    }
    val tabs = listOf("Terms of Service", "Privacy Policy")

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
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = BrandTeal
                )
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "Legal",
                    color = BrandTeal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.size(48.dp))
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BrandBackground,
            contentColor = BrandTeal
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            if (selectedTab == 0) TermsContent() else PrivacyContent()
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF1A1A1A),
        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color(0xFF4A4A4A),
        lineHeight = 20.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun TermsContent() {
    Text(
        text = "Last updated: April 2026",
        fontSize = 12.sp,
        color = Color(0xFF9E9E9E),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    SectionTitle("1. Acceptance of Terms")
    BodyText("By creating an account and using SmartFit Fuel, you agree to these Terms of Service. If you do not agree, do not use the app.")
    SectionTitle("2. Use of Service")
    BodyText("SmartFit Fuel is intended for personal wellness and fitness tracking. You must be at least 13 years of age to use the service. You are responsible for maintaining the confidentiality of your account credentials.")
    SectionTitle("3. Health Disclaimer")
    BodyText("The app provides general fitness information only. It is not a substitute for professional medical advice, diagnosis, or treatment. Consult a qualified healthcare provider before starting any exercise program, especially if you have a pre-existing condition.")
    SectionTitle("4. User Content")
    BodyText("You retain ownership of data you enter into the app. By using the service, you grant us a limited licence to store and process your data to provide the features of the app.")
    SectionTitle("5. Modifications")
    BodyText("We may update these terms from time to time. Continued use of the app after changes constitutes acceptance of the new terms.")
    SectionTitle("6. Termination")
    BodyText("We reserve the right to suspend or terminate accounts that violate these terms.")
    SectionTitle("7. Contact")
    BodyText("For questions about these terms, contact us at support@smartfitfuel.app.")
    Spacer(Modifier.height(32.dp))
}

@Composable
private fun PrivacyContent() {
    Text(
        text = "Last updated: April 2026",
        fontSize = 12.sp,
        color = Color(0xFF9E9E9E),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    SectionTitle("1. Information We Collect")
    BodyText("We collect information you provide directly: name, email address, fitness goals, and session data. We also collect usage data such as exercise logs and session history.")
    SectionTitle("2. How We Use Your Information")
    BodyText("Your data is used to personalise your fitness experience, track your progress, and send reminders you configure. We do not sell your personal data to third parties.")
    SectionTitle("3. Data Storage")
    BodyText("Your data is stored securely using Firebase services provided by Google. Data is encrypted in transit and at rest.")
    SectionTitle("4. Data Sharing")
    BodyText("We share data only with service providers necessary to operate the app (e.g. Firebase). We may disclose data if required by law.")
    SectionTitle("5. Your Rights")
    BodyText("You may request access to, correction of, or deletion of your personal data at any time by contacting us. You can delete your account from the Profile screen.")
    SectionTitle("6. Cookies & Analytics")
    BodyText("The app may use anonymised analytics to improve performance. No personally identifiable information is included in analytics data.")
    SectionTitle("7. Children's Privacy")
    BodyText("The app is not directed to children under 13. We do not knowingly collect data from children under 13.")
    SectionTitle("8. Contact")
    BodyText("For privacy enquiries, contact us at privacy@smartfitfuel.app.")
    Spacer(Modifier.height(32.dp))
}
