package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.receiver.NotificationHelper
import com.example.ui.Tab
import com.example.ui.TodoViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.AddEditTaskSheet
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.GridMenuSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TaskDetailScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.BackgroundWhite
import com.example.ui.theme.TodoReminderTheme

class MainActivity : ComponentActivity() {

    private var viewModelInstance: TodoViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
        enableEdgeToEdge()

        val initialTaskId = intent?.getLongExtra("EXTRA_TASK_ID", -1L)?.takeIf { it != -1L }

        setContent {
            TodoReminderTheme {
                val viewModel: TodoViewModel = viewModel()
                viewModelInstance = viewModel

                LaunchedEffect(initialTaskId) {
                    if (initialTaskId != null) {
                        viewModel.openDetail(initialTaskId)
                    }
                }

                TodoReminderApp(viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val taskId = intent.getLongExtra("EXTRA_TASK_ID", -1L).takeIf { it != -1L }
        if (taskId != null) {
            viewModelInstance?.openDetail(taskId)
        }
    }
}

@Composable
fun TodoReminderApp(viewModel: TodoViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentDetailTaskId by viewModel.currentDetailTaskId.collectAsState()
    val showAddEditSheet by viewModel.showAddEditSheet.collectAsState()
    val showGridMenuSheet by viewModel.showGridMenuSheet.collectAsState()

    // Proper back-stack handling
    BackHandler(enabled = currentDetailTaskId != null) {
        viewModel.closeDetail()
    }

    BackHandler(enabled = currentDetailTaskId == null && currentTab != Tab.HOME) {
        viewModel.selectTab(Tab.HOME)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        if (currentDetailTaskId != null) {
            TaskDetailScreen(viewModel = viewModel)
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = BackgroundWhite,
                bottomBar = {
                    BottomNavBar(
                        selectedTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            ) { innerPadding ->
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.padding(innerPadding),
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        Tab.HOME -> HomeScreen(viewModel = viewModel)
                        Tab.TASKS -> TasksScreen(viewModel = viewModel)
                        Tab.CALENDAR -> CalendarScreen(viewModel = viewModel)
                        Tab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }

        // Add / Edit Task Modal Sheet
        if (showAddEditSheet) {
            AddEditTaskSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.closeAddEditTask() }
            )
        }

        // Grid Menu (Lists & Categories) Modal Sheet
        if (showGridMenuSheet) {
            GridMenuSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.setGridMenuVisible(false) }
            )
        }
    }
}
