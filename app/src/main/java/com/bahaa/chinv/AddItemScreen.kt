package com.bahaa.chinv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bahaa.chinv.data.Item
import com.bahaa.chinv.viewmodel.ItemViewModel


@Composable
fun AddItemScreen(
    navController: NavHostController,
    itemId: Int = 0,
    existingName: String = "",
    existingUnit: String = "box",
    existingBoxPrice: Double = 0.0,
    existingPiecesPerBox: Int = 1
) {
    var name by remember { mutableStateOf(existingName) }
    var unit by remember { mutableStateOf(existingUnit) }
    var boxPrice by remember { mutableStateOf(existingBoxPrice.toString()) }
    var piecesPerBox by remember { mutableStateOf(existingPiecesPerBox.toString()) }
    val viewModel: ItemViewModel = viewModel()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add New Item", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { unit = "box" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (unit == "box") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Box")
            }
            Button(
                onClick = { unit = "piece" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (unit == "piece") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Piece")
            }
        }

        OutlinedTextField(
            value = boxPrice,
            onValueChange = { boxPrice = it },
            label = { Text("Box Price") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = piecesPerBox,
            onValueChange = { piecesPerBox = it },
            label = { Text("Pieces per Box") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (name.isNotBlank() && boxPrice.isNotBlank() && piecesPerBox.isNotBlank()) {
                    val item = Item(
                        id = itemId,
                        name = name,
                        unit = unit,
                        boxPrice = boxPrice.toDoubleOrNull() ?: 0.0,
                        piecesPerBox = piecesPerBox.toIntOrNull() ?: 1
                    )
                    viewModel.insertItem(item) // Room handles insert vs update using primary key
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
