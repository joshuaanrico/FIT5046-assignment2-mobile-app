package com.example.ass2.ui.sessions
import com.example.ass2.ui.navigation.BottomNavBar
import com.example.ass2.ui.navigation.NavTab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.data.local.Session
import com.example.ass2.ui.theme.BrandBackground
import com.example.ass2.ui.theme.BrandTeal
import com.example.ass2.viewmodel.SessionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class SessionRow(
    val id: Long,
    val title: String,
    val durationMins: Int,
    val durationLabel: String,
    val date: String,
    val dateMillis: Long,
    val status: String,
    val accentColor: Color,
    val icon: ImageVector,
    val week: String
)

private fun startOfWeekMillis(now: Long = System.currentTimeMillis()): Long =
    Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }.timeInMillis

private fun isSameDay(a: Long, b: Long): Boolean {
    val calA = Calendar.getInstance().apply { timeInMillis = a }
    val calB = Calendar.getInstance().apply { timeInMillis = b }
    return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
            calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
}

private fun formatDateShort(millis: Long): String =
    SimpleDateFormat("MMM d", Locale.ENGLISH).format(Date(millis))

private fun iconFor(name: String): ImageVector = when {
    name.contains("breath", true) || name.contains("medit", true) -> Icons.Outlined.Spa
    name.contains("twist", true) || name.contains("stretch", true) -> Icons.Outlined.Spa
    name.contains("neck", true) || name.contains("rotation", true) -> Icons.Outlined.Person
    else -> Icons.Outlined.FitnessCenter
}

private fun accentFor(status: String): Color =
    if (status == "Completed") BrandTeal else Color(0xFF315C7C)

