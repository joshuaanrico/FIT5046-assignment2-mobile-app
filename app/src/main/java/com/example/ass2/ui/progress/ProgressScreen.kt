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

// Targets scale with period length so progress bars stay meaningful per scope.
// 5 sessions/wk = roughly daily activity; 20/month = same cadence with 4x window.
private fun sessionTarget(period: Period) = when (period) {
    Period.THIS_WEEK, Period.LAST_WEEK -> 5
    Period.THIS_MONTH, Period.LAST_MONTH -> 20
}

// 100 min/wk ≈ 20 min/session × 5 sessions; 400/month keeps the same ratio.
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

// ISO-style week: Monday start. Override Calendar default (locale-dependent, often Sunday)
// so weekly buckets are consistent across devices/regions.
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

// Long.MAX_VALUE for the current period's untilMs means "include anything up to now and beyond" —
// avoids racing against clock drift if a session is logged a few ms after we compute the window.
// Past periods (LAST_WEEK/LAST_MONTH) get a bounded end since they're closed intervals.
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

// Bounded window N periods before the selected period (offset 0 == current, 1 == previous, ...).
// Used to seed the 4-period TrendSparkline; unlike periodWindow() this never returns Long.MAX_VALUE.
private fun shiftedWindow(period: Period, offset: Int): PeriodWindow {
    val now = System.currentTimeMillis()
    return when (period) {
        Period.THIS_WEEK -> PeriodWindow(startOfWeek(now, -offset), startOfWeek(now, -offset + 1))
        Period.LAST_WEEK -> PeriodWindow(startOfWeek(now, -1 - offset), startOfWeek(now, -offset))
        Period.THIS_MONTH -> PeriodWindow(startOfMonth(now, -offset), startOfMonth(now, -offset + 1))
        Period.LAST_MONTH -> PeriodWindow(startOfMonth(now, -1 - offset), startOfMonth(now, -offset))
    }
}

private fun isWeekly(period: Period): Boolean =
    period == Period.THIS_WEEK || period == Period.LAST_WEEK

// Shared model for one bar in any BarChart. showLabel=false means the bar still renders but its
// x-axis tick stays blank (used for monthly bins where we only label every 5th day).
private data class Bar(val label: String, val value: Int, val showLabel: Boolean = true)

// Weekly binning: 7 bars (Mon..Sun), value = session count that day-of-week.
// Explicit order list (not DayOfWeek.values()) because java.time enum starts at MONDAY which is
// what we want, but the single-char labels (M T W T F S S) must match the visual axis exactly.
private fun countsByDayOfWeek(sessions: List<Session>): List<Bar> {
    val zone = ZoneId.systemDefault()
    val counts = HashMap<DayOfWeek, Int>()
    for (s in sessions) {
        // Convert epoch millis -> local-zone day-of-week so users in any timezone see "their" days.
        val dow = Instant.ofEpochMilli(s.startTime).atZone(zone).dayOfWeek
        counts[dow] = (counts[dow] ?: 0) + 1
    }
    val order = listOf(
        DayOfWeek.MONDAY to "M",
        DayOfWeek.TUESDAY to "T",
        DayOfWeek.WEDNESDAY to "W",
        DayOfWeek.THURSDAY to "T",
        DayOfWeek.FRIDAY to "F",
        DayOfWeek.SATURDAY to "S",
        DayOfWeek.SUNDAY to "S"
    )
    return order.map { (dow, label) -> Bar(label, counts[dow] ?: 0) }
}

// Weekly binning: same 7 buckets, value = minutes summed per day-of-week.
private fun minutesByDayOfWeek(sessions: List<Session>): List<Bar> {
    val zone = ZoneId.systemDefault()
    val mins = HashMap<DayOfWeek, Int>()
    for (s in sessions) {
        val dow = Instant.ofEpochMilli(s.startTime).atZone(zone).dayOfWeek
        mins[dow] = (mins[dow] ?: 0) + (s.durationSec / 60)
    }
    val order = listOf(
        DayOfWeek.MONDAY to "M",
        DayOfWeek.TUESDAY to "T",
        DayOfWeek.WEDNESDAY to "W",
        DayOfWeek.THURSDAY to "T",
        DayOfWeek.FRIDAY to "F",
        DayOfWeek.SATURDAY to "S",
        DayOfWeek.SUNDAY to "S"
    )
    return order.map { (dow, label) -> Bar(label, mins[dow] ?: 0) }
}

