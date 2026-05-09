package com.example.ass2.ui.exercises
import com.example.ass2.R
import com.example.ass2.ui.navigation.BottomNavBar
import com.example.ass2.ui.navigation.NavTab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    val imageRes: Int
)

/** Maps an exercise name to a drawable resource - used by both list and detail screens. */
fun exerciseImageForName(name: String): Int = when {
    name.contains("Morning", ignoreCase = true) -> R.drawable.exercise4
    name.contains("Torso", ignoreCase = true) -> R.drawable.exercise1
    name.contains("Leg", ignoreCase = true) -> R.drawable.exercise2
    name.contains("Push", ignoreCase = true) -> R.drawable.exercise3
    name.contains("Shoulder", ignoreCase = true) -> R.drawable.exercise1
    name.contains("Ankle", ignoreCase = true) -> R.drawable.exercise2
    else -> R.drawable.exercise1
}

private val sampleExercises = listOf(
    Exercise(
        name = "Morning Mobility: Seated Flow",
        description = "A gentle sequence to wake up your joints and improve circulation before starting the day.",
        category = "MOBILITY",
        tags = listOf("Recommended", "Seated", "Beginner"),
        imageRes = R.drawable.exercise4
    ),
    Exercise(
        name = "Gentle Torso Twists",
        description = "Build upper back strength and improve posture safely from a seated position.",
        category = "STRENGTH",
        tags = listOf("Seated", "Beginner"),
        imageRes = R.drawable.exercise1
    ),
    Exercise(
        name = "Leg Extensions",
        description = "Gentle knee mobilisation to maintain joint fluid and strengthen the quadriceps.",
        category = "MOBILITY",
        tags = listOf("Lower Body", "Intermediate"),
        imageRes = R.drawable.exercise2
    ),
    Exercise(
        name = "Wall Push-ups",
        description = "A low-impact variation of the classic push-up, excellent for chest and arm strength without floor work.",
        category = "STRENGTH",
        tags = listOf("Standing", "Beginner"),
        imageRes = R.drawable.exercise3
    ),
    Exercise(
        name = "Seated Shoulder Rolls",
        description = "Relieve upper body tension and improve shoulder mobility from a comfortable seated position.",
        category = "MOBILITY",
        tags = listOf("Seated", "Beginner"),
        imageRes = R.drawable.exercise1
    ),
    Exercise(
        name = "Ankle Circles",
        description = "Simple ankle rotations to improve circulation and maintain lower limb flexibility.",
        category = "FLEXIBILITY",
        tags = listOf("Seated", "Beginner"),
        imageRes = R.drawable.exercise2
    ),
)

private val filterCategories = listOf("All", "Seated", "Recommended", "Lower Body")

@Composable
fun ExercisesScreen(
    onExerciseClick: (String) -> Unit,
    onTabSelected: (NavTab) -> Unit,
    initialFilter: String = "All"
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(initialFilter) }

    val filteredExercises = sampleExercises.filter { exercise ->
        val matchesSearch = searchQuery.isEmpty() ||
                exercise.name.contains(searchQuery, ignoreCase = true) ||
                exercise.description.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == "All" ||
                exercise.tags.any { it.equals(selectedFilter, ignoreCase = true) }
        matchesSearch && matchesFilter
    }

    Scaffold(
        bottomBar = { BottomNavBar(selected = NavTab.EXERCISES, onTabSelected = onTabSelected) },
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Exercises",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = BrandTeal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            SearchField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(filterCategories) { category ->
                    FilterChipItem(
                        label = category,
                        selected = category == selectedFilter,
                        onClick = { selectedFilter = category }
                    )
                }
            }

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
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search exercises...", color = Color(0xFFBBBBBB), fontSize = 14.sp) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF9E9E9E))
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = BrandTeal
        ),
        modifier = modifier
    )
}

@Composable
private fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) BrandTeal else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Color.White else Color(0xFF4A4A4A)
        )
    }
}

@Composable
private fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Image(
                painter = painterResource(exercise.imageRes),
                contentDescription = exercise.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            Column(modifier = Modifier.padding(16.dp)) {
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
