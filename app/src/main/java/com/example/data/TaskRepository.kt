package com.example.data

import android.content.Context
import com.example.receiver.TaskReminderScheduler
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskRepository(
    private val context: Context,
    private val taskDao: TaskDao
) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksForDate(date: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksForDate(date)
    }

    fun getUpcomingTasks(date: String): Flow<List<TaskEntity>> {
        return taskDao.getUpcomingTasks(date)
    }

    fun getTaskById(id: Long): Flow<TaskEntity?> {
        return taskDao.getTaskById(id)
    }

    suspend fun getTaskByIdSync(id: Long): TaskEntity? {
        return taskDao.getTaskByIdSync(id)
    }

    suspend fun insertTask(task: TaskEntity): Long {
        val id = taskDao.insertTask(task)
        val insertedTask = task.copy(id = id)
        TaskReminderScheduler.scheduleReminder(context, insertedTask)
        return id
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
        if (task.isCompleted) {
            TaskReminderScheduler.cancelReminder(context, task.id)
        } else {
            TaskReminderScheduler.scheduleReminder(context, task)
        }
    }

    suspend fun toggleTaskCompletion(task: TaskEntity) {
        val updated = task.copy(
            isCompleted = !task.isCompleted,
            completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
        )
        taskDao.updateTask(updated)
        if (updated.isCompleted) {
            TaskReminderScheduler.cancelReminder(context, updated.id)
        } else {
            TaskReminderScheduler.scheduleReminder(context, updated)
        }
    }

    suspend fun deleteTask(task: TaskEntity) {
        TaskReminderScheduler.cancelReminder(context, task.id)
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) {
        TaskReminderScheduler.cancelReminder(context, id)
        taskDao.deleteTaskById(id)
    }

    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }

    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    suspend fun cleanupWebRedesignCategory() {
        taskDao.cleanupWebRedesignCategory()
    }

    suspend fun seedSampleTasks() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val todayStr = dateFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowStr = dateFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, 2)
        val futureStr = dateFormat.format(cal.time)

        val samples = listOf(
            TaskEntity(
                title = "Design homepage",
                category = "Work",
                dueDate = todayStr,
                dueTime = "09:00",
                priority = "High",
                isCompleted = false,
                notes = "Focus on the hero section, key features, and responsive behavior. Keep it clean and consistent with the brand.",
                reminderMinutesBefore = 15
            ),
            TaskEntity(
                title = "Team stand-up",
                category = "Work",
                dueDate = todayStr,
                dueTime = "11:00",
                priority = "Medium",
                isCompleted = false,
                notes = "Sprint 4 kickoff, blockers review and design handoff review.",
                reminderMinutesBefore = 10
            ),
            TaskEntity(
                title = "Lunch with Sarah",
                category = "Personal",
                dueDate = todayStr,
                dueTime = "13:00",
                priority = "Low",
                isCompleted = false,
                notes = "Catch up at Green Bistro on 4th Ave.",
                reminderMinutesBefore = 30
            ),
            TaskEntity(
                title = "Pick up groceries",
                category = "Personal",
                dueDate = todayStr,
                dueTime = "17:00",
                priority = "Medium",
                isCompleted = false,
                notes = "Almond milk, avocados, whole wheat bread, fresh salmon.",
                reminderMinutesBefore = 15
            ),
            TaskEntity(
                title = "Review Q2 report",
                category = "Work",
                dueDate = todayStr,
                dueTime = "19:00",
                priority = "High",
                isCompleted = false,
                notes = "Check marketing ROI numbers and prepare slides for Thursday sync.",
                reminderMinutesBefore = 15
            ),
            TaskEntity(
                title = "Project proposal",
                category = "Work",
                dueDate = tomorrowStr,
                dueTime = "10:00",
                priority = "High",
                isCompleted = false,
                notes = "Send final draft to product leadership team.",
                reminderMinutesBefore = 30
            ),
            TaskEntity(
                title = "Dentist appointment",
                category = "Personal",
                dueDate = futureStr,
                dueTime = "09:30",
                priority = "Medium",
                isCompleted = false,
                notes = "Routine 6-month cleaning and checkup.",
                reminderMinutesBefore = 60
            )
        )

        taskDao.insertAll(samples)
    }
}
