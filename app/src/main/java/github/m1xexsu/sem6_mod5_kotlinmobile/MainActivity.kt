package github.m1xexsu.sem6_mod5_kotlinmobile

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
  import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import github.m1xexsu.sem6_mod5_kotlinmobile.ui.theme.Sem6_mod5_kotlinmobileTheme
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sem6_mod5_kotlinmobileTheme {
                GalleryApp()
            }
        }
    }
}

data class PhotoItem(
    val file: File
)

@Preview(showSystemUi = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryApp() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val picturesDir = remember {
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: context.filesDir
    }

    var photos by remember { mutableStateOf(scanPhotos(picturesDir)) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPhoto by remember { mutableStateOf<PhotoItem?>(null) }
    var photoToExportAfterPermission by remember { mutableStateOf<PhotoItem?>(null) }

    val captureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photos = scanPhotos(picturesDir)
        } else {
            pendingCaptureUri?.let { uri ->
                runCatching { context.contentResolver.delete(uri, null, null) }
            }
        }
        pendingCaptureUri = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingCaptureUri = createOutputUri(context, picturesDir)
            pendingCaptureUri?.let { captureLauncher.launch(it) }
        } else {
            scope.launch { snackbarHostState.showSnackbar("Разрешение CAMERA не выдано") }
        }
    }

    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val photo = photoToExportAfterPermission
        photoToExportAfterPermission = null
        if (!granted || photo == null) {
            scope.launch { snackbarHostState.showSnackbar("Экспорт недоступен без разрешения") }
            return@rememberLauncherForActivityResult
        }

        if (exportPhotoToGallery(context, photo.file)) {
            scope.launch { snackbarHostState.showSnackbar("Фото добавлено в галерею") }
        } else {
            scope.launch { snackbarHostState.showSnackbar("Не удалось экспортировать фото") }
        }
    }

    fun requestTakePhoto() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            pendingCaptureUri = createOutputUri(context, picturesDir)
            pendingCaptureUri?.let { captureLauncher.launch(it) }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun requestExport(photo: PhotoItem) {
        val requiresLegacyWrite = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
        if (!requiresLegacyWrite || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (exportPhotoToGallery(context, photo.file)) {
                scope.launch { snackbarHostState.showSnackbar("Фото добавлено в галерею") }
            } else {
                scope.launch { snackbarHostState.showSnackbar("Не удалось экспортировать фото") }
            }
            return
        }

        photoToExportAfterPermission = photo
        writePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = ::requestTakePhoto) {
                Text("+")
            }
        }
    ) { padding ->
        if (photos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("У вас пока нет фото", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(4.dp)
            ) {
                items(photos, key = { it.file.absolutePath }) { item ->
                    PhotoGridCell(
                        item = item,
                        onExport = { requestExport(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoGridCell(
    item: PhotoItem,
    onExport: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(120.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = { menuExpanded = true } )
    ) {
        val bitmap = remember(item.file.absolutePath, item.file.lastModified()) {
            BitmapFactory.decodeFile(item.file.absolutePath)
        }

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = item.file.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(
            onClick = { menuExpanded = true },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text("...")
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Экспорт в галерею") },
                onClick = {
                    menuExpanded = false
                    onExport()
                }
            )
        }
    }
}

private fun scanPhotos(dir: File): List<PhotoItem> {
    return dir.listFiles()
        ?.filter { it.isFile && it.extension.equals("jpg", ignoreCase = true) }
        ?.sortedByDescending { it.lastModified() }
        ?.map { PhotoItem(file = it) }
        .orEmpty()
}

private fun createOutputUri(context: Context, picturesDir: File): Uri {
    if (!picturesDir.exists()) {
        picturesDir.mkdirs()
    }

    val fileName = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
    val file = File(picturesDir, fileName)

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

private fun exportPhotoToGallery(context: Context, sourceFile: File): Boolean {
    val resolver = context.contentResolver
    val name = sourceFile.name

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
    }

    val targetUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false

    return runCatching {
        var copied = false
        FileInputStream(sourceFile).use { input ->
            resolver.openOutputStream(targetUri).use { output: OutputStream? ->
                if (output != null) {
                    input.copyTo(output)
                    copied = true
                }
            }
        }
        copied
    }.getOrElse {
        resolver.delete(targetUri, null, null)
        false
    }
}
