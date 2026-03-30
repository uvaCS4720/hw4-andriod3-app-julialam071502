package edu.nd.pmcburne.hello.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

private val UvaBlue   = Color(0xFF232D4B)
private val UvaOrange = Color(0xFFE57200)

@Composable
fun CampusMapScreen(vm: MapViewModel = viewModel()) {
    val tags              by vm.tags.collectAsState()
    val selectedTag       by vm.selectedTag.collectAsState()
    val filteredLocations by vm.filteredLocations.collectAsState()
    val isLoading         by vm.isLoading.collectAsState()

    val uvaCenter = LatLng(38.0356, -78.5034)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uvaCenter, 15f)
    }

    Column(Modifier.fillMaxSize()) {

        // Header
        Box(
            Modifier
                .fillMaxWidth()
                .background(UvaBlue)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("UVA Campus Map", color = Color.White,
                fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Dropdown
        TagDropdown(
            tags          = tags,
            selectedTag   = selectedTag,
            onTagSelected = { vm.selectTag(it) },
            modifier      = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )

        // Map
        Box(Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color    = UvaOrange
                )
            } else {
                GoogleMap(
                    modifier            = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState
                ) {
                    filteredLocations.forEach { loc ->
                        MarkerInfoWindow(
                            state   = rememberMarkerState(LatLng(loc.latitude, loc.longitude)),
                            title   = loc.name,
                            snippet = loc.description,
                            icon    = BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_ORANGE
                            )
                        ) { marker ->
                            Column(
                                Modifier
                                    .widthIn(max = 280.dp)
                                    .shadow(4.dp, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text       = marker.title ?: "",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 14.sp,
                                    color      = UvaBlue
                                )
                                if (!marker.snippet.isNullOrBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text       = marker.snippet ?: "",
                                        fontSize   = 12.sp,
                                        color      = Color.DarkGray,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Badge
                Surface(
                    modifier        = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    shape           = RoundedCornerShape(20.dp),
                    color           = UvaBlue,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text     = "${filteredLocations.size} location${if (filteredLocations.size != 1) "s" else ""}",
                        color    = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TagDropdown(
    tags: List<String>,
    selectedTag: String,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, UvaBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text("Filter by tag", fontSize = 11.sp,
                    color = Color.Gray, fontWeight = FontWeight.Medium)
                Text(selectedTag, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = UvaBlue)
            }
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = UvaBlue)
        }

        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(max = 300.dp)
                .background(Color.White)
        ) {
            tags.forEach { tag ->
                val isSelected = tag == selectedTag
                DropdownMenuItem(
                    text = {
                        Text(
                            text       = tag,
                            color      = if (isSelected) UvaOrange else UvaBlue,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize   = 14.sp
                        )
                    },
                    onClick  = { onTagSelected(tag); expanded = false },
                    modifier = Modifier.background(
                        if (isSelected) UvaOrange.copy(alpha = 0.08f) else Color.Transparent
                    )
                )
            }
        }
    }
}