// Monthly binning: 1 bar per day in [start, end). Label every 5th day to keep axis readable —
// 30 numeric labels under a phone-width chart would collide.
private fun countsByDayOfMonth(sessions: List<Session>, start: Long, end: Long): List<Bar> {
    val zone = ZoneId.systemDefault()
    // getActualMaximum reads the real day count for that specific month (28/29/30/31)
    // instead of the generic getMaximum() which always returns 31.
    val startCal = Calendar.getInstance().apply { timeInMillis = start }
    val daysInMonth = startCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val counts = IntArray(daysInMonth)
    for (s in sessions) {
        // Defensive bounds check: callers pass the unfiltered "since" Flow result which can
        // contain sessions older than start or newer than end (sparkline shares its source).
        if (s.startTime < start || s.startTime >= end) continue
        val d = Instant.ofEpochMilli(s.startTime).atZone(zone).dayOfMonth
        val idx = d - 1
        if (idx in counts.indices) counts[idx]++
    }
    return (0 until daysInMonth).map { i ->
        // Show "1" and every 5th day after that: 1, 5, 10, 15, 20, 25, 30.
        val showLabel = (i == 0 || (i + 1) % 5 == 0)
        Bar(label = if (showLabel) (i + 1).toString() else "", value = counts[i], showLabel = showLabel)
    }
}

