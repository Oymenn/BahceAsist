package com.ytkf2.bahceasist.feature.notes

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object NotesRoute

fun NavController.navigateToNotes() {
    navigate(NotesRoute)
}

fun NavGraphBuilder.notesScreen() {
    composable<NotesRoute> {
        val viewModel: AgendaViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsState().value

        AgendaScreen(
            uiState = uiState,
            currentLocation = uiState.selectedLocation,
            onCategorySelected = { viewModel.filterByCategory(it) },
            onAddNote = { title, content, cat -> viewModel.addNote(title, content, cat) },
            onDeleteNote = { id -> viewModel.deleteNote(id) },
            onAddTask = { title, time, cat -> viewModel.addTask(title, time, cat) },
            onToggleTask = { id, comp -> viewModel.toggleTask(id, comp) },
            onDeleteTask = { id -> viewModel.deleteTask(id) },
            onSearchLocation = { viewModel.searchLocations(it) },
            onSelectLocation = { viewModel.selectLocation(it) },
            locationSearchResults = uiState.locationSearchResults,
            isSearchingLocation = uiState.isSearchingLocation
        )
    }
}
