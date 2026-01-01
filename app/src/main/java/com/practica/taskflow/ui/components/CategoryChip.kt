package com.practica.taskflow.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChip(
    categoryName: String,
    categoryColor: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = categoryName,
        modifier = modifier
            .background(
                color = categoryColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = MaterialTheme.typography.bodySmall,
        color = categoryColor,
        fontWeight = FontWeight.Medium
    )
}