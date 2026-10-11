package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DeviceInfo
import com.example.data.model.SecurityCheckInfo
import com.example.ui.components.CutePawPrint
import com.example.ui.components.ErrorCardView
import com.example.ui.components.LoadingCatView
import com.example.ui.components.PinkCard
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenContainer
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.StatusOrangeContainer
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedContainer
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToDevice: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deviceState by viewModel.deviceInfoState.collectAsState()
    val securityState by viewModel.securityState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Mascot Hero Header
            MascotHeroCard(
                onRefresh = { viewModel.loadAll() }
            )
        }

        when (val dState = deviceState) {
            is UiState.Loading -> {
                item {
                    LoadingCatView(text = "Membaca data perangkat dan sensor...")
                }
            }
            is UiState.Error -> {
                item {
                    ErrorCardView(
                        message = dState.message,
                        onRetry = { viewModel.refreshDeviceInfo() }
                    )
                }
            }
            is UiState.Success -> {
                val info = dState.data
                item {
                    DeviceIdentityBanner(
                        info = info,
                        onInspectClick = onNavigateToDevice
                    )
                }

                item {
                    Text(
                        text = "Status Perangkat Saat Ini",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                // Grid of Status Cards
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatusQuickCard(
                                modifier = Modifier.weight(1f),
                                title = "Baterai",
                                value = "${info.batteryPercentage}%",
                                subtitle = if (info.batteryStatus.contains("Charging", true)) "Sedang Mengisi" else "Dipakai",
                                icon = Icons.Default.BatteryChargingFull,
                                iconTint = MaterialTheme.colorScheme.primary,
                                progress = info.batteryPercentage / 100f
                            )
                            StatusQuickCard(
                                modifier = Modifier.weight(1f),
                                title = "Memori RAM",
                                value = "${(info.usedRamBytes * 100 / info.totalRamBytes).toInt()}%",
                                subtitle = "Tersedia ${(info.availRamBytes / (1024 * 1024 * 1024.0)).let { String.format(java.util.Locale.US, "%.1f GB", it) }}",
                                icon = Icons.Default.Memory,
                                iconTint = MaterialTheme.colorScheme.secondary,
                                progress = (info.usedRamBytes.toFloat() / info.totalRamBytes.toFloat()).coerceIn(0f, 1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatusQuickCard(
                                modifier = Modifier.weight(1f),
                                title = "Penyimpanan",
                                value = "${(info.usedStorageBytes * 100 / info.totalStorageBytes).toInt()}%",
                                subtitle = "Sisa ${(info.availStorageBytes / (1024 * 1024 * 1024.0)).let { String.format(java.util.Locale.US, "%.1f GB", it) }}",
                                icon = Icons.Default.SdStorage,
                                iconTint = MaterialTheme.colorScheme.tertiary,
                                progress = (info.usedStorageBytes.toFloat() / info.totalStorageBytes.toFloat()).coerceIn(0f, 1f)
                            )
                            StatusQuickCard(
                                modifier = Modifier.weight(1f),
                                title = "Jaringan",
                                value = info.networkType,
                                subtitle = if (info.isNetworkConnected) "Terhubung Aktif" else "Terputus",
                                icon = if (info.isNetworkConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                                iconTint = if (info.isNetworkConnected) StatusGreen else StatusOrange,
                                progress = null
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatusQuickCard(
                                modifier = Modifier.weight(1f),
                                title = "Bluetooth",
                                value = if (info.bluetoothStatusText.contains("ON", true)) "Aktif (ON)" else if (info.bluetoothStatusText.contains("OFF", true)) "Nonaktif" else "Terkunci/Off",
                                subtitle = info.bluetoothStatusText,
                                icon = Icons.Default.Bluetooth,
                                iconTint = MaterialTheme.colorScheme.primary,
                                progress = null
                            )
                            StatusQuickCard(
                                modifier = Modifier.weight(1f),
                                title = "Waktu Sistem",
                                value = info.systemTimeFormatted.split(",").getOrNull(1)?.trim() ?: "",
                                subtitle = info.systemTimeFormatted.split(",").getOrNull(0) ?: "",
                                icon = Icons.Default.AccessTime,
                                iconTint = MaterialTheme.colorScheme.secondary,
                                progress = null
                            )
                        }
                    }
                }
            }
        }

        // Security / Permission Summary Card
        item {
            Text(
                text = "Ringkasan Keamanan & Privasi",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        item {
            when (val sState = securityState) {
                is UiState.Loading -> {
                    LoadingCatView(text = "Mengaudit perizinan & status keamanan...")
                }
                is UiState.Error -> {
                    ErrorCardView(
                        message = sState.message,
                        onRetry = { viewModel.loadInstalledApps() }
                    )
                }
                is UiState.Success -> {
                    SecuritySummaryCard(
                        security = sState.data,
                        onAuditPermissions = onNavigateToPermissions
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MascotHeroCard(
    onRefresh: () -> Unit
) {
    PinkCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mascot Image
                Image(
                    painter = painterResource(id = R.drawable.pink_cat_hero),
                    contentDescription = "Maskot Pink Cat Manager",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(18.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PinkCat Manager",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        CutePawPrint(size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = "Pengawas Pintar & Penjaga Izin Perangkat Nyata",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.testTag("refresh_home_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Muat Ulang Semua",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceIdentityBanner(
    info: DeviceInfo,
    onInspectClick: () -> Unit
) {
    PinkCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = info.deviceName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "Android ${info.androidVersion}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Model: ${info.manufacturer} ${info.model} • SDK ${info.sdkInt} • CPU: ${info.cpuAbi}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onInspectClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inspect_device_button")
            ) {
                Text("Lihat Spesifikasi & Sensor Lengkap", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun StatusQuickCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    progress: Float?
) {
    PinkCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (progress != null) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = iconTint,
                    trackColor = iconTint.copy(alpha = 0.18f),
                    strokeCap = StrokeCap.Round
                )
            }

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SecuritySummaryCard(
    security: SecurityCheckInfo,
    onAuditPermissions: () -> Unit
) {
    PinkCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Status Keamanan Nyata",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (security.isScreenLockEnabled) StatusGreenContainer else StatusRedContainer
                ) {
                    Text(
                        text = if (security.isScreenLockEnabled) "Perangkat Terkunci" else "Layar Terbuka",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (security.isScreenLockEnabled) StatusGreen else StatusRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Key indicators
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SecurityItemRow(
                    label = "Kunci Layar (Screen Lock)",
                    status = security.screenLockStatusText,
                    isSafe = security.isScreenLockEnabled,
                    safeIcon = Icons.Default.Lock,
                    unsafeIcon = Icons.Default.LockOpen
                )
                SecurityItemRow(
                    label = "USB Debugging",
                    status = security.usbDebuggingStatusText,
                    isSafe = !security.isUsbDebuggingEnabled,
                    safeIcon = Icons.Default.CheckCircle,
                    unsafeIcon = Icons.Default.Warning
                )
                SecurityItemRow(
                    label = "Opsi Pengembang (Developer Options)",
                    status = security.devOptionsStatusText,
                    isSafe = !security.isDevOptionsEnabled,
                    safeIcon = Icons.Default.CheckCircle,
                    unsafeIcon = Icons.Default.DeveloperMode
                )
            }

            // Sensitive permissions summary chips
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Aplikasi Memiliki Izin Sensitif:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PermissionStatChip(
                        icon = Icons.Default.CameraAlt,
                        label = "Kamera",
                        count = security.appsWithCameraCount
                    )
                    PermissionStatChip(
                        icon = Icons.Default.Mic,
                        label = "Mikrofon",
                        count = security.appsWithMicCount
                    )
                    PermissionStatChip(
                        icon = Icons.Default.LocationOn,
                        label = "Lokasi",
                        count = security.appsWithLocationCount
                    )
                }
            }

            Button(
                onClick = onAuditPermissions,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audit_permissions_button")
            ) {
                Text("Audit Semua Izin Aplikasi (${security.totalAppsAudited} Aplikasi)", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SecurityItemRow(
    label: String,
    status: String,
    isSafe: Boolean,
    safeIcon: ImageVector,
    unsafeIcon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSafe) safeIcon else unsafeIcon,
                contentDescription = null,
                tint = if (isSafe) StatusGreen else StatusOrange,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = status,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PermissionStatChip(
    icon: ImageVector,
    label: String,
    count: Int
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "$label: $count",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
