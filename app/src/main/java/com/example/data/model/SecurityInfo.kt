package com.example.data.model

data class SecurityCheckInfo(
    val isScreenLockEnabled: Boolean,
    val screenLockStatusText: String,
    val isDevOptionsEnabled: Boolean,
    val devOptionsStatusText: String,
    val isUsbDebuggingEnabled: Boolean,
    val usbDebuggingStatusText: String,
    val playProtectStatusTitle: String,
    val playProtectExplanation: String,
    val appsWithCameraCount: Int,
    val appsWithMicCount: Int,
    val appsWithLocationCount: Int,
    val totalAppsAudited: Int
)

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val autoRefresh: Boolean = false
)
