package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SessionItem(
    val title: String,
    val duration: String,
    val date: String,
    val status: String,
    val accentColor: Color,
    val icon: ImageVector
)

@Composable
fun SessionsScreen() {
    val bgColor = Color(0xFFF5F5F5)
    val headerColor = Color(0xFFE8F1EC)
    val teal = Color(0xFF0D7377)
    val darkText = Color(0xFF222222)
    val lightText = Color(0xFF777777)
    val searchBg = Color(0xFFF0F0F0)

    val thisWeek = remember {
        listOf(
            SessionItem(
                "Seated Torso Twist",
                "12 mins",
                "Oct 24, 2023",
                "Completed",
                Color(0xFF0D7377),
                Icons.Outlined.Spa
            ),
            SessionItem(
                "Neck Rotation",
                "5 mins",
                "Oct 22, 2023",
                "Completed",
                Color(0xFF315C7C),
                Icons.Outlined.Person
            )
        )
    }

    val lastWeek = remember {
        listOf(
            SessionItem(
                "Arm Raises",
                "8 mins",
                "Oct 18, 2023",
                "Partial",
                Color.LightGray,
                Icons.Outlined.FitnessCenter
            ),
            SessionItem(
                "Guided Breathing",
                "10 mins",
                "Oct 15, 2023",
                "Completed",
                Color(0xFF0D7377),
                Icons.Outlined.Spa
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor)
                    .padding(top = 42.dp, bottom = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sessions History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF184D4D)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                item {
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = {
                            Text("Search sessions...", color = lightText)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = lightText
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = searchBg,
                            unfocusedContainerColor = searchBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip("Type", true, Icons.Outlined.FilterList)
                        FilterChip("Date", false, null)
                        FilterChip("Duration", false, null)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "This Week",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )
                }

                items(thisWeek) { item ->
                    SessionCard(item, teal, darkText, lightText)
                }

                item {
                    Text(
                        text = "Last Week",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )
                }

                items(lastWeek) { item ->
                    SessionCard(item, teal, darkText, lightText)
                }

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
                                color = teal,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            BottomBar(teal)
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    icon: ImageVector?
) {
    val bg = if (selected) Color(0xFFDCEFED) else Color(0xFFF1F1F1)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun SessionCard(
    item: SessionItem,
    teal: Color,
    darkText: Color,
    lightText: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    Text(item.duration, fontSize = 13.sp, color = lightText)

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

@Composable
fun BottomBar(teal: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem("Home", Icons.Outlined.Home, teal, false)
        BottomBarItem("Exercises", Icons.Outlined.FitnessCenter, teal, false)
        BottomBarItem("Sessions", Icons.Outlined.AccessTime, teal, true)
        BottomBarItem("Progress", Icons.Outlined.ShowChart, teal, false)
        BottomBarItem("Profile", Icons.Outlined.Person, teal, false)
    }
}

@Composable
fun BottomBarItem(
    label: String,
    icon: ImageVector,
    color: Color,
    selected: Boolean
) {
    if (selected) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(color)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = color
            )
        }
    }
}