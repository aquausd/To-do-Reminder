package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TextPrimary

@Composable
fun Grid4DotsIcon(
    modifier: Modifier = Modifier,
    tint: Color = TextPrimary,
    size: Dp = 18.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val dotSize = this.size.width * 0.36f
        val gap = this.size.width * 0.28f
        val corner = dotSize * 0.32f

        // Top-left
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, 0f),
            size = Size(dotSize, dotSize),
            cornerRadius = CornerRadius(corner, corner)
        )
        // Top-right
        drawRoundRect(
            color = tint,
            topLeft = Offset(dotSize + gap, 0f),
            size = Size(dotSize, dotSize),
            cornerRadius = CornerRadius(corner, corner)
        )
        // Bottom-left
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, dotSize + gap),
            size = Size(dotSize, dotSize),
            cornerRadius = CornerRadius(corner, corner)
        )
        // Bottom-right
        drawRoundRect(
            color = tint,
            topLeft = Offset(dotSize + gap, dotSize + gap),
            size = Size(dotSize, dotSize),
            cornerRadius = CornerRadius(corner, corner)
        )
    }
}
