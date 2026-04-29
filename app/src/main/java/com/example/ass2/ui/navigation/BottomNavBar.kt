package com.example.ass2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ass2.ui.theme.BrandTeal

enum class NavTab { HOME, EXERCISES, SESSIONS, PROGRESS, PROFILE }

private data class NavItem(val tab: NavTab, val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem(NavTab.HOME, "Home", Icons.Default.Home),
    NavItem(NavTab.EXERCISES, "Exercises", Icons.Default.FitnessCenter),
    NavItem(NavTab.SESSIONS, "Sessions", Icons.Default.Timer),
    NavItem(NavTab.PROGRESS, "Progress", Icons.Default.ShowChart),
    NavItem(NavTab.PROFILE, "Profile", Icons.Default.Person),
)

@Composable
fun BottomNavBar(selected: NavTab, onTabSelected: (NavTab) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        navItems.forEach { item ->
            val isSelected = item.tab == selected
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandTeal,
                    selectedTextColor = BrandTeal,
                    indicatorColor = BrandTeal.copy(alpha = 0.12f),
                    unselectedIconColor = Color(0xFF9E9E9E),
                    unselectedTextColor = Color(0xFF9E9E9E)
                )
            )
        }
    }
}
