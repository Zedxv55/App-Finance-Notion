package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.Screen
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.AddTransactionScreen
import com.example.ui.screens.AiAdvisorScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.DashboardScreen

@Composable
fun FinanceTrackerApp(viewModel: FinanceViewModel = viewModel()) {
    val navController = rememberNavController()

    Scaffold(
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center,
        bottomBar = {
            androidx.compose.foundation.layout.Box(modifier = Modifier.padding(16.dp)) {
                NavigationBar(
                    modifier = Modifier.clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp)),
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    val items = listOf(
                        NavigationItem("Home", Screen.Dashboard, Icons.Filled.Home),
                        NavigationItem("Accounts", Screen.Accounts, Icons.Filled.AccountBalanceWallet),
                        NavigationItem("AI Advisor", Screen.AiAdvisor, Icons.Filled.AutoAwesome),
                        NavigationItem("Calendar", Screen.CalendarView, Icons.Filled.CalendarMonth)
                    )

                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            if (navBackStackEntry?.destination?.route == Screen.Dashboard) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddTransaction) },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard) { DashboardScreen(viewModel) }
            composable(Screen.AddTransaction) { AddTransactionScreen(viewModel) { navController.popBackStack() } }
            composable(Screen.Accounts) { AccountsScreen(viewModel) }
            composable(Screen.AiAdvisor) { AiAdvisorScreen(viewModel) }
            composable(Screen.CalendarView) { CalendarScreen(viewModel) }
        }
    }
}

data class NavigationItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
