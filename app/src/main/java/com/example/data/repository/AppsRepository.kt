package com.example.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import com.example.data.model.InstalledAppItem
import com.example.data.model.PermissionCheckItem
import com.example.data.model.PermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppsRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    suspend fun getInstalledApps(): Result<List<InstalledAppItem>> = withContext(Dispatchers.IO) {
        try {
            val rawPackages: List<PackageInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
            } else {
                packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            }

            val appList = ArrayList<InstalledAppItem>(rawPackages.size)

            for (pkg in rawPackages) {
                try {
                    val appInfo = pkg.applicationInfo ?: continue
                    val appName = try {
                        val label = appInfo.loadLabel(packageManager).toString()
                        if (label.isBlank()) pkg.packageName else label
                    } catch (e: Exception) {
                        pkg.packageName
                    }

                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    val versionName = pkg.versionName ?: "1.0"
                    val versionCode = PackageInfoCompat.getLongVersionCode(pkg)
                    val targetSdk = appInfo.targetSdkVersion
                    val minSdk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        appInfo.minSdkVersion
                    } else {
                        21
                    }

                    val hasLaunchIntent = try {
                        packageManager.getLaunchIntentForPackage(pkg.packageName) != null
                    } catch (e: Exception) {
                        false
                    }

                    val requestedPermissions = pkg.requestedPermissions?.toList() ?: emptyList()
                    val totalRequested = requestedPermissions.size

                    // Check Camera
                    val cameraState = checkSinglePermission(pkg.packageName, requestedPermissions, Manifest.permission.CAMERA)

                    // Check Microphone
                    val micState = checkSinglePermission(pkg.packageName, requestedPermissions, Manifest.permission.RECORD_AUDIO)

                    // Check Location
                    val hasLocationReq = requestedPermissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) ||
                            requestedPermissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION)
                    val locationState = if (!hasLocationReq) {
                        PermissionState.NOT_REQUESTED
                    } else {
                        val fineGranted = packageManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, pkg.packageName) == PackageManager.PERMISSION_GRANTED
                        val coarseGranted = packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, pkg.packageName) == PackageManager.PERMISSION_GRANTED
                        if (fineGranted || coarseGranted) PermissionState.ALLOWED else PermissionState.NOT_ALLOWED
                    }

                    // Check Notifications
                    val notificationState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        checkSinglePermission(pkg.packageName, requestedPermissions, Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // In Android 12 and below, notifications were automatically granted unless user turned them off in system settings
                        if (targetSdk < 33) {
                            PermissionState.ALLOWED
                        } else {
                            PermissionState.NOT_REQUESTED
                        }
                    }

                    // Count granted permissions
                    var grantedCount = 0
                    if (pkg.requestedPermissionsFlags != null && pkg.requestedPermissions != null) {
                        for (i in pkg.requestedPermissionsFlags!!.indices) {
                            val flag = pkg.requestedPermissionsFlags!![i]
                            if ((flag and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                                grantedCount++
                            }
                        }
                    } else {
                        for (p in requestedPermissions) {
                            if (packageManager.checkPermission(p, pkg.packageName) == PackageManager.PERMISSION_GRANTED) {
                                grantedCount++
                            }
                        }
                    }

                    appList.add(
                        InstalledAppItem(
                            packageName = pkg.packageName,
                            appName = appName,
                            versionName = versionName,
                            versionCode = versionCode,
                            targetSdk = targetSdk,
                            minSdk = minSdk,
                            isSystemApp = isSystem,
                            firstInstallTime = pkg.firstInstallTime,
                            lastUpdateTime = pkg.lastUpdateTime,
                            hasLaunchIntent = hasLaunchIntent,
                            cameraState = cameraState,
                            micState = micState,
                            locationState = locationState,
                            notificationState = notificationState,
                            allRequestedPermissions = requestedPermissions,
                            grantedPermissionsCount = grantedCount,
                            totalRequestedPermissionsCount = totalRequested
                        )
                    )
                } catch (e: Exception) {
                    // Skip unreadable package safely
                    continue
                }
            }

            // Sort alphabetically by app name
            appList.sortBy { it.appName.lowercase() }
            Result.success(appList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun checkSinglePermission(
        packageName: String,
        requestedList: List<String>,
        permission: String
    ): PermissionState {
        if (!requestedList.contains(permission)) {
            return PermissionState.NOT_REQUESTED
        }
        return try {
            val check = packageManager.checkPermission(permission, packageName)
            if (check == PackageManager.PERMISSION_GRANTED) {
                PermissionState.ALLOWED
            } else {
                PermissionState.NOT_ALLOWED
            }
        } catch (e: Exception) {
            PermissionState.RESTRICTED_BY_ANDROID
        }
    }

    fun getAppIcon(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    fun getDetailedPermissions(app: InstalledAppItem): List<PermissionCheckItem> {
        val list = mutableListOf<PermissionCheckItem>()

        // 1. Camera
        list.add(
            PermissionCheckItem(
                permissionId = Manifest.permission.CAMERA,
                title = "Kamera (Camera)",
                state = app.cameraState,
                note = when (app.cameraState) {
                    PermissionState.ALLOWED -> "Aplikasi memiliki akses untuk mengambil foto/video."
                    PermissionState.NOT_ALLOWED -> "Aplikasi meminta izin kamera, namun belum/tidak diizinkan oleh sistem."
                    PermissionState.NOT_REQUESTED -> "Aplikasi ini tidak meminta izin kamera dalam manifestnya."
                    PermissionState.RESTRICTED_BY_ANDROID -> "Status tidak dapat diperiksa oleh aplikasi ini karena pembatasan Android."
                }
            )
        )

        // 2. Microphone
        list.add(
            PermissionCheckItem(
                permissionId = Manifest.permission.RECORD_AUDIO,
                title = "Mikrofon (Microphone)",
                state = app.micState,
                note = when (app.micState) {
                    PermissionState.ALLOWED -> "Aplikasi memiliki izin merekam audio/suara."
                    PermissionState.NOT_ALLOWED -> "Aplikasi meminta izin mikrofon, tetapi belum diizinkan pengguna/sistem."
                    PermissionState.NOT_REQUESTED -> "Aplikasi ini tidak meminta izin mikrofon dalam manifestnya."
                    PermissionState.RESTRICTED_BY_ANDROID -> "Status tidak dapat diperiksa oleh aplikasi ini karena pembatasan Android."
                }
            )
        )

        // 3. Location
        list.add(
            PermissionCheckItem(
                permissionId = Manifest.permission.ACCESS_FINE_LOCATION,
                title = "Lokasi Presisi (Location)",
                state = app.locationState,
                note = when (app.locationState) {
                    PermissionState.ALLOWED -> "Aplikasi memiliki izin membaca lokasi GPS/jaringan."
                    PermissionState.NOT_ALLOWED -> "Izin lokasi diminta tetapi saat ini ditolak atau belum diizinkan."
                    PermissionState.NOT_REQUESTED -> "Aplikasi ini tidak meminta izin lokasi."
                    PermissionState.RESTRICTED_BY_ANDROID -> "Status tidak dapat diperiksa oleh aplikasi ini karena pembatasan Android."
                }
            )
        )

        // 4. Notifications
        list.add(
            PermissionCheckItem(
                permissionId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else "android.permission.NOTIFICATIONS_LEGACY",
                title = "Notifikasi (Notifications)",
                state = app.notificationState,
                note = when (app.notificationState) {
                    PermissionState.ALLOWED -> if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        "Diizinkan secara otomatis oleh sistem Android (< Android 13)."
                    } else {
                        "Aplikasi memiliki izin mengirim notifikasi di status bar."
                    }
                    PermissionState.NOT_ALLOWED -> "Izin notifikasi ditolak oleh pengguna atau sistem."
                    PermissionState.NOT_REQUESTED -> "Aplikasi tidak meminta izin notifikasi runtime."
                    PermissionState.RESTRICTED_BY_ANDROID -> "Status tidak dapat diperiksa oleh aplikasi ini karena pembatasan Android."
                }
            )
        )

        return list
    }
}
