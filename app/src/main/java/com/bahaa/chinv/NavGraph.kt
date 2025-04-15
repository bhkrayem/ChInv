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
        composable("edit_item/{id}/{name}/{unit}/{price}/{pieces}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val unit = backStackEntry.arguments?.getString("unit") ?: "box"
            val price = backStackEntry.arguments?.getString("price")?.toDoubleOrNull() ?: 0.0
            val pieces = backStackEntry.arguments?.getString("pieces")?.toIntOrNull() ?: 1

            AddItemScreen(
                navController = navController,
                itemId = id,
                existingName = name,
                existingUnit = unit,
                existingBoxPrice = price,
                existingPiecesPerBox = pieces
            )
        }
        composable(Screen.AddCustomer.route) {
            AddCustomerScreen(navController)
        }

        composable("edit_customer/{id}/{name}/{phone}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val phone = backStackEntry.arguments?.getString("phone") ?: ""

            AddCustomerScreen(
                navController = navController,
                customerId = id,
                existingName = name,
                existingPhone = phone
            )
        }

    }


}



