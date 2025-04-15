package com.bahaa.chinv

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bahaa.chinv.viewmodel.ItemViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider

@Composable
fun ItemsScreen(navController: NavHostController) {
    val viewModel: ItemViewModel = viewModel()
    val items by viewModel.items.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Items", modifier = Modifier.padding(bottom = 16.dp))

        Button(
            onClick = { navController.navigate(Screen.AddItem.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Add New Item")
        }

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No items yet.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Name: ${item.name}")
                        Text("Unit: ${item.unit}")
                        Text("Box Price: ${item.boxPrice}")
                        Text("Pieces per box: ${item.piecesPerBox}")
                        Divider(modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}
