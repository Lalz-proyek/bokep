package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CutePawPrint(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Main palm pad (slightly oval, centered at bottom)
        val mainCenter = Offset(w * 0.5f, h * 0.65f)
        val mainRadius = w * 0.28f
        drawCircle(
            color = tint,
            radius = mainRadius,
            center = mainCenter
        )

        // 4 cute toe beans
        // Left outer
        drawCircle(
            color = tint,
            radius = w * 0.12f,
            center = Offset(w * 0.20f, h * 0.38f)
        )
        // Left inner
        drawCircle(
            color = tint,
            radius = w * 0.13f,
            center = Offset(w * 0.38f, h * 0.22f)
        )
        // Right inner
        drawCircle(
            color = tint,
            radius = w * 0.13f,
            center = Offset(w * 0.62f, h * 0.22f)
        )
        // Right outer
        drawCircle(
            color = tint,
            radius = w * 0.12f,
            center = Offset(w * 0.80f, h * 0.38f)
        )
    }
}
