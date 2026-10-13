package com.ytkf2.bahceasist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ytkf2.bahceasist.core.ui.components.BahcemBottomBar
import com.ytkf2.bahceasist.core.ui.components.BahcemTab
import com.ytkf2.bahceasist.core.ui.screens.PlantGuideScreen
import com.ytkf2.bahceasist.core.ui.screens.ProfileScreen
import com.ytkf2.bahceasist.core.ui.theme.BahceAsistTheme
import com.ytkf2.bahceasist.feature.dashboard.DashboardRoute
import com.ytkf2.bahceasist.feature.dashboard.dashboardScreen
import com.ytkf2.bahceasist.feature.notes.NotesRoute
import com.ytkf2.bahceasist.feature.notes.notesScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@Serializable
object PlantGuideRoute

@Serializable
object ProfileRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BahceAsistTheme {
                BahceAsistMainApp()
            }
        }
    }
}

@Composable
fun BahceAsistMainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val currentTab = when {
        currentDestination?.contains("DashboardRoute") == true -> BahcemTab.WEATHER
        currentDestination?.contains("NotesRoute") == true -> BahcemTab.AGENDA
        currentDestination?.contains("PlantGuideRoute") == true -> BahcemTab.PLANT_GUIDE
        currentDestination?.contains("ProfileRoute") == true -> BahcemTab.PROFILE
        else -> BahcemTab.WEATHER
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BahcemBottomBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    val targetRoute = when (tab) {
                        BahcemTab.WEATHER -> DashboardRoute
                        BahcemTab.AGENDA -> NotesRoute
                        BahcemTab.PLANT_GUIDE -> PlantGuideRoute
                        BahcemTab.PROFILE -> ProfileRoute
                    }
                    navController.navigate(targetRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            dashboardScreen(
                onNavigateToAgenda = {
                    navController.navigate(NotesRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            notesScreen()

            composable<PlantGuideRoute> {
                PlantGuideScreen()
            }

            composable<ProfileRoute> {
                ProfileScreen()
            }
        }
    }
}
