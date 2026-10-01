package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TodoViewModel
import com.example.ui.components.AgendaTaskRow
import com.example.ui.components.NativeDatePicker
import com.example.ui.components.SquareIconButton
import com.example.ui.components.TaskCard
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BackgroundWhite
import com.example.ui.theme.ButtonShadowColor
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.util.DateTimeUtils
import java.util.Calendar

@Composable
fun CalendarScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedCalendarDate.collectAsState()
    val calendarTasks by viewModel.calendarTasks.collectAsState()
    val startOfWeekSunday by viewModel.startOfWeekSunday.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val daysOfWeek = remember(selectedDate, startOfWeekSunday) {
        DateTimeUtils.getDaysForWeek(selectedDate, startOfWeekSunday)
    }

    val monthYearTitle = remember(selectedDate) {
        DateTimeUtils.formatMonthYear(selectedDate)
    }

    if (showDatePicker) {
        NativeDatePicker(
            currentIsoDate = selectedDate,
            onDateSelected = {
                viewModel.selectedCalendarDate.value = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("calendar_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Top Bar: Month & Year Title on Left, Ellipsis on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthYearTitle,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.4).sp,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false, radius = 32.dp),
                                onClick = { showDatePicker = true }
                            )
                            .testTag("calendar_month_title")
                    )

                    Box {
                        SquareIconButton(
                            icon = Icons.Filled.MoreHoriz,
                            contentDescription = "Calendar Options",
                            onClick = { showMenu = true },
                            testTag = "calendar_more_button"
                        )

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(SurfaceWhite)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Jump to Today", fontSize = 14.sp) },
                                onClick = {
                                    viewModel.selectedCalendarDate.value = DateTimeUtils.todayIso()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Pick Date...", fontSize = 14.sp) },
                                onClick = {
                                    showDatePicker = true
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Previous Week", fontSize = 14.sp) },
                                onClick = {
                                    val cal = DateTimeUtils.parseIsoCalendar(selectedDate)
                                    cal.add(Calendar.DAY_OF_YEAR, -7)
                                    viewModel.selectedCalendarDate.value =
                                        String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Next Week", fontSize = 14.sp) },
                                onClick = {
                                    val cal = DateTimeUtils.parseIsoCalendar(selectedDate)
                                    cal.add(Calendar.DAY_OF_YEAR, 7)
                                    viewModel.selectedCalendarDate.value =
                                        String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
                                    showMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Weekday / Date Strip exactly like reference
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    daysOfWeek.forEach { dayItem ->
                        val isSelected = dayItem.isoDate == selectedDate

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = false, radius = 24.dp),
                                    onClick = {
                                        viewModel.selectedCalendarDate.value = dayItem.isoDate
                                    }
                                )
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                                .testTag("calendar_day_${dayItem.isoDate}")
                        ) {
                            Text(
                                text = dayItem.dayOfWeekLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) AccentBlack else BackgroundWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayItem.dayNumber.toString(),
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SurfaceWhite else TextPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Subtle chevron / pull indicator under date strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand Calendar",
                        tint = TextTertiary,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showDatePicker = true }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Chronological Agenda Tasks Card
            item {
                TaskCard(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    if (calendarTasks.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tasks scheduled for this day",
                                fontSize = 15.sp,
                                color = TextTertiary,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "+ Add Task",
                                fontSize = 14.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = false, radius = 28.dp),
                                        onClick = { viewModel.openAddTask(selectedDate) }
                                    )
                                    .padding(6.dp)
                            )
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            calendarTasks.forEachIndexed { index, task ->
                                AgendaTaskRow(
                                    task = task,
                                    onClick = { viewModel.openDetail(task.id) },
                                    onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                                    showDivider = index < calendarTasks.size - 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(96.dp)) // Extra space for FAB and bottom bar
            }
        }

        // Add task button for selected date
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        ambientColor = ButtonShadowColor,
                        spotColor = ButtonShadowColor
                    )
                    .clip(CircleShape)
                    .background(AccentBlack)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = SurfaceWhite.copy(alpha = 0.25f)),
                        onClick = { viewModel.openAddTask(selectedDate) }
                    )
                    .testTag("calendar_fab_add"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Task for Selected Date",
                    tint = SurfaceWhite,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
