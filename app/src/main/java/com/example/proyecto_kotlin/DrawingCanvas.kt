package com.example.proyecto_kotlin


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



@Composable
    fun DrawingCanvas(
        paths: List<PathData>,
        currentPath: PathData?,
        onAction: (DrawingAction) -> Unit,
        brushSize: Float,
        state: DrawingState,
        modifier: Modifier = Modifier
    ) {

    val context = LocalContext.current
    val texture = remember {
        ImageBitmap.imageResource(context.resources, R.drawable.canvas_546877_1920)
    }

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(paths) {
        withContext(Dispatchers.IO) {
        bitmap = createBitmapFromCanvasWithTexture(
            texture.width,
            texture.height,
            paths = paths,
            context
        )
            }
    }

    Canvas(
        modifier = modifier
            .clipToBounds()
            .background(Color.Transparent)
            .fillMaxSize()
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = {
                        onAction(DrawingAction.OnNewPathStart)
                    },
                    onDragEnd = {
                        onAction(DrawingAction.OnPathEnd)
                    },
                    onDrag = { change, _ ->
                        onAction(DrawingAction.OnDraw(change.position))
                    },
                    onDragCancel = {
                        onAction(DrawingAction.OnPathEnd)
                    },
                )
            }

    ) {


        android.util.Log.d("BitmapCheck", "¿Bitmap generado correctamente? ${bitmap != null}")

        bitmap?.let { safeBitmap ->
            drawIntoCanvas { canvas ->

                canvas.nativeCanvas.drawBitmap(
                    safeBitmap,
                    null,
                    android.graphics.Rect(0, 0, size.width.toInt(), size.height.toInt()),
                    Paint()
                )
            }


            drawImage(
                safeBitmap.asImageBitmap()
            )

            paths.fastForEach { pathData ->
                drawPath(
                    path = Path().apply {
                        pathData.path.forEachIndexed { index, offset ->
                            if (index == 0) moveTo(offset.x, offset.y)
                            else lineTo(offset.x, offset.y)
                        }
                    },

                    color = pathData.color,
                    style = Stroke(
                        width = pathData.strokeWidth, pathEffect = PathEffect.cornerPathEffect(10f)
                    ),

                    )

            }
            currentPath?.let {
                drawPath(
                    path = Path().apply {
                        it.path.forEachIndexed { index, offset ->
                            if (index == 0) moveTo(offset.x, offset.y)
                            else lineTo(offset.x, offset.y)
                        }
                    },
                    color = it.color,
                    style = Stroke(width = it.strokeWidth)
                )
            }
        }
    }
    bitmap?.let { BotonesSave(it,context,onAction,state.selectedcolor,brushSize,
        onBrushSizeChanged = { newSize ->
            onAction(DrawingAction.OnBrushSizeChanged(newSize))
        }

    ) }
}
   /* fun <DrawScope> DrawScope.drawPath(
        path: List<Offset>,
        color: Color,
        thickness: Float = 10f,
        style: Stroke
    ) {
        val smoothedPath = Path().apply {
            if (path.isNotEmpty()) {
                moveTo(path.first().x, path.first().y)
                val smoothness = 5
                for (i in 1..path.lastIndex) {
                    val from = path[i - 1]
                    val to = path[i]
                    val dx = abs(from.x - to.x)
                    val dy = abs(from.y - to.y)
                    if (dx >= smoothness || dy >= smoothness) {
                        quadraticBezierTo(
                            x1 = (from.x + to.x) / 2f,
                            y1 = (from.y + to.y) / 2f,
                            x2 = to.x,
                            y2 = to.y
                        )
                    }
                }
            }
        }
       /* drawPath(
            path = smoothedPath,
            color = color,
            style = Stroke(
                width = thickness,
                cap = StrokeCap.Butt,
                join = StrokeJoin.Round
            )
        )*/
    }

    fun List<Offset>.toPath(): Path {
        return Path().apply {
            if (isNotEmpty()) {
                moveTo(first().x, first().y)
                for (offset in drop(1)) {
                    lineTo(offset.x, offset.y)
                }
            }
        }
    }

    fun List<PathData>.toPathList(): List<Path> {
        return this.map { it.toPath() }
    }
*/

   suspend fun createBitmapFromCanvasWithTexture(
        width: Int,
        height: Int,
        paths: List<PathData>,
        context: Context
    ): Bitmap = withContext(Dispatchers.Default) {
       // Cargar la textura del lienzo (asegúrate de que esta imagen esté en la carpeta res/drawable)
       val textureBitmap =
           BitmapFactory.decodeResource(context.resources, R.drawable.canvas_546877_1920)

       // Crear el bitmap donde vamos a dibujar
       val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
       val canvas = android.graphics.Canvas(bitmap)

       // Dibujar la textura en el fondo
       val paint = Paint()
       canvas.drawBitmap(textureBitmap, 0f, 0f, paint)

       // Establecer un Paint similar al usado en Compose para los trazos
      /* val pathPaint = Paint().apply {
           color = Color.Black.toArgb()
           strokeWidth = 5f
           style = Paint.Style.STROKE
       }*/

      // val pathList = paths.toPathList()

       // Dibujar los trazos
       paths.forEach { pathData ->
           val path = pathData.toPath()
           val pathPaint = Paint().apply {
               color = pathData.color.toArgb() // Convertimos Color de Compose a Android
               strokeWidth = pathData.strokeWidth
               style = Paint.Style.STROKE
           }
           canvas.drawPath(path.asAndroidPath(), pathPaint)
       }

        return@withContext bitmap
        }


