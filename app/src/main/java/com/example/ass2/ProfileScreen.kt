package com.example.ass2

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
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    onTabSelected: (NavTab) -> Unit,
    onReminderListClick: () -> Unit,
    onSignOut: () -> Unit,
) {
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
            Text(
                text = "Profile",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = teal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF18333C)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Alex Johnson",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "alex@example.com",
                fontSize = 16.sp,
                color = textGray
            )

            Spacer(modifier = Modifier.height(28.dp))

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

                    ProfileChip(
                        text = "Seated Mobility",
                        background = lightTeal,
                        textColor = textGray,
                        icon = Icons.Outlined.Accessibility
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileChip(
                        text = "Primary Goal: Flexibility",
                        background = lightGray,
                        textColor = textDark,
                        icon = Icons.Outlined.FitnessCenter
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Your workouts are automatically adapted to prioritize seated exercises and upper-body range of motion.",
                        fontSize = 15.sp,
                        lineHeight = 25.sp,
                        color = textGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            MenuCard(
                icon = Icons.Outlined.Notifications,
                title = "Reminder List",
                textDark = textDark,
                iconBg = Color(0xFFE5F0EE),
                iconTint = teal,
                onClick = onReminderListClick
            )

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier.clickable { onSignOut() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ExitToApp,
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

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
