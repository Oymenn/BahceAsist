package com.ytkf2.bahceasist.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ytkf2.bahceasist.core.ui.theme.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Surface

enum class BahcemTab(val title: String, val icon: ImageVector) {
    WEATHER("Hava & Bahçe", Icons.Default.WbSunny),
    AGENDA("Ajanda", Icons.Default.EventAvailable),
    PLANT_GUIDE("Bitki Rehberi", Icons.Default.LocalFlorist),
    PROFILE("Bahçem & Profil", Icons.Default.AccountCircle)
}

@Composable
fun BahcemBottomBar(
    currentTab: BahcemTab,
    onTabSelected: (BahcemTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = GardenSurfaceContainerLowest,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(0.5.dp, GardenSurfaceContainerHigh.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BahcemTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                val tint = if (isSelected) ForestPrimary else OnGardenSurfaceVariant
                val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = fontWeight,
                        color = tint,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
