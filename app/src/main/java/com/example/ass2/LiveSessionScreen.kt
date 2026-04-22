package com.example.ass2

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandTealDark

@Composable
fun LiveSessionScreen(
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onPause: () -> Unit,
    onEndSession: () -> Unit
){
    // the whole screen is housed in a Column.
    Column(modifier = Modifier.fillMaxSize())
    {
        // top bar itself is a row.
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.90f))
                .statusBarsPadding().padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            // top bar element structures.
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = BrandTealDark,
                modifier = Modifier.size(28.dp).clickable{onBack()})
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Live Session", color = BrandTealDark, fontSize = 20.sp, fontWeight = Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Help", color = BrandTealDark, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable{onHelp()}
            )
        }

        // content below the top bar
        Box(modifier = Modifier.fillMaxSize())
        {
            // background image (for now ) - represents the actual live camera feed
            Image(
                painter = painterResource(R.drawable.live_session_bg),
                contentDescription = "live session image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // overlay components (Exercise name, heart rate, progress check)
            Column(modifier = Modifier.fillMaxSize())
            {
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                    )
                {
                    // exercise name & Target info
                    Card(modifier = Modifier.width(190.dp), shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.66f)))
                    {
                        Column(modifier = Modifier.padding(18.dp))
                        {
                            Text(text = "Seated Torso\nTwist", fontSize = 24.sp, lineHeight = 30.sp,
                                fontWeight = FontWeight.Bold, color = BrandTealDark)
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(text = "Target", fontSize = 14.sp, color = Color(0xFF666666))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "4/12\nReps", fontSize = 22.sp, lineHeight = 28.sp,
                                fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp))
                    {
                        // for the timer display
                        Card(modifier = Modifier.width(170.dp).height(90.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.66f))
                        ) {
                            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Timer",
                                    tint = BrandTealDark, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = "12:45 min", fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
                            }
                        }
                        // for the heart rate display
                        Card(modifier = Modifier.width(170.dp), shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.66f)))
                        {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically)
                            {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = null,
                                    tint = Color(0xFFC62828), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = "78 BPM", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F1F1F)
                                )
                            }
                        }
                    }
                }

                // Progress bar - indicator.
                Spacer(modifier = Modifier.height(18.dp))
                LinearProgressIndicator(progress = {0.43f},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp).height(10.dp)
                        .clip(RoundedCornerShape(20.dp)), color = BrandTealDark,
                    trackColor = Color.White.copy(alpha = 0.76f))

                // pose quality indicator
                Spacer(modifier = Modifier.height(140.dp))
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(36.dp))
                        .background(Color(0xFFD6E9E8).copy(alpha = 0.88f))
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null,
                        tint = BrandTealDark, modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(text = "Good Form", color = BrandTealDark, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // pause & session end button.
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp).navigationBarsPadding()
                        .padding(bottom = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Surface(modifier = Modifier.size(65.dp).clickable { onPause() }, shape = CircleShape,
                        color = Color.White.copy(alpha = 0.66f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Pause, contentDescription = "Pause",
                                tint = BrandTealDark, modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Button(onClick = onEndSession, modifier = Modifier.weight(1f).height(65.dp),
                        shape = RoundedCornerShape(41.dp), colors = ButtonDefaults.buttonColors(
                            containerColor = BrandTealDark.copy(alpha = 0.94f)
                        )
                    ) {
                        Icon(imageVector = Icons.Default.StopCircle, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "End Session", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

            }
        }

    }
}