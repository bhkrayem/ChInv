package com.bahaa.chinv

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bahaa.chinv.data.AppDatabase
import com.bahaa.chinv.data.Invoice
import com.bahaa.chinv.data.InvoiceItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
@Composable
fun ReportsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val invoiceDao = AppDatabase.getDatabase(context).invoiceDao()
    val scope = rememberCoroutineScope()

    val today = SimpleDateFormat("yyyy-MM-dd").format(Date())
    var startDate by remember { mutableStateOf(today) }
    var endDate by remember { mutableStateOf(today) }

    var reportData by remember { mutableStateOf<Map<Invoice, List<InvoiceItem>>>(emptyMap()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Sales Report", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("From") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("To") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                scope.launch {
                    val invoices = invoiceDao.getInvoicesBetween(startDate, endDate)
                    val fullData = mutableMapOf<Invoice, List<InvoiceItem>>()

                    for (invoice in invoices) {
                        val items = invoiceDao.getItemsForInvoiceOnce(invoice.id)
                        fullData[invoice] = items
                    }
                    reportData = fullData
                }
            }) {
                Text("Show")
            }

            Button(onClick = {
                // PDF logic (Step 3)
            }) {
                Text("Share PDF")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            reportData.forEach { (invoice, items) ->
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("Invoice Number: ${invoice.id}")
                        Text("Date: ${invoice.date}    Time: ${invoice.time}")
                        Text("Customer Name: ${invoice.customerName}")

                        items.forEach { item ->
                            Text(
                                "${item.itemName}    ${item.quantity} ${item.unit} x${item.unitPrice} = ${
                                    String.format(
                                        "%.2f",
                                        item.value
                                    )
                                }   Free: ${item.freeQuantity}"
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Total: ${
                                String.format(
                                    "%.2f",
                                    invoice.total
                                )
                            }    Discount: ${
                                String.format(
                                    "%.2f",
                                    invoice.discount
                                )
                            }    Net Value: ${String.format("%.2f", invoice.netValue)}"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}
