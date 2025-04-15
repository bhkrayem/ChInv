package com.bahaa.chinv

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bahaa.chinv.data.Item
import com.bahaa.chinv.viewmodel.ItemViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ItemsScreen(navController: NavHostController) {
    val viewModel: ItemViewModel = viewModel()
    val itemListState = viewModel.items.collectAsState()

    val items: List<Item> = itemListState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Items",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { navController.navigate(Screen.AddItem.route) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Add New Item")
        }

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No items yet.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = items, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Name: ${item.name}")
                            Text("Unit: ${item.unit}")
                            Text("Box Price: ${item.boxPrice}")
                            Text("Pieces per box: ${item.piecesPerBox}")
                        }
                    }
                }
            }
        }
    }
}
