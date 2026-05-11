package com.example.ass2.ui.reminders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.data.local.Reminder
import com.example.ass2.viewmodel.ReminderViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

private fun formatTime(hour: Int, minute: Int): String =
    LocalTime.of(hour, minute).format(timeFormatter)

@Composable
fun ReminderListScreen(
    onBack: () -> Unit,
    onReminderClick: (Long) -> Unit,
    onAdd: () -> Unit,
    vm: ReminderViewModel = viewModel()
) {
    val bgColor = Color(0xFFF4F5F4)
    val teal = Color(0xFF0D7377)
    val darkText = Color(0xFF202124)
    val grayText = Color(0xFF666666)
    val cardColor = Color(0xFFF8F8F8)

    // collectAsStateWithLifecycle pauses collection when screen is in background, saving battery vs collectAsState
    val reminders by vm.reminders.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = teal
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Reminders",
                            color = teal,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.size(48.dp))
                }

                // LazyColumn only renders visible items - efficient for large reminder lists
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 80.dp)
                ) {
                    item {
                        Text(
                            text = "Daily\nRoutine",
                            color = teal,
                            fontSize = 34.sp,
                            lineHeight = 38.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Manage your wellness check-ins and guided routines to stay consistent.",
                            color = grayText,
                            fontSize = 15.sp,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(26.dp))
                    }

                    if (reminders.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No reminders yet - tap + to add one.",
                                    color = grayText,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // key = id ensures Compose reuses existing card views on update instead of recreating them
                    items(reminders, key = { it.id }) { item ->
                        ReminderCard(
                            item = item,
                            teal = teal,
                            darkText = darkText,
                            grayText = grayText,
                            cardColor = cardColor,
                            onClick = { onReminderClick(item.id) },
                            onToggle = { enabled -> vm.toggleEnabled(item, enabled) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(teal)
                    .clickable { onAdd() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

// Single reminder row - dims to 45% opacity when disabled to signal inactive state
@Composable
private fun ReminderCard(
    item: Reminder,
    teal: Color,
    darkText: Color,
    grayText: Color,
    cardColor: Color,
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    val contentAlpha = if (item.enabled) 1f else 0.45f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(contentAlpha)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.label,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = Color(0xFF36546D),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " ${formatTime(item.hour, item.minute)}",
                        fontSize = 14.sp,
                        color = grayText
                    )
                }
            }

            Switch(
                checked = item.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = teal,
                    uncheckedThumbColor = Color(0xFF9E9E9E),
                    uncheckedTrackColor = Color(0xFFE2E2E2)
                )
            )
        }
    }
}
