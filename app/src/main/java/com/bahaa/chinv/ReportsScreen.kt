package com.bahaa.chinv

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val invoiceDao = remember { AppDatabase.getDatabase(context).invoiceDao() }
    val coroutineScope = rememberCoroutineScope()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    var startDate by remember { mutableStateOf(today) }
    var endDate by remember { mutableStateOf(today) }
    var filteredInvoices by remember { mutableStateOf<List<Invoice>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Reports", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateField(label = "Start Date", date = startDate, onDateSelected = { startDate = it })
            Spacer(modifier = Modifier.width(8.dp))
            DateField(label = "End Date", date = endDate, onDateSelected = { endDate = it })

            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                coroutineScope.launch {
                    // Collect from Flow using .first()
                    filteredInvoices =
                        invoiceDao.getInvoicesBetweenDates(startDate, endDate).first()
                }
            }) {
                Text("Show")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        filteredInvoices.forEach { invoice ->
            Text("Invoice #${invoice.id}")
            Text("Customer: ${invoice.customerName}")
            Text("Date: ${invoice.date}   Time: ${invoice.time}")
            Text("Discount: ${invoice.discount}")
            Text("Total: ${invoice.total}")
            Text("Net: ${invoice.netValue}")
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun DateField(label: String, date: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year, month, dayOfMonth ->
                val formatted = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                onDateSelected(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    OutlinedTextField(
        value = date,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier
            .width(140.dp)
            .clickable {
                datePickerDialog.show()
            }
    )
}
