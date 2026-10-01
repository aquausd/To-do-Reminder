package com.example.ui.components

import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun NativeTimePicker(
    currentTime: String?,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val cal = Calendar.getInstance()
        var hour = cal.get(Calendar.HOUR_OF_DAY)
        var minute = cal.get(Calendar.MINUTE)

        if (!currentTime.isNullOrBlank() && currentTime.contains(":")) {
            try {
                val parts = currentTime.split(":")
                hour = parts[0].toInt()
                minute = parts[1].toInt()
            } catch (_: Exception) {
            }
        }

        val dialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val hourStr = selectedHour.toString().padStart(2, '0')
                val minStr = selectedMinute.toString().padStart(2, '0')
                onTimeSelected("$hourStr:$minStr")
            },
            hour,
            minute,
            false // 12-hour view with AM/PM
        )

        dialog.setOnDismissListener { onDismiss() }
        dialog.show()

        onDispose {
            dialog.dismiss()
        }
    }
}
