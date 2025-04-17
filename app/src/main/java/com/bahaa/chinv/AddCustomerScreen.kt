package com.bahaa.chinv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bahaa.chinv.data.Customer
import com.bahaa.chinv.viewmodel.CustomerViewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect





@Composable
fun AddCustomerScreen(
    navController: NavHostController,
    customerId: Int = 0,
    existingName: String = "",
    existingPhone: String = "",
    existingAddress: String = ""
) {
    val viewModel: CustomerViewModel = viewModel()

    var name by remember { mutableStateOf(existingName) }
    var phone by remember { mutableStateOf(existingPhone) }
    var address by remember { mutableStateOf(existingAddress) }
    val allCustomers by viewModel.customers.collectAsState()
    LaunchedEffect(allCustomers) {
        println("LOADED CUSTOMERS: ${allCustomers.size}")
        allCustomers.forEach { println(it.name) }
    }

    var showSuggestions by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add Customer", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                showSuggestions = true
            },
            label = { Text("Customer Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        if (showSuggestions && name.isNotBlank()) {
            val suggestions = allCustomers.filter {
                it.name.contains(name, ignoreCase = true)
            }

            Column {
                suggestions.forEach { customer ->
                    Text(
                        text = customer.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                name = customer.name
                                address = customer.address
                                phone = customer.phone
                                showSuggestions = false
                            }
                    )
                }
            }
        }





        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    val customer = Customer(
                        id = customerId,
                        name = name,
                        phone = phone,
                        address = address

                    )
                    viewModel.insert(customer)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
