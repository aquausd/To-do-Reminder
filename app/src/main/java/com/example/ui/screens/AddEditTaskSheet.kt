package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TodoViewModel
import com.example.ui.components.NativeDatePicker
import com.example.ui.components.NativeTimePicker
import com.example.ui.components.TaskCard
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ButtonShadowColor
import com.example.ui.theme.ChipBackground
import com.example.ui.theme.DividerLight
import com.example.ui.theme.SearchBackground
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskSheet(
    viewModel: TodoViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editingTaskId by viewModel.editingTaskId.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val availableCategories by viewModel.categories.collectAsState()
    val defaultReminder by viewModel.defaultReminderMinutes.collectAsState()
    val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsState()

    val taskToEdit = remember(editingTaskId, allTasks) {
        if (editingTaskId != null) allTasks.find { it.id == editingTaskId } else null
    }

    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var category by remember { mutableStateOf(taskToEdit?.category ?: availableCategories.firstOrNull() ?: "Personal") }
    var dueDate by remember { mutableStateOf(taskToEdit?.dueDate ?: selectedCalendarDate) }
    var dueTime by remember { mutableStateOf(taskToEdit?.dueTime) }
    var priority by remember { mutableStateOf(taskToEdit?.priority ?: "Medium") }
    var notes by remember { mutableStateOf(taskToEdit?.notes ?: "") }
    var reminderMinutes by remember { mutableStateOf(taskToEdit?.reminderMinutesBefore ?: defaultReminder) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isTitleError by remember { mutableStateOf(false) }

    if (showDatePicker) {
        NativeDatePicker(
            currentIsoDate = dueDate,
            onDateSelected = {
                dueDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        NativeTimePicker(
            currentTime = dueTime,
            onTimeSelected = {
                dueTime = it
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.ime)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
                .testTag("add_edit_task_sheet")
        ) {
            // Header Row: Title & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (taskToEdit != null) "Edit Task" else "New Task",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SearchBackground)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Task Title Input
            Text(
                text = "TITLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SearchBackground)
                    .border(
                        width = if (isTitleError) 1.dp else 0.dp,
                        color = if (isTitleError) TextPrimary else BorderSubtle,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                BasicTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) isTitleError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    ),
                    cursorBrush = SolidColor(TextPrimary),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                text = "What needs to be done?",
                                fontSize = 16.sp,
                                color = TextTertiary
                            )
                        }
                        innerTextField()
                    }
                )
            }
            if (isTitleError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Please enter a task title",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category Chips Row
            Text(
                text = "CATEGORY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableCategories.forEach { cat ->
                    val isSelected = cat.equals(category, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AccentBlack else ChipBackground)
                            .clickable { category = cat }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) SurfaceWhite else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Configuration Options Card
            TaskCard(
                elevation = 1.dp,
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Date row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Date",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Date", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = DateTimeUtils.formatStandardDate(dueDate),
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 44.dp, end = 14.dp))

                    // Time row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePicker = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Time",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Time", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = DateTimeUtils.formatTime12h(dueTime) ?: "Set time (Optional)",
                            fontSize = 14.sp,
                            color = if (dueTime != null) TextPrimary else TextTertiary
                        )
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 44.dp, end = 14.dp))

                    // Priority row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = "Priority",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Priority", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Low", "Medium", "High").forEach { p ->
                                val selected = priority.equals(p, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) AccentBlack else SearchBackground)
                                        .clickable { priority = p }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = p,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) SurfaceWhite else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 44.dp, end = 14.dp))

                    // Reminder row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Reminder",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Reminder", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))

                        val reminderLabel = when (reminderMinutes) {
                            null, -1 -> "None"
                            0 -> "At time"
                            5 -> "5m before"
                            15 -> "15m before"
                            30 -> "30m before"
                            60 -> "1h before"
                            else -> "${reminderMinutes}m before"
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SearchBackground)
                                .clickable {
                                    reminderMinutes = when (reminderMinutes) {
                                        null, -1 -> 0
                                        0 -> 15
                                        15 -> 30
                                        30 -> 60
                                        else -> -1
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = reminderLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Notes Section
            Text(
                text = "NOTES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SearchBackground)
                    .padding(14.dp)
            ) {
                BasicTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_notes_input"),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    ),
                    cursorBrush = SolidColor(TextPrimary),
                    decorationBox = { innerTextField ->
                        if (notes.isEmpty()) {
                            Text(
                                text = "Add details, checklists, or notes...",
                                fontSize = 14.sp,
                                color = TextTertiary
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save / Create Button
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
                        onClick = {
                            if (title.isBlank()) {
                                isTitleError = true
                            } else {
                                viewModel.saveTask(
                                    title = title.trim(),
                                    category = category.trim().ifEmpty { "Personal" },
                                    dueDate = dueDate,
                                    dueTime = dueTime,
                                    priority = priority,
                                    notes = notes.trim(),
                                    reminderMinutes = if (reminderMinutes == -1) null else reminderMinutes,
                                    existingId = taskToEdit?.id
                                )
                            }
                        }
                    )
                    .testTag("save_task_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (taskToEdit != null) "Update Task" else "Create Task",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SurfaceWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
