package com.example.ass2.ui.session

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2.pose.PoseOverlayView
import com.example.ass2.ui.theme.BrandTeal
import com.example.ass2.ui.theme.BrandTealDark
import com.example.ass2.ui.theme.FieldBackground
import com.example.ass2.viewmodel.LiveSessionViewModel
import com.example.ass2.viewmodel.PoseViewModel
import kotlinx.coroutines.delay


@Composable
fun LiveSessionScreen(
    energyLevel: Int = 3,
    exerciseName: String = "Bicep Curl",
    liveSessionViewModel : LiveSessionViewModel,
    poseViewModel: PoseViewModel,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onPause: () -> Unit,
    onEndSession: () -> Unit,
) {

    var targetReps by remember{
        mutableIntStateOf(
            when(energyLevel)
            {
                1 -> 5
                2 -> 8
                3 -> 12
                4 -> 15
                5 -> 20
                else -> 12
            }
        )
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe ViewModel state
    val update by poseViewModel.exerciseUpdate.collectAsStateWithLifecycle()
    val selectedExerciseName by poseViewModel.currentExerciseName.collectAsStateWithLifecycle()
    // Tracks whether we've already selected the exercise. Stays true across the pause/resume
    // round trip so selectExercise isn't re-called (which would reset the rep count).
    var exerciseSelected by rememberSaveable { mutableStateOf(false) }

    val heartRate by liveSessionViewModel.heartRate.collectAsStateWithLifecycle()

    // Camera permission
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    if (!hasCameraPermission) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val isGoodForm = update.poseQuality >= 70f && update.feedback.isNotEmpty()


// -------------------------------------------------------------------------------------------------
    // the number of seconds that has gone by since the screen opened.
    var secondsPassed by remember { mutableIntStateOf(0) }
    // to check if the lower intensity version of exercise is to be executed
    val lowerIntensitySelected by liveSessionViewModel.selectedLow.collectAsStateWithLifecycle()

    // to be run once when the screen opens
    LaunchedEffect("session")
    {
        // starting the HR simulator and the timer at the same time.
        liveSessionViewModel.sessionStart()
        while (true){
            delay(1000L)
            secondsPassed = liveSessionViewModel.getDurationOfSession()
        }
    }

    // watches the rep count and ends session once the taget is met.
    LaunchedEffect(update.repCount)
    {
       if (update.repCount >= targetReps)
       {
           poseViewModel.lockExercise()
       }
    }

    // collect the one shot event.
    LaunchedEffect("hrAlert")
    {
        liveSessionViewModel.hRAlertEvent.collect {
            onPause()
        }
    }

    // makes sure the correct exercise is being selected.
    LaunchedEffect(exerciseName)
    {
        if (!exerciseSelected){
            val exercise = poseViewModel.availableExercises.firstOrNull{
                it.displayName == exerciseName
            }
            if (exercise != null)
            {
                poseViewModel.selectExercise(exercise)
                exerciseSelected = true
            }
        }
    }

    LaunchedEffect(lowerIntensitySelected)
    {
        if (lowerIntensitySelected)
        {
            targetReps = when {
                targetReps > 15 -> 15
                targetReps > 12 -> 12
                targetReps > 8 -> 8
                targetReps > 5 -> 5
                else -> 5
            }
            liveSessionViewModel.acknowledgeTheLowerIntensity()
        }
    }

    /**
     * Keeping the live session view model aware of the target rep count.
     */
    LaunchedEffect(targetReps) {
        liveSessionViewModel.setTargetReps(targetReps)
    }
// -------------------------------------------------------------------------------------------------


    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.statusBarsPadding())

        // Top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FieldBackground)
                .padding(horizontal = 18.dp, vertical = 18.dp),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack, "Back",
                tint = BrandTealDark,
                modifier = Modifier.size(28.dp).align(Alignment.CenterStart).clickable { onBack() },
            )
            Text("Live Session", color = BrandTealDark, fontSize = 20.sp, fontWeight = Bold,
                modifier = Modifier.align(Alignment.Center))
            Text("Help", color = BrandTealDark, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterEnd).clickable { onHelp() })
        }


        // Camera + overlay + UI overlays
        Box(modifier = Modifier.fillMaxSize()) {

            if (hasCameraPermission) {
                // Camera preview
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).also { pv ->
                            pv.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            poseViewModel.bindCamera(lifecycleOwner, pv)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )

                // Skeleton overlay drawn on top of camera preview
                AndroidView(
                    factory = { ctx ->
                        PoseOverlayView(ctx).also { ov ->
                            ov.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            poseViewModel.setOverlayView(ov)
                        }
                    },
                    update = { ov -> poseViewModel.setOverlayView(ov) },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(Modifier.fillMaxSize().background(Color.Black), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Camera permission required\nfor pose tracking",
                            color = Color.White, fontSize = 15.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            // Info overlays on top
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Card(modifier = Modifier.width(190.dp), shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.66f))) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(selectedExerciseName, fontSize = 22.sp, lineHeight = 28.sp,
                                fontWeight = FontWeight.Bold, color = BrandTealDark)
                            Spacer(Modifier.height(24.dp))
                            Text("Target", fontSize = 14.sp, color = Color(0xFF666666))
                            Spacer(Modifier.height(8.dp))
                            Text("${update.repCount.coerceAtMost(targetReps)}/$targetReps\nReps", fontSize = 22.sp,
                                lineHeight = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Card(modifier = Modifier.width(170.dp).height(90.dp), shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.66f))) {
                            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, "Timer", tint = BrandTealDark, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "%02d:%02d min".format(secondsPassed / 60, secondsPassed % 60),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }
                        Card(modifier = Modifier.width(170.dp), shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.66f))) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Favorite, null, tint = Color(0xFFC62828), modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = if (heartRate == 0) "-- BPM" else "$heartRate BPM",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                LinearProgressIndicator(
                    progress = { (update.repCount.toFloat() / targetReps).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
                        .height(10.dp).clip(RoundedCornerShape(20.dp)),
                    color = BrandTealDark,
                    trackColor = Color.White.copy(alpha = 0.76f),
                )

                Spacer(Modifier.height(20.dp))
                if (update.feedback.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(36.dp))
                            .background(
                                if (isGoodForm) Color(0xFFD6E9E8).copy(alpha = 0.88f)
                                else Color(0xFFFFF3CD).copy(alpha = 0.92f)
                            )
                            .padding(horizontal = 22.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            if (isGoodForm) Icons.Default.CheckCircle else Icons.Default.Warning,
                            null,
                            tint = if (isGoodForm) BrandTealDark else Color(0xFFE65100),
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(update.feedback,
                            color = if (isGoodForm) BrandTealDark else Color(0xFFE65100),
                            fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
                        .navigationBarsPadding().padding(bottom = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Surface(modifier = Modifier.size(65.dp).clickable { liveSessionViewModel.pauseSession()
                        onPause() },
                        shape = CircleShape, color = Color.White.copy(alpha = 0.66f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Pause, "Pause", tint = BrandTealDark, modifier = Modifier.size(32.dp))
                        }
                    }
                    Button(onClick = {
                        liveSessionViewModel.finalizeSession(
                            exerciseName = selectedExerciseName,
                            reps = update.repCount,
                            poseQuality = update.poseQuality
                        )
                        onEndSession() },
                        modifier = Modifier.weight(1f).height(65.dp),
                        shape = RoundedCornerShape(41.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)) {
                        Icon(Icons.Default.StopCircle, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("End Session", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
