package com.bahaa.chinv

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bahaa.chinv.data.Customer
import com.bahaa.chinv.viewmodel.CustomerViewModel


@Composable
fun CustomersScreen(navController: NavHostController) {
    val viewModel: CustomerViewModel = viewModel()
    val customerList by viewModel.customers.collectAsState()
    val customers: List<Customer> = customerList

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Customers",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                navController.navigate(Screen.AddCustomer.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Add New Customer")
        }

        if (customers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No customers yet.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(customers, key = { it.id }) { customer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Name: ${customer.name}")
                            Text("Phone: ${customer.phone}")
                            Text("Address: ${customer.address}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            "edit_customer/${customer.id}/${Uri.encode(customer.name)}/${
                                                Uri.encode(
                                                    customer.phone
                                                )
                                            }/${Uri.encode(customer.address)}"
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Edit")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        viewModel.delete(customer)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
