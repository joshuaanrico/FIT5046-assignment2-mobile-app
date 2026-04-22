package com.example.ass2

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PreSessionCheckInScreen(
    onClose: () -> Unit,
    onResumeToSession: () -> Unit,
    onCancel: () -> Unit
){
    Text(
        "Pre-session check-in screen !!"
    )
}