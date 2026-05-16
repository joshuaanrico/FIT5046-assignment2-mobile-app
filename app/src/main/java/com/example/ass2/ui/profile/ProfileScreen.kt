package com.example.ass2.ui.profile
import com.example.ass2.R
import com.example.ass2.ui.navigation.BottomNavBar
import com.example.ass2.ui.navigation.NavTab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.Image
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.viewmodel.ProfileViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.viewmodel.ProfileViewModel

// Displays the user's profile summary — name, email, accessibility info, and navigation menu.
// Data is sourced from Room (via ProfileViewModel) and Firebase Auth.
@Composable
fun ProfileScreen(
    onTabSelected: (NavTab) -> Unit,
    onReminderListClick: () -> Unit,
    onProfileSetupClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    // Collect the profile Flow as Compose state — recomposes automatically when Room data changes
    val profile by viewModel.profile.collectAsState()

    // Fall back to placeholder text if the user hasn't completed profile setup yet
    val displayName = profile?.displayName?.takeIf { it.isNotBlank() } ?: "No name set"
    val email = viewModel.email.takeIf { it.isNotBlank() } ?: "No email"
    val conditions = profile?.conditions?.takeIf { it.isNotBlank() } ?: "None recorded"

    val bgColor = Color(0xFFF4F5F4)
    val teal = Color(0xFF0D7377)
    val lightTeal = Color(0xFFD5ECE8)
    val lightGray = Color(0xFFECECEC)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF666666)
    val cardColor = Color(0xFFF8F8F8)

    Scaffold(
        bottomBar = { BottomNavBar(selected = NavTab.PROFILE, onTabSelected = onTabSelected) },
        containerColor = bgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with screen title and Help shortcut
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = teal
                )
                Text(
                    text = "Help",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = teal,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { onHelpClick() }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Profile avatar image
            Image(
                painter = painterResource(R.drawable.exercise4),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Display name from Room — updates automatically when profile is saved
            Text(
                text = displayName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Email from Firebase Auth — always reflects the currently logged-in account
            Text(
                text = email,
                fontSize = 16.sp,
                color = textGray
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Accessibility profile card — shows mobility type, goal, and health conditions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Accessibility Profile",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )

                        Icon(
                            imageVector = Icons.Outlined.Accessibility,
                            contentDescription = null,
                            tint = teal
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Static chips for mobility type and primary goal
                    ProfileChip(
                        text = conditionText,
                        background = lightTeal,
                        textColor = textGray,
                        icon = Icons.Outlined.Accessibility
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileChip(
                        text = painText,
                        background = lightGray,
                        textColor = textDark,
                        icon = Icons.Outlined.FitnessCenter
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Health conditions entered during profile setup — from Room
                    Text(
                        text = "Conditions: $conditions",
                        fontSize = 15.sp,
                        lineHeight = 25.sp,
                        color = textGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation menu cards — each routes to a different settings/config screen
            MenuCard(
                icon = Icons.Outlined.AccountCircle,
                title = "Profile Setup",
                textDark = textDark,
                iconBg = Color(0xFFE5F0EE),
                iconTint = teal,
                onClick = onProfileSetupClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            MenuCard(
                icon = Icons.Outlined.Notifications,
                title = "Reminder List",
                textDark = textDark,
                iconBg = Color(0xFFE5F0EE),
                iconTint = teal,
                onClick = onReminderListClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            MenuCard(
                icon = Icons.Outlined.Settings,
                title = "Settings",
                textDark = textDark,
                iconBg = Color(0xFFE5F0EE),
                iconTint = teal,
                onClick = onSettingsClick
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Sign out — calls Firebase signOut via AuthViewModel in MainActivity
            Row(
                modifier = Modifier.clickable {
                    viewModel.signOut()
                    onSignOut() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = teal
                )
                Text(
                    text = " Sign Out",
                    color = teal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// A pill-shaped chip used to display accessibility attributes like mobility type or goal
@Composable
private fun ProfileChip(
    text: String,
    background: Color,
    textColor: Color,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = " $text",
            color = textColor,
            fontSize = 15.sp
        )
    }
}

// A tappable card row used for navigating to settings sub-screens
@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    textDark: Color,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular icon background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Text(
                text = title,
                modifier = Modifier.padding(start = 14.dp),
                fontSize = 18.sp,
                color = textDark
            )

            Spacer(modifier = Modifier.weight(1f))

            // Chevron indicates this card is tappable and leads to another screen
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
