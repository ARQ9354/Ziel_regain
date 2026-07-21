package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (Any) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(SurfaceWhite)
            .border(1.dp, Slate100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentRoute?.contains("DashboardRoute") == true,
                onClick = { onNavigate(DashboardRoute) }
            )
            BottomNavItem(
                icon = Icons.Default.CenterFocusStrong,
                label = "Focus",
                isSelected = currentRoute?.contains("FocusRoute") == true,
                onClick = { onNavigate(FocusRoute) }
            )
            BottomNavItem(
                icon = Icons.Default.CalendarMonth,
                label = "Planner",
                isSelected = currentRoute?.contains("PlannerRoute") == true,
                onClick = { onNavigate(PlannerRoute) }
            )
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                label = "Analytics",
                isSelected = currentRoute?.contains("StatsRoute") == true,
                onClick = { onNavigate(StatsRoute) }
            )
            BottomNavItem(
                icon = Icons.Default.Star,
                label = "AI",
                isSelected = currentRoute?.contains("LabsRoute") == true,
                onClick = { onNavigate(LabsRoute) }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        ).padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Indigo600 else Slate400,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) Indigo600 else Slate400,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