private fun minutesByDayOfMonth(sessions: List<Session>, start: Long, end: Long): List<Bar> {
    val zone = ZoneId.systemDefault()
    val startCal = Calendar.getInstance().apply { timeInMillis = start }
    val daysInMonth = startCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val mins = IntArray(daysInMonth)
    for (s in sessions) {
        if (s.startTime < start || s.startTime >= end) continue
        val d = Instant.ofEpochMilli(s.startTime).atZone(zone).dayOfMonth
        val idx = d - 1
        if (idx in mins.indices) mins[idx] += s.durationSec / 60
    }
    return (0 until daysInMonth).map { i ->
        val showLabel = (i == 0 || (i + 1) % 5 == 0)
        Bar(label = if (showLabel) (i + 1).toString() else "", value = mins[i], showLabel = showLabel)
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
    // Trailing 4 same-cadence windows (oldest -> newest, current is index 3) for the TrendSparkline.
    val sparkWindows = remember(selectedPeriod) {
        (3 downTo 0).map { shiftedWindow(selectedPeriod, it) }
    }

    // collectAsStateWithLifecycle pauses collection when screen is in background, saving battery vs collectAsState
    val sessionsSince by vm.since(window.sinceMs).collectAsStateWithLifecycle(initialValue = emptyList())
    val sessionsPrev by vm.since(prevWindow.sinceMs).collectAsStateWithLifecycle(initialValue = emptyList())
    // Single subscription covering all 4 sparkline windows; bin in-memory below.
    val sessionsForSparkline by vm.since(sparkWindows.first().sinceMs)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // Trim the DB results to the exact window boundary (since query only filters start, not end)
    val periodSessions = remember(sessionsSince, window) {
        sessionsSince.filter { it.startTime < window.untilMs }
    }
    val prevPeriodSessions = remember(sessionsPrev, prevWindow) {
        sessionsPrev.filter { it.startTime < prevWindow.untilMs }
    }
    // Bucket the single sparkline Flow into per-window counts in-memory; 4 DB subscriptions
    // would also work but one subscription is cheaper and keeps invalidation atomic.
    val sparkCounts = remember(sessionsForSparkline, sparkWindows) {
        sparkWindows.map { w -> sessionsForSparkline.count { it.startTime >= w.sinceMs && it.startTime < w.untilMs } }
    }

    val sessionCount = periodSessions.size
    val prevCount = prevPeriodSessions.size
    val totalDurationMin = periodSessions.sumOf { it.durationSec } / 60
    val sessTarget = sessionTarget(selectedPeriod)
    val minTarget = minuteTarget(selectedPeriod)

    // Compare current vs previous period session count to determine activity trend
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
                        windowEnd = window.untilMs,
                        period = selectedPeriod,
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = totalDurationMin.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkText
                        )
                        Text(
                            text = " min  /  $minTarget min target",
                            fontSize = 14.sp,
                            color = grayText,
                            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    DurationChart(
                        sessions = periodSessions,
                        windowStart = window.sinceMs,
                        windowEnd = window.untilMs,
                        period = selectedPeriod,
                        barColor = Color(0xFF5B7574),
                        emptyColor = softGray,
                        labelColor = grayText
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Minutes per day during ${selectedPeriod.label.lowercase()}. Target: $minTarget min total.",
                        color = grayText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            RecoveryTrendCard(
                trend = trend,
                sessionCount = sessionCount,
                prevCount = prevCount,
                selectedPeriod = selectedPeriod,
                sparkCounts = sparkCounts,
                sparkColor = teal,
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
    sparkCounts: List<Int>,
    sparkColor: Color,
    darkText: Color,
    grayText: Color
) {
    val prevLabel = when (selectedPeriod) {
        Period.THIS_WEEK -> "last week"
        Period.LAST_WEEK -> "the week prior"
        Period.THIS_MONTH -> "last month"
        Period.LAST_MONTH -> "the month prior"
    }

    val sparkAxis = when (selectedPeriod) {
        Period.THIS_WEEK, Period.LAST_WEEK -> "last 4 weeks"
        Period.THIS_MONTH, Period.LAST_MONTH -> "last 4 months"
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Spacer(modifier = Modifier.width(12.dp))

                // Skip the sparkline when there's nothing to compare (all zeros or single non-zero point).
                if (sparkCounts.any { it > 0 } && sparkCounts.distinct().size > 1) {
                    TrendSparkline(
                        counts = sparkCounts,
                        lineColor = sparkColor,
                        axisLabel = sparkAxis,
                        labelColor = grayText
                    )
                }
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

// Period-aware Canvas bar chart.
//  - Weekly periods: 7 wide bars (Mon..Sun), value = session count per day-of-week.
//  - Monthly periods: 28-31 thin bars (day 1..N), value = session count per day-of-month.
// Bin choice lives in the helpers above so DurationChart can mirror the same shape.
@Composable
private fun FrequencyChart(
    sessions: List<Session>,
    windowStart: Long,
    windowEnd: Long,
    period: Period,
    barColor: Color,
    emptyColor: Color,
    labelColor: Color
) {
    val bars = remember(sessions, period, windowStart, windowEnd) {
        if (isWeekly(period)) countsByDayOfWeek(sessions)
        else countsByDayOfMonth(sessions, windowStart, windowEnd)
    }
    BarChart(
        bars = bars,
        chartHeight = 170.dp,
        barColor = barColor,
        emptyColor = emptyColor,
        labelColor = labelColor,
        valueLabelColor = barColor,
        description = "Frequency chart: ${sessions.size} sessions across ${bars.size} bars",
        showValueLabels = isWeekly(period) // monthly chart would overcrowd the value row
    )
}

// Same Canvas pattern as FrequencyChart, but y-axis = minutes summed per bucket.
@Composable
private fun DurationChart(
    sessions: List<Session>,
    windowStart: Long,
    windowEnd: Long,
    period: Period,
    barColor: Color,
    emptyColor: Color,
    labelColor: Color
) {
    val bars = remember(sessions, period, windowStart, windowEnd) {
        if (isWeekly(period)) minutesByDayOfWeek(sessions)
        else minutesByDayOfMonth(sessions, windowStart, windowEnd)
    }
    BarChart(
        bars = bars,
        chartHeight = 140.dp,
        barColor = barColor,
        emptyColor = emptyColor,
        labelColor = labelColor,
        valueLabelColor = barColor,
        description = "Duration chart: ${bars.sumOf { it.value }} minutes across ${bars.size} bars",
        showValueLabels = isWeekly(period)
    )
}

// Shared Canvas bar renderer. Bar width adapts so 7 or 31 bars both fit the row width.
@Composable
private fun BarChart(
    bars: List<Bar>,
    chartHeight: androidx.compose.ui.unit.Dp,
    barColor: Color,
    emptyColor: Color,
    labelColor: Color,
    valueLabelColor: Color,
    description: String,
    showValueLabels: Boolean
) {
    val maxValue = (bars.maxOfOrNull { it.value } ?: 0).coerceAtLeast(1)
    val density = LocalDensity.current
    val cornerRadiusPx = with(density) { 6.dp.toPx() }
    val gapHeightPx = with(density) { 4.dp.toPx() }

    Column {
        if (showValueLabels) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                bars.forEach { bar ->
                    Text(
                        text = if (bar.value > 0) bar.value.toString() else "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = valueLabelColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .semantics { contentDescription = description }
        ) {
            val w = size.width
            val h = size.height
            val slot = w / bars.size
            // 70% of the slot leaves a visible gap between bars; cap at 28dp so 7 weekly bars
            // don't look chunky on tablets while 31 monthly bars still get >70% of slot width.
            val barWidthPx = (slot * 0.7f).coerceAtMost(with(density) { 28.dp.toPx() })
            bars.forEachIndexed { index, bar ->
                val ratio = bar.value.toFloat() / maxValue
                // Reserve gapHeightPx at the top so the tallest bar doesn't touch the chart edge.
                val barHeight = (h - gapHeightPx) * ratio.coerceIn(0f, 1f)
                val cx = slot * index + slot / 2f
                val left = cx - barWidthPx / 2f
                val top = h - barHeight
                val color = if (bar.value > 0) barColor else emptyColor
                if (bar.value == 0) {
                    // Empty bins still render a 10px stub so the x-axis stays visually populated
                    // (a fully invisible bar would make 0-session days look like missing data).
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(left, h - 10f),
                        size = Size(barWidthPx, 10f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                } else {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(left, top),
                        size = Size(barWidthPx, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                    // 1px alpha-0.3 stroke adds a darker edge for accessibility — helps users with
                    // low contrast vision distinguish adjacent bars in the dense monthly view.
                    drawRoundRect(
                        color = color.copy(alpha = 0.3f),
                        topLeft = Offset(left, top),
                        size = Size(barWidthPx, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
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
            bars.forEach { bar ->
                Text(
                    text = bar.label,
                    fontSize = 11.sp,
                    color = labelColor
                )
            }
        }
    }
}

// Mini Canvas line chart of last 4 same-cadence period totals. Oldest -> newest.
@Composable
private fun TrendSparkline(
    counts: List<Int>,
    lineColor: Color,
    axisLabel: String,
    labelColor: Color
) {
    val maxValue = (counts.maxOrNull() ?: 0).coerceAtLeast(1)
    val density = LocalDensity.current
    val strokePx = with(density) { 2.dp.toPx() }
    val dotPx = with(density) { 3.dp.toPx() }

    Column {
        Canvas(
            modifier = Modifier
                .width(96.dp)
                .height(28.dp)
                .semantics {
                    contentDescription = "Trend sparkline: ${counts.joinToString(", ")} sessions over $axisLabel"
                }
        ) {
            val w = size.width
            val h = size.height
            // Polyline needs >=2 points; gate here so a 1-point sparkline never tries to drawLine.
            if (counts.size < 2) return@Canvas
            val step = w / (counts.size - 1)
            val points = counts.mapIndexed { i, v ->
                val x = step * i
                // Inset top/bottom by dotPx so the dots at max/min values aren't clipped at the edge.
                val y = h - (h - dotPx * 2) * (v.toFloat() / maxValue).coerceIn(0f, 1f) - dotPx
                Offset(x, y)
            }
            // polyline connecting all 4 period totals
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = lineColor,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = strokePx
                )
            }
            // dots on each period so users see individual data points, not just a line
            points.forEach { p ->
                drawCircle(color = lineColor, radius = dotPx, center = p)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = axisLabel, fontSize = 10.sp, color = labelColor)
    }
}