private fun Session.toRow(): SessionRow {
    val mins = (durationSec / 60).coerceAtLeast(0)
    val status = if (poseQuality == null || poseQuality < 0.5f) "Partial" else "Completed"
    val weekStart = startOfWeekMillis()
    val lastWeekStart = weekStart - 7L * 24 * 60 * 60 * 1000
    val bucket = when {
        startTime >= weekStart -> "This Week"
        startTime >= lastWeekStart -> "Last Week"
        else -> "Earlier"
    }
    return SessionRow(
        id = id,
        title = exerciseName,
        durationMins = mins,
        durationLabel = "$mins mins",
        date = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Date(startTime)),
        dateMillis = startTime,
        status = status,
        accentColor = accentFor(status),
        icon = iconFor(exerciseName),
        week = bucket
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    onTabSelected: (NavTab) -> Unit,
    onSessionClick: (Long) -> Unit,
    vm: SessionViewModel = viewModel()
) {
    val darkText = Color(0xFF222222)
    val lightText = Color(0xFF777777)

    var searchQuery by remember { mutableStateOf("") }
    var typeFilter by remember { mutableStateOf("All") }
    var durationFilter by remember { mutableStateOf("All") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var showTypePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDurationPicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val sessions by vm.sessions.collectAsStateWithLifecycle()
    val rows = remember(sessions) { sessions.map { it.toRow() } }

    val hasActiveFilter = searchQuery.isNotEmpty() || typeFilter != "All" ||
            selectedDateMillis != null || durationFilter != "All"

    val filteredRows = remember(rows, searchQuery, typeFilter, selectedDateMillis, durationFilter) {
        rows.filter { item ->
            val matchesSearch = searchQuery.isEmpty() ||
                    item.title.contains(searchQuery, ignoreCase = true)
            val matchesType = typeFilter == "All" || item.status == typeFilter
            val matchesDate = selectedDateMillis == null ||
                    isSameDay(selectedDateMillis!!, item.dateMillis)
            val matchesDuration = when (durationFilter) {
                "≤5 min" -> item.durationMins <= 5
                "6-10 min" -> item.durationMins in 6..10
                ">10 min" -> item.durationMins > 10
                else -> true
            }
            matchesSearch && matchesType && matchesDate && matchesDuration
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(selected = NavTab.SESSIONS, onTabSelected = onTabSelected) },
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Sessions History",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = BrandTeal,
                textAlign = TextAlign.Center
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search sessions...",
                                color = Color(0xFFBBBBBB),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF9E9E9E)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = BrandTeal
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box {
                            SessionFilterChip(
                                text = if (typeFilter == "All") "Type" else typeFilter,
                                selected = typeFilter != "All",
                                icon = Icons.Outlined.FilterList,
                                onClick = { showTypePicker = true }
                            )
                            DropdownMenu(
                                expanded = showTypePicker,
                                onDismissRequest = { showTypePicker = false }
                            ) {
                                listOf("All", "Completed", "Partial").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            typeFilter = option
                                            showTypePicker = false
                                        }
                                    )
                                }
                            }
                        }

                        SessionFilterChip(
                            text = if (selectedDateMillis == null) "Date"
                                   else formatDateShort(selectedDateMillis!!),
                            selected = selectedDateMillis != null,
                            icon = null,
                            onClick = { showDatePicker = true }
                        )

                        Box {
                            SessionFilterChip(
                                text = if (durationFilter == "All") "Duration" else durationFilter,
                                selected = durationFilter != "All",
                                icon = null,
                                onClick = { showDurationPicker = true }
                            )
                            DropdownMenu(
                                expanded = showDurationPicker,
                                onDismissRequest = { showDurationPicker = false }
                            ) {
                                listOf("All", "≤5 min", "6-10 min", ">10 min").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            durationFilter = option
                                            showDurationPicker = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }

                if (rows.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No sessions yet — start your first one",
                                color = lightText,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else if (hasActiveFilter) {
                    item {
                        Text(
                            text = if (filteredRows.isEmpty()) "No results" else "Results",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkText
                        )
                    }
                    items(filteredRows, key = { it.id }) { item ->
                        SessionCard(item, BrandTeal, darkText, lightText) { onSessionClick(item.id) }
                    }
                } else {
                    val thisWeek = rows.filter { it.week == "This Week" }
                    val lastWeek = rows.filter { it.week == "Last Week" }
                    val earlier = rows.filter { it.week == "Earlier" }
                    if (thisWeek.isNotEmpty()) {
                        item { SectionHeader("This Week", darkText) }
                        items(thisWeek, key = { it.id }) { item ->
                            SessionCard(item, BrandTeal, darkText, lightText) { onSessionClick(item.id) }
                        }
                    }
                    if (lastWeek.isNotEmpty()) {
                        item { SectionHeader("Last Week", darkText) }
                        items(lastWeek, key = { it.id }) { item ->
                            SessionCard(item, BrandTeal, darkText, lightText) { onSessionClick(item.id) }
                        }
                    }
                    if (earlier.isNotEmpty()) {
                        item { SectionHeader("Earlier", darkText) }
                        items(earlier, key = { it.id }) { item ->
                            SessionCard(item, BrandTeal, darkText, lightText) { onSessionClick(item.id) }
                        }
                    }
                }

                if (rows.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp))
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Load More History",
                                    color = BrandTeal,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        selectedDateMillis = null
                        showDatePicker = false
                    }) {
                        Text("Clear")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String, color: Color) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Composable
private fun SessionFilterChip(
    text: String,
    selected: Boolean,
    icon: ImageVector?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) BrandTeal else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(16.dp),
                tint = if (selected) Color.White else Color(0xFF4A4A4A)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Color.White else Color(0xFF4A4A4A)
        )
    }
}

@Composable
private fun SessionCard(
    item: SessionRow,
    teal: Color,
    darkText: Color,
    lightText: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(70.dp)
                    .background(item.accentColor)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEAEAEA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = teal,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = darkText
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (item.status == "Completed") Color(0xFFDCEFED)
                                else Color(0xFFEAEAEA)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.status,
                            fontSize = 12.sp,
                            color = if (item.status == "Completed") teal else lightText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = lightText
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item.durationLabel, fontSize = 13.sp, color = lightText)

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = lightText
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item.date, fontSize = 13.sp, color = lightText)
                }
            }
        }
    }
}
