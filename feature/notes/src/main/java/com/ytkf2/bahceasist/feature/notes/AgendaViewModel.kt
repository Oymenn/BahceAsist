package com.ytkf2.bahceasist.feature.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytkf2.bahceasist.core.common.result.AppResult
import com.ytkf2.bahceasist.domain.model.FieldNote
import com.ytkf2.bahceasist.domain.model.GardenTask
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.model.NoteCategory
import com.ytkf2.bahceasist.domain.usecase.location.ObserveSelectedLocationUseCase
import com.ytkf2.bahceasist.domain.usecase.location.SearchLocationUseCase
import com.ytkf2.bahceasist.domain.usecase.location.SelectLocationUseCase
import com.ytkf2.bahceasist.domain.usecase.notes.DeleteNoteUseCase
import com.ytkf2.bahceasist.domain.usecase.notes.ObserveNotesUseCase
import com.ytkf2.bahceasist.domain.usecase.notes.SaveNoteUseCase
import com.ytkf2.bahceasist.domain.usecase.tasks.DeleteTaskUseCase
import com.ytkf2.bahceasist.domain.usecase.tasks.ObserveTasksUseCase
import com.ytkf2.bahceasist.domain.usecase.tasks.SaveTaskUseCase
import com.ytkf2.bahceasist.domain.usecase.tasks.ToggleTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AgendaUiState(
    val notes: List<FieldNote> = emptyList(),
    val tasks: List<GardenTask> = emptyList(),
    val selectedCategory: NoteCategory? = null,
    val selectedLocation: Location? = null,
    val locationSearchResults: List<Location> = emptyList(),
    val isSearchingLocation: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val observeNotesUseCase: ObserveNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val toggleTaskUseCase: ToggleTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val observeSelectedLocationUseCase: ObserveSelectedLocationUseCase,
    private val searchLocationUseCase: SearchLocationUseCase,
    private val selectLocationUseCase: SelectLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    private var observeNotesJob: Job? = null

    init {
        loadNotes(null)
        viewModelScope.launch {
            observeTasksUseCase().collectLatest { taskList ->
                _uiState.value = _uiState.value.copy(tasks = taskList)
            }
        }
        viewModelScope.launch {
            observeSelectedLocationUseCase().collectLatest { location ->
                _uiState.value = _uiState.value.copy(selectedLocation = location)
            }
        }
    }

    fun filterByCategory(category: NoteCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadNotes(category)
    }

    private fun loadNotes(category: NoteCategory?) {
        observeNotesJob?.cancel()
        observeNotesJob = viewModelScope.launch {
            observeNotesUseCase(category).collectLatest { noteList ->
                _uiState.value = _uiState.value.copy(notes = noteList)
            }
        }
    }

    fun addNote(title: String, content: String, category: NoteCategory) {
        viewModelScope.launch {
            val newNote = FieldNote(
                title = title,
                content = content,
                category = category,
                timestampMillis = System.currentTimeMillis()
            )
            saveNoteUseCase(newNote)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(id)
        }
    }

    fun addTask(title: String, dueTime: String, category: String) {
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
}
