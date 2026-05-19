package com.example.ass2.ui.home
import com.example.ass2.ui.navigation.BottomNavBar
import com.example.ass2.ui.navigation.NavTab

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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandBackground
import com.example.ass2.ui.theme.BrandTeal
import com.example.ass2.ui.theme.BrandTealDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.viewmodel.HomeViewModel

private val moods = listOf(
    Icons.Default.SentimentVerySatisfied to "Great",
    Icons.Default.SentimentSatisfied to "Okay",
    Icons.Default.SentimentDissatisfied to "Sore"
)

@Composable
fun HomeScreen(
    onGoToExerciseDetail: (String) -> Unit,
    onTabSelected: (NavTab) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedMood by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = { BottomNavBar(selected = NavTab.HOME, onTabSelected = onTabSelected) },
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Sit&Fit",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = BrandTeal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Good morning,\n${uiState.greetingName}",
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

                UpNextCard(
                    exerciseName = uiState.recommendedExercise?.name ?: "Seated Mobility Flow",
                    description = uiState.recommendedExercise?.instructions
                        ?: "A gentle sequence to improve circulation and reduce stiffness.",
                    isLoading = uiState.isLoadingExercise,
                    onStart = {
                        // navigates to the exercise details screen for the current recommended exercise.
                        uiState.recommendedExercise?.let { onGoToExerciseDetail(it.name) }
                    }
                )

//                Spacer(Modifier.height(16.dp))
//
//                SectionCard(icon = Icons.Default.Mood, title = "Daily Check-in") {
//                    Text(
//                        text = "How is your body feeling right now?",
//                        fontSize = 13.sp,
//                        color = Color(0xFF6B6B6B),
//                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
//                    )
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceEvenly
//                    ) {
//                        moods.forEach { (icon, label) ->
//                            MoodChip(
//                                icon = icon,
//                                label = label,
//                                selected = selectedMood == label,
//                                onClick = { selectedMood = label }
//                            )
//                        }
//                    }
//                }

                Spacer(Modifier.height(16.dp))

                SectionCard(
                    icon = Icons.Default.History,
                    title = "Last Session",
                    trailing = {
                        Text(
                            text = if (uiState.recentSession == null) "Empty" else "Latest",
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                ) {

                    Spacer(Modifier.height(10.dp))

                    val recentSession = uiState.recentSession

                    if (recentSession == null) {
                        Text(
                            text = "No sessions completed yet.",
                            fontSize = 14.sp,
                            color = Color(0xFF6B6B6B)
                        )

                        Text(
                            text = "Start your first exercise session to build your history.",
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF8F8F8))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = recentSession.exerciseName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1A1A1A)
                                )

                                Text(
                                    text = "${recentSession.durationSec / 60} min session",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B6B6B),
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = Color(0xFF9E9E9E),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "${recentSession.durationSec / 60}m",
                                            fontSize = 12.sp,
                                            color = Color(0xFF6B6B6B)
                                        )
                                    }

                                    Text(
                                        text = recentSession.avgHr?.let { "$it bpm avg" } ?: "No HR data",
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

                SectionCard(
                    icon = Icons.Default.FitnessCenter,
                    title = "Recent Summary"
                ) {
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryStat(
                            label = "Sessions",
                            value = uiState.totalSessions.toString()
                        )

                        SummaryStat(
                            label = "Minutes",
                            value = uiState.totalMinutes.toString()
                        )
                    }
                }


                Spacer(Modifier.height(16.dp))

                SectionCard(icon = Icons.Default.Favorite, title = "Heart Rate") {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("72", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                        Spacer(Modifier.width(4.dp))
                        Text("bpm", fontSize = 14.sp, color = Color(0xFF6B6B6B), modifier = Modifier.padding(bottom = 6.dp))
                    }
                    Text("Resting average. Looking steady.", fontSize = 13.sp, color = Color(0xFF6B6B6B))

                    Spacer(Modifier.height(12.dp))

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
                                    .background(
                                        if (index == barHeights.lastIndex) BrandTeal else BrandTeal.copy(
                                            alpha = 0.3f + index * 0.1f
                                        )
                                    )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                AdaptiveRecommendationBanner(message = uiState.adaptiveMessage)

                Spacer(Modifier.height(20.dp))

//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable { onGoToExercises() },
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(18.dp))
//                    Spacer(Modifier.width(6.dp))
//                    Text("Go to Exercises", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BrandTeal)
//                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun UpNextCard(
    exerciseName: String,
    description: String,
    isLoading: Boolean,
    onStart: () -> Unit
) {
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
                    Text(
                        text = "Recommended",
                        fontSize = 12.sp,
                        color = BrandTeal,
                        fontWeight = FontWeight.Medium
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
                    Text("10-15 min", fontSize = 12.sp, color = Color(0xFF6B6B6B))
                }
            }

            Spacer(Modifier.height(12.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = BrandTeal,
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    text = exerciseName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 28.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = description
                        .replace(Regex("<.*?>"), "")
                        .take(160),
                    fontSize = 13.sp,
                    color = Color(0xFF4A4A4A),
                    lineHeight = 18.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
            ) {
                Text(
                    text = "View Exercises",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
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
                    Icon(icon, contentDescription = null, tint = BrandTeal, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                }
                trailing?.invoke()
            }
            content()
        }
    }
}

@Composable
private fun MoodChip(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) BrandTeal.copy(alpha = 0.1f) else Color(0xFFF5F5F5))
            .border(
                width = if (selected) 1.5.dp else 0.dp,
                color = if (selected) BrandTeal else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = if (selected) BrandTeal else Color(0xFF4A4A4A)
        )
        Text(label, fontSize = 12.sp, color = Color(0xFF4A4A4A), modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun SummaryStat(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BrandTeal
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B6B6B)
        )
    }
}

@Composable
private fun AdaptiveRecommendationBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFDCEEF8))
            .padding(16.dp)
    ) {
        Row {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = BrandTeal,
                modifier = Modifier.size(22.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = "Today’s Recommendation",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = message,
                    fontSize = 13.sp,
                    color = Color(0xFF4A4A4A),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
