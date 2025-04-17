package com.bahaa.chinv

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
@Composable
fun ReportsScreen(navController: NavHostController) {
    val context = LocalContext.current

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val today = remember { dateFormat.format(Date()) }

    var fromDate by remember { mutableStateOf(today) }
    var toDate by remember { mutableStateOf(today) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Sales Report",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DatePickerWithLabel("From", fromDate) { fromDate = it }
            DatePickerWithLabel("To", toDate) { toDate = it }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                // TODO: Load invoices by date range
            }) {
                Text("Show")
            }
            Button(onClick = {
                // TODO: Share as PDF
            }) {
                Text("Share PDF")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 2: Invoices List will go here
        Text("Invoices will appear here...", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun DatePickerWithLabel(label: String, selectedDate: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current

    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Button(onClick = {
            showDatePicker(context, selectedDate, onDateSelected)
        }) {
            Text(selectedDate)
        }
        Text(
            "Date Picker",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@SuppressLint("SimpleDateFormat")
fun showDatePicker(context: Context, initial: String, onPicked: (String) -> Unit) {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = sdf.parse(initial) ?: Date()
    val calendar = Calendar.getInstance().apply { time = date }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            val selected = Calendar.getInstance()
            selected.set(year, month, day)
            onPicked(sdf.format(selected.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
