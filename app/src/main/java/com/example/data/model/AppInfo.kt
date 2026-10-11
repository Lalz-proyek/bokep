package com.example.data.model

enum class PermissionState {
    ALLOWED,
    NOT_ALLOWED,
    NOT_REQUESTED,
    RESTRICTED_BY_ANDROID
}

data class PermissionCheckItem(
    val permissionId: String,
    val title: String,
    val state: PermissionState,
    val note: String
)

data class InstalledAppItem(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val targetSdk: Int,
    val minSdk: Int,
    val isSystemApp: Boolean,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val hasLaunchIntent: Boolean,
    val cameraState: PermissionState,
    val micState: PermissionState,
    val locationState: PermissionState,
    val notificationState: PermissionState,
    val allRequestedPermissions: List<String>,
    val grantedPermissionsCount: Int,
    val totalRequestedPermissionsCount: Int
)

enum class AppFilterType {
    ALL,
    WITH_PERMISSIONS,
    SYSTEM,
    USER
}
