package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskReminderReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_CATEGORY = "extra_task_category"
        const val EXTRA_TASK_TIME = "extra_task_time"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return

        val title = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Reminder"
        val category = intent.getStringExtra(EXTRA_TASK_CATEGORY) ?: ""
        val timeString = intent.getStringExtra(EXTRA_TASK_TIME)

        NotificationHelper.showTaskReminder(context, taskId, title, category, timeString)
    }
}
