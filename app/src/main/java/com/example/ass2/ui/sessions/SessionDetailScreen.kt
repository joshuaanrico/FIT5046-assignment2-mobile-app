package com.example.ass2.ui.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.viewmodel.SessionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SessionDetailScreen(
    sessionId: Long,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    vm: SessionViewModel = viewModel()
) {
    val bgColor = Color(0xFFF4F5F4)
    val teal = Color(0xFF0D7377)
    val lightTeal = Color(0xFFD6E9E7)
    val textDark = Color(0xFF1F1F1F)
    val textGray = Color(0xFF666666)
    val cardColor = Color(0xFFF7F7F7)
    val blueCircle = Color(0xFF496F93)

    // collectAsStateWithLifecycle pauses collection when screen is in background, saving battery vs collectAsState
    val session by vm.byId(sessionId).collectAsStateWithLifecycle(initialValue = null)

    var journalText by remember { mutableStateOf("") }
    var journalEditing by remember { mutableStateOf(false) }
    var journalLoaded by remember { mutableStateOf(false) }

    // Populate journal text once when session loads - journalLoaded flag prevents overwriting user edits on recompose
    LaunchedEffect(session?.id) {
        val s = session
        if (s != null && !journalLoaded) {
            journalText = s.notes.orEmpty()
            journalLoaded = true
        }
    }

    val title = session?.exerciseName ?: "Session"
    val dateLabel = session?.let {
        SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.ENGLISH).format(Date(it.startTime))
    } ?: "-"
    val durationLabel = session?.let {
        val total = it.durationSec
        "%d:%02d".format(total / 60, total % 60)
    } ?: "-"
    // poseQuality is stored on a 0-100 scale (see UpperBodyExercise.symmetryQuality / estimateQuality)
    val poseQ = session?.poseQuality ?: 0f
    val poseLabel = when {
        session?.poseQuality == null -> "Not recorded"
        poseQ >= 85f -> "Good Form - ${poseQ.toInt()}%"
        poseQ >= 60f -> "Fair Form - ${poseQ.toInt()}%"
        else -> "Needs Work - ${poseQ.toInt()}%"
    }
    val hrLabel = session?.avgHr?.let { "Avg $it BPM" } ?: "Not recorded"
    val fatigueLabel = session?.fatigueLevel?.takeIf { it.isNotBlank() } ?: "Not recorded"
    val discomfortLabel = session?.discomfort?.takeIf { it.isNotBlank() } ?: "Not recorded"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {

            TopDetailBar(teal, onBack) {
                session?.let {
                    vm.delete(it)
                    onDelete()
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = title,
                fontSize = 34.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                color = teal
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = textGray,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = " $dateLabel",
                    color = textGray,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoChip(
                    icon = Icons.Outlined.Timer,
                    text = durationLabel,
                    background = lightTeal,
                    textColor = textGray
                )
                InfoChip(
                    icon = Icons.Outlined.Accessibility,
                    text = "Seated",
                    background = lightTeal,
                    textColor = textGray
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleIcon(
                            bgColor = teal,
                            icon = Icons.Outlined.Psychology,
                            iconTint = Color.White
                        )

                        Spacer(modifier = Modifier.size(14.dp))

                        Column {
                            Text(
                                text = "Pose Quality",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = teal
                            )
                            Text(
                                text = poseLabel,
                                fontSize = 15.sp,
                                color = textGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    LinearProgressIndicator(
                        progress = { (poseQ / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        color = teal,
                        trackColor = Color(0xFFE8E8E8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIcon(
                        bgColor = blueCircle,
                        icon = Icons.Outlined.FavoriteBorder,
                        iconTint = Color.White
                    )

                    Spacer(modifier = Modifier.size(14.dp))

                    Column {
                        Text(
                            text = "Heart Rate",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF20486A)
                        )
                        Text(
                            text = hrLabel,
                            fontSize = 15.sp,
                            color = textGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircleIcon(
                            bgColor = teal,
                            icon = Icons.Outlined.Psychology,
                            iconTint = Color.White
                        )

                        Spacer(modifier = Modifier.size(14.dp))

                        Text(
                            text = "How You Felt",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = teal
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Fatigue Level",
                            fontSize = 15.sp,
                            color = textGray,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = fatigueLabel,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = textDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Discomfort",
                            fontSize = 15.sp,
                            color = textGray,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = discomfortLabel,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = textDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircleIcon(
                            bgColor = lightTeal,
                            icon = Icons.Outlined.MonitorHeart,
                            iconTint = textGray
                        )

                        Spacer(modifier = Modifier.size(14.dp))

                        Text(
                            text = "Journal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Toggle between view and edit mode - saves to DB when switching from edit -> view
                        IconButton(onClick = {
                            if (journalEditing) {
                                session?.let { vm.update(it.copy(notes = journalText)) }
                            }
                            journalEditing = !journalEditing
                        }) {
                            Icon(
                                imageVector = if (journalEditing) Icons.Outlined.Check else Icons.Outlined.Edit,
                                contentDescription = if (journalEditing) "Save" else "Edit",
                                tint = teal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = journalText,
                        onValueChange = { journalText = it },
                        readOnly = !journalEditing,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = { Text("Add notes about this session...", color = textGray) },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            disabledContainerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = teal,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = textDark,
                            unfocusedTextColor = textGray,
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 26.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TopDetailBar(teal: Color, onBack: () -> Unit, onDelete: () -> Unit) {
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
            text = "Session Detail",
            color = teal,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = teal
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    background: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = " $text",
            color = textColor,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun CircleIcon(
    bgColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}
