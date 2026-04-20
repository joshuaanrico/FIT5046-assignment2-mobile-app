package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandTeal
import com.example.ass2.ui.theme.BrandBackground

@Composable
fun HomeScreen(
    onGoToExercises: () -> Unit,
    onTabSelected: (NavTab) -> Unit
) {
    var selectedMood by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(color = Color(0xFFE0E0E0))
                BottomNavBar(selected = NavTab.HOME, onTabSelected = onTabSelected)
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
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SmartFit Fuel",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandTeal
                )
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(24.dp))

                // Greeting
                Text(
                    text = "Good morning,\nAlex",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 34.sp
                )
                Text(
                    text = "Let's find your rhythm for today.",
                    fontSize = 14.sp,
                    color = Color(0xFF6B6B6B),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(24.dp))

                // Up Next card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8F4F4))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFB2DFDB))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Up Next", fontSize = 12.sp, color = BrandTeal, fontWeight = FontWeight.Medium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF6B6B6B), modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("15 min", fontSize = 12.sp, color = Color(0xFF6B6B6B))
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Morning Mobility:\nSeated Flow",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            lineHeight = 28.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "A gentle sequence to wake up your joints and improve circulation before starting the day.",
                            fontSize = 13.sp,
                            color = Color(0xFF4A4A4A),
                            lineHeight = 18.sp
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = { onGoToExercises() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D3D40))
                        ) {
                            Text("Start Recommended Session", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Daily Check-in
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Daily Check-in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                        }
                        Text(
                            text = "How is your body feeling right now?",
                            fontSize = 13.sp,
                            color = Color(0xFF6B6B6B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("😊" to "Great", "😐" to "Okay", "😣" to "Sore").forEach { (emoji, label) ->
                                val isSelected = selectedMood == label
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) BrandTeal.copy(alpha = 0.1f) else Color(0xFFF5F5F5))
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.dp,
                                            color = if (isSelected) BrandTeal else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedMood = label }
                                        .padding(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    Text(emoji, fontSize = 22.sp)
                                    Text(label, fontSize = 12.sp, color = Color(0xFF4A4A4A), modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Last Session
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Last Session", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                            }
                            Text("Yesterday", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                        }

                        Spacer(Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF8F8F8))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Evening Wind Down", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                                Text("Low impact stretching", fontSize = 13.sp, color = Color(0xFF6B6B6B), modifier = Modifier.padding(top = 2.dp))
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("20m", fontSize = 12.sp, color = Color(0xFF6B6B6B))
                                    }
                                    Text(
                                        text = "View Details >",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandTeal
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Heart Rate
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Heart Rate", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("72", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                            Spacer(Modifier.width(4.dp))
                            Text("bpm", fontSize = 14.sp, color = Color(0xFF6B6B6B), modifier = Modifier.padding(bottom = 6.dp))
                        }
                        Text("Resting average. Looking steady.", fontSize = 13.sp, color = Color(0xFF6B6B6B))

                        Spacer(Modifier.height(12.dp))

                        // Bar chart placeholder
                        val barHeights = listOf(0.4f, 0.5f, 0.45f, 0.6f, 0.55f, 0.7f, 0.9f)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            barHeights.forEachIndexed { index, fraction ->
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .fillMaxHeight(fraction)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(if (index == barHeights.lastIndex) BrandTeal else BrandTeal.copy(alpha = 0.3f + index * 0.1f))
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Stay Hydrated tip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFDCEEF8))
                        .padding(16.dp)
                ) {
                    Row {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Stay Hydrated", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Remember to drink water before starting your mobility routine. It helps keep your joints lubricated and moving smoothly.",
                                fontSize = 13.sp,
                                color = Color(0xFF4A4A4A),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Go to Exercises link
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGoToExercises() },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Go to Exercises", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BrandTeal)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
