package com.bahaa.chinv

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bahaa.chinv.data.AppDatabase
import com.bahaa.chinv.data.InvoiceItem
import com.bahaa.chinv.data.Item
import com.bahaa.chinv.viewmodel.InvoiceViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
@Composable
fun InvoiceScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dao = AppDatabase.getDatabase(context).invoiceDao()
    val viewModel = remember { InvoiceViewModel(dao) }
    val nextInvoiceNumber by viewModel.getNextInvoiceNumber().collectAsState(initial = 1)

    var customerName by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    val items by viewModel.invoiceItems.collectAsState()

    val date = remember { SimpleDateFormat("yyyy-MM-dd").format(Date()) }
    val time = remember { SimpleDateFormat("HH:mm:ss").format(Date()) }

    val total = items.sumOf { it.value }
    val discountValue = discount.toDoubleOrNull() ?: 0.0
    val net = total - discountValue

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Invoice #$nextInvoiceNumber", style = MaterialTheme.typography.titleMedium)
        Text("Date: $date", style = MaterialTheme.typography.bodySmall)
        Text("Time: $time", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Customer Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saved
        )

        OutlinedTextField(
            value = customerAddress,
            onValueChange = { customerAddress = it },
            label = { Text("Customer Address") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !saved
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
            itemsIndexed(items) { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${item.itemName}", modifier = Modifier.weight(1f))
                    Text("Qty: ${item.quantity}", modifier = Modifier.weight(1f))
                    Text("${item.unit}", modifier = Modifier.weight(1f))
                    Text("${item.unitPrice}", modifier = Modifier.weight(1f))
                    Text("= ${item.value}", modifier = Modifier.weight(1f))
                    if (!saved) {
                        IconButton(onClick = { viewModel.removeItem(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }
        }

        if (!saved) {
            AddInvoiceItemRow(onAdd = { item -> viewModel.addItem(item) })
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = discount,
            onValueChange = { discount = it },
            label = { Text("Discount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !saved
        )

        Text("Total: $total")
        Text("Net: $net")

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                viewModel.saveInvoice(customerName, customerAddress, date, time, discountValue)
                saved = true
            }, enabled = !saved) {
                Text("Save")
            }

            Button(onClick = { saved = false }, enabled = saved) {
                Text("Edit")
            }

            Button(onClick = { /* Print logic */ }, enabled = saved) {
                Text("Print")
            }
        }
    }
}

@Composable
fun AddInvoiceItemRow(onAdd: (InvoiceItem) -> Unit) {
    val context = LocalContext.current
    val itemDao = remember { AppDatabase.getDatabase(context).itemDao() }
    var allItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("box") }
    var price by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        itemDao.getAll().collect { itemList ->
            allItems = itemList
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = itemName,
            onValueChange = {
                itemName = it
                showSuggestions = true
            },
            label = { Text("Item") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        if (showSuggestions && itemName.isNotBlank()) {
            val suggestions = allItems.filter {
                it.name.contains(itemName, ignoreCase = true)
            }

            Column {
                suggestions.forEach { item ->
                    Text(
                        text = item.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                itemName = item.name
                                val unitPrice = if (unit == "box") item.boxPrice
                                else item.boxPrice / item.piecesPerBox
                                price = unitPrice.toString()
                                quantity = "1.0"
                                showSuggestions = false
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Qty") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Button(
            onClick = {
                val qty = quantity.toDoubleOrNull() ?: 0.0
                val unitPrice = price.toDoubleOrNull() ?: 0.0
                val value = qty * unitPrice
                onAdd(
                    InvoiceItem(
                        itemName = itemName,
                        quantity = qty,
                        unit = unit,
                        unitPrice = unitPrice,
                        value = value,
                        invoiceId = 0
                    )
                )
                itemName = ""
                quantity = ""
                unit = "box"
                price = ""
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
        ) {
            Text("Add Item")
        }
    }
}
