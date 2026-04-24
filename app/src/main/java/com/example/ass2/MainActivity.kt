package com.example.ass2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ass2.ui.theme.Ass2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ass2Theme(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph()
                }
            }
        }
    }
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    fun handleTabSelected(tab: NavTab) {
        val route = when (tab) {
            NavTab.HOME -> "home"
            NavTab.EXERCISES -> "exercises?filter=All"
            NavTab.SESSIONS -> "sessions"
            NavTab.PROGRESS -> "progress"
            NavTab.PROFILE -> "profile"
        }
        navController.navigate(route) {
            popUpTo("home") { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("profile_setup") },
                onNavigateToLegal = { tab -> navController.navigate("legal?tab=$tab") }
            )
        }
        composable("home") {
            HomeScreen(
                onGoToExercises = { navController.navigate("exercises?filter=Recommended") },
                onTabSelected = ::handleTabSelected
            )
        }
        composable(
            route = "exercises?filter={filter}",
            arguments = listOf(navArgument("filter") { defaultValue = "All" })
        ) { backStackEntry ->
            val filter = backStackEntry.arguments?.getString("filter") ?: "All"
            ExercisesScreen(
                onExerciseClick = { name ->
                    navController.navigate("exercise_detail/${name}")
                },
                onTabSelected = ::handleTabSelected,
                initialFilter = filter
            )
        }
        composable(
            route = "exercise_detail/{exerciseName}",
            arguments = listOf(navArgument("exerciseName") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("exerciseName") ?: "Exercise"
            ExerciseDetailScreen(
                exerciseName = name,
                onBack = { navController.popBackStack() },
                onStartSession = { navController.navigate("pre_session_check_in") }
            )
        }
        composable("sessions") {
            SessionsScreen(
                onTabSelected = ::handleTabSelected,
                onSessionClick = { navController.navigate("sessionDetails") }
            )
        }
        composable("sessionDetails") {
            SessionDetailScreen(
                onBack = { navController.popBackStack() },
                onDelete = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                onTabSelected = ::handleTabSelected,
                onReminderListClick = { navController.navigate("reminderList") },
                onProfileSetupClick = { navController.navigate("profile_setup") },
                onSettingsClick = { navController.navigate("settings") },
                onHelpClick = { navController.navigate("help") },
                onSignOut = {
                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                }
            )
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable("help") {
            HelpScreen(onBack = { navController.popBackStack() })
        }
        composable("progress") {
            ProgressScreen(onTabSelected = ::handleTabSelected)
        }
        composable("reminderList") {
            ReminderListScreen(
                onBack = { navController.popBackStack() },
                onReminderClick = { navController.navigate("reminderSetting") },
                onAdd = { navController.navigate("reminderSetting") }
            )
        }
        composable("reminderSetting") {
            ReminderSettingScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onDelete = { navController.popBackStack() }
            )
        }
        composable("profile_setup") {
            ProfileSetupScreen(
                onBack = { navController.popBackStack() },
                onSaveAndContinue = {
                    navController.navigate("home") {
                        popUpTo("profile_setup") { inclusive = true }
                    }
                }
            )
        }
        composable("pre_session_check_in") {
            PreSessionCheckInScreen(
                onClose = { navController.popBackStack() },
                onResumeToSession = { navController.navigate("live_session") },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("live_session") {
            LiveSessionScreen(
                onBack = { navController.popBackStack() },
                onHelp = { navController.navigate("help") },
                onPause = { navController.navigate("pause_alert") },
                onEndSession = { navController.navigate("session_result") }
            )
        }
        composable("pause_alert") {
            AlertScreen(
                onRest = { navController.popBackStack() },
                onContinueLowerIntensity = { navController.popBackStack() },
                onEndSession = { navController.navigate("session_result") }
            )
        }
        composable("session_result") {
            SessionResultScreen(
                onCancel = { navController.navigate("home") { popUpTo("home") { inclusive = false } } },
                onSaveSession = { navController.navigate("sessions") { popUpTo("home") { inclusive = false } } },
                onRepeatLater = { navController.navigate("exercises") { popUpTo("home") { inclusive = false } } },
                onHome = { navController.navigate("home") { popUpTo("home") { inclusive = false } } }
            )
        }
        composable(
            route = "legal?tab={tab}",
            arguments = listOf(navArgument("tab") { defaultValue = "terms" })
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab") ?: "terms"
            LegalScreen(
                initialTab = tab,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
