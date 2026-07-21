package com.example.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.gamification.GamificationScreen
import com.example.ui.gamification.GamificationViewModel
import com.example.ui.stats.StatsScreen
import com.example.ui.labs.LabsScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.add.AddSessionScreen
import com.example.ui.focus.FocusScreen
import com.example.ui.planner.PlannerScreen
import com.example.ui.block.AppBlockerScreen
import com.example.ui.theme.*
import kotlinx.serialization.Serializable

@Serializable object DashboardRoute
@Serializable object StatsRoute
@Serializable object LabsRoute
@Serializable object SettingsRoute
@Serializable object GamificationRoute
@Serializable object AddSessionRoute
@Serializable object FocusRoute
@Serializable object PlannerRoute
@Serializable object AppBlockerRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    var showFabSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showFabSheet = true },
                containerColor = Indigo600,
                contentColor = SurfaceWhite
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<DashboardRoute> { DashboardScreen(onNavigateToFocus = { navController.navigate(FocusRoute) }, onNavigateToPlanner = { navController.navigate(PlannerRoute) }, onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }, onNavigateToSettings = { navController.navigate(SettingsRoute) }, onNavigateToStats = { navController.navigate(StatsRoute) }, onNavigateToLabs = { navController.navigate(LabsRoute) }, onXpClick = { navController.navigate(GamificationRoute) }) }
            composable<StatsRoute> { StatsScreen() }
            composable<LabsRoute> { LabsScreen() }
            composable<GamificationRoute> {
                val context = androidx.compose.ui.platform.LocalContext.current.applicationContext as com.example.ZielApplication
                val viewModel: GamificationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = GamificationViewModel.provideFactory(context.container.usageRepository)
                    
                )
                GamificationScreen(viewModel = viewModel)
            }
            composable<SettingsRoute> { 
                SettingsScreen(
                    onNavigateToFocus = { navController.navigate(FocusRoute) },
                    onNavigateToPlanner = { navController.navigate(PlannerRoute) },
                    onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }
                ) 
            }
            composable<AddSessionRoute> { AddSessionScreen(onBack = { navController.popBackStack() }) }
            composable<FocusRoute> { FocusScreen(onNavigateBack = { navController.popBackStack() }) }
            composable<PlannerRoute> { PlannerScreen() }
            composable<AppBlockerRoute> { AppBlockerScreen() }
        }
        
        if (showFabSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFabSheet = false },
                sheetState = sheetState,
                containerColor = SurfaceWhite
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 8.dp)
                ) {
                    Text(
                        "Create New",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FabSheetItem(
                        icon = Icons.Default.CenterFocusStrong,
                        title = "Start Focus Session",
                        onClick = { 
                            showFabSheet = false
                            navController.navigate(FocusRoute)
                        }
                    )
                    FabSheetItem(
                        icon = Icons.Default.TaskAlt,
                        title = "Add Task",
                        onClick = { 
                            showFabSheet = false
                            navController.navigate(PlannerRoute)
                        }
                    )
                    val context = LocalContext.current
                    FabSheetItem(
                        icon = Icons.Default.NoteAdd,
                        title = "Quick Note",
                        onClick = { 
                            showFabSheet = false 
                            Toast.makeText(context, "Quick Note functionality coming soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                    FabSheetItem(
                        icon = Icons.Default.Event,
                        title = "Schedule Session",
                        onClick = { 
                            showFabSheet = false
                            navController.navigate(PlannerRoute)
                        }
                    )
                    FabSheetItem(
                        icon = Icons.Default.Loop,
                        title = "Add Habit",
                        onClick = { 
                            showFabSheet = false 
                            Toast.makeText(context, "Add Habit functionality coming soon", Toast.LENGTH_SHORT).show()
                        }
                    )
                    FabSheetItem(
                        icon = Icons.Default.OfflineBolt,
                        title = "Log Offline Activity",
                        onClick = { 
                            showFabSheet = false
                            navController.navigate(AddSessionRoute)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FabSheetItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Slate500,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Slate900
        )
    }
}
