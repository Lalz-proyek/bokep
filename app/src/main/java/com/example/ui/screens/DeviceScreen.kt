package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.util.Locale

@Composable
fun DeviceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val deviceState by viewModel.deviceInfoState.collectAsState()
    val securityState by viewModel.securityState.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Pemeriksaan Perangkat",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        CutePawPrint(size = 18.dp, tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = "Data resmi dari API & Hardware Android",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.refreshDeviceInfo() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("refresh_device_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Device Information",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Refresh Device Information", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (val dState = deviceState) {
            is UiState.Loading -> {
                item {
                    LoadingCatView(text = "Mengumpulkan spesifikasi perangkat langsung dari Android API...")
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

                // 1. Identitas Hardware & Sistem Operasi
                item {
                    DeviceSectionCard(
                        title = "Identitas & Sistem Operasi",
                        icon = Icons.Default.PhoneAndroid,
                        accentColor = MaterialTheme.colorScheme.primary
                    ) {
                        InfoDetailRow("Nama Perangkat", info.deviceName)
                        InfoDetailRow("Merek (Brand)", info.brand)
                        InfoDetailRow("Produsen (Manufacturer)", info.manufacturer)
                        InfoDetailRow("Model Perangkat", info.model)
                        InfoDetailRow("Kode Perangkat (Device)", info.deviceCodename)
                        InfoDetailRow("Produk (Product)", info.product)
                        InfoDetailRow("Hardware", info.hardware)
                        InfoDetailRow("Versi Android", "Android ${info.androidVersion}")
                        InfoDetailRow("Versi SDK", "API ${info.sdkInt} (${info.sdkCodename})")
                        InfoDetailRow("Waktu Aktif (Uptime)", info.uptimeFormatted)
                    }
                }

                // 2. CPU & Prosesor
                item {
                    DeviceSectionCard(
                        title = "Prosesor & Arsitektur CPU",
                        icon = Icons.Default.Speed,
                        accentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        InfoDetailRow("Arsitektur CPU (ABI)", info.cpuAbi)
                        InfoDetailRow("Jumlah Core CPU", "${info.cpuCores} Cores Aktif")
                    }
                }

                // 3. Memori RAM
                item {
                    val ramTotalGb = info.totalRamBytes / (1024.0 * 1024.0 * 1024.0)
                    val ramUsedGb = info.usedRamBytes / (1024.0 * 1024.0 * 1024.0)
                    val ramAvailGb = info.availRamBytes / (1024.0 * 1024.0 * 1024.0)
                    val ramPercent = if (info.totalRamBytes > 0) ((info.usedRamBytes.toDouble() / info.totalRamBytes) * 100).toInt() else 0

                    DeviceSectionCard(
                        title = "Memori RAM",
                        icon = Icons.Default.Memory,
                        accentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Penggunaan RAM",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$ramPercent% (${String.format(Locale.US, "%.2f", ramUsedGb)} / ${String.format(Locale.US, "%.2f", ramTotalGb)} GB)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { ramPercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.secondary,
                                strokeCap = StrokeCap.Round
                            )
                        }
                        InfoDetailRow("Total RAM", String.format(Locale.US, "%.2f GB", ramTotalGb))
                        InfoDetailRow("RAM Digunakan", String.format(Locale.US, "%.2f GB", ramUsedGb))
                        InfoDetailRow("RAM Tersedia", String.format(Locale.US, "%.2f GB", ramAvailGb))
                        InfoDetailRow("Status Memori", if (info.isLowMemory) "Peringatan: Memori Kritis" else "Optimal")
                    }
                }

                // 4. Penyimpanan Internal
                item {
                    val storageTotalGb = info.totalStorageBytes / (1024.0 * 1024.0 * 1024.0)
                    val storageUsedGb = info.usedStorageBytes / (1024.0 * 1024.0 * 1024.0)
                    val storageAvailGb = info.availStorageBytes / (1024.0 * 1024.0 * 1024.0)
                    val storagePercent = if (info.totalStorageBytes > 0) ((info.usedStorageBytes.toDouble() / info.totalStorageBytes) * 100).toInt() else 0

                    DeviceSectionCard(
                        title = "Penyimpanan Internal",
                        icon = Icons.Default.SdStorage,
                        accentColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Kapasitas Penyimpanan",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$storagePercent% (${String.format(Locale.US, "%.1f", storageUsedGb)} / ${String.format(Locale.US, "%.1f", storageTotalGb)} GB)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { storagePercent / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.tertiary,
                                strokeCap = StrokeCap.Round
                            )
                        }
                        InfoDetailRow("Total Kapasitas", String.format(Locale.US, "%.2f GB", storageTotalGb))
                        InfoDetailRow("Kapasitas Terpakai", String.format(Locale.US, "%.2f GB", storageUsedGb))
                        InfoDetailRow("Kapasitas Tersedia", String.format(Locale.US, "%.2f GB", storageAvailGb))
                    }
                }

                // 5. Baterai & Daya
                item {
                    DeviceSectionCard(
                        title = "Baterai & Pengisian Daya",
                        icon = Icons.Default.BatteryChargingFull,
                        accentColor = MaterialTheme.colorScheme.primary
                    ) {
                        InfoDetailRow("Persentase Baterai", "${info.batteryPercentage}%")
                        InfoDetailRow("Status Pengisian", info.batteryStatus)
                        InfoDetailRow("Sumber Daya", info.batteryPlugged)
                        InfoDetailRow("Kesehatan Baterai", info.batteryHealth)
                        InfoDetailRow("Suhu Baterai", "${info.batteryTemperatureC} °C")
                        InfoDetailRow("Tegangan Baterai", "${String.format(Locale.US, "%.2f", info.batteryVoltageV)} V")
                        InfoDetailRow("Teknologi Baterai", info.batteryTechnology)
                    }
                }

                // 6. Konektivitas & Jaringan
                item {
                    DeviceSectionCard(
                        title = "Konektivitas Jaringan & Nirkabel",
                        icon = Icons.Default.Wifi,
                        accentColor = StatusGreen
                    ) {
                        InfoDetailRow("Koneksi Aktif", info.networkType)
                        InfoDetailRow("Status Internet", if (info.isNetworkConnected) "Terhubung ke Internet" else "Tidak Terhubung")
                        InfoDetailRow("Status Wi-Fi", info.wifiStatusText)
                        InfoDetailRow("Status Bluetooth", info.bluetoothStatusText)
                    }
                }

                // 7. Layar & Tampilan
                item {
                    DeviceSectionCard(
                        title = "Layar & Resolusi Display",
                        icon = Icons.Default.Tv,
                        accentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        InfoDetailRow("Resolusi Layar", info.screenResolution)
                        InfoDetailRow("Kerapatan Piksel (DPI)", "${info.screenDensityDpi} DPI")
                        InfoDetailRow("Skala Densitas", "${info.screenDensityScale}x (${info.screenDensityBucket})")
                    }
                }
            }
        }

        // 8. Pemeriksaan Keamanan (Security Check)
        item {
            when (val sState = securityState) {
                is UiState.Success -> {
                    val sec = sState.data
                    DeviceSectionCard(
                        title = "Pemeriksaan Keamanan Sistem",
                        icon = Icons.Default.Security,
                        accentColor = MaterialTheme.colorScheme.primary
                    ) {
                        InfoDetailRow(
                            "Kunci Layar (Screen Lock)",
                            sec.screenLockStatusText,
                            badgeColor = if (sec.isScreenLockEnabled) StatusGreen else StatusRed
                        )
                        InfoDetailRow(
                            "Opsi Pengembang (Dev Options)",
                            sec.devOptionsStatusText,
                            badgeColor = if (!sec.isDevOptionsEnabled) StatusGreen else StatusOrange
                        )
                        InfoDetailRow(
                            "USB Debugging",
                            sec.usbDebuggingStatusText,
                            badgeColor = if (!sec.isUsbDebuggingEnabled) StatusGreen else StatusOrange
                        )

                        // Google Play Protect with official explanation and direct open button
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Status Google Play Protect",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StatusOrangeContainer
                                ) {
                                    Text(
                                        text = sec.playProtectStatusTitle,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusOrange,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text(
                                text = sec.playProtectExplanation,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedButton(
                                onClick = {
                                    openPlayProtectSettings(context)
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Buka Pengaturan Google Play Protect di Play Store", fontSize = 12.sp)
                            }
                        }
                    }
                }
                else -> {}
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun openPlayProtectSettings(context: Context) {
    try {
        // Try direct Play Protect Intent
        val intent = Intent("com.google.android.gms.security.settings.VerifyAppsSettingsActivity").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to Google Play Store app page
        try {
            val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.android.vending")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(playIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Tidak dapat membuka Google Play Protect", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun DeviceSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    PinkCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            content()
        }
    }
}

@Composable
private fun InfoDetailRow(
    label: String,
    value: String,
    badgeColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        if (badgeColor != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        } else {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
