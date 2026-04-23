package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SessionDetailScreen() {
    val bgColor = Color(0xFFF4F5F4)
    val teal = Color(0xFF0D7377)
    val lightTeal = Color(0xFFD6E9E7)
    val textDark = Color(0xFF1F1F1F)
    val textGray = Color(0xFF666666)
    val cardColor = Color(0xFFF7F7F7)
    val blueCircle = Color(0xFF496F93)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {

            TopDetailBar(teal)

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Seated\nTorso Twist",
                fontSize = 34.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                color = teal
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = textGray,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = " Oct 24, 2023 • 9:00 AM",
                    color = textGray,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoChip(
                    icon = Icons.Outlined.Timer,
                    text = "15:30",
                    background = lightTeal,
                    textColor = textGray
                )
                InfoChip(
                    icon = Icons.Outlined.Accessibility,
                    text = "Seated",
                    background = lightTeal,
                    textColor = textGray
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleIcon(
                            bgColor = teal,
                            icon = Icons.Outlined.Psychology,
                            iconTint = Color.White
                        )

                        Spacer(modifier = Modifier.size(14.dp))

                        Column {
                            Text(
                                text = "Pose Quality",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = teal
                            )
                            Text(
                                text = "Good Form - 92%",
                                fontSize = 15.sp,
                                color = textGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    LinearProgressIndicator(
                        progress = { 0.92f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        color = teal,
                        trackColor = Color(0xFFE8E8E8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIcon(
                        bgColor = blueCircle,
                        icon = Icons.Outlined.FavoriteBorder,
                        iconTint = Color.White
                    )

                    Spacer(modifier = Modifier.size(14.dp))

                    Column {
                        Text(
                            text = "Heart Rate",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF20486A)
                        )
                        Text(
                            text = "Avg 85 BPM",
                            fontSize = 15.sp,
                            color = textGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircleIcon(
                            bgColor = lightTeal,
                            icon = Icons.Outlined.MonitorHeart,
                            iconTint = textGray
                        )

                        Spacer(modifier = Modifier.size(14.dp))

                        Text(
                            text = "Journal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = teal
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF0F0F0))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Feeling energized, no pain. The twisting motion felt much smoother today than last week.",
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            color = textGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopDetailBar(teal: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = teal
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Session Detail",
            color = teal,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = teal
            )
        }
    }
}

@Composable
fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    background: Color,
    textColor: Color
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
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = " $text",
            color = textColor,
            fontSize = 16.sp
        )
    }
}

@Composable
fun CircleIcon(
    bgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}
