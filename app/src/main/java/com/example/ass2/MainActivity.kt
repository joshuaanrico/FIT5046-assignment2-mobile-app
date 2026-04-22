package com.example.ass2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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
            NavTab.EXERCISES -> "exercises"
            NavTab.SESSIONS -> "home"   // placeholder — no sessions screen yet
            NavTab.PROGRESS -> "home"   // placeholder
            NavTab.PROFILE -> "profile_setup"
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
                onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("profile_setup") }
            )
        }
        composable("home") {
            HomeScreen(
                onGoToExercises = { navController.navigate("exercises") },
                onTabSelected = ::handleTabSelected
            )
        }
        composable("exercises") {
            ExercisesScreen(
                onExerciseClick = { name ->
                    navController.navigate("exercise_detail/${name}")
                },
                onTabSelected = ::handleTabSelected
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
//                onStartSession = { navController.popBackStack() }
                // route change to pre-session check-in page (delete comment after PR).
                onStartSession = { navController.navigate("pre_session_check_in")}
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
                onClose = { navController.popBackStack()},
                onResumeToSession = { navController.popBackStack()},
                onCancel = { navController.popBackStack()}
            )
        }

    }
}
