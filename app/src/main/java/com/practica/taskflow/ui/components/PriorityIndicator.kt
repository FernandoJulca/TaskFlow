package com.practica.taskflow.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.taskflow.data.local.entities.Priority
import com.practica.taskflow.ui.theme.PriorityHigh
import com.practica.taskflow.ui.theme.PriorityLow
import com.practica.taskflow.ui.theme.PriorityMedium

@Composable
fun PriorityIndicator(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (priority) {
        Priority.HIGH -> PriorityHigh to "Alta"
        Priority.MEDIUM -> PriorityMedium to "Media"
        Priority.LOW -> PriorityLow to "Baja"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Círculo de color
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        // Texto de prioridad
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}