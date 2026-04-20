package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandTeal

enum class NavTab { HOME, EXERCISES, SESSIONS, PROGRESS, PROFILE }

data class NavItem(val tab: NavTab, val label: String, val icon: ImageVector)

@Composable
fun BottomNavBar(selected: NavTab, onTabSelected: (NavTab) -> Unit) {
    val items = listOf(
        NavItem(NavTab.HOME, "Home", Icons.Default.Home),
        NavItem(NavTab.EXERCISES, "Exercises", Icons.Default.FitnessCenter),
        NavItem(NavTab.SESSIONS, "Sessions", Icons.Default.Timer),
        NavItem(NavTab.PROGRESS, "Progress", Icons.Default.ShowChart),
        NavItem(NavTab.PROFILE, "Profile", Icons.Default.Person),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = item.tab == selected
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(item.tab) }
                    .background(if (isSelected) BrandTeal.copy(alpha = 0.12f) else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (isSelected) BrandTeal else Color(0xFF9E9E9E),
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = item.label,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) BrandTeal else Color(0xFF9E9E9E)
                )
            }
        }
    }
}
