package com.smallApps.Notes

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smallApps.Notes.viewModel.ItemViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppWithGridBackground()
            }
        }
    }
}

@Composable
fun AppWithGridBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        GridBackground(
            lineColor = Color(0xFF9AA0A6),
            cellSize = 32.dp
        )

        val app = LocalContext.current.applicationContext as Application
        val vm: ItemViewModel = viewModel(factory = ItemViewModel.factory(app))

        MainScreen(vm = vm)
    }
}

@Composable
fun GridBackground(
    lineColor: Color = Color(0xFF6B7A90),
    cellSize: Dp = 20.dp
) {
    Canvas(Modifier.fillMaxSize()) {
        drawRect(Color(0xFFFFFFFF))

        val step = cellSize.toPx()
        val w = size.width
        val h = size.height

        val minor = lineColor.copy(alpha = 0.10f)
        val major = lineColor.copy(alpha = 0.22f)

        var x = 0f
        var i = 0
        while (x < w) {
            drawLine(
                color = if (i % 4 == 0) major else minor,
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = if (i % 4 == 0) 1.2f else 0.7f
            )
            x += step; i++
        }

        var y = 0f
        i = 0
        while (y < h) {
            drawLine(
                color = if (i % 4 == 0) major else minor,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = if (i % 4 == 0) 1.2f else 0.7f
            )
            y += step; i++
        }
    }
}
