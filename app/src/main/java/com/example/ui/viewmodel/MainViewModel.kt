package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppFilterType
import com.example.data.model.DeviceInfo
import com.example.data.model.InstalledAppItem
import com.example.data.model.QuickDeviceSummary
import com.example.data.model.SecurityCheckInfo
import com.example.data.model.ThemeMode
import com.example.data.model.UserPreferences
import com.example.data.repository.AppsRepository
import com.example.data.repository.DeviceRepository
import com.example.data.repository.SecurityRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val deviceRepository = DeviceRepository(application)
    private val appsRepository = AppsRepository(application)
    private val securityRepository = SecurityRepository(application)
    private val settingsRepository = SettingsRepository(application)

    // Current navigation tab: 0=Home, 1=Device, 2=Permissions, 3=Settings
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Device Info State
    private val _deviceInfoState = MutableStateFlow<UiState<DeviceInfo>>(UiState.Loading)
    val deviceInfoState: StateFlow<UiState<DeviceInfo>> = _deviceInfoState.asStateFlow()

    // Quick Summary State (derived from DeviceInfo)
    val quickSummary: StateFlow<QuickDeviceSummary?> = _deviceInfoState.combine(_deviceInfoState) { state, _ ->
        if (state is UiState.Success) {
            deviceRepository.getQuickSummary(state.data)
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Installed Apps State
    private val _installedAppsState = MutableStateFlow<UiState<List<InstalledAppItem>>>(UiState.Loading)
    val installedAppsState: StateFlow<UiState<List<InstalledAppItem>>> = _installedAppsState.asStateFlow()

    // Security Check State
    private val _securityState = MutableStateFlow<UiState<SecurityCheckInfo>>(UiState.Loading)
    val securityState: StateFlow<UiState<SecurityCheckInfo>> = _securityState.asStateFlow()

    // Search and Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow(AppFilterType.ALL)
    val filterType: StateFlow<AppFilterType> = _filterType.asStateFlow()

    // Selected App for Detail Dialog
    private val _selectedApp = MutableStateFlow<InstalledAppItem?>(null)
    val selectedApp: StateFlow<InstalledAppItem?> = _selectedApp.asStateFlow()

    // User Preferences (Theme & Auto-Refresh)
    val userPreferences: StateFlow<UserPreferences> = settingsRepository.userPreferences

    // Filtered Apps List
    val filteredApps: StateFlow<List<InstalledAppItem>> = combine(
        _installedAppsState,
        _searchQuery,
        _filterType
    ) { appsState, query, filter ->
        if (appsState !is UiState.Success) {
            emptyList()
        } else {
            val all = appsState.data
            all.filter { app ->
                // Apply search filter
                val matchesQuery = if (query.isBlank()) {
                    true
                } else {
                    app.appName.contains(query, ignoreCase = true) ||
                            app.packageName.contains(query, ignoreCase = true)
                }

                // Apply category filter
                val matchesFilter = when (filter) {
                    AppFilterType.ALL -> true
                    AppFilterType.WITH_PERMISSIONS -> app.totalRequestedPermissionsCount > 0
                    AppFilterType.SYSTEM -> app.isSystemApp
                    AppFilterType.USER -> !app.isSystemApp
                }

                matchesQuery && matchesFilter
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var autoRefreshJob: Job? = null

    init {
        loadAll()
        observeAutoRefresh()
    }

    fun selectTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    fun loadAll() {
        refreshDeviceInfo()
        loadInstalledApps()
    }

    fun refreshDeviceInfo() {
        viewModelScope.launch {
            _deviceInfoState.value = UiState.Loading
            val result = deviceRepository.getDeviceInfo()
            result.onSuccess { info ->
                _deviceInfoState.value = UiState.Success(info)
            }.onFailure { err ->
                _deviceInfoState.value = UiState.Error(
                    err.localizedMessage ?: "Data perangkat tidak dapat dibaca. Silakan coba lagi."
                )
            }
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _installedAppsState.value = UiState.Loading
            val result = appsRepository.getInstalledApps()
            result.onSuccess { apps ->
                _installedAppsState.value = UiState.Success(apps)
                // Also update security check based on audited apps
                val secInfo = securityRepository.checkSecurity(apps)
                _securityState.value = UiState.Success(secInfo)
            }.onFailure { err ->
                _installedAppsState.value = UiState.Error(
                    err.localizedMessage ?: "Daftar aplikasi tidak dapat dibaca oleh Android. Silakan coba lagi."
                )
                // Security check with empty apps
                val secInfo = securityRepository.checkSecurity(emptyList())
                _securityState.value = UiState.Success(secInfo)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(filter: AppFilterType) {
        _filterType.value = filter
    }

    fun selectApp(app: InstalledAppItem?) {
        _selectedApp.value = app
    }

    fun setThemeMode(mode: ThemeMode) {
        settingsRepository.setThemeMode(mode)
    }

    fun setAutoRefresh(enabled: Boolean) {
        settingsRepository.setAutoRefresh(enabled)
    }

    fun clearLocalCache(): String {
        val bytes = settingsRepository.clearLocalCache()
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb >= 1.0) {
            String.format(Locale.US, "%.2f MB berhasil dibersihkan", mb)
        } else {
            val kb = bytes / 1024.0
            String.format(Locale.US, "%.1f KB berhasil dibersihkan", kb)
        }
    }

    fun getDetailedPermissions(app: InstalledAppItem) = appsRepository.getDetailedPermissions(app)

    fun getAppIcon(packageName: String) = appsRepository.getAppIcon(packageName)

    private fun observeAutoRefresh() {
        viewModelScope.launch {
            userPreferences.collect { prefs ->
                autoRefreshJob?.cancel()
                if (prefs.autoRefresh) {
                    autoRefreshJob = viewModelScope.launch {
                        while (true) {
                            delay(10_000)
                            // Refresh device info quietly
                            val result = deviceRepository.getDeviceInfo()
                            if (result.isSuccess) {
                                _deviceInfoState.value = UiState.Success(result.getOrThrow())
                            }
                        }
                    }
                }
            }
        }
    }
}
