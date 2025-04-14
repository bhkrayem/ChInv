package com.bahaa.chinv

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate(Screen.Invoice.route) }) {
            Text("Invoice")
        }
        Button(onClick = { navController.navigate(Screen.Items.route) }) {
            Text("Items")
        }
        Button(onClick = { navController.navigate(Screen.Customers.route) }) {
            Text("Customers")
        }
        Button(onClick = { navController.navigate(Screen.Reports.route) }) {
            Text("Reports")
        }
    }
}
