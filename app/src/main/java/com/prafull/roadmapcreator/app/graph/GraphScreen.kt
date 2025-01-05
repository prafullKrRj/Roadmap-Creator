package com.prafull.roadmapcreator.app.graph

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prafull.roadmapcreator.R
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(
    graph: String,
    title: String,
    navController: NavController,
    onScreenshotTaken: (Uri?) -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Expanded, skipHiddenState = false
        )
    )
    val lightTheme by rememberSaveable {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            captureWebView(
                context = context,
                webView = webViewRef.value,
                onScreenshotTaken = {
                    onScreenshotTaken(it)
                    Toast.makeText(
                        context,
                        "Screenshot saved to gallery",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onError = onError
            )
        } else {
            onError("Storage permission denied")
        }
    }
    Column(
        Modifier
            .systemBarsPadding()
            .padding(top = 12.dp)
    ) {
        BottomSheetScaffold(sheetContent = {
            Mermaid(string = graph, lightTheme, onWebViewCreated = { webView ->
                webViewRef.value = webView
            })
        }, sheetSwipeEnabled = false, sheetDragHandle = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(Modifier.weight(.85f), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = navController::popBackStack) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Close")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = title.trim(), style = MaterialTheme.typography.titleMedium)
                }
                IconButton(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10 and above don't need storage permission for saving to Pictures
                        captureWebView(
                            context = context,
                            webView = webViewRef.value,
                            onScreenshotTaken = {
                                onScreenshotTaken(it)
                                Toast.makeText(
                                    context,
                                    "Screenshot saved to gallery",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onError = onError
                        )
                    } else {
                        // Request storage permission for Android 9 and below
                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }, Modifier.weight(.15f)) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.outline_camera_alt_24),
                        contentDescription = "Theme"
                    )
                }
            }
        }, scaffoldState = sheetState, modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
        ) {}
    }
}

private fun captureWebView(
    context: Context,
    webView: WebView?,
    onScreenshotTaken: (Uri?) -> Unit,
    onError: (String) -> Unit
) {
    if (webView == null) {
        onError("WebView not initialized")
        return
    }

    try {
        // Create a bitmap with the WebView's dimensions
        val screenshot = Bitmap.createBitmap(
            webView.width,
            webView.height,
            Bitmap.Config.ARGB_8888
        )

        // Draw the WebView content onto the bitmap
        val canvas = Canvas(screenshot)
        webView.draw(canvas)

        // Save the bitmap to the device
        saveBitmapToGallery(
            context = context,
            bitmap = screenshot,
            onSaved = onScreenshotTaken,
            onError = onError
        )
    } catch (e: Exception) {
        onError("Failed to capture screenshot: ${e.message}")
    }
}

private fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    onSaved: (Uri?) -> Unit,
    onError: (String) -> Unit
) {
    val filename = "Mermaid_Diagram_${System.currentTimeMillis()}.png"

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore for Android 10 and above
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            context.contentResolver.let { resolver ->
                val uri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let { imageUri ->
                    resolver.openOutputStream(imageUri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        onSaved(imageUri)
                    }
                } ?: onError("Failed to create image file")
            }
        } else {
            // Legacy approach for Android 9 and below
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File(imagesDir, filename)

            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                // Notify the gallery about the new image
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(imageFile.absolutePath),
                    arrayOf("image/png"),
                    null
                )
                onSaved(Uri.fromFile(imageFile))
            }
        }
    } catch (e: Exception) {
        onError("Failed to save image: ${e.message}")
    } finally {
        bitmap.recycle()
    }
}