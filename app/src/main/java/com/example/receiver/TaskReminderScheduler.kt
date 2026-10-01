package com.example.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.TaskEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TaskReminderScheduler {

    fun scheduleReminder(context: Context, task: TaskEntity) {
        cancelReminder(context, task.id)

        if (task.isCompleted) return
        val minutesBefore = task.reminderMinutesBefore ?: return
        if (minutesBefore < 0) return // None

        val triggerTime = calculateTriggerTime(task.dueDate, task.dueTime, minutesBefore)
        if (triggerTime <= System.currentTimeMillis()) {
            return // In the past
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TASK_ID, task.id)
            putExtra(TaskReminderReceiver.EXTRA_TASK_TITLE, task.title)
            putExtra(TaskReminderReceiver.EXTRA_TASK_CATEGORY, task.category)
            putExtra(TaskReminderReceiver.EXTRA_TASK_TIME, task.dueTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        NotificationHelper.cancelNotification(context, taskId)
    }

    private fun calculateTriggerTime(
        dueDateStr: String,
        dueTimeStr: String?,
        minutesBefore: Int
    ): Long {
        return try {
            val dateParts = dueDateStr.split("-")
            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1
            val day = dateParts[2].toInt()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)

                if (!dueTimeStr.isNullOrBlank() && dueTimeStr.contains(":")) {
                    val timeParts = dueTimeStr.split(":")
                    set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                    set(Calendar.MINUTE, timeParts[1].toInt())
                } else {
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                }
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            calendar.timeInMillis - (minutesBefore * 60 * 1000L)
        } catch (_: Exception) {
            0L
        }
    }
}
