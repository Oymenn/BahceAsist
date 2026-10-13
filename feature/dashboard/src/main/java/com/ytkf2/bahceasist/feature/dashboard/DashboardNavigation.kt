package com.ytkf2.bahceasist.feature.dashboard

import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute

fun NavController.navigateToDashboard() {
    navigate(DashboardRoute)
}

fun NavGraphBuilder.dashboardScreen(
    onNavigateToAgenda: () -> Unit
) {
    composable<DashboardRoute> {
        val viewModel: DashboardViewModel = hiltViewModel()
        val uiState = viewModel.uiState.collectAsState().value

        DashboardScreen(
            uiState = uiState,
            onRefresh = { viewModel.refresh() },
            onNavigateToAgenda = onNavigateToAgenda,
            onSearchLocation = { viewModel.searchLocations(it) },
            onSelectLocation = { viewModel.selectLocation(it) },
            onToggleTask = { id, comp -> viewModel.toggleTask(id, comp) },
            onDeleteTask = { id -> viewModel.deleteTask(id) },
            onAddTask = { title, time, cat -> viewModel.addTask(title, time, cat) }
        )
    }
}
