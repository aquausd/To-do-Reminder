package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.TodoViewModel
import com.example.ui.components.TaskCard
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BackgroundWhite
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DividerLight
import com.example.ui.theme.SearchBackground
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SettingsScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsState()
    val defaultReminder by viewModel.defaultReminderMinutes.collectAsState()
    val startOfWeekSunday by viewModel.startOfWeekSunday.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userName) }

    var showReminderDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var tempCategory by remember { mutableStateOf("") }

    var showClearCompletedDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showSeedDialog by remember { mutableStateOf(false) }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    // Name dialog
    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Edit Name", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SearchBackground)
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                        cursorBrush = SolidColor(TextPrimary)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setUserName(tempName)
                        showNameDialog = false
                    }
                ) {
                    Text("Save", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Reminder dialog
    if (showReminderDialog) {
        val options = listOf(
            0 to "At time of event",
            5 to "5 minutes before",
            15 to "15 minutes before",
            30 to "30 minutes before",
            60 to "1 hour before"
        )
        AlertDialog(
            onDismissRequest = { showReminderDialog = false },
            title = { Text("Default Reminder", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column {
                    options.forEach { (mins, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDefaultReminderMinutes(mins)
                                    showReminderDialog = false
                                }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, fontSize = 15.sp, color = TextPrimary)
                            if (defaultReminder == mins) {
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

    // Add category dialog
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("New Category", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SearchBackground)
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = tempCategory,
                        onValueChange = { tempCategory = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 16.sp, color = TextPrimary),
                        cursorBrush = SolidColor(TextPrimary)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tempCategory.isNotBlank()) {
                            viewModel.addCategory(tempCategory)
                            tempCategory = ""
                        }
                        showAddCategoryDialog = false
                    }
                ) {
                    Text("Add", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Clear completed dialog
    if (showClearCompletedDialog) {
        AlertDialog(
            onDismissRequest = { showClearCompletedDialog = false },
            title = { Text("Clear Completed Tasks", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Remove all completed tasks from the database?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearCompletedTasks()
                        showClearCompletedDialog = false
                    }
                ) {
                    Text("Clear", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCompletedDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Reset dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Tasks", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Are you sure? This will delete all your tasks.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllData()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite
        )
    }

    // Seed sample tasks dialog
    if (showSeedDialog) {
        AlertDialog(
            onDismissRequest = { showSeedDialog = false },
            title = { Text("Load Reference Tasks", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Load the example tasks shown in the design preview (\"Design homepage\", \"Team stand-up\", etc.)?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.seedSampleTasks()
                        showSeedDialog = false
                    }
                ) {
                    Text("Load", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSeedDialog = false }) {
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
            .testTag("settings_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            letterSpacing = (-0.4).sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Profile
        SettingsSectionHeader("PROFILE")
        TaskCard(elevation = 1.dp, shape = RoundedCornerShape(16.dp)) {
            SettingsRow(
                icon = Icons.Outlined.Person,
                title = "Your Name",
                value = userName,
                onClick = {
                    tempName = userName
                    showNameDialog = true
                }
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section: Notifications & Reminders
        SettingsSectionHeader("NOTIFICATIONS & REMINDERS")
        TaskCard(elevation = 1.dp, shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notification",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text("Notifications Permission", fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (hasNotificationPermission) "Enabled" else "Tap to Allow",
                        fontSize = 14.sp,
                        color = if (hasNotificationPermission) TextSecondary else TextPrimary,
                        fontWeight = if (hasNotificationPermission) FontWeight.Normal else FontWeight.SemiBold
                    )
                }

                HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 50.dp, end = 16.dp))

                SettingsRow(
                    icon = Icons.Outlined.Notifications,
                    title = "Default Reminder",
                    value = "${defaultReminder}m before",
                    onClick = { showReminderDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section: Preferences
        SettingsSectionHeader("PREFERENCES")
        TaskCard(elevation = 1.dp, shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setStartOfWeek(!startOfWeekSunday)
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Start of Week",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text("Start of Week", fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (startOfWeekSunday) "Sunday" else "Monday",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 50.dp, end = 16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddCategoryDialog = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Category",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text("Add Custom Category", fontSize = 15.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("New", fontSize = 13.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Section: Data & Samples
        SettingsSectionHeader("DATA MANAGEMENT")
        TaskCard(elevation = 1.dp, shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SettingsRow(
                    icon = Icons.Outlined.PlaylistAdd,
                    title = "Load Reference Sample Tasks",
                    value = "Populate",
                    onClick = { showSeedDialog = true }
                )

                HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 50.dp, end = 16.dp))

                SettingsRow(
                    icon = Icons.Outlined.CleaningServices,
                    title = "Clear Completed Tasks",
                    value = "Clear",
                    onClick = { showClearCompletedDialog = true }
                )

                HorizontalDivider(thickness = 0.5.dp, color = DividerLight, modifier = Modifier.padding(start = 50.dp, end = 16.dp))

                SettingsRow(
                    icon = Icons.Filled.Delete,
                    title = "Reset All Tasks",
                    value = "Reset",
                    onClick = { showResetDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // About section
        SettingsSectionHeader("ABOUT")
        TaskCard(elevation = 1.dp, shape = RoundedCornerShape(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("Todo Reminder", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("Version 1.0 • Minimalist Productivity", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
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
            contentDescription = title,
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(title, fontSize = 15.sp, color = TextPrimary)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, color = TextSecondary)
    }
}
