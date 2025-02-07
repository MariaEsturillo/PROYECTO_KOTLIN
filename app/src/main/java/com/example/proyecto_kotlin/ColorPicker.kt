package com.example.proyecto_kotlin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HsvColorPicker(
    hue: Float,
    saturation: Float,
    brightness: Float,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier
) {

    var currentSaturation by remember { mutableStateOf(saturation) }
    var currentBrightness by remember { mutableStateOf(brightness) }

    Column(modifier = modifier
        .background(Color.LightGray)) {

        Row (modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val newHue = (offset.x / size.width) * 360f
                            onColorChanged(Color.hsv(newHue, 1f, 1f))
                        }
                    }
            ) {
                val width = size.width
                val height = size.height

                for (i in 0 until width.toInt()) {
                    drawRect(
                        color = Color.hsv((i / width) * 360f, 1f, 1f),
                        topLeft = Offset(i.toFloat(), 0f),
                        size = androidx.compose.ui.geometry.Size(1f, height)
                    )
                }
            }
        }
        Row( modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = "Sat.:",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp,0.dp,0.dp,0.dp),
                    fontFamily = FontFamily(Font(R.font.grape_nuts)),
                    fontWeight = FontWeight.Black

                )
                Slider(
                    value = currentSaturation,
                    onValueChange = {
                        currentSaturation = it
                        onColorChanged(Color.hsv(hue, currentSaturation, currentBrightness))
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(6.dp)
                        .width(160.dp)
                )

                Text(
                    text = "Bri.:",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp,0.dp,0.dp,0.dp),
                    fontFamily = FontFamily(Font(R.font.grape_nuts)),
                    fontWeight = FontWeight.Black

                )
                Slider(
                    value = currentBrightness,
                    onValueChange = {
                        currentBrightness = it
                        onColorChanged(Color.hsv(hue, currentSaturation, currentBrightness))
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(6.dp)
                        .width(160.dp)
                )
        }
    }
}

