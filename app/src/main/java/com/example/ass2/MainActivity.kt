package com.example.ass2

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.navigation
import com.example.ass2.ui.theme.Ass2Theme
import com.example.ass2.ui.navigation.NavTab
import com.example.ass2.ui.auth.LoginScreen
import com.example.ass2.viewmodel.AuthViewModel
import com.example.ass2.ui.auth.RegisterScreen
import com.example.ass2.ui.auth.ForgotPasswordScreen
import com.example.ass2.ui.home.HomeScreen
import com.example.ass2.ui.exercises.ExercisesScreen
import com.example.ass2.ui.exercises.ExerciseDetailScreen
import com.example.ass2.ui.sessions.SessionsScreen
import com.example.ass2.ui.sessions.SessionDetailScreen
import com.example.ass2.ui.session.LiveSessionScreen
import com.example.ass2.ui.session.PreSessionCheckInScreen
import com.example.ass2.ui.session.AlertScreen
import com.example.ass2.ui.session.SessionResultScreen
import com.example.ass2.ui.progress.ProgressScreen
import com.example.ass2.ui.reminders.ReminderListScreen
import com.example.ass2.ui.reminders.ReminderSettingScreen
import com.example.ass2.ui.profile.ProfileScreen
import com.example.ass2.ui.profile.ProfileSetupScreen
import com.example.ass2.ui.profile.SettingsScreen
import com.example.ass2.ui.profile.HelpScreen
import com.example.ass2.ui.profile.LegalScreen
import com.example.ass2.viewmodel.LiveSessionViewModel
import com.example.ass2.viewmodel.PoseViewModel
import com.example.ass2.viewmodel.SessionViewModel

