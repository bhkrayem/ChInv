package com.bahaa.chinv.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
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
        invoiceMap: Map<Invoice, List<InvoiceItem>>
    ): File {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        var pageNumber = 1
        var y = 50

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        paint.textSize = 16f
        canvas.drawText("Invoice Report", 40f, y.toFloat(), paint)
        y += 30

        invoiceMap.forEach { (invoice, items) ->
            canvas.drawText(
                "Invoice #${invoice.id} | Date: ${invoice.date} | Time: ${invoice.time}",
                40f,
                y.toFloat(),
                paint
            )
            y += 20
            canvas.drawText("Customer: ${invoice.customerName}", 40f, y.toFloat(), paint)
            y += 20

            items.forEach { item ->
                canvas.drawText(
                    "- ${item.itemName} | Qty: ${item.quantity} ${item.unit} + Free: ${item.freeQuantity} | Unit Price: ${item.unitPrice} | Total: ${item.value}",
                    40f,
                    y.toFloat(),
                    paint
                )
                y += 20
                if (y > 800) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    y = 50
                    page = pdfDocument.startPage(
                        PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                    )
                    canvas.drawText("Invoice Report Continued...", 40f, y.toFloat(), paint)
                    y += 30
                }
            }

            canvas.drawText("Discount: ${invoice.discount}", 40f, y.toFloat(), paint)
            y += 20
            canvas.drawText(
                "Total: ${invoice.total} | Net: ${invoice.netValue}",
                40f,
                y.toFloat(),
                paint
            )
            y += 40
        }

        pdfDocument.finishPage(page)

        val dateTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "invoice_report_$dateTime.pdf"
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        return file
    }

    fun sharePdf(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share Invoice Report PDF"))
    }
}
