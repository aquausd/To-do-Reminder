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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Tab
import com.example.ui.TodoViewModel
import com.example.ui.components.Grid4DotsIcon
import com.example.ui.components.SquareIconButton
import com.example.ui.components.TaskCard
import com.example.ui.components.TaskRow
import com.example.ui.components.UpcomingTaskRow
import com.example.ui.theme.BackgroundWhite
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.SearchBackground
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.util.DateTimeUtils

@Composable
fun HomeScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val allCount by viewModel.allTasksCount.collectAsState()
    val todayCount by viewModel.todayTasksCount.collectAsState()
    val upcomingCount by viewModel.upcomingTasksCount.collectAsState()
    val completedCount by viewModel.completedTasksCount.collectAsState()

    val todayTasks by viewModel.todayTasks.collectAsState()
    val upcomingTasks by viewModel.upcomingTasks.collectAsState()

    val greeting = remember { DateTimeUtils.formatHeaderGreeting() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 22.dp)
            .testTag("home_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Top action controls (4-dots grid on left, Plus on right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceWhite)
                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = TextPrimary.copy(alpha = 0.12f)),
                            onClick = { viewModel.setGridMenuVisible(true) }
                        )
                        .testTag("home_grid_menu_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Grid4DotsIcon(size = 18.dp)
                }

                SquareIconButton(
                    icon = Icons.Filled.Add,
                    contentDescription = "Add Task",
                    onClick = { viewModel.openAddTask() },
                    testTag = "home_add_task_button"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Large greeting split over two lines
            Text(
                text = greeting,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                lineHeight = 36.sp,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = userName,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                lineHeight = 36.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Static product tagline
            Text(
                text = "Stay focused. Get things done.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                letterSpacing = 0.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4-Column Statistics Summary Card
            TaskCard(
                elevation = 2.dp,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatColumn(
                        icon = Icons.Outlined.RadioButtonUnchecked,
                        count = allCount,
                        label = "All Tasks",
                        onClick = { viewModel.selectTab(Tab.TASKS) },
                        testTag = "stat_all_tasks"
                    )
                    StatColumn(
                        icon = Icons.Filled.WbSunny,
                        count = todayCount,
                        label = "Today",
                        onClick = { viewModel.selectTab(Tab.TASKS) },
                        testTag = "stat_today"
                    )
                    StatColumn(
                        icon = Icons.Filled.CalendarMonth,
                        count = upcomingCount,
                        label = "Upcoming",
                        onClick = { viewModel.selectTab(Tab.CALENDAR) },
                        testTag = "stat_upcoming"
                    )
                    StatColumn(
                        icon = Icons.Outlined.CheckCircle,
                        count = completedCount,
                        label = "Completed",
                        onClick = {
                            viewModel.selectTab(Tab.TASKS)
                            viewModel.showCompletedInTasks.value = true
                        },
                        testTag = "stat_completed"
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Today Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.2).sp
                )
                Text(
                    text = "See All",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false, radius = 24.dp),
                            onClick = { viewModel.selectTab(Tab.TASKS) }
                        )
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                        .testTag("today_see_all_button")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Today Task Group Card
            TaskCard(
                elevation = 2.dp,
                shape = RoundedCornerShape(18.dp)
            ) {
                if (todayTasks.isEmpty()) {
                    EmptySectionState(
                        message = "No tasks scheduled for today",
                        buttonText = "+ Add Task",
                        onAction = { viewModel.openAddTask() }
                    )
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        todayTasks.forEachIndexed { index, task ->
                            TaskRow(
                                task = task,
                                onClick = { viewModel.openDetail(task.id) },
                                onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                                showDivider = index < todayTasks.size - 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Upcoming Section Header
            Text(
                text = "Upcoming",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-0.2).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Upcoming Task Group Card
            TaskCard(
                elevation = 2.dp,
                shape = RoundedCornerShape(18.dp)
            ) {
                if (upcomingTasks.isEmpty()) {
                    EmptySectionState(
                        message = "No upcoming tasks",
                        buttonText = "+ Schedule Task",
                        onAction = { viewModel.openAddTask(DateTimeUtils.tomorrowIso()) }
                    )
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        upcomingTasks.take(5).forEachIndexed { index, task ->
                            UpcomingTaskRow(
                                task = task,
                                onClick = { viewModel.openDetail(task.id) },
                                showDivider = index < upcomingTasks.take(5).size - 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatColumn(
    icon: ImageVector,
    count: Int,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 32.dp),
                onClick = onClick
            )
            .padding(horizontal = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SearchBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = (-0.3).sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = TextSecondary
        )
    }
}

@Composable
private fun EmptySectionState(
    message: String,
    buttonText: String,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = TextTertiary,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buttonText,
            fontSize = 13.sp,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 24.dp),
                    onClick = onAction
                )
                .padding(4.dp)
        )
    }
}
