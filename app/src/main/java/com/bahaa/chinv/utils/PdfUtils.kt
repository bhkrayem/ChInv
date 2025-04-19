package com.bahaa.chinv

import android.content.Context
import android.os.Environment
import androidx.core.content.FileProvider
import com.bahaa.chinv.data.Invoice
import com.bahaa.chinv.data.InvoiceItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfUtils {

    fun createReportPdfToDownloads(
        context: Context,
        invoicesWithItems: Map<Invoice, List<InvoiceItem>>
    ): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "invoice_report_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        }.pdf"
        val file = File(downloadsDir, fileName)

        FileOutputStream(file).use { outputStream ->
            outputStream.write("Invoice Report\n\n".toByteArray())

            invoicesWithItems.forEach { (invoice, items) ->
                outputStream.write("Invoice #${invoice.id} - ${invoice.date} ${invoice.time}\n".toByteArray())
                outputStream.write("Customer: ${invoice.customerName}\n".toByteArray())

                items.forEach {
                    outputStream.write("- ${it.itemName}: Qty ${it.quantity} ${it.unit} + Free: ${it.freeQuantity} | Unit Price: ${it.unitPrice} | Total: ${it.value}\n".toByteArray())
                }

                outputStream.write("Total: ${invoice.total}, Discount: ${invoice.discount}, Net: ${invoice.netValue}\n\n".toByteArray())
            }
        }

        return file
    }

    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            android.content.Intent.createChooser(shareIntent, "Share Report PDF")
        )
    }
}
