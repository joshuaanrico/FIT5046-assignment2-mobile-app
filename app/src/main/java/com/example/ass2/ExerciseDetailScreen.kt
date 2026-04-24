package com.example.ass2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandTeal

private data class Benefit(val icon: ImageVector, val title: String, val description: String)

private val sampleBenefits = listOf(
    Benefit(
        icon = Icons.Default.Accessibility,
        title = "Improves Mobility",
        description = "Gradually increases the healthy range of motion in your mid and lower spine."
    ),
    Benefit(
        icon = Icons.Default.SelfImprovement,
        title = "Relieves Tension",
        description = "Gently releases built-up muscular stress around the lower back and obliques."
    ),
    Benefit(
        icon = Icons.Default.Favorite,
        title = "Promotes Circulation",
        description = "Smooth, continuous movement encourages healthy blood flow to the spinal discs."
    ),
)

@Composable
fun ExerciseDetailScreen(
    exerciseName: String,
    onBack: () -> Unit,
    onStartSession: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HeaderImage(exerciseName = exerciseName, onBack = onBack)

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .navigationBarsPadding()
            ) {
                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Seated", "Low Impact").forEach { tag ->
                        TealPill(tag)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF0F0F0))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text("Lower-Body Friendly", fontSize = 12.sp, color = Color(0xFF4A4A4A))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF6B6B6B), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("10 Mins", fontSize = 12.sp, color = Color(0xFF6B6B6B))
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = exerciseName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 34.sp
                )

                Spacer(Modifier.height(20.dp))

                GrayCard {
                    Text("Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "A calming, low-impact exercise designed to gradually improve spinal mobility and reduce lower back tension. This restorative movement is perfect for beginning your day with ease or unwinding safely before rest. Highly suitable for users with lower-body mobility limitations as it is performed fully seated.",
                        fontSize = 14.sp,
                        color = Color(0xFF4A4A4A),
                        lineHeight = 20.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text("Benefits", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))

                Spacer(Modifier.height(12.dp))

                sampleBenefits.forEachIndexed { index, benefit ->
                    if (index > 0) Spacer(Modifier.height(10.dp))
                    BenefitCard(benefit)
                }

                Spacer(Modifier.height(10.dp))

                GrayCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconCircle(Icons.Default.Shield)
                        Spacer(Modifier.width(12.dp))
                        Text("Safety First", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Move slowly and coordinate with your breathing. Never force a twist past your point of comfort. If you feel any sharp pain, gently return to the centre position.\n\nEnsure your chair is sturdy and your feet are flat on the floor before beginning the sequence.",
                        fontSize = 14.sp,
                        color = Color(0xFF4A4A4A),
                        lineHeight = 20.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onStartSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text("Start Session", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(Modifier.height(22.dp))
            }
        }
    }
}

@Composable
private fun HeaderImage(exerciseName: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Image(
            painter = painterResource(exerciseImageForName(exerciseName)),
            contentDescription = exerciseName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1A1A1A))
        }
    }
}

@Composable
private fun TealPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BrandTeal.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(text, fontSize = 12.sp, color = BrandTeal, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun GrayCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun IconCircle(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(BrandTeal.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun BenefitCard(benefit: Benefit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconCircle(benefit.icon)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(benefit.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                Spacer(Modifier.height(4.dp))
                Text(benefit.description, fontSize = 13.sp, color = Color(0xFF6B6B6B), lineHeight = 18.sp)
            }
        }
    }
}
