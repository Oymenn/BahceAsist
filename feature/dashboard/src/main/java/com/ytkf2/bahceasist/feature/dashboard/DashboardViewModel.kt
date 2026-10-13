package com.ytkf2.bahceasist.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytkf2.bahceasist.core.common.result.AppResult
import com.ytkf2.bahceasist.domain.model.Advice
import com.ytkf2.bahceasist.domain.model.GardenTask
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.model.WeatherSnapshot
import com.ytkf2.bahceasist.domain.usecase.advice.EvaluateFieldAdviceUseCase
import com.ytkf2.bahceasist.domain.usecase.location.ObserveSelectedLocationUseCase
import com.ytkf2.bahceasist.domain.usecase.location.SearchLocationUseCase
import com.ytkf2.bahceasist.domain.usecase.location.SelectLocationUseCase
import com.ytkf2.bahceasist.domain.usecase.tasks.DeleteTaskUseCase
import com.ytkf2.bahceasist.domain.usecase.tasks.ObserveTasksUseCase
import com.ytkf2.bahceasist.domain.usecase.tasks.SaveTaskUseCase
import com.ytkf2.bahceasist.domain.usecase.tasks.ToggleTaskUseCase
import com.ytkf2.bahceasist.domain.usecase.weather.ObserveWeatherUseCase
import com.ytkf2.bahceasist.domain.usecase.weather.RefreshWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val selectedLocation: Location? = null,
    val weather: WeatherSnapshot? = null,
    val advices: List<Advice> = emptyList(),
    val tasks: List<GardenTask> = emptyList(),
    val locationSearchResults: List<Location> = emptyList(),
    val isSearchingLocation: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val observeWeatherUseCase: ObserveWeatherUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val observeSelectedLocationUseCase: ObserveSelectedLocationUseCase,
    private val searchLocationUseCase: SearchLocationUseCase,
    private val selectLocationUseCase: SelectLocationUseCase,
    private val evaluateFieldAdviceUseCase: EvaluateFieldAdviceUseCase,
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val toggleTaskUseCase: ToggleTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // 1. Canlı Konum ve Hava Durumu Akışı
        viewModelScope.launch {
            observeSelectedLocationUseCase().collectLatest { location ->
                _uiState.value = _uiState.value.copy(selectedLocation = location)
                if (location != null) {
                    refreshWeather(location)
                    observeWeatherUseCase(location).collectLatest { weatherSnapshot ->
                        val advices = if (weatherSnapshot != null) {
                            evaluateFieldAdviceUseCase(weatherSnapshot, System.currentTimeMillis())
                        } else {
                            emptyList()
                        }

                        _uiState.value = _uiState.value.copy(
                            weather = weatherSnapshot,
                            advices = advices,
                            isLoading = false
                        )
                    }
                }
            }
        }

        // 2. Canlı Görevler Akışı (SQLite)
        viewModelScope.launch {
            observeTasksUseCase().collectLatest { taskList ->
                _uiState.value = _uiState.value.copy(tasks = taskList)
            }
        }
    }

    fun refresh() {
        val loc = _uiState.value.selectedLocation ?: return
        refreshWeather(loc)
    }

    private fun refreshWeather(location: Location) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = refreshWeatherUseCase(location)
            when (result) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                is AppResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Hava durumu güncellenemedi (çevrimdışı önbellek kullanılıyor)"
                    )
                }
            }
        }
    }

    // Konum Arama ve Değiştirme
    fun searchLocations(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearchingLocation = true)
            when (val res = searchLocationUseCase(query)) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        locationSearchResults = res.data,
                        isSearchingLocation = false
                    )
                }
                is AppResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        locationSearchResults = emptyList(),
                        isSearchingLocation = false
                    )
                }
            }
        }
    }

    fun selectLocation(location: Location) {
        viewModelScope.launch {
            selectLocationUseCase(location)
            _uiState.value = _uiState.value.copy(
                locationSearchResults = emptyList(),
                isSearchingLocation = false
            )
        }
    }

    // Dinamik Görev Yönetimi
    fun addTask(title: String, dueTime: String, category: String = "Genel") {
        viewModelScope.launch {
            saveTaskUseCase(
                GardenTask(
                    title = title,
                    dueTime = dueTime,
                    category = category,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleTask(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            toggleTaskUseCase(id, isCompleted)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            deleteTaskUseCase(id)
        }
    }
}