class MainActivity : ComponentActivity() {
    // Registers the system permission dialog launcher for POST_NOTIFICATIONS (Android 13+)
    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* result is best-effort */ }

    // Entry point: sets up edge-to-edge UI, requests notification permission, and mounts the nav graph
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestPostNotificationsIfNeeded()
        setContent {
            Ass2Theme(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph()
                }
            }
        }
    }

    // Skips on Android 12 and below - POST_NOTIFICATIONS didn't exist before Android 13
    private fun requestPostNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val startDestination = if (authViewModel.isUserLoggedIn()) "home" else "login"

    // Navigates to the selected bottom tab, preserving back stack state and avoiding duplicate destinations
    fun handleTabSelected(tab: NavTab) {
        val route = when (tab) {
            NavTab.HOME -> "home"
            NavTab.EXERCISES -> "exercises?filter=All"
            NavTab.SESSIONS -> "sessions"
            NavTab.PROGRESS -> "progress"
            NavTab.PROFILE -> "profile"
        }
        navController.navigate(route)
    }

    NavHost(navController = navController, startDestination = startDestination) {
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
                onBackToLogin = {
                    navController.navigate("login") {
                        popUpTo("forgot_password") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("profile_setup?fromRegister=true") },
                onNavigateToLegal = { tab -> navController.navigate("legal?tab=$tab") }
            )
        }
        composable("home") {
            HomeScreen(
                onGoToExerciseDetail = { exerciseName ->
                    navController.navigate("exercise_detail/$exerciseName") },
                onTabSelected = ::handleTabSelected
            )
        }
        // Exercises screen accepts an optional filter param (e.g. "Recommended" from Home, "All" from tab bar)
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
                onStartSession = { navController.navigate("pre_session_check_in/$name") }
            )
        }
        composable("sessions") {
            SessionsScreen(
                onTabSelected = ::handleTabSelected,
                onSessionClick = { id -> navController.navigate("sessionDetails/$id") }
            )
        }
        composable(
            route = "sessionDetails/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            SessionDetailScreen(
                sessionId = id,
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
                    authViewModel.signOut()
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
        // Reminder list screen - id=-1 signals new reminder, positive id opens existing one for editing
        composable("reminderList") {
            ReminderListScreen(
                onBack = { navController.popBackStack() },
                onReminderClick = { id -> navController.navigate("reminderSetting?id=$id") },
                onAdd = { navController.navigate("reminderSetting?id=-1") }
            )
        }
        composable(
            route = "reminderSetting?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            ReminderSettingScreen(
                reminderId = if (id < 0) null else id,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onDelete = { navController.popBackStack() }
            )
        }
        composable(
            route = "profile_setup?fromRegister={fromRegister}",
            arguments = listOf(navArgument("fromRegister") {
                type = NavType.BoolType
                defaultValue = false
            })
        ) { backStackEntry ->
            val fromRegister = backStackEntry.arguments?.getBoolean("fromRegister") ?: false
            ProfileSetupScreen(
                onBack = { navController.popBackStack() },
                onSaveAndContinue = {
                    navController.navigate("home") {
                        popUpTo("profile_setup") { inclusive = true }
                    }
                },
                showBackButton = !fromRegister
            )
        }
        // Session flow: check-in -> live session -> (optional pause alert) -> result
        composable(
            route = "pre_session_check_in/{exerciseName}",
            arguments = listOf(navArgument("exerciseName"){type = NavType.StringType})
        ) { backStackEntry ->
            val exerciseName = backStackEntry.arguments?.getString("exerciseName")?:"Bicep Curl"
            PreSessionCheckInScreen(
                onClose = { navController.popBackStack() },
                onResumeToSession = { energyLvl ->
                    navController.navigate("session_flow/$energyLvl/$exerciseName"){
                        popUpTo("pre_session_check_in/$exerciseName"){ inclusive=false }
                    } },
                onCancel = { navController.popBackStack() }
            )
        }

        navigation(
            route = "session_flow/{energyLvl}/{exerciseName}",
            startDestination = "live_session",
            arguments = listOf(
                navArgument("energyLvl"){type = NavType.IntType},
                navArgument("exerciseName"){type = NavType.StringType}
            )

        ) {
            composable("live_session") { backStackEntry ->
                val parentEntry: NavBackStackEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("session_flow/{energyLvl}/{exerciseName}")
                }
                val liveSessionViewModel: LiveSessionViewModel = viewModel(parentEntry)
                val poseViewModel: PoseViewModel = viewModel(parentEntry)
                val energyLvl = backStackEntry.arguments?.getInt("energyLvl") ?: 3
                val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: ""
                LiveSessionScreen(
                    energyLevel = energyLvl,
                    exerciseName = exerciseName,
                    liveSessionViewModel = liveSessionViewModel,
                    poseViewModel = poseViewModel,
                    onBack = { navController.popBackStack() },
                    onHelp = { navController.navigate("help") },
                    onPause = {
                        navController.navigate("pause_alert") {
                            // when pause_alert is already on top of the back stack not pushing another copy
                            launchSingleTop = true
                        }
                    },
                    onEndSession = { navController.navigate("session_result") }
                )
            }
            composable("pause_alert") { backStackEntry ->
                val parentEntry: NavBackStackEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("session_flow/{energyLvl}/{exerciseName}")
                }
                val liveSessionViewModel: LiveSessionViewModel = viewModel(parentEntry)
                val poseViewModel: PoseViewModel = viewModel(parentEntry)
                AlertScreen(
                    onRest = {
                        liveSessionViewModel.resumeSession()
                        navController.popBackStack()
                    },
                    onContinueLowerIntensity = {
                        liveSessionViewModel.sameExerciseWithLowIntensity()
                        poseViewModel.resetExercise()
                        navController.popBackStack()
                    },
                    onEndSession = {
                        val update = poseViewModel.exerciseUpdate.value
                        liveSessionViewModel.finalizeSession(
                            exerciseName = poseViewModel.currentExerciseName.value,
                            reps = update.repCount,
                            poseQuality = update.poseQuality
                        )
                        navController.navigate("session_result")

                    }
                )
            }


        }

        // Session result - all exit paths popUpTo home to prevent back-navigating into the session flow
        composable("session_result") { backStackEntry ->
            val parentEntry: NavBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry("session_flow/{energyLvl}/{exerciseName}")
            }
            val liveSessionViewModel: LiveSessionViewModel = viewModel(parentEntry)
            val sessionViewModel: SessionViewModel = viewModel()
            SessionResultScreen(
                liveSessionViewModel = liveSessionViewModel,
                sessionViewModel = sessionViewModel,
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
