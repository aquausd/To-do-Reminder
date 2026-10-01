package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.TaskEntity
import com.example.data.TaskRepository
import com.example.data.UserPreferences
import com.example.ui.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Tab {
    HOME,
    TASKS,
    CALENDAR,
    SETTINGS
}

enum class TaskSort {
    TIME,
    PRIORITY,
    TITLE
}

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = TaskRepository(application, db.taskDao())
    val userPrefs = UserPreferences(application)

    init {
        viewModelScope.launch {
            repository.cleanupWebRedesignCategory()
        }
    }

    // Navigation state
    private val _currentTab = MutableStateFlow(Tab.HOME)
    val currentTab: StateFlow<Tab> = _currentTab.asStateFlow()

    private val _currentDetailTaskId = MutableStateFlow<Long?>(null)
    val currentDetailTaskId: StateFlow<Long?> = _currentDetailTaskId.asStateFlow()

    private val _showAddEditSheet = MutableStateFlow(false)
    val showAddEditSheet: StateFlow<Boolean> = _showAddEditSheet.asStateFlow()

    private val _editingTaskId = MutableStateFlow<Long?>(null)
    val editingTaskId: StateFlow<Long?> = _editingTaskId.asStateFlow()

    private val _showGridMenuSheet = MutableStateFlow(false)
    val showGridMenuSheet: StateFlow<Boolean> = _showGridMenuSheet.asStateFlow()

    // User settings
    private val _userName = MutableStateFlow(userPrefs.userName)
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _defaultReminderMinutes = MutableStateFlow(userPrefs.defaultReminderMinutes)
    val defaultReminderMinutes: StateFlow<Int> = _defaultReminderMinutes.asStateFlow()

    private val _startOfWeekSunday = MutableStateFlow(userPrefs.startOfWeek == "SUNDAY")
    val startOfWeekSunday: StateFlow<Boolean> = _startOfWeekSunday.asStateFlow()

    private val _categories = MutableStateFlow(userPrefs.categories)
    val categories: StateFlow<Set<String>> = _categories.asStateFlow()

    // Tasks state
    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayDate = MutableStateFlow(DateTimeUtils.todayIso())

    val todayTasks: StateFlow<List<TaskEntity>> = todayDate.flatMapLatest { date ->
        repository.getTasksForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingTasks: StateFlow<List<TaskEntity>> = todayDate.flatMapLatest { date ->
        repository.getUpcomingTasks(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calendar state
    val selectedCalendarDate = MutableStateFlow(DateTimeUtils.todayIso())

    val calendarTasks: StateFlow<List<TaskEntity>> = selectedCalendarDate.flatMapLatest { date ->
        repository.getTasksForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tasks search and filters
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<String?>(null)
    val taskSort = MutableStateFlow(TaskSort.TIME)
    val showCompletedInTasks = MutableStateFlow(true)

    // Reactive filtered tasks for Today/Tasks tab
    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        todayTasks,
        searchQuery,
        selectedCategoryFilter,
        taskSort,
        showCompletedInTasks
    ) { tasks, query, category, sort, showCompleted ->
        var result = tasks
        if (!showCompleted) {
            result = result.filter { !it.isCompleted }
        }
        if (!category.isNullOrBlank()) {
            result = result.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            result = result.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.notes.contains(query, ignoreCase = true)
            }
        }
        when (sort) {
            TaskSort.TIME -> result.sortedWith(
                compareBy<TaskEntity> { it.isCompleted }
                    .thenBy { it.dueTime ?: "99:99" }
                    .thenBy { it.id }
            )
            TaskSort.PRIORITY -> result.sortedWith(
                compareBy<TaskEntity> { it.isCompleted }
                    .thenByDescending {
                        when (it.priority) {
                            "High" -> 3
                            "Medium" -> 2
                            else -> 1
                        }
                    }
            )
            TaskSort.TITLE -> result.sortedWith(
                compareBy<TaskEntity> { it.isCompleted }
                    .thenBy { it.title.lowercase() }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Statistics counts
    val allTasksCount: StateFlow<Int> = allTasks.combine(MutableStateFlow(Unit)) { tasks, _ ->
        tasks.count { !it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayTasksCount: StateFlow<Int> = todayTasks.combine(MutableStateFlow(Unit)) { tasks, _ ->
        tasks.count { !it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val upcomingTasksCount: StateFlow<Int> = upcomingTasks.combine(MutableStateFlow(Unit)) { tasks, _ ->
        tasks.count { !it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedTasksCount: StateFlow<Int> = allTasks.combine(MutableStateFlow(Unit)) { tasks, _ ->
        tasks.count { it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Current detail task flow
    val detailTask: StateFlow<TaskEntity?> = _currentDetailTaskId.flatMapLatest { id ->
        if (id != null) repository.getTaskById(id) else MutableStateFlow(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation methods
    fun selectTab(tab: Tab) {
        _currentDetailTaskId.value = null
        _currentTab.value = tab
    }

    fun openDetail(taskId: Long) {
        _currentDetailTaskId.value = taskId
    }

    fun closeDetail() {
        _currentDetailTaskId.value = null
    }

    fun openAddTask(prefilledDate: String? = null) {
        _editingTaskId.value = null
        if (prefilledDate != null) {
            selectedCalendarDate.value = prefilledDate
        }
        _showAddEditSheet.value = true
    }

    fun openEditTask(taskId: Long) {
        _editingTaskId.value = taskId
        _showAddEditSheet.value = true
    }

    fun closeAddEditTask() {
        _showAddEditSheet.value = false
        _editingTaskId.value = null
    }

    fun setGridMenuVisible(visible: Boolean) {
        _showGridMenuSheet.value = visible
    }

    // Task CRUD operations
    fun saveTask(
        title: String,
        category: String,
        dueDate: String,
        dueTime: String?,
        priority: String,
        notes: String,
        reminderMinutes: Int?,
        existingId: Long? = null
    ) {
        val cleanCategory = if (category.equals("Web Redesign", ignoreCase = true)) "Work" else category
        viewModelScope.launch {
            if (existingId != null && existingId > 0) {
                val existing = repository.getTaskByIdSync(existingId)
                if (existing != null) {
                    val updated = existing.copy(
                        title = title,
                        category = cleanCategory,
                        dueDate = dueDate,
                        dueTime = dueTime,
                        priority = priority,
                        notes = notes,
                        reminderMinutesBefore = reminderMinutes
                    )
                    repository.updateTask(updated)
                }
            } else {
                val newTask = TaskEntity(
                    title = title,
                    category = cleanCategory,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    priority = priority,
                    notes = notes,
                    reminderMinutesBefore = reminderMinutes
                )
                repository.insertTask(newTask)
            }
            closeAddEditTask()
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            if (_currentDetailTaskId.value == task.id) {
                closeDetail()
            }
        }
    }

    fun deleteTaskById(id: Long) {
        viewModelScope.launch {
            repository.deleteTaskById(id)
            if (_currentDetailTaskId.value == id) {
                closeDetail()
            }
        }
    }

    fun duplicateTask(task: TaskEntity) {
        viewModelScope.launch {
            val duplicate = task.copy(
                id = 0,
                title = "${task.title} (Copy)",
                isCompleted = false,
                completedAt = null,
                createdAt = System.currentTimeMillis()
            )
            val newId = repository.insertTask(duplicate)
            _currentDetailTaskId.value = newId
        }
    }

    fun updateDetailDate(date: String) {
        val current = detailTask.value ?: return
        viewModelScope.launch {
            repository.updateTask(current.copy(dueDate = date))
        }
    }

    fun updateDetailTime(time: String?) {
        val current = detailTask.value ?: return
        viewModelScope.launch {
            repository.updateTask(current.copy(dueTime = time))
        }
    }

    fun updateDetailPriority(priority: String) {
        val current = detailTask.value ?: return
        viewModelScope.launch {
            repository.updateTask(current.copy(priority = priority))
        }
    }

    fun updateDetailReminder(reminder: Int?) {
        val current = detailTask.value ?: return
        viewModelScope.launch {
            repository.updateTask(current.copy(reminderMinutesBefore = reminder))
        }
    }

    fun updateDetailNotes(notes: String) {
        val current = detailTask.value ?: return
        viewModelScope.launch {
            repository.updateTask(current.copy(notes = notes))
        }
    }

    // User settings
    fun setUserName(name: String) {
        val trimmed = name.trim().ifEmpty { "Alex" }
        userPrefs.userName = trimmed
        _userName.value = trimmed
    }

    fun setDefaultReminderMinutes(minutes: Int) {
        userPrefs.defaultReminderMinutes = minutes
        _defaultReminderMinutes.value = minutes
    }

    fun setStartOfWeek(sunday: Boolean) {
        userPrefs.startOfWeek = if (sunday) "SUNDAY" else "MONDAY"
        _startOfWeekSunday.value = sunday
    }

    fun addCategory(category: String) {
        val trimmed = category.trim()
        if (trimmed.isNotBlank() && !trimmed.equals("Web Redesign", ignoreCase = true)) {
            userPrefs.addCategory(trimmed)
            _categories.value = userPrefs.categories
        }
    }

    fun removeCategory(category: String) {
        userPrefs.removeCategory(category)
        _categories.value = userPrefs.categories
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.deleteCompletedTasks()
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.deleteAllTasks()
            _currentDetailTaskId.value = null
        }
    }

    fun seedSampleTasks() {
        viewModelScope.launch {
            repository.seedSampleTasks()
        }
    }
}
