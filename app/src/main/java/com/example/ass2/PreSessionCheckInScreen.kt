package com.example.ass2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ass2.ui.theme.BrandBackground
import com.example.ass2.ui.theme.BrandTeal
import com.example.ass2.ui.theme.BrandTealDark
import com.example.ass2.ui.theme.CardBackground
import com.example.ass2.ui.theme.IconBackground

@Composable
fun PreSessionCheckInScreen(
    onClose: () -> Unit,
    onResumeToSession: () -> Unit,
    onCancel: () -> Unit
) {
    var energyLevel by remember { mutableIntStateOf(3) }
    var painLevel by remember { mutableIntStateOf(1) }
    var isSpaceClear by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = BrandBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top bar containing the cancel screen title
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.align(Alignment.CenterStart).size(36.dp).clip(CircleShape).clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BrandTealDark,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "Pre-Session",
                    color = BrandTealDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // The header message.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                        .background(Color(0xFFDFF0F0))
                )

                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "How are you\nfeeling today?",
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandTealDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Let’s take a moment to tune into your\nbody and ensure your environment is\nsafe before we begin.",
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF4F4F4F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Takes the initial level of energy the user feels.
            CheckInCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.BatteryAlert,
                        contentDescription = null,
                        tint = Color(0xFF5E7474),
                        modifier = Modifier.size(22.dp)
                    )
                },
                title = "Energy Level"
            ) {
                NumberSelectorRow(
                    selected = energyLevel,
                    onSelected = { energyLevel = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Low", fontSize = 12.sp, color = Color(0xFF5A5A5A))
                    Text("High", fontSize = 12.sp, color = Color(0xFF5A5A5A))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Taking the current pain level
            CheckInCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color(0xFF5E7474),
                        modifier = Modifier.size(22.dp)
                    )
                },
                title = "Current Pain Level"
            ) {
                NumberSelectorRow(
                    selected = painLevel,
                    onSelected = { painLevel = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("None", fontSize = 12.sp, color = Color(0xFF5A5A5A))
                    Text("Severe", fontSize = 12.sp, color = Color(0xFF5A5A5A))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check to see whether the user has enough space.
            CheckInCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Weekend,
                        contentDescription = null,
                        tint = Color(0xFF5E7474),
                        modifier = Modifier.size(22.dp)
                    )
                },
                title = "Is your space clear?"
            ) {
                Text(
                    text = "Ensure you have an arm’s length of\nclear space around you without trip\nhazards.",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF4F4F4F)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    YesNoOption(
                        text = "No",
                        selected = !isSpaceClear,
                        onClick = { isSpaceClear = false },
                        selectedColor = Color(0xFFE9F2F2),
                        selectedTextColor = Color(0xFF4A4A4A),
                        showCheck = true,
                        modifier = Modifier.weight(1f)
                    )

                    YesNoOption(
                        text = "Yes",
                        selected = isSpaceClear,
                        onClick = { isSpaceClear = true },
                        selectedColor = Color(0xFFCFE7E7),
                        selectedTextColor = Color(0xFF4A4A4A),
                        showCheck = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Giving safety instructions
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = BrandTealDark,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Please stop immediately if you feel any\nsharp pain or unusual discomfort. Your\nsafety is our priority.",
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF4F4F4F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onResumeToSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
            ) {
                Text(
                    text = "Continue to Session",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier.fillMaxWidth().clickable { onCancel() }.padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancel",
                    color = BrandTealDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/**
 * Composable to be reused - card ready to use with styling in place.
 */
@Composable
private fun CheckInCard(
    icon: @Composable () -> Unit,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(IconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E1E)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
private fun NumberSelectorRow(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        (1..5).forEach { value ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onSelected(value) }
            ) {
                RadioButton(
                    selected = value == selected,
                    onClick = { onSelected(value) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = BrandTealDark,
                        unselectedColor = Color(0xFF9E9E9E)
                    )
                )
                Text(
                    text = value.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (value == selected) BrandTealDark else Color(0xFF4A4A4A)
                )
            }
        }
    }
}

@Composable
private fun YesNoOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    selectedTextColor: Color,
    showCheck: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = if (selected) selectedColor else Color(0xFFF8F8F8)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected && showCheck) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF5E7474),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) selectedTextColor else Color(0xFF4A4A4A)
            )
        }
    }
}