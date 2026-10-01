package com.example.ui.components

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun NativeDatePicker(
    currentIsoDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val cal = Calendar.getInstance()
        try {
            val parts = currentIsoDate.split("-")
            cal.set(Calendar.YEAR, parts[0].toInt())
            cal.set(Calendar.MONTH, parts[1].toInt() - 1)
            cal.set(Calendar.DAY_OF_MONTH, parts[2].toInt())
        } catch (_: Exception) {
        }

        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val monthStr = (month + 1).toString().padStart(2, '0')
                val dayStr = dayOfMonth.toString().padStart(2, '0')
                onDateSelected("$year-$monthStr-$dayStr")
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        dialog.setOnDismissListener { onDismiss() }
        dialog.show()

        onDispose {
            dialog.dismiss()
        }
    }
}
