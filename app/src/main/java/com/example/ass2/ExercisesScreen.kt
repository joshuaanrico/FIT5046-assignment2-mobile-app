package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandBackground
import com.example.ass2.ui.theme.BrandTeal

data class Exercise(
    val name: String,
    val description: String,
    val category: String,
    val tags: List<String>,
    val placeholderColor: Color
)

val sampleExercises = listOf(
    Exercise(
        name = "Gentle Torso Twists",
        description = "Build upper back strength and improve posture safely from a seated position.",
        category = "STRENGTH",
        tags = listOf("Seated", "Beginner"),
        placeholderColor = Color(0xFFB2DFDB)
    ),
    Exercise(
        name = "Leg Extensions",
        description = "Gentle knee mobilization to maintain joint fluid and strengthen the quadriceps.",
        category = "MOBILITY",
        tags = listOf("Lower Body", "Intermediate"),
        placeholderColor = Color(0xFFA5D6D2)
    ),
    Exercise(
        name = "Wall Push-ups",
        description = "A low-impact variation of the classic push-up, excellent for chest and arm strength without...",
        category = "STRENGTH",
        tags = listOf("Standing", "Beginner"),
        placeholderColor = Color(0xFF80CBC4)
    ),
    Exercise(
        name = "Seated Shoulder Rolls",
        description = "Relieve upper body tension and improve shoulder mobility from a comfortable seated position.",
        category = "MOBILITY",
        tags = listOf("Seated", "Beginner"),
        placeholderColor = Color(0xFF4DB6AC)
    ),
    Exercise(
        name = "Ankle Circles",
        description = "Simple ankle rotations to improve circulation and maintain lower limb flexibility.",
        category = "FLEXIBILITY",
        tags = listOf("Seated", "Beginner"),
        placeholderColor = Color(0xFF26A69A)
    ),
)

val filterCategories = listOf("All", "Seated", "Recommended", "Lower Body")

@Composable
fun ExercisesScreen(
    onExerciseClick: (String) -> Unit,
    onTabSelected: (NavTab) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredExercises = sampleExercises.filter { exercise ->
        val matchesSearch = searchQuery.isEmpty() ||
                exercise.name.contains(searchQuery, ignoreCase = true) ||
                exercise.description.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == "All" ||
                exercise.tags.any { it.equals(selectedFilter, ignoreCase = true) }
        matchesSearch && matchesFilter
    }

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(color = Color(0xFFE0E0E0))
                BottomNavBar(selected = NavTab.EXERCISES, onTabSelected = onTabSelected)
            }
        },
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    text = "Exercises",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandTeal
                )
            }

            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) {
                                Text("Search exercises...", fontSize = 14.sp, color = Color(0xFFBBBBBB))
                            }
                            inner()
                        }
                    )
                }
            }

            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(filterCategories) { category ->
                    val isSelected = category == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) BrandTeal else Color.White)
                            .clickable { selectedFilter = category }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFF4A4A4A)
                        )
                    }
                }
            }

            // Exercise list
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredExercises) { exercise ->
                    ExerciseCard(exercise = exercise, onClick = { onExerciseClick(exercise.name) })
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(exercise.placeholderColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = exercise.name.take(1),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Category badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrandTeal.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(exercise.category, fontSize = 11.sp, color = BrandTeal, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(8.dp))

                Text(exercise.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))

                Spacer(Modifier.height(4.dp))

                Text(exercise.description, fontSize = 13.sp, color = Color(0xFF6B6B6B), lineHeight = 18.sp)

                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    exercise.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF0F0F0))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(tag, fontSize = 12.sp, color = Color(0xFF4A4A4A))
                        }
                    }
                }
            }
        }
    }
}
