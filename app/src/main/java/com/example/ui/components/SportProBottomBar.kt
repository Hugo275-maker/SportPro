package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun SportProBottomBar(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val userRole = currentUser?.rol ?: UserRole.DT

    // US-003: Define items based strictly on role
    val items = when (userRole) {
        UserRole.DT -> listOf(
            BottomNavItem(Screen.HOME, "Inicio", Icons.Default.Home),
            BottomNavItem(Screen.TEAMS, "Equipo", Icons.Default.Groups),
            BottomNavItem(Screen.TRAINING, "Entrenamientos", Icons.Default.FitnessCenter),
            BottomNavItem(Screen.MATCHES, "Partidos", Icons.Default.SportsSoccer),
            BottomNavItem(Screen.COMMUNITY, "Comunidad", Icons.Default.Forum)
        )
        UserRole.JUG -> listOf(
            BottomNavItem(Screen.HOME, "Inicio", Icons.Default.Home),
            BottomNavItem(Screen.MY_PROFILE, "Mi perfil", Icons.Default.Person),
            BottomNavItem(Screen.TRAINING, "Entrenamientos", Icons.Default.FitnessCenter),
            BottomNavItem(Screen.MATCHES, "Partidos", Icons.Default.SportsSoccer),
            BottomNavItem(Screen.COMMUNITY, "Comunidad", Icons.Default.Forum)
        )
        UserRole.PAD -> listOf(
            BottomNavItem(Screen.HOME, "Inicio", Icons.Default.Home),
            BottomNavItem(Screen.MY_CHILD, "Mi hijo", Icons.Default.FamilyRestroom),
            BottomNavItem(Screen.MONTHLY_FEES, "Mensualidades", Icons.Default.Payments),
            BottomNavItem(Screen.COMMUNITY, "Comunidad", Icons.Default.Forum)
        )
        UserRole.ADM -> listOf(
            BottomNavItem(Screen.HOME, "Inicio", Icons.Default.Home),
            BottomNavItem(Screen.ACADEMY, "Academia", Icons.Default.Shield),
            BottomNavItem(Screen.USERS_TEAMS, "Usuarios", Icons.Default.ManageAccounts),
            BottomNavItem(Screen.EVENT_CATALOG, "Catálogo", Icons.Default.ListAlt),
            BottomNavItem(Screen.COMMUNITY, "Comunidad", Icons.Default.Forum)
        )
    }

    NavigationBar(
        modifier = modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { viewModel.navigateTo(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
