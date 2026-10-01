package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.SurfaceWhite

@Composable
fun CompletionCheckbox(
    isCompleted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
    testTag: String = "completion_checkbox"
) {
    val bgColor by animateColorAsState(
        targetValue = if (isCompleted) AccentBlack else Color.Transparent,
        animationSpec = tween(durationMillis = 180),
        label = "checkbox_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isCompleted) AccentBlack else Color(0xFFC7C7CC),
        animationSpec = tween(durationMillis = 180),
        label = "checkbox_border"
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor)
            .border(width = 1.5.dp, color = borderColor, shape = CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 20.dp),
                onClick = onToggle
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = SurfaceWhite,
                modifier = Modifier.size(size * 0.65f)
            )
        }
    }
}
