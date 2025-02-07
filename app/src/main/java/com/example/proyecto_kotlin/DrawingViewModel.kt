package com.example.proyecto_kotlin

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DrawingState(
    val selectedcolor : Color = Color.Black,
    val currentPath : PathData? = null,
    val paths: List<PathData> = emptyList(),
    val brushSize: Float = 10f
)

val allcolors = listOf(
    Color.Black,
    Color.White,
    Color.LightGray,
    Color.DarkGray,
    Color.Gray,
    Color.Cyan,
    Color.Blue,
    Color.Red,
    Color.Green,
    Color.Magenta,
    Color.Yellow
)

data class PathData(
    val id : String,
    val color : Color,
    val path : MutableList<Offset>,
    val strokeWidth: Float
) {
    fun toPath(): Path {
        val path1 = Path()
        if (path.isNotEmpty()) {
            path1.moveTo(path.first().x, path.first().y)
            for (point in path.drop(1)) {
                path1.lineTo(point.x, point.y)
            }
        }
        return path1
    }
}

sealed interface DrawingAction{
    data object OnNewPathStart : DrawingAction
    data class OnDraw (val offset: Offset) : DrawingAction
    data object OnPathEnd : DrawingAction
    data class OnSelectColor (val color: Color) : DrawingAction
    data object OnClearCanvasClick : DrawingAction
    data class OnBrushSizeChanged (val newSize: Float) : DrawingAction

}

class DrawingViewModel: ViewModel() {

    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> get() = _bitmap

    fun setBitmap(bmp: Bitmap) {
        _bitmap.value = bmp
    }

   private fun onBrushSizeChanged(newSize: Float) {
        _state.update { it.copy(brushSize = newSize) }
    }

    fun onAction(action: DrawingAction) {
        when (action) {
            DrawingAction.OnClearCanvasClick -> clearCanvasClick()
            is DrawingAction.OnDraw -> onDraw(action.offset)
            DrawingAction.OnNewPathStart -> onNewPathStart()
            DrawingAction.OnPathEnd -> onPathEnd()
            is DrawingAction.OnSelectColor -> onSelectColor(action.color)
            is DrawingAction.OnBrushSizeChanged -> onBrushSizeChanged(action.newSize)
            else -> {}
        }
    }

    private fun onSelectColor(color: Color) {
        _state.update {
            it.copy(
                selectedcolor = color
            )

        }
    }

    private fun onPathEnd() {
        val currentPathData = state.value.currentPath ?: return
        _state.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPathData
            )

        }

    }

    private fun onNewPathStart() {
        _state.update {
            it.copy(
                currentPath = PathData(
                    id = System.currentTimeMillis().toString(),
                    color = it.selectedcolor,
                    path = mutableListOf(),
                    strokeWidth = it.brushSize
                )
            )
        }
    }

    private fun onDraw(offset: Offset) {
        val currentPathData = state.value.currentPath ?: return

        currentPathData.path.add(offset)
        _state.update {
            it.copy(
                currentPath = currentPathData
            )

        }
    }

    private fun clearCanvasClick() {
        _state.update {
            it.copy(
                currentPath = null,
                paths = emptyList()
            )

        }
    }

}

