package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ThemeMode
import com.example.data.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class SettingsRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("pink_cat_manager_prefs", Context.MODE_PRIVATE)

    private val _userPreferences = MutableStateFlow(loadPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val themeModeName = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val themeMode = try {
            ThemeMode.valueOf(themeModeName)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
        val autoRefresh = prefs.getBoolean(KEY_AUTO_REFRESH, false)
        return UserPreferences(
            themeMode = themeMode,
            autoRefresh = autoRefresh
        )
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _userPreferences.value = _userPreferences.value.copy(themeMode = mode)
    }

    fun setAutoRefresh(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_REFRESH, enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(autoRefresh = enabled)
    }

    fun clearLocalCache(): Long {
        val cacheDir = context.cacheDir
        val bytesDeleted = getFolderSize(cacheDir)
        deleteDirContent(cacheDir)
        return bytesDeleted
    }

    private fun getFolderSize(file: File?): Long {
        if (file == null || !file.exists()) return 0L
        var size = 0L
        val children = file.listFiles() ?: return 0L
        for (child in children) {
            size += if (child.isDirectory) getFolderSize(child) else child.length()
        }
        return size
    }

    private fun deleteDirContent(file: File?) {
        if (file == null || !file.exists()) return
        val children = file.listFiles() ?: return
        for (child in children) {
            if (child.isDirectory) {
                deleteDirContent(child)
            }
            child.delete()
        }
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_AUTO_REFRESH = "auto_refresh"
    }
}
