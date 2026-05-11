package com.example.ass2.ui.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.data.local.Reminder
import com.example.ass2.viewmodel.ReminderViewModel

private val REMINDER_TYPES = listOf("Exercise Routine", "Meditation", "Water Break", "Stretch Break", "Custom")

private val DAY_KEYS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val DAY_LABELS = listOf("M", "T", "W", "T", "F", "S", "S")

// Converts selected day strings (e.g. "Mon", "Wed") into a bitmask — each day is a bit position
private fun encodeMask(set: Set<String>): Int {
    var mask = 0
    DAY_KEYS.forEachIndexed { i, key -> if (set.contains(key)) mask = mask or (1 shl i) }
    return mask
}

private fun decodeMask(mask: Int): Set<String> =
    DAY_KEYS.filterIndexed { i, _ -> mask and (1 shl i) != 0 }.toSet()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingScreen(
    reminderId: Long? = null,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    vm: ReminderViewModel = viewModel()
) {
    val bgColor = Color(0xFFF4F5F4)
    val teal = Color(0xFF0D7377)
    val darkText = Color(0xFF1F1F1F)
    val grayText = Color(0xFF5F6368)
    val cardColor = Color(0xFFF0F2F1)
    val whiteCard = Color(0xFFFCFCFC)

    // collectAsStateWithLifecycle pauses collection when screen is in background, saving battery vs collectAsState
    val existing by (reminderId?.let { vm.byId(it) } ?: kotlinx.coroutines.flow.flowOf<Reminder?>(null))
        .collectAsStateWithLifecycle(initialValue = null)

    var reminderTypeExpanded by remember { mutableStateOf(false) }
    var selectedReminderType by remember { mutableStateOf(REMINDER_TYPES[0]) }
    val timePickerState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = false)
    var showTimePicker by remember { mutableStateOf(false) }
    val selectedDays = remember { mutableStateOf(setOf("Mon", "Tue", "Wed", "Thu", "Fri")) }
    val reminderEnabled = remember { mutableStateOf(true) }
    var prefilled by remember { mutableStateOf(false) }

    // Pre-fill form with existing reminder data once loaded — prefilled flag prevents overwriting user edits
    LaunchedEffect(existing) {
        val e = existing
        if (e != null && !prefilled) {
            selectedReminderType = if (REMINDER_TYPES.contains(e.label)) e.label else "Custom"
            timePickerState.hour = e.hour
            timePickerState.minute = e.minute
            selectedDays.value = decodeMask(e.repeatDaysMask)
            reminderEnabled.value = e.enabled
            prefilled = true
        }
    }

    val isEditing = reminderId != null

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = whiteCard)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Time",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = darkText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = cardColor,
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = darkText,
                            selectorColor = teal,
                            timeSelectorSelectedContainerColor = teal,
                            timeSelectorUnselectedContainerColor = cardColor,
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorUnselectedContentColor = darkText,
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("Cancel", color = grayText)
                        }
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("OK", color = teal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
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
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (isEditing) "Edit Reminder" else "Set Reminder",
                    color = teal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                if (isEditing) {
                    IconButton(onClick = {
                        existing?.let {
                            vm.delete(it)
                            onDelete()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = teal
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "Configure",
                    color = teal,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Set up a gentle nudge to stay on track.",
                    color = grayText,
                    fontSize = 16.sp,
                    lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(26.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = grayText
                            )
                            Text(
                                text = " Reminder Type",
                                color = teal,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ExposedDropdownMenuBox(
                            expanded = reminderTypeExpanded,
                            onExpandedChange = { reminderTypeExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedReminderType,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reminderTypeExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = whiteCard,
                                    unfocusedContainerColor = whiteCard,
                                    focusedIndicatorColor = teal,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = darkText,
                                    unfocusedTextColor = darkText,
                                    focusedTrailingIconColor = teal,
                                    unfocusedTrailingIconColor = grayText,
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = reminderTypeExpanded,
                                onDismissRequest = { reminderTypeExpanded = false }
                            ) {
                                REMINDER_TYPES.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            selectedReminderType = type
                                            reminderTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = grayText
                            )
                            Text(
                                text = " Time",
                                color = teal,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        val hour = timePickerState.hour
                        val minute = timePickerState.minute
                        val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                        val amPm = if (hour < 12) "AM" else "PM"
                        val timeText = "%02d:%02d %s".format(displayHour, minute, amPm)
                        OutlinedTextField(
                            value = timeText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Schedule,
                                    contentDescription = null,
                                    tint = teal
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = whiteCard,
                                unfocusedContainerColor = whiteCard,
                                focusedIndicatorColor = teal,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = darkText,
                                unfocusedTextColor = darkText,
                            ),
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }.also { source ->
                                androidx.compose.runtime.LaunchedEffect(source) {
                                    source.interactions.collect {
                                        if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                            showTimePicker = true
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                tint = grayText
                            )
                            Text(
                                text = " Repeat",
                                color = teal,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DAY_LABELS.forEachIndexed { index, label ->
                                val key = DAY_KEYS[index]
                                val checked = selectedDays.value.contains(key)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (checked) teal else grayText
                                    )
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = { isChecked ->
                                            selectedDays.value = if (isChecked)
                                                selectedDays.value + key
                                            else
                                                selectedDays.value - key
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = teal,
                                            uncheckedColor = grayText,
                                            checkmarkColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Reminder Status",
                                color = teal,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Turn off to pause this reminder",
                                color = grayText,
                                fontSize = 14.sp
                            )
                        }
                        Switch(
                            checked = reminderEnabled.value,
                            onCheckedChange = { reminderEnabled.value = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = teal,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.LightGray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(34.dp))

                // id=0 triggers insert in upsert; existing id triggers update
                Button(
                    onClick = {
                        val reminder = Reminder(
                            id = existing?.id ?: 0L,
                            label = selectedReminderType,
                            hour = timePickerState.hour,
                            minute = timePickerState.minute,
                            repeatDaysMask = encodeMask(selectedDays.value),
                            enabled = reminderEnabled.value
                        )
                        vm.upsert(reminder)
                        onSave()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = teal)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = " Save Reminder",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancel",
                        color = teal,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
