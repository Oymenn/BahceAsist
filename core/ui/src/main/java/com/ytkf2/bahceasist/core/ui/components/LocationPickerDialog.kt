package com.ytkf2.bahceasist.core.ui.components

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ytkf2.bahceasist.core.common.TurkeyDistrict
import com.ytkf2.bahceasist.core.common.TurkeyLocationRegistry
import com.ytkf2.bahceasist.core.ui.theme.*
import com.ytkf2.bahceasist.domain.model.Location

@Composable
fun LocationPickerDialog(
    currentLocation: Location?,
    onDismissRequest: () -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<Location>,
    isSearching: Boolean,
    onSelectLocation: (Location) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val allProvinces = remember { TurkeyLocationRegistry.allProvinces }

    var selectedProvince by remember {
        mutableStateOf(currentLocation?.region ?: "Ankara")
    }

    val currentDistricts = remember(selectedProvince) {
        TurkeyLocationRegistry.getDistricts(selectedProvince)
    }

    // Anlık yerel Türkçe ilçe ve il arama sonuçları
    val instantLocalMatches = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            TurkeyLocationRegistry.search(searchQuery).map { item ->
                Location(
                    name = item.district,
                    region = item.province,
                    country = "Türkiye",
                    latitude = item.latitude,
                    longitude = item.longitude
                )
            }
        }
    }

    // Yerel sonuçlar ile varsa uzaktan gelen sonuçları birleştir (tekilleştirilmiş)
    val combinedSearchResults = remember(instantLocalMatches, searchResults) {
        val list = instantLocalMatches.toMutableList()
        for (remote in searchResults) {
            val alreadyPresent = list.any {
                it.name.equals(remote.name, ignoreCase = true) &&
                        (it.region == null || remote.region == null || it.region.equals(remote.region, ignoreCase = true))
            }
            if (!alreadyPresent) {
                list.add(remote)
            }
        }
        list
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Başlık
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GardenSecondaryFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = ForestPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Konum Seçimi",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestPrimary
                            )
                            Text(
                                text = "Türkiye İl & İlçe Seçimi (Saha Konumu)",
                                fontSize = 11.sp,
                                color = OnGardenSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = OnGardenSurfaceVariant
                        )
                    }
                }

                // Arama Kutusu (İl veya İlçe)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.length >= 2) {
                            onSearch(it)
                        }
                    },
                    placeholder = { Text("İl veya ilçe ara (örn: Polatlı, Nilüfer, Urla)") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = GardenSecondary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Temizle", tint = GardenSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (searchQuery.isNotBlank()) {
                    // Canlı Arama Sonuçları Bölümü
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ARAMA SONUÇLARI (${combinedSearchResults.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnGardenSurfaceVariant
                        )
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = ForestPrimary
                            )
                        }
                    }

                    if (combinedSearchResults.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(combinedSearchResults) { loc ->
                                val isSelected = currentLocation?.name == loc.name &&
                                        (currentLocation.region == loc.region || currentLocation.region == null)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) ForestPrimaryContainer else GardenSurfaceContainerLow)
                                        .clickable {
                                            onSelectLocation(loc)
                                            onDismissRequest()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = if (isSelected) ForestPrimary else GardenSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Column {
                                            Text(
                                                text = if (loc.region != null && loc.region != loc.name) {
                                                    "${loc.region}, ${loc.name}"
                                                } else {
                                                    loc.name
                                                },
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                color = if (isSelected) ForestPrimary else OnGardenSurface
                                            )
                                            Text(
                                                text = "Türkiye",
                                                fontSize = 11.sp,
                                                color = OnGardenSurfaceVariant
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Text(
                                            text = "Seçili",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestPrimary
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "'$searchQuery' için Türkiye'de il veya ilçe bulunamadı",
                                fontSize = 12.sp,
                                color = OnGardenSurfaceVariant
                            )
                        }
                    }
                } else {
                    // 81 İl ve Seçili İlin İlçeleri
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "İL SEÇİNİZ (81 İL)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnGardenSurfaceVariant
                        )

                        // 81 İl Yatay Çubuğu
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allProvinces.forEach { prov ->
                                val isSelected = selectedProvince.equals(prov, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) ForestPrimary else GardenSurfaceContainerLow,
                                    modifier = Modifier.clickable { selectedProvince = prov }
                                ) {
                                    Text(
                                        text = prov,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) Color.White else OnGardenSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$selectedProvince İLÇELERİ (${currentDistricts.size} İlçe)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnGardenSurfaceVariant
                        )

                        // İlçeler Listesi
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(currentDistricts) { district ->
                                val isCurrent = currentLocation?.name == district.district &&
                                        (currentLocation.region.equals(selectedProvince, ignoreCase = true) || currentLocation.region == null)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isCurrent) ForestPrimaryContainer else GardenSurfaceContainerLow)
                                        .clickable {
                                            onSelectLocation(
                                                Location(
                                                    name = district.district,
                                                    region = selectedProvince,
                                                    country = "Türkiye",
                                                    latitude = district.latitude,
                                                    longitude = district.longitude
                                                )
                                            )
                                            onDismissRequest()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = if (isCurrent) ForestPrimary else GardenSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "$selectedProvince, ${district.district}",
                                            fontSize = 13.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCurrent) ForestPrimary else OnGardenSurface
                                        )
                                    }

                                    if (isCurrent) {
                                        Text(
                                            text = "Seçili",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
