package com.example.ass2.ui.exercises


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Accessibility
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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.data.local.Exercise
import com.example.ass2.viewmodel.ExerciseDetailViewModel
import androidx.compose.runtime.getValue

private data class Benefit(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val exerciseBenefits = listOf(
    Benefit(
        icon = Icons.Default.Accessibility,
        title = "Accessible Movement",
        description = "Supports low-impact movement for users who need seated or lower-body friendly exercise options."
    ),
    Benefit(
        icon = Icons.Default.SelfImprovement,
        title = "Gentle Control",
        description = "Encourages slow, steady movement without forcing the body beyond comfort."
    ),
    Benefit(
        icon = Icons.Default.Favorite,
        title = "Confidence Building",
        description = "Helps users practise safe movement patterns before starting a guided session."
    )
)


private fun safetyForExercise(name: String): String {
    return when {
        name.contains("Bicep", ignoreCase = true) ->
            "Use light weight or no weight at first. Keep your wrist neutral and avoid swinging your arm. Stop if you feel shoulder, elbow, or wrist pain."

        name.contains("Side", ignoreCase = true) ->
            "Keep both hips grounded on the chair. Do not collapse forward or twist while leaning. Stop if you feel sharp pain in your back or ribs."

        name.contains("Forward", ignoreCase = true) ->
            "Hinge from the hips, not the lower back. Keep the movement gentle and avoid locking your knees. Return upright slowly if you feel dizzy."

        name.contains("Twist", ignoreCase = true) ->
            "Move slowly and coordinate with your breathing. Never force the twist. Keep your chair stable and feet flat on the floor."

        else ->
            "Move slowly, stay within a comfortable range, and stop if you feel sharp pain."
    }
}

@Composable
fun ExerciseDetailScreen(
    exerciseName: String,
    onBack: () -> Unit,
    onStartSession: () -> Unit,
    viewModel: ExerciseDetailViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(exerciseName) {
        viewModel.loadExercise(exerciseName)
    }

    val exercise = uiState.exercise
    val displayName = exercise?.name ?: exerciseName
    val instructions = exercise?.instructions ?: ""
    val category = exercise?.category ?: "Accessible"

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
            HeaderImage(
                exerciseName = displayName,
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .navigationBarsPadding()
            ) {
                Spacer(Modifier.height(16.dp))


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
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            color = Color(0xFF4A4A4A)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF6B6B6B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "10 Mins",
                            fontSize = 12.sp,
                            color = Color(0xFF6B6B6B)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = displayName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 34.sp
                )

                Spacer(Modifier.height(20.dp))

                GrayCard {
                    Text(
                        text = "Overview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = instructions,
                        fontSize = 14.sp,
                        color = Color(0xFF4A4A4A),
                        lineHeight = 20.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Benefits",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(Modifier.height(12.dp))

                exerciseBenefits.forEachIndexed { index, benefit ->
                    if (index > 0) Spacer(Modifier.height(10.dp))
                    BenefitCard(benefit)
                }

                Spacer(Modifier.height(10.dp))

                GrayCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconCircle(Icons.Default.Shield)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Safety First",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = safetyForExercise(exerciseName),
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
                    Text(
                        text = "Start Session",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(22.dp))
            }
        }
    }
}

@Composable
private fun HeaderImage(
    exerciseName: String,
    onBack: () -> Unit
) {
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
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF1A1A1A)
            )
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
        Text(
            text = text,
            fontSize = 12.sp,
            color = BrandTeal,
            fontWeight = FontWeight.Medium
        )
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
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
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
        Icon(
            icon,
            contentDescription = null,
            tint = BrandTeal,
            modifier = Modifier.size(22.dp)
        )
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
                Text(
                    text = benefit.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = benefit.description,
                    fontSize = 13.sp,
                    color = Color(0xFF6B6B6B),
                    lineHeight = 18.sp
                )
            }
        }
    }
}