@Composable
fun BotonesSave(
    bitmap: Bitmap,
    context: Context,
    onAction: (DrawingAction) -> Unit,
    selectedColor: Color,
    brushSize: Float,
    onBrushSizeChanged : (Float) -> Unit
    ) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    HsvColorPicker(
        hue = selectedColor.toHsv()[0],
        saturation = 1f,
        brightness = 1f,
        onColorChanged = { newColor -> onAction(DrawingAction.OnSelectColor(newColor)) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(100.dp)
    )

Column(modifier = Modifier
    .fillMaxWidth()
    .height(80.dp)
    .background(Color.LightGray),
    verticalArrangement = Arrangement.Center) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
            .padding(0.dp,0.dp,0.dp,0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("T. pincel: ${brushSize.toInt()}", fontSize = 14.sp, modifier = Modifier.padding(8.dp,0.dp,0.dp,0.dp), fontFamily = FontFamily(
            Font(R.font.grape_nuts),
        ), fontWeight = FontWeight.Black)

        Slider(
            value = brushSize,
            onValueChange = { newSize ->
                onBrushSizeChanged(newSize)
            },
            valueRange = 1f..50f,
            colors = SliderDefaults.colors(
                thumbColor = Color.Blue,
                activeTrackColor = Color.Blue,
                inactiveTrackColor = Color.Black

            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp,0.dp,0.dp,8.dp)
        )

    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.spacedBy(35.dp),
        verticalAlignment = Alignment.CenterVertically
    ) { Button(
        onClick = {
            android.util.Log.d("SaveButton", "Botón de guardar presionado")
            saveBitmapToGallery(context, bitmap)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.size(72.dp)

    ) {
        Image(
            painter = painterResource(R.drawable.baseline_save_alt_32),
            contentDescription = "Guardar",
            contentScale = ContentScale.Crop,
        )
    }


        Button(
            onClick = {onAction(DrawingAction.OnClearCanvasClick)},
            modifier = Modifier.background(Color.Transparent)
        ) {
            Text("Limpiar lienzo", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }

        Button(
            onClick = {
                shareLastSavedImage(context)

            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),

            ) {

            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Compartir",
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )
        }

    }
}
}


fun Color.toHsv(): FloatArray {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(this.toArgb(), hsv) // Convierte Compose Color a HSV
    return hsv
}

fun saveBitmapToGallery(context: Context, bitmap: Bitmap)  {
    android.util.Log.d("SaveBitmap", "Entrando a saveBitmapToGallery()")

     try {

        val filename = "Dibujo_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Dibujos")
            put(MediaStore.Images.Media.IS_PENDING, 1)

        }
         val resolver = context.contentResolver

         val imageuri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)


         imageuri.let { uri ->
             uri?.let {
                 resolver.openOutputStream(it)?.use { outputStream ->
                     if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                         android.util.Log.d("SaveBitmap", "Imagen guardada con éxito en la galería: $uri")

                     } else {
                         android.util.Log.e("SaveBitmap", "Error al comprimir la imagen")

                     }
                 }
             } ?:
             android.util.Log.e("SaveBitmap", "No se pudo abrir el OutputStream")

             // Finaliza el guardado y hace que la imagen sea visible
             contentValues.clear()
             contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
             if (uri != null) {
                 resolver.update(uri, contentValues, null, null)
             }

         }

        android.util.Log.d("SaveBitmap", "Guardado completado")
    }catch (e: Exception) {
        android.util.Log.e("SaveBitmap", "Error al guardar imagen", e)
    }

}

fun shareLastSavedImage(context: Context) {
    val imageUri = getLastSavedImageUri(context)

    if (imageUri == null) {
        android.util.Log.e("ShareImage", "No se encontró la imagen para compartir")
        return
    }

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, imageUri)
        type = "image/png"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    try {
        context.startActivity(Intent.createChooser(shareIntent, "Compartir imagen"))
    } catch (e: Exception) {
        android.util.Log.e("ShareImage", "Error al compartir la imagen", e)
    }
}

fun getLastSavedImageUri(context: Context): Uri? {
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
    val selectionArgs = arrayOf("Pictures/Dibujos%")
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val id = cursor.getLong(idColumn)
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
        }
    }

    return null
}


