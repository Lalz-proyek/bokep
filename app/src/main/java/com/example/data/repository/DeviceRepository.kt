package com.example.data.repository

import android.app.ActivityManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import com.example.data.model.DeviceInfo
import com.example.data.model.QuickDeviceSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DeviceRepository(private val context: Context) {

    fun getDeviceInfo(): Result<DeviceInfo> {
        return try {
            val brand = Build.BRAND.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            val manufacturer = Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            val model = Build.MODEL
            val deviceCodename = Build.DEVICE
            val product = Build.PRODUCT
            val hardware = Build.HARDWARE
            val androidVersion = Build.VERSION.RELEASE
            val sdkInt = Build.VERSION.SDK_INT
            val sdkCodename = Build.VERSION.CODENAME

            val deviceName = try {
                val name1 = Settings.Global.getString(context.contentResolver, "device_name")
                val name2 = if (name1.isNullOrBlank()) Settings.Secure.getString(context.contentResolver, "bluetooth_name") else name1
                if (!name2.isNullOrBlank()) name2 else "$manufacturer $model"
            } catch (e: Exception) {
                "$manufacturer $model"
            }

            val cpuAbi = if (Build.SUPPORTED_ABIS.isNotEmpty()) {
                Build.SUPPORTED_ABIS.joinToString(", ")
            } else {
                "Unknown"
            }
            val cpuCores = Runtime.getRuntime().availableProcessors()

            // Memory Info
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memInfo)
            val totalRam = memInfo.totalMem
            val availRam = memInfo.availMem
            val usedRam = totalRam - availRam
            val isLowMem = memInfo.lowMemory

            // Storage Info
            val dataDir = Environment.getDataDirectory()
            val statFs = StatFs(dataDir.path)
            val blockSize = statFs.blockSizeLong
            val totalBlocks = statFs.blockCountLong
            val availBlocks = statFs.availableBlocksLong
            val totalStorage = totalBlocks * blockSize
            val availStorage = availBlocks * blockSize
            val usedStorage = totalStorage - availStorage

            // Battery Info via sticky broadcast
            val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryIntent = context.registerReceiver(null, batteryFilter)

            val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = if (level >= 0 && scale > 0) (level * 100) / scale else 0

            val rawStatus = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val batteryStatus = when (rawStatus) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Sedang Mengisi Daya (Charging)"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Sedang Dipakai (Discharging)"
                BatteryManager.BATTERY_STATUS_FULL -> "Penuh (Full 100%)"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Tidak Mengisi (Not Charging)"
                else -> "Status Tidak Diketahui"
            }

            val plugged = batteryIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
            val batteryPlugged = when (plugged) {
                BatteryManager.BATTERY_PLUGGED_AC -> "Pengisi Daya Dinding (AC)"
                BatteryManager.BATTERY_PLUGGED_USB -> "Kabel USB"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Nirkabel (Wireless)"
                else -> "Tidak Terhubung Pengisi Daya"
            }

            val rawHealth = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
            val batteryHealth = when (rawHealth) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "Baik (Good)"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Panas Berlebih (Overheat)"
                BatteryManager.BATTERY_HEALTH_DEAD -> "Rusak (Dead)"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Tegangan Berlebih"
                else -> "Normal"
            }

            val tempTenths = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
            val batteryTempC = tempTenths / 10.0f
            val rawVoltageMv = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
            val batteryVoltageV = rawVoltageMv / 1000.0f
            val batteryTechnology = batteryIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

            // Network Info
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

            var networkType = "Tidak Terhubung"
            var isConnected = false
            var isWifi = false

            if (capabilities != null) {
                isConnected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    networkType = "Wi-Fi"
                    isWifi = true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    networkType = "Data Seluler (Cellular)"
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    networkType = "Ethernet"
                } else {
                    networkType = "Jaringan Lainnya"
                }
            }

            // Wi-Fi Status
            val wifiStatusText = try {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                if (wifiManager != null) {
                    if (wifiManager.isWifiEnabled) {
                        if (isWifi) "Wi-Fi Aktif & Terhubung" else "Wi-Fi Aktif (Belum Terhubung)"
                    } else {
                        "Wi-Fi Nonaktif"
                    }
                } else {
                    if (isWifi) "Terhubung ke Wi-Fi" else "Tidak Diketahui"
                }
            } catch (e: Exception) {
                if (isWifi) "Wi-Fi Terhubung" else "Tidak dapat diakses oleh Android"
            }

            // Bluetooth Status
            val bluetoothStatusText = try {
                val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                val adapter = bluetoothManager?.adapter
                if (adapter == null) {
                    "Perangkat Tidak Mendukung Bluetooth"
                } else {
                    if (adapter.isEnabled) "Bluetooth Aktif (ON)" else "Bluetooth Nonaktif (OFF)"
                }
            } catch (e: SecurityException) {
                "Izin Bluetooth Diperlukan oleh Android 12+"
            } catch (e: Exception) {
                "Tidak dapat diakses oleh Android"
            }

            // Screen Display
            val displayMetrics = context.resources.displayMetrics
            val resolution = "${displayMetrics.widthPixels} × ${displayMetrics.heightPixels} px"
            val densityDpi = displayMetrics.densityDpi
            val densityScale = displayMetrics.density
            val densityBucket = when (densityDpi) {
                in 0..120 -> "ldpi"
                in 121..160 -> "mdpi"
                in 161..240 -> "hdpi"
                in 241..320 -> "xhdpi"
                in 321..480 -> "xxhdpi"
                in 481..640 -> "xxxhdpi"
                else -> "${densityDpi}dpi"
            }

            // Time & Uptime
            val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale("id", "ID"))
            val systemTimeFormatted = dateFormat.format(Date())
            val systemTimeZone = TimeZone.getDefault().displayName

            val uptimeMillis = SystemClock.elapsedRealtime()
            val uptimeSeconds = uptimeMillis / 1000
            val hours = uptimeSeconds / 3600
            val minutes = (uptimeSeconds % 3600) / 60
            val uptimeFormatted = "${hours}j ${minutes}m"

            Result.success(
                DeviceInfo(
                    brand = brand,
                    manufacturer = manufacturer,
                    model = model,
                    deviceCodename = deviceCodename,
                    product = product,
                    hardware = hardware,
                    androidVersion = androidVersion,
                    sdkInt = sdkInt,
                    sdkCodename = sdkCodename,
                    deviceName = deviceName,
                    cpuAbi = cpuAbi,
                    cpuCores = cpuCores,
                    totalRamBytes = totalRam,
                    availRamBytes = availRam,
                    usedRamBytes = usedRam,
                    isLowMemory = isLowMem,
                    totalStorageBytes = totalStorage,
                    availStorageBytes = availStorage,
                    usedStorageBytes = usedStorage,
                    batteryPercentage = batteryPct,
                    batteryStatus = batteryStatus,
                    batteryPlugged = batteryPlugged,
                    batteryHealth = batteryHealth,
                    batteryTemperatureC = batteryTempC,
                    batteryVoltageV = batteryVoltageV,
                    batteryTechnology = batteryTechnology,
                    networkType = networkType,
                    isNetworkConnected = isConnected,
                    isWifiConnected = isWifi,
                    wifiStatusText = wifiStatusText,
                    bluetoothStatusText = bluetoothStatusText,
                    screenResolution = resolution,
                    screenDensityDpi = densityDpi,
                    screenDensityScale = densityScale,
                    screenDensityBucket = densityBucket,
                    systemTimeFormatted = systemTimeFormatted,
                    systemTimeZone = systemTimeZone,
                    uptimeFormatted = uptimeFormatted
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getQuickSummary(deviceInfo: DeviceInfo): QuickDeviceSummary {
        val totalRamGb = deviceInfo.totalRamBytes / (1024.0 * 1024.0 * 1024.0)
        val usedRamGb = deviceInfo.usedRamBytes / (1024.0 * 1024.0 * 1024.0)
        val ramUsedPercent = if (deviceInfo.totalRamBytes > 0) {
            ((deviceInfo.usedRamBytes.toDouble() / deviceInfo.totalRamBytes.toDouble()) * 100).toInt()
        } else 0
        val ramFreeGb = String.format(Locale.US, "%.1f GB", (deviceInfo.availRamBytes / (1024.0 * 1024.0 * 1024.0)))

        val availStorageGb = deviceInfo.availStorageBytes / (1024.0 * 1024.0 * 1024.0)
        val totalStorageGb = deviceInfo.totalStorageBytes / (1024.0 * 1024.0 * 1024.0)
        val storageFreeFormatted = String.format(Locale.US, "%.1f GB", availStorageGb)
        val storageTotalFormatted = String.format(Locale.US, "%.1f GB", totalStorageGb)

        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = timeFormat.format(Date())

        val isCharging = deviceInfo.batteryStatus.contains("Charging", ignoreCase = true)

        return QuickDeviceSummary(
            deviceName = deviceInfo.deviceName,
            androidVersion = "Android ${deviceInfo.androidVersion} (API ${deviceInfo.sdkInt})",
            batteryPercentage = deviceInfo.batteryPercentage,
            isBatteryCharging = isCharging,
            ramUsedPercent = ramUsedPercent,
            ramFreeGb = ramFreeGb,
            storageFreeGb = storageFreeFormatted,
            storageTotalGb = storageTotalFormatted,
            networkStatus = deviceInfo.networkType,
            bluetoothStatus = deviceInfo.bluetoothStatusText,
            currentTime = currentTime
        )
    }
}
