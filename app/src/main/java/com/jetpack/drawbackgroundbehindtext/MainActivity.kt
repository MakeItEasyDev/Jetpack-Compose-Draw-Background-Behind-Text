package com.jetpack.drawbackgroundbehindtext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpack.drawbackgroundbehindtext.ui.theme.DrawBackgroundBehindTextTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrawBackgroundBehindTextTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Draw Background Behind Text",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        DrawBackgroundBehindText()
                    }
                }
            }
        }
    }
}

@Composable
fun DrawBackgroundBehindText() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        //Static text
        val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
        val selectedParts = listOf(
            "consectetur adipiscing",
            "officia deserunt",
            "dolore magna aliqua. Ut enim ad minim veniam, quis nostrud",
            "consequat.",
        )
        var selectedPartPaths by remember { mutableStateOf(listOf<Path>()) }

        Text(
            text = text,
            style = MaterialTheme.typography.h6,
            onTextLayout = { textLayoutResult ->
                selectedPartPaths = selectedParts.map { part ->
                    val cornerRadius = CornerRadius(x = 20f, y = 20f)
                    Path().apply {
                        val startIndex = text.indexOf(part)
                        val boundingBoxes = textLayoutResult
                            .getBoundingBoxesForRange(
                                start = startIndex,
                                end = startIndex + part.count()
                            )

                        for (i in boundingBoxes.indices) {
                            val boundingBox = boundingBoxes[i]
                            val leftCornerRoundRect = if (i == 0) cornerRadius else CornerRadius.Zero
                            val rightCornerRoundRect = if (i == boundingBoxes.indices.last) cornerRadius else CornerRadius.Zero

                            addRoundRect(
                                RoundRect(
                                    boundingBox.inflate(verticalDelta = -2f, horizontalDelta = 7f),
                                    topLeft = leftCornerRoundRect,
                                    topRight = rightCornerRoundRect,
                                    bottomRight = rightCornerRoundRect,
                                    bottomLeft = leftCornerRoundRect
                                )
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .drawBehind {
                    selectedPartPaths.forEach { path ->
                        drawPath(path, style = Fill, color = Color.Blue.copy(alpha = 0.2f))
                        drawPath(path, style = Stroke(width = 2f), color = Color.Blue)
                    }
                },
            lineHeight = 30.sp
        )
    }
}

fun Rect.inflate(verticalDelta: Float, horizontalDelta: Float) =
    Rect(
        left = left - horizontalDelta,
        top = top - verticalDelta,
        right = right + horizontalDelta,
        bottom = bottom + verticalDelta
    )

fun TextLayoutResult.getBoundingBoxesForRange(start: Int, end: Int): List<Rect> {
    var prevRect: Rect? = null
    var firstLineCharRect: Rect? = null
    val boundingBoxes = mutableListOf<Rect>()
    for (i in start..end) {
        val rect = getBoundingBox(i)
        val isLastRect = i == end

        if (isLastRect && firstLineCharRect == null) {
            firstLineCharRect = rect
            prevRect = rect
        }

        if (!isLastRect && rect.right == 0f) continue

        if (firstLineCharRect == null) {
            firstLineCharRect = rect
        } else if (prevRect != null) {
            if (prevRect.bottom != rect.bottom || isLastRect) {
                boundingBoxes.add(
                    firstLineCharRect.copy(right = prevRect.right)
                )
                firstLineCharRect = rect
            }
        }
        prevRect = rect
    }
    return boundingBoxes
}




















