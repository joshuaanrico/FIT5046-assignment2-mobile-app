package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandTealDark
import com.example.ass2.ui.theme.BrandTeal
import com.example.ass2.ui.theme.CardBackground

@Composable
fun AlertScreen(
    onRest: () -> Unit,
    onContinueLowerIntensity : () -> Unit,
    onEndSession : () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize().background(CardBackground),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Box(modifier = Modifier.size(70.dp).background(Color(0xFFD7ECEA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = null,
                        tint = Color(0xFFFF9800)
                    )
                }
                Text(text = "Safety Alert: Take a\n\nMoment", fontSize = 26.sp,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Color.Black
                )
                Text(
                    text = "We noticed your heart rate is slightly elevated.", fontSize = 14.sp,
                    textAlign = TextAlign.Center, color = Color.Black
                )
                Text(
                    text = "Let’s pause for a short rest to ensure you stay within your comfort zone.",
                    fontSize = 13.sp, textAlign = TextAlign.Center, color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = onRest, modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rest", color = Color.White)
                }

                Button(
                    onClick = onContinueLowerIntensity, modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(30.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD2E8E6)
                    )
                ) {
                    Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color.DarkGray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Continue at Lower Intensity", color = Color.DarkGray)
                }

                Text(text = "End Session", color = BrandTealDark, fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onEndSession() }
                )
            }
        }
    }
}