package com.bahaa.chinv

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bahaa.chinv.data.AppDatabase
import com.bahaa.chinv.data.InvoiceItem
import com.bahaa.chinv.data.Item
import com.bahaa.chinv.viewmodel.CustomerViewModel
import com.bahaa.chinv.viewmodel.InvoiceViewModel
import java.text.SimpleDateFormat
import java.util.Date


@SuppressLint("SimpleDateFormat")
@Composable
fun InvoiceScreen(navController: NavHostController, invoiceId: Int? = null) {
    val context = LocalContext.current
    val dao = AppDatabase.getDatabase(context).invoiceDao()
    val viewModel = remember { InvoiceViewModel(dao) }
    val customerViewModel: CustomerViewModel = viewModel()
    val allCustomers by customerViewModel.customers.collectAsState()
    var showCustomerSuggestions by remember { mutableStateOf(false) }
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


    LaunchedEffect(invoiceId) {
        if (invoiceId != null) {
            dao.getAllInvoices().collect { list ->
                val invoice = list.find { it.id == invoiceId }
                invoice?.let {
                    customerName = it.customerName
                    customerAddress = it.customerAddress
                    discount = it.discount.toString()
                    saved = true
                }
            }
            dao.getItemsForInvoice(invoiceId).collect { items ->
                viewModel.loadExistingItems(items)
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Invoice #$nextInvoiceNumber", style = MaterialTheme.typography.titleMedium)
        Text("Date: $date", style = MaterialTheme.typography.bodySmall)
        Text("Time: $time", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))



        OutlinedTextField(
            value = customerName,
            onValueChange = {
                customerName = it
                showCustomerSuggestions = true
            },
            label = { Text("Customer Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            enabled = !saved
        )

        if (showCustomerSuggestions && customerName.isNotBlank()) {
            val suggestions = allCustomers.filter {
                it.name.contains(customerName, ignoreCase = true)
            }

            Column {
                suggestions.forEach { customer ->
                    Text(
                        text = customer.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                customerName = customer.name
                                showCustomerSuggestions = false
                            }
                    )
                }
            }
        }


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
                    Text("Free: ${item.freeQuantity}", modifier = Modifier.weight(1f))

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

        // Text("Total: $total")
        Text("Total: ${String.format("%.2f", total)}")
        Text("Discount: ${if (discountValue > 0) String.format("%.2f", discountValue) else "0.00"}")
        Text("Net: ${String.format("%.2f", net)}")


        // Text("Net: $net")


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
    var selectedItem by remember { mutableStateOf<Item?>(null) }

    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("box") }
    val unitOptions = listOf("box", "piece")
    var expanded by remember { mutableStateOf(false) }

    var price by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var freeQuantity by remember { mutableStateOf("") }


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
                                selectedItem = item
                                quantity = "1.0"
                                showSuggestions = false

                                val unitPrice =
                                    if (unit == "box") item.boxPrice else item.boxPrice / item.piecesPerBox
                                price = String.format("%.2f", unitPrice)

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

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {

                OutlinedTextField(
                    value = unit,
                    onValueChange = {},
                    label = { Text("Unit") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        }
                    }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    unitOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                unit = option
                                expanded = false

                                selectedItem?.let {
                                    val unitPrice =
                                        if (unit == "box") it.boxPrice else it.boxPrice / it.piecesPerBox
                                    price = String.format("%.2f", unitPrice)

                                }
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = freeQuantity,
                onValueChange = { freeQuantity = it },
                label = { Text("Free Qty") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                val freeQty = freeQuantity.toDoubleOrNull() ?: 0.0
                onAdd(
                    InvoiceItem(
                        itemName = itemName,
                        quantity = qty,
                        freeQuantity = freeQty,
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
                freeQuantity = ""
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
        ) {
            Text("Add Item")
        }
    }
}
