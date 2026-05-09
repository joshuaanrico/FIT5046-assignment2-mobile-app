package com.example.ass2.ui.progress
import com.example.ass2.ui.navigation.BottomNavBar
import com.example.ass2.ui.navigation.NavTab

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.data.local.Session
import com.example.ass2.viewmodel.SessionViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

private fun sessionTarget(period: Period) = when (period) {
    Period.THIS_WEEK, Period.LAST_WEEK -> 5
    Period.THIS_MONTH, Period.LAST_MONTH -> 20
}

private fun minuteTarget(period: Period) = when (period) {
    Period.THIS_WEEK, Period.LAST_WEEK -> 100
    Period.THIS_MONTH, Period.LAST_MONTH -> 400
}

private enum class Period(val label: String) {
    THIS_WEEK("This Week"),
    LAST_WEEK("Last Week"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month")
}

private fun startOfWeek(now: Long = System.currentTimeMillis(), weekOffset: Int = 0): Long =
    Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        add(Calendar.WEEK_OF_YEAR, weekOffset)
    }.timeInMillis

private fun startOfMonth(now: Long = System.currentTimeMillis(), monthOffset: Int = 0): Long =
    Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.MONTH, monthOffset)
    }.timeInMillis

private enum class Trend { IMPROVING, STABLE, DECLINING }

private data class PeriodWindow(val sinceMs: Long, val untilMs: Long)

private fun periodWindow(period: Period): PeriodWindow {
    val now = System.currentTimeMillis()
    return when (period) {
        Period.THIS_WEEK -> PeriodWindow(startOfWeek(now, 0), Long.MAX_VALUE)
        Period.LAST_WEEK -> PeriodWindow(startOfWeek(now, -1), startOfWeek(now, 0))
        Period.THIS_MONTH -> PeriodWindow(startOfMonth(now, 0), Long.MAX_VALUE)
        Period.LAST_MONTH -> PeriodWindow(startOfMonth(now, -1), startOfMonth(now, 0))
    }
}

private fun previousWindow(period: Period): PeriodWindow {
    val now = System.currentTimeMillis()
    return when (period) {
        Period.THIS_WEEK -> PeriodWindow(startOfWeek(now, -1), startOfWeek(now, 0))
        Period.LAST_WEEK -> PeriodWindow(startOfWeek(now, -2), startOfWeek(now, -1))
        Period.THIS_MONTH -> PeriodWindow(startOfMonth(now, -1), startOfMonth(now, 0))
        Period.LAST_MONTH -> PeriodWindow(startOfMonth(now, -2), startOfMonth(now, -1))
    }
}

