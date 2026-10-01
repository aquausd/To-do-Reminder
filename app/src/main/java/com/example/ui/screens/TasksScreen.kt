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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Tab
import com.example.ui.TaskSort
import com.example.ui.TodoViewModel
import com.example.ui.components.SquareIconButton
import com.example.ui.components.TaskCard
import com.example.ui.components.TaskRow
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BackgroundWhite
import com.example.ui.theme.ButtonShadowColor
import com.example.ui.theme.SearchBackground
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.util.DateTimeUtils

@Composable
fun TasksScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.filteredTasks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val taskSort by viewModel.taskSort.collectAsState()
    val showCompleted by viewModel.showCompletedInTasks.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    val todaySubtitle = remember { DateTimeUtils.formatTodaySubtitle() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .windowInsetsPadding(WindowInsets.statusBars)
            .testTag("tasks_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Top Controls: Back on Left, Ellipsis on Right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SquareIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        onClick = { viewModel.selectTab(Tab.HOME) },
                        testTag = "tasks_back_button"
                    )

                    Box {
                        SquareIconButton(
                            icon = Icons.Filled.MoreHoriz,
                            contentDescription = "Task Options",
                            onClick = { showMenu = true },
                            testTag = "tasks_more_button"
                        )

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(SurfaceWhite)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (showCompleted) "Hide Completed" else "Show Completed",
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    viewModel.showCompletedInTasks.value = !showCompleted
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Sort: ${if (taskSort == TaskSort.TIME) "By Priority" else "By Time"}",
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    viewModel.taskSort.value =
                                        if (taskSort == TaskSort.TIME) TaskSort.PRIORITY else TaskSort.TIME
                                    showMenu = false
                                }
                            )

                            if (selectedCategory != null) {
                                DropdownMenuItem(
                                    text = { Text("Clear Category Filter", fontSize = 14.sp) },
                                    onClick = {
                                        viewModel.selectedCategoryFilter.value = null
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Screen Title
                Text(
                    text = "Today",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    letterSpacing = (-0.4).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Dynamic Date Subtitle
                Text(
                    text = todaySubtitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Search field
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SearchBackground)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchQuery.value = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_tasks_input"),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = TextPrimary
                            ),
                            cursorBrush = SolidColor(TextPrimary),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search tasks",
                                        fontSize = 15.sp,
                                        color = TextTertiary
                                    )
                                }
                                innerTextField()
                            }
                        )

                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear Search",
                                tint = TextSecondary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.searchQuery.value = "" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Tasks List Card
            item {
                TaskCard(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    if (tasks.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isNotEmpty()) "No tasks match \"$searchQuery\"" else "No tasks for today",
                                fontSize = 15.sp,
                                color = TextTertiary,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "+ Add a new task",
                                fontSize = 14.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = false, radius = 28.dp),
                                        onClick = { viewModel.openAddTask() }
                                    )
                                    .padding(6.dp)
                            )
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            tasks.forEachIndexed { index, task ->
                                TaskRow(
                                    task = task,
                                    onClick = { viewModel.openDetail(task.id) },
                                    onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                                    showDivider = index < tasks.size - 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(96.dp)) // Extra space for FAB and bottom bar
            }
        }

        // Black Primary Floating Action Button at lower-right
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
                        onClick = { viewModel.openAddTask() }
                    )
                    .testTag("tasks_fab_add"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Task",
                    tint = SurfaceWhite,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
