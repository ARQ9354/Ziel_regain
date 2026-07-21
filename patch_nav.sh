#!/bin/bash
sed -i 's/import com.example.ui.dashboard.DashboardScreen/import com.example.ui.dashboard.DashboardScreen\nimport com.example.ui.gamification.GamificationScreen\nimport com.example.ui.gamification.GamificationViewModel/g' /app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt
sed -i 's/@Serializable object SettingsRoute/@Serializable object SettingsRoute\n@Serializable object GamificationRoute/g' /app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt

cat << 'INNER_EOF' > /tmp/gamification_nav.txt
            composable<GamificationRoute> {
                val viewModel: GamificationViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = GamificationViewModel.provideFactory(repository)
                )
                GamificationScreen(viewModel = viewModel)
            }
INNER_EOF

sed -i -e '/composable<SettingsRoute> {/r /tmp/gamification_nav.txt' /app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt

sed -i 's/DashboardScreen(onNavigateToFocus = { navController.navigate(FocusRoute) }, onNavigateToPlanner = { navController.navigate(PlannerRoute) }, onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) })/DashboardScreen(onNavigateToFocus = { navController.navigate(FocusRoute) }, onNavigateToPlanner = { navController.navigate(PlannerRoute) }, onNavigateToAppBlocker = { navController.navigate(AppBlockerRoute) }, onXpClick = { navController.navigate(GamificationRoute) })/g' /app/applet/app/src/main/java/com/example/ui/navigation/AppNavigation.kt