@Composable
fun ProgressScreen(
    onTabSelected: (NavTab) -> Unit,
    vm: SessionViewModel = viewModel()
) {
    val bgColor = Color(0xFFF4F5F4)
    val teal = Color(0xFF0D7377)
    val tealLight = Color(0xFFBFE9E8)
    val darkText = Color(0xFF1F1F1F)
    val grayText = Color(0xFF666666)
    val cardColor = Color(0xFFF8F8F8)
    val softGray = Color(0xFFE7E7E7)

    val scrollState = rememberScrollState()
    var selectedPeriod by remember { mutableStateOf(Period.THIS_WEEK) }
    var showPeriodMenu by remember { mutableStateOf(false) }

    val window = remember(selectedPeriod) { periodWindow(selectedPeriod) }
    val prevWindow = remember(selectedPeriod) { previousWindow(selectedPeriod) }

    val sessionsSince by vm.since(window.sinceMs).collectAsStateWithLifecycle(initialValue = emptyList())
    val sessionsPrev by vm.since(prevWindow.sinceMs).collectAsStateWithLifecycle(initialValue = emptyList())

    val periodSessions = remember(sessionsSince, window) {
        sessionsSince.filter { it.startTime < window.untilMs }
    }
    val prevPeriodSessions = remember(sessionsPrev, prevWindow) {
        sessionsPrev.filter { it.startTime < prevWindow.untilMs }
    }

    val sessionCount = periodSessions.size
    val prevCount = prevPeriodSessions.size
    val totalDurationMin = periodSessions.sumOf { it.durationSec } / 60
    val sessTarget = sessionTarget(selectedPeriod)
    val minTarget = minuteTarget(selectedPeriod)

    val trend = when {
        sessionCount > prevCount -> Trend.IMPROVING
        sessionCount < prevCount -> Trend.DECLINING
        else -> Trend.STABLE
    }

    Scaffold(
        bottomBar = { BottomNavBar(selected = NavTab.PROGRESS, onTabSelected = onTabSelected) },
        containerColor = bgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Progress",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = teal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF0F0F0))
                        .clickable { showPeriodMenu = true }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedPeriod.label,
                        fontSize = 16.sp,
                        color = darkText
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Dropdown",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showPeriodMenu,
                    onDismissRequest = { showPeriodMenu = false }
                ) {
                    Period.entries.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p.label) },
                            onClick = { selectedPeriod = p; showPeriodMenu = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = teal)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Consistency Goal",
                        color = Color(0xFFCDEFEF),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = sessionCount.toString(),
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " / $sessTarget Sessions",
                            color = Color(0xFFB9DDDB),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = consistencyMessage(sessionCount, sessTarget),
                        color = Color(0xFFB9DDDB),
                        fontSize = 15.sp,
                        lineHeight = 25.sp
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    LinearProgressIndicator(
                        progress = { (sessionCount.toFloat() / sessTarget).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = tealLight,
                        trackColor = Color(0xFF2B8E90)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Frequency",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    FrequencyChart(
                        sessions = periodSessions,
                        windowStart = window.sinceMs,
                        barColor = teal,
                        emptyColor = softGray,
                        labelColor = grayText
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Duration",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Box(
                        modifier = Modifier.size(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { (totalDurationMin.toFloat() / minTarget).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 12.dp,
                            color = Color(0xFF5B7574),
                            trackColor = Color(0xFFDDE3E2)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = totalDurationMin.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkText
                            )
                            Text(
                                text = "min",
                                fontSize = 16.sp,
                                color = grayText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Total time spent in active recovery during ${selectedPeriod.label.lowercase()}. Target: $minTarget min.",
                        color = grayText,
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            RecoveryTrendCard(
                trend = trend,
                sessionCount = sessionCount,
                prevCount = prevCount,
                selectedPeriod = selectedPeriod,
                darkText = darkText,
                grayText = grayText
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private data class TrendChip(
    val bg: Color,
    val textColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

@Composable
private fun RecoveryTrendCard(
    trend: Trend,
    sessionCount: Int,
    prevCount: Int,
    selectedPeriod: Period,
    darkText: Color,
    grayText: Color
) {
    val prevLabel = when (selectedPeriod) {
        Period.THIS_WEEK -> "last week"
        Period.LAST_WEEK -> "the week prior"
        Period.THIS_MONTH -> "last month"
        Period.LAST_MONTH -> "the month prior"
    }

    val chip = when (trend) {
        Trend.IMPROVING -> TrendChip(Color(0xFFD6ECE8), Color(0xFF3B7A6F), Icons.AutoMirrored.Outlined.TrendingUp, "Activity Increasing")
        Trend.DECLINING -> TrendChip(Color(0xFFFFE5E5), Color(0xFF9B3535), Icons.AutoMirrored.Outlined.TrendingDown, "Activity Decreasing")
        Trend.STABLE   -> TrendChip(Color(0xFFF0F0F0), Color(0xFF555555), Icons.Outlined.Remove, "Activity Stable")
    }

    val message = when (trend) {
        Trend.IMPROVING -> "$sessionCount session(s) this period vs $prevCount $prevLabel - you're building momentum!"
        Trend.DECLINING -> "$sessionCount session(s) this period vs $prevCount $prevLabel - try to squeeze a few more in."
        Trend.STABLE -> if (sessionCount == 0)
            "No sessions yet this period or $prevLabel. Start one to track your trend."
        else
            "$sessionCount session(s) - same as $prevLabel. Consistency is key!"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Activity Trend",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkText
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(chip.bg)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = chip.icon,
                    contentDescription = null,
                    tint = chip.textColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = " ${chip.label}",
                    color = chip.textColor,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = message,
                color = grayText,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
        }
    }
}

private fun consistencyMessage(count: Int, target: Int): String = when {
    count == 0 -> "No sessions logged yet. Start a session\nto begin tracking your rhythm."
    count >= target -> "Goal hit! You're maintaining a strong\nrhythm. Keep moving at your own pace."
    else -> "You're maintaining a steady rhythm.\n${target - count} more session(s) to hit your\ntarget. Keep moving at your own pace."
}

@Composable
private fun FrequencyChart(
    sessions: List<Session>,
    windowStart: Long,
    barColor: Color,
    emptyColor: Color,
    labelColor: Color
) {
    val zone = ZoneId.systemDefault()
    val countsByDow = remember(sessions) {
        val map = HashMap<DayOfWeek, Int>()
        for (s in sessions) {
            val dow = Instant.ofEpochMilli(s.startTime).atZone(zone).dayOfWeek
            map[dow] = (map[dow] ?: 0) + 1
        }
        map
    }
    val maxCount = (countsByDow.values.maxOrNull() ?: 0).coerceAtLeast(1)
    val days = listOf(
        DayOfWeek.MONDAY to "M",
        DayOfWeek.TUESDAY to "T",
        DayOfWeek.WEDNESDAY to "W",
        DayOfWeek.THURSDAY to "T",
        DayOfWeek.FRIDAY to "F",
        DayOfWeek.SATURDAY to "S",
        DayOfWeek.SUNDAY to "S"
    )
    val description = "Frequency chart: ${sessions.size} sessions across ${days.size} days"

    val density = LocalDensity.current
    val chartHeight = 170.dp
    val barWidth = 28.dp
    val cornerRadius = with(density) { 8.dp.toPx() }
    val barWidthPx = with(density) { barWidth.toPx() }
    val gapHeight = with(density) { 6.dp.toPx() }
    val maxBarColor = barColor

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { (dow, _) ->
                val count = countsByDow[dow] ?: 0
                Text(
                    text = if (count > 0) count.toString() else "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = barColor
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .semantics { contentDescription = description }
        ) {
            val w = size.width
            val h = size.height
            val slot = w / days.size
            days.forEachIndexed { index, (dow, _) ->
                val count = countsByDow[dow] ?: 0
                val ratio = count.toFloat() / maxCount
                val barHeight = (h - gapHeight) * ratio.coerceIn(0f, 1f)
                val cx = slot * index + slot / 2f
                val left = cx - barWidthPx / 2f
                val top = h - barHeight
                val color = if (count > 0) maxBarColor else emptyColor
                if (count == 0) {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(left, h - 12f),
                        size = Size(barWidthPx, 12f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
                    )
                } else {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(left, top),
                        size = Size(barWidthPx, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
                    )
                    // outline for accessibility
                    drawRoundRect(
                        color = color.copy(alpha = 0.3f),
                        topLeft = Offset(left, top),
                        size = Size(barWidthPx, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
                        style = Stroke(width = 1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { (_, label) ->
                Text(text = label, fontSize = 13.sp, color = labelColor)
            }
        }
    }
}
