package com.bahaa.chinv

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).invoiceDao() }
    val coroutineScope = rememberCoroutineScope()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    var startDate by remember { mutableStateOf(today) }
    var endDate by remember { mutableStateOf(today) }

    var invoicesWithItems by remember { mutableStateOf<Map<Invoice, List<InvoiceItem>>>(emptyMap()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Invoice Reports", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                DateField("Start Date", startDate) { startDate = it }
            }

            Column(modifier = Modifier.weight(1f)) {
                DateField("End Date", endDate) { endDate = it }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        val filteredInvoices =
                            dao.getInvoicesBetweenDates(startDate, endDate).first()
                        val result = mutableMapOf<Invoice, List<InvoiceItem>>()
                        for (invoice in filteredInvoices) {
                            val items = dao.getItemsForInvoice(invoice.id).first()
                            result[invoice] = items
                        }
                        invoicesWithItems = result
                    }
                },
                modifier = Modifier.alignByBaseline()
            ) {
                Text("Show")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        invoicesWithItems.forEach { (invoice, items) ->
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text("Invoice #${invoice.id}", style = MaterialTheme.typography.titleMedium)
                Text("Customer: ${invoice.customerName}")
                Text("Date: ${invoice.date}   Time: ${invoice.time}")

                Spacer(modifier = Modifier.height(8.dp))

                items.forEach { item ->
                    Text("- ${item.itemName} | Qty: ${item.quantity} ${item.unit} + Free: ${item.freeQuantity} | Unit Price: ${item.unitPrice} | Total: ${item.value}")
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Discount: ${invoice.discount}")
                Text("Total: ${invoice.total}")
                Text("Net: ${invoice.netValue}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun DateField(label: String, selectedDate: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    Box(
        modifier = Modifier
            .width(140.dp)
            .clickable {
                DatePickerDialog(
                    context,
                    { _: DatePicker, y, m, d ->
                        val formatted = "%04d-%02d-%02d".format(y, m + 1, d)
                        onDateSelected(formatted)
                    },
                    year, month, day
                ).show()
            }) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            enabled = false, // 👈 disable input completely, but still show UI
            modifier = Modifier.fillMaxWidth()
        )
    }
}

