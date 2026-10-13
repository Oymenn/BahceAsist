package com.ytkf2.bahceasist.core.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ytkf2.bahceasist.core.ui.components.BahcemHeader
import com.ytkf2.bahceasist.core.ui.theme.*

@Composable
fun ProfileScreen(
    locationName: String = "Bursa, Nilüfer",
    modifier: Modifier = Modifier
) {
    var alertsEnabled by remember { mutableStateOf(true) }
    var frostAlertsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { BahcemHeader(subtitle = "Bahçem & Profil") },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLowest),
                    border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(ForestPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = OnForestPrimaryContainer,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Bahçıvan Asistanı",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnGardenSurface
                            )
                            Text(
                                text = "$locationName • Saha Sahibi",
                                fontSize = 12.sp,
                                color = OnGardenSurfaceVariant
                            )
                            Text(
                                text = "Offline-First Aktif • Güvenli Yerel Depolama",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GardenSecondary
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLowest),
                    border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Bildirim & Uyarı Ayarları",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Günlük Tarım Uyarıları",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnGardenSurface
                                )
                                Text(
                                    text = "Sabah hava ve don kontrolleri",
                                    fontSize = 11.sp,
                                    color = OnGardenSurfaceVariant
                                )
                            }
                            Switch(
                                checked = alertsEnabled,
                                onCheckedChange = { alertsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ForestPrimary,
                                    checkedTrackColor = GardenSecondaryFixed
                                )
                            )
                        }

                        HorizontalDivider(color = GardenSurfaceContainer)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Kritik Don & Fırtına Alarmı",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnGardenSurface
                                )
                                Text(
                                    text = "Sıcaklık ≤ 2°C olduğunda acil uyarı",
                                    fontSize = 11.sp,
                                    color = OnGardenSurfaceVariant
                                )
                            }
                            Switch(
                                checked = frostAlertsEnabled,
                                onCheckedChange = { frostAlertsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ForestPrimary,
                                    checkedTrackColor = GardenSecondaryFixed
                                )
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Bahçe Asist v1.0.0",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary
                        )
                        Text(
                            text = "Hava tahmin verileri Open-Meteo.com tarafından sağlanmaktadır (CC BY 4.0 lisanslı).",
                            fontSize = 11.sp,
                            color = OnGardenSurfaceVariant
                        )
                        Text(
                            text = "7 modüllü Clean Architecture & Offline-First saha asistanı.",
                            fontSize = 10.sp,
                            color = OnGardenSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
