package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskEntity
import com.example.ui.TodoViewModel
import com.example.ui.components.NativeDatePicker
import com.example.ui.components.NativeTimePicker
import com.example.ui.components.SquareIconButton
import com.example.ui.components.TaskCard
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BackgroundWhite
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ButtonShadowColor
import com.example.ui.theme.ChipBackground
import com.example.ui.theme.DividerLight
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.util.DateTimeUtils

@Composable
fun TaskDetailScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val task by viewModel.detailTask.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showPriorityDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }

    if (task == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundWhite)
                .windowInsetsPadding(WindowInsets.statusBars),
            contentAlignment = Alignment.Center
        ) {
            Text("Task not found", color = TextSecondary)
        }
        return
    }

    val currentTask = task!!

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Task", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Are you sure you want to delete \"${currentTask.title}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(currentTask)
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Delete", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    if (showDatePicker) {
        NativeDatePicker(
            currentIsoDate = currentTask.dueDate,
            onDateSelected = {
                viewModel.updateDetailDate(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        NativeTimePicker(
            currentTime = currentTask.dueTime,
            onTimeSelected = {
                viewModel.updateDetailTime(it)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showPriorityDialog) {
        AlertDialog(
            onDismissRequest = { showPriorityDialog = false },
            title = { Text("Select Priority", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column {
                    listOf("High", "Medium", "Low").forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateDetailPriority(p)
                                    showPriorityDialog = false
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = p, fontSize = 16.sp, color = TextPrimary)
                            if (currentTask.priority.equals(p, ignoreCase = true)) {
                                Text("✓", fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPriorityDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    if (showReminderDialog) {
        val reminderOptions = listOf(
            -1 to "None",
            0 to "At time of event",
            5 to "5 minutes before",
            15 to "15 minutes before",
            30 to "30 minutes before",
            60 to "1 hour before",
            1440 to "1 day before"
        )
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = { Text("Set Reminder", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column {
                    reminderOptions.forEach { (mins, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateDetailReminder(if (mins < 0) null else mins)
                                    showReminderDialog = false
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = label, fontSize = 15.sp, color = TextPrimary)
                            val isSelected = (currentTask.reminderMinutesBefore == mins) ||
                                    (currentTask.reminderMinutesBefore == null && mins == -1)
                            if (isSelected) {
                                Text("✓", fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showReminderDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .testTag("task_detail_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Navigation: Back Button on Left, Ellipsis on Right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SquareIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = { viewModel.closeDetail() },
                testTag = "detail_back_button"
            )

            Box {
                SquareIconButton(
                    icon = Icons.Filled.MoreHoriz,
                    contentDescription = "Task Actions",
                    onClick = { showMenu = true },
                    testTag = "detail_more_button"
                )

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(SurfaceWhite)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Task", fontSize = 14.sp) },
                        onClick = {
                            showMenu = false
                            viewModel.openEditTask(currentTask.id)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicate Task", fontSize = 14.sp) },
                        onClick = {
                            showMenu = false
                            viewModel.duplicateTask(currentTask)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Task", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                        onClick = {
                            showMenu = false
                            showDeleteConfirm = true
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Task Title
        Text(
            text = currentTask.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = 32.sp,
            letterSpacing = (-0.4).sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Chip (e.g. "• Work")
        if (currentTask.category.isNotBlank()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(ChipBackground)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(TextSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentTask.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Rounded White Details Card
        TaskCard(
            elevation = 2.dp,
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Date Row
                val isToday = currentTask.dueDate == DateTimeUtils.todayIso()
                val dateLabel = if (isToday) "Today" else "Date"
                val dateDisplay = DateTimeUtils.formatStandardDate(currentTask.dueDate)
                DetailRow(
                    icon = Icons.Filled.CalendarMonth,
                    label = dateLabel,
                    value = dateDisplay,
                    onClick = { showDatePicker = true }
                )

                HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 52.dp, end = 16.dp))

                // Time Row
                val timeDisplay = DateTimeUtils.formatTime12h(currentTask.dueTime) ?: "Not set"
                DetailRow(
                    icon = Icons.Outlined.Schedule,
                    label = "Time",
                    value = timeDisplay,
                    onClick = { showTimePicker = true }
                )

                HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 52.dp, end = 16.dp))

                // Priority Row
                DetailRow(
                    icon = Icons.Outlined.Flag,
                    label = "Priority",
                    value = currentTask.priority,
                    onClick = { showPriorityDialog = true }
                )

                HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 52.dp, end = 16.dp))

                // Status Row
                DetailRow(
                    icon = Icons.Filled.FormatListBulleted,
                    label = "Status",
                    value = if (currentTask.isCompleted) "Completed" else "Not Completed",
                    onClick = { viewModel.toggleTaskCompletion(currentTask) }
                )

                HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 52.dp, end = 16.dp))

                // Reminder Row
                val reminderDisplay = when (currentTask.reminderMinutesBefore) {
                    null, -1 -> "None"
                    0 -> "At time of event"
                    5 -> "5 minutes before"
                    15 -> "15 minutes before"
                    30 -> "30 minutes before"
                    60 -> "1 hour before"
                    1440 -> "1 day before"
                    else -> "${currentTask.reminderMinutesBefore} minutes before"
                }
                DetailRow(
                    icon = Icons.Outlined.Notifications,
                    label = "Reminder",
                    value = reminderDisplay,
                    onClick = { showReminderDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Notes Section
        Text(
            text = "Notes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = (-0.2).sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        TaskCard(
            elevation = 2.dp,
            shape = RoundedCornerShape(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openEditTask(currentTask.id) }
                    .padding(18.dp)
            ) {
                Text(
                    text = currentTask.notes.ifEmpty { "No additional notes. Tap to add notes." },
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = if (currentTask.notes.isEmpty()) TextTertiary else TextPrimary,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Full-width Primary Action Button: "Mark as Completed" / "Mark as Incomplete"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = ButtonShadowColor,
                    spotColor = ButtonShadowColor
                )
                .clip(RoundedCornerShape(14.dp))
                .background(AccentBlack)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = SurfaceWhite.copy(alpha = 0.2f)),
                    onClick = { viewModel.toggleTaskCompletion(currentTask) }
                )
                .testTag("detail_mark_completed_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (currentTask.isCompleted) "Mark as Incomplete" else "Mark as Completed",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = SurfaceWhite
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Full-width Secondary Action Button: "Edit Task"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceWhite)
                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = TextPrimary.copy(alpha = 0.08f)),
                    onClick = { viewModel.openEditTask(currentTask.id) }
                )
                .testTag("detail_edit_task_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Edit Task",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = TextPrimary.copy(alpha = 0.08f)),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = TextSecondary
        )
    }
}
