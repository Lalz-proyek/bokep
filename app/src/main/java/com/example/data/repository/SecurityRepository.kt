package com.example.data.repository

import android.app.KeyguardManager
import android.content.Context
import android.provider.Settings
import com.example.data.model.InstalledAppItem
import com.example.data.model.PermissionState
import com.example.data.model.SecurityCheckInfo

class SecurityRepository(private val context: Context) {

    fun checkSecurity(installedApps: List<InstalledAppItem>): SecurityCheckInfo {
        // Screen Lock
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isScreenLockSecure = try {
            keyguardManager?.isDeviceSecure == true
        } catch (e: Exception) {
            false
        }
        val screenLockStatusText = if (isScreenLockSecure) {
            "Terkunci Aman (PIN/Pola/Biometrik Aktif)"
        } else {
            "Tidak Ada Kunci Layar (Perangkat Tidak Dikunci)"
        }

        // Developer Options
        val isDevMode = try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
        val devOptionsStatusText = if (isDevMode) {
            "Opsi Pengembang Aktif"
        } else {
            "Opsi Pengembang Nonaktif (Standar)"
        }

        // USB Debugging
        val isAdb = try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }
        val usbDebuggingStatusText = if (isAdb) {
            "USB Debugging Sedang Aktif"
        } else {
            "USB Debugging Nonaktif (Aman)"
        }

        // Play Protect explanation
        val playProtectTitle = "Tidak dapat diakses oleh Android"
        val playProtectExplanation = "Android tidak menyediakan public API pihak ketiga untuk membaca status internal Google Play Protect demi alasan integritas sistem. Anda dapat memverifikasi statusnya secara langsung di aplikasi Google Play Store."

        // App permissions stats
        val appsWithCamera = installedApps.count { it.cameraState == PermissionState.ALLOWED }
        val appsWithMic = installedApps.count { it.micState == PermissionState.ALLOWED }
        val appsWithLocation = installedApps.count { it.locationState == PermissionState.ALLOWED }

        return SecurityCheckInfo(
            isScreenLockEnabled = isScreenLockSecure,
            screenLockStatusText = screenLockStatusText,
            isDevOptionsEnabled = isDevMode,
            devOptionsStatusText = devOptionsStatusText,
            isUsbDebuggingEnabled = isAdb,
            usbDebuggingStatusText = usbDebuggingStatusText,
            playProtectStatusTitle = playProtectTitle,
            playProtectExplanation = playProtectExplanation,
            appsWithCameraCount = appsWithCamera,
            appsWithMicCount = appsWithMic,
            appsWithLocationCount = appsWithLocation,
            totalAppsAudited = installedApps.size
        )
    }
}
