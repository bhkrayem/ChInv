package com.bahaa.chinv

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AddItemScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("box") }
    var boxPrice by remember { mutableStateOf("") }
    var piecesPerBox by remember { mutableStateOf("") }

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
                // TODO: Save item
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
