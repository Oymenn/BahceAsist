package com.ytkf2.bahceasist.core.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ytkf2.bahceasist.core.ui.components.BahcemHeader
import com.ytkf2.bahceasist.core.ui.theme.*

data class PlantItem(
    val id: Int,
    val name: String,
    val latinName: String,
    val category: String, // "sebze", "meyve", "aromatik", "cicek"
    val categoryLabel: String,
    val frostTag: String,
    val isFrostSensitive: Boolean,
    val waterTag: String,
    val details: List<String>,
    val description: String,
    val tip: String
)

@Composable
fun PlantGuideScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("all") }

    val categories = listOf(
        Pair("all", "Tümü (24)"),
        Pair("sebze", "Sebzeler (12)"),
        Pair("meyve", "Meyve Ağaçları (6)"),
        Pair("aromatik", "Aromatik Otlar (4)"),
        Pair("cicek", "Çiçekler (2)")
    )

    val plants = remember {
        listOf(
            PlantItem(
                id = 1,
                name = "Sırık Domates Fidesi",
                latinName = "Lycopersicon esculentum",
                category = "sebze",
                categoryLabel = "Sebze",
                frostTag = "Don Hassas <5°C",
                isFrostSensitive = true,
                waterTag = "2-3 Günde Bir",
                details = listOf("Tam Güneş (6+ saat)", "Koltuk Alma Gerekli", "Hasat: 70-85 Gün"),
                description = "Sıcak ve güneşi sever. 0°C altında fidan tamamen donar. Dikim sonrası yan sürgünlerin (koltuk alma) temizlenmesi meyve iriliğini ve verimini iki kat artırır.",
                tip = "Bölge ekim takvimine uygun"
            ),
            PlantItem(
                id = 2,
                name = "Bodur Elma Ağacı",
                latinName = "Malus domestica",
                category = "meyve",
                categoryLabel = "Meyve Ağacı",
                frostTag = "Kışa Dirençli -25°C",
                isFrostSensitive = false,
                waterTag = "Damla Sulama (Haftalık)",
                details = listOf("Damla Sulama", "Meyve: Sonbahar", "Max Boy: 2 - 2.5m"),
                description = "Kış dinlenmesinde dondan etkilenmez ancak ilkbahar erken çiçeklenmesinde don zararı riski vardır. Düzenli taç budaması ve seyreltme meyve kalitesi için şarttır.",
                tip = "Balkon & Bahçe için ideal"
            ),
            PlantItem(
                id = 3,
                name = "Kıl Biber (Tatlı)",
                latinName = "Capsicum annuum",
                category = "sebze",
                categoryLabel = "Sebze",
                frostTag = "Çok Hassas <8°C",
                isFrostSensitive = true,
                waterTag = "Düzenli Nem",
                details = listOf("Bol Sıcaklık (22-28°C)", "Saksıya Uygun (15L+)"),
                description = "Kökleri sığdır, aşırı kuraklıkta çiçeklerini döker. Ani gece soğuklarında (10°C altı) gelişimi durur; örtü altına alınmalıdır.",
                tip = "Günde 2 kez toplama tavsiyesi"
            ),
            PlantItem(
                id = 4,
                name = "Biberiye & Kekik",
                latinName = "Rosmarinus & Thymus",
                category = "aromatik",
                categoryLabel = "Aromatik",
                frostTag = "Kuraklığa Dayanıklı",
                isFrostSensitive = false,
                waterTag = "Kışa Dayanıklı",
                details = listOf("Tam Güneş / Taşlık Toprak", "Doğal Zararlı Kovucu"),
                description = "Aşırı sudan hoşlanmaz, kök çürümesini önlemek için geçirgen süzek toprak ister. Sebze tarhlarının kenarına ekildiğinde kokusuyla böcekleri uzak tutar.",
                tip = "Çok yıllık kalıcı bitki"
            )
        )
    }

    val filteredPlants = plants.filter { plant ->
        val matchesCategory = selectedCat == "all" || plant.category == selectedCat
        val matchesQuery = searchQuery.isEmpty() || plant.name.contains(searchQuery, ignoreCase = true) || plant.latinName.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Scaffold(
        topBar = { BahcemHeader(subtitle = "Bitki Rehberi") },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Bitki, sebze veya ağaç ara...", fontSize = 13.sp, color = GardenOutline) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GardenOutline)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Temizle", tint = GardenOutline)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GardenSurfaceContainerLowest,
                            unfocusedContainerColor = GardenSurfaceContainerLowest,
                            focusedBorderColor = ForestPrimary,
                            unfocusedBorderColor = GardenSurfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(GardenSurfaceContainerHigh)
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = "Filtre", tint = ForestPrimary)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { (catId, label) ->
                        val isSelected = selectedCat == catId
                        val chipBg = if (isSelected) ForestPrimary else GardenSurfaceContainerHigh
                        val chipText = if (isSelected) Color.White else OnGardenSurfaceVariant

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(chipBg)
                                .clickable { selectedCat = catId }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = chipText
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
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GardenSecondaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = ForestPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Bahçıvan İpucu",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestPrimary
                                )
                                Text(
                                    text = "Saha Önerisi",
                                    fontSize = 10.sp,
                                    color = GardenSecondary,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(GardenSurfaceContainerHigh)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "İlkbahar geç donlarına karşı bitkilerinizi malçlama ve agril örtüyle koruyabilirsiniz. Toprak sıcaklığını 2-3°C yüksek tutar.",
                                fontSize = 12.sp,
                                color = OnGardenSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            items(filteredPlants) { plant ->
                PlantCard(plant = plant)
            }
        }
    }
}

@Composable
fun PlantCard(plant: PlantItem) {
    var isFavorited by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLowest),
        border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GardenSurfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (plant.category == "meyve") Icons.Default.Park else Icons.Default.Eco,
                        contentDescription = null,
                        tint = GardenSecondary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = plant.categoryLabel.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GardenSecondary,
                            letterSpacing = 0.5.sp
                        )

                        IconButton(
                            onClick = { isFavorited = !isFavorited },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorited) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Favori",
                                tint = if (isFavorited) ForestPrimary else GardenOutline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = plant.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnGardenSurface
                    )
                    Text(
                        text = plant.latinName,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = GardenOutline
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = plant.frostTag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (plant.isFrostSensitive) AlertError else GardenSecondary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (plant.isFrostSensitive) AlertErrorContainer.copy(alpha = 0.6f) else GardenSecondaryFixed.copy(alpha = 0.6f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                        Text(
                            text = plant.waterTag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WeatherTertiary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(WeatherTertiaryFixed)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                plant.details.forEach { detail ->
                    Text(
                        text = detail,
                        fontSize = 10.sp,
                        color = OnGardenSurfaceVariant,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GardenSurfaceContainerLow)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(GardenSurfaceContainerLow.copy(alpha = 0.7f))
                    .padding(10.dp)
            ) {
                Text(
                    text = plant.description,
                    fontSize = 11.sp,
                    color = OnGardenSurface,
                    lineHeight = 15.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plant.tip,
                    fontSize = 11.sp,
                    color = GardenOutline
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.clickable { }
                ) {
                    Text(
                        text = "Rehberi Aç",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = ForestPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
