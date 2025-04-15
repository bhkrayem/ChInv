package com.bahaa.chinv

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) { MainScreen(navController) }
        composable(Screen.Invoice.route) { InvoiceScreen(navController) }
        composable(Screen.Items.route) { ItemsScreen(navController) }
        composable(Screen.Customers.route) { CustomersScreen(navController) }
        composable(Screen.Reports.route) { ReportsScreen(navController) }
        composable("add_item") { AddItemScreen(navController) }
    }
}
