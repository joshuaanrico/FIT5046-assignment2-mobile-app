package com.example.ass2.ui.session

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandBackground
import com.example.ass2.ui.theme.BrandTeal
import com.example.ass2.ui.theme.BrandTealDark
import com.example.ass2.ui.theme.CardBackground

@Composable
fun SessionResultScreen(
    onCancel: () -> Unit,
    onSaveSession: () -> Unit,
    onRepeatLater: () -> Unit,
    onHome: () -> Unit
) {
    var selectedFatigueLevel by remember { mutableStateOf("") }
    var selectedDiscomfort by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.statusBarsPadding())

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandTealDark)
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "directed home",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterStart)
                    .clickable { onCancel() }
            )
            Text(
                text = "Session Summary",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // session details card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandBackground)
                        .padding(25.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "100% complete",
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFBBCBCF),
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "Great\n\nSession!",
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = BrandTealDark,
                            fontWeight = FontWeight.Light,
                            fontSize = 36.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "You successfully completed the\nseated torso twist routine.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BrandBackground)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "duration",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = "Duration", fontSize = 14.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "12.45",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BrandBackground)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.MonitorHeart,
                                        contentDescription = "Average heart rate of user",
                                        tint = Color.DarkGray,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = "Avg Heart Rate", fontSize = 14.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "80",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "BPM", fontSize = 14.sp, color = Color.DarkGray)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandBackground)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Pose Quality", fontSize = 14.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Good Form",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandTealDark
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .background(BrandTealDark, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Accessibility,
                                        contentDescription = "quality of pose",
                                        tint = Color.White,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // user feedback card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 25.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "How do you feel?",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E1E1E)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "Fatigue Level", fontSize = 16.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (selectedFatigueLevel == "Energized") BrandTealDark else CardBackground,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedFatigueLevel = "Energized" }
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.SentimentVerySatisfied,
                                        contentDescription = "Energized",
                                        modifier = Modifier.size(28.dp),
                                        tint = if (selectedFatigueLevel == "Energized") Color.White else Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Energized",
                                        fontSize = 12.sp,
                                        color = if (selectedFatigueLevel == "Energized") Color.White else Color.Black
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (selectedFatigueLevel == "Normal") BrandTealDark else CardBackground,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedFatigueLevel = "Normal" }
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.SentimentSatisfied,
                                        contentDescription = "Normal",
                                        tint = if (selectedFatigueLevel == "Normal") Color.White else Color.DarkGray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Normal",
                                        fontSize = 12.sp,
                                        color = if (selectedFatigueLevel == "Normal") Color.White else Color.Black
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (selectedFatigueLevel == "Fatigued") BrandTealDark else CardBackground,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedFatigueLevel = "Fatigued" }
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.SentimentDissatisfied,
                                        contentDescription = "Fatigued",
                                        modifier = Modifier.size(28.dp),
                                        tint = if (selectedFatigueLevel == "Fatigued") Color.White else Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Fatigued",
                                        fontSize = 12.sp,
                                        color = if (selectedFatigueLevel == "Fatigued") Color.White else Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color.Gray)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Any discomfort during exercise?",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (selectedDiscomfort == "None") BrandTealDark else CardBackground,
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .clickable { selectedDiscomfort = "None" }
                                    .padding(vertical = 18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "None",
                                    fontSize = 14.sp,
                                    color = if (selectedDiscomfort == "None") Color.White else Color.Black
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (selectedDiscomfort == "Mild") BrandTealDark else CardBackground,
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .clickable { selectedDiscomfort = "Mild" }
                                    .padding(vertical = 18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Mild",
                                    fontSize = 14.sp,
                                    color = if (selectedDiscomfort == "Mild") Color.White else Color.Black
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (selectedDiscomfort == "Severe") Color(0xFFF8D9D9) else CardBackground,
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .clickable { selectedDiscomfort = "Severe" }
                                    .padding(vertical = 18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Severe",
                                    fontSize = 14.sp,
                                    color = if (selectedDiscomfort == "Severe") Color(0xFFB00020) else Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Button(
                        onClick = onSaveSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save session",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Session",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = onRepeatLater,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD6E9E8))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Repeat later",
                            tint = BrandTealDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Repeat Later",
                            color = BrandTealDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Button(
                        onClick = onHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Return to home screen",
                            tint = BrandTealDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Return to home",
                            color = BrandTealDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
