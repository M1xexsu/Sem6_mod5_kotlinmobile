package github.m1xexsu.sem6_mod5_kotlinmobile

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import github.m1xexsu.sem6_mod5_kotlinmobile.ui.theme.Sem6_mod5_kotlinmobileTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PREVIEW_CHARS = 40

data class NoteItem(
    val fileName: String,
    val title: String,
    val content: String,
    val modifiedAt: Long
)

object Screens {
    const val List = "list"
    const val New = "new"
    const val Edit = "edit/{file_name}"

    fun editRoute(fileName: String): String = "edit/${Uri.encode(fileName)}"
}

class DiaryViewModel(private val filesDir: File) : ViewModel() {
    private val _notes = mutableStateOf<List<NoteItem>>(emptyList())
    val notes = _notes

    init {
        // Полный скан директории только один раз при запуске VM.
        _notes.value = loadAllNotesOnce().sortedByDescending { it.modifiedAt }
    }

    fun createNote(title: String, content: String) {
        val fileName = buildFileName(title)
        val file = File(filesDir, fileName)
        file.writeText(packNote(title, content))

        val created = readOne(file)
        _notes.value = listOf(created) + _notes.value
    }

    fun updateNote(fileName: String, title: String, content: String) {
        val file = File(filesDir, fileName)
        if (!file.exists()) return

        file.writeText(packNote(title, content))
        val updated = readOne(file)

        _notes.value = _notes.value.map { note ->
            if (note.fileName == fileName) updated else note
        }
    }

    fun deleteNote(fileName: String) {
        val file = File(filesDir, fileName)
        if (file.exists()) {
            file.delete()
        }

        _notes.value = _notes.value.filterNot { it.fileName == fileName }
    }

    fun findByFileName(fileName: String): NoteItem? =
        _notes.value.firstOrNull { it.fileName == fileName }

    private fun loadAllNotesOnce(): List<NoteItem> {
        return filesDir.listFiles()
            ?.filter { it.isFile && it.name.endsWith(".txt") }
            ?.map { readOne(it) }
            .orEmpty()
    }

    private fun readOne(file: File): NoteItem {
        val lines = file.readLines()
        val title = lines.firstOrNull().orEmpty()
        val body = if (lines.size > 1) lines.drop(1).joinToString("\n") else ""

        return NoteItem(
            fileName = file.name,
            title = title,
            content = body,
            modifiedAt = file.lastModified()
        )
    }

    private fun packNote(title: String, content: String): String {
        return title.trim() + "\n" + content
    }

    private fun buildFileName(title: String): String {
        val timestamp = System.currentTimeMillis()
        val safeTitle = title.trim()
            .lowercase()
            .replace(Regex("[^a-z0-9_\\-]+"), "_")
            .trim('_')
            .take(40)

        return if (safeTitle.isBlank()) {
            "${timestamp}.txt"
        } else {
            "${timestamp}_${safeTitle}.txt"
        }
    }

    class Factory(private val filesDir: File) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DiaryViewModel(filesDir) as T
        }
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: DiaryViewModel by viewModels {
        DiaryViewModel.Factory(filesDir)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Sem6_mod5_kotlinmobileTheme {
                DiaryApp(viewModel)
            }
        }
    }
}

@Composable
private fun DiaryApp(viewModel: DiaryViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.List,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        composable(Screens.List) {
            MainScreen(
                notes = viewModel.notes.value,
                onCreateNew = { navController.navigate(Screens.New) },
                onOpen = { fileName -> navController.navigate(Screens.editRoute(fileName)) },
                onDelete = { fileName -> viewModel.deleteNote(fileName) }
            )
        }

        composable(Screens.New) {
            EditorScreen(
                initialTitle = "",
                initialContent = "",
                onSave = { title, content ->
                    viewModel.createNote(title, content)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screens.Edit,
            arguments = listOf(navArgument("file_name") { type = NavType.StringType })
        ) { entry ->
            val fileName = entry.arguments?.getString("file_name")?.let(Uri::decode) ?: return@composable
            val note = viewModel.findByFileName(fileName)

            if (note == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Запись не найдена")
                }
            } else {
                EditorScreen(
                    initialTitle = note.title,
                    initialContent = note.content,
                    onSave = { title, content ->
                        viewModel.updateNote(fileName, title, content)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    notes: List<NoteItem>,
    onCreateNew: () -> Unit,
    onOpen: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Дневник") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNew) {
                Icon(Icons.Default.Add, contentDescription = "Новая запись")
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "У вас пока нет записей",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Нажмите +, чтобы создать первую",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(notes, key = { it.fileName }) { note ->
                    NoteRow(
                        note = note,
                        onOpen = { onOpen(note.fileName) },
                        onDelete = { onDelete(note.fileName) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteRow(
    note: NoteItem,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onOpen,
                onLongClick = { menuExpanded = true }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val header = if (note.title.isBlank()) "Без заголовка" else note.title
                Text(
                    text = header,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatDate(note.modifiedAt),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Text(
                text = buildPreview(note.content),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Удалить") },
                onClick = {
                    menuExpanded = false
                    onDelete()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorScreen(
    initialTitle: String,
    initialContent: String,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    var content by remember(initialContent) { mutableStateOf(initialContent) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Запись") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок (опционально)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Текст записи") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 12.dp),
                minLines = 10
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onSave(title, content) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Сохранить")
                }
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Назад")
                }
            }
        }
    }
}

private fun buildPreview(text: String): String {
    val clean = text.trim().replace("\n", " ")
    return if (clean.length <= PREVIEW_CHARS) clean else clean.take(PREVIEW_CHARS) + "..."
}

private fun formatDate(timeMillis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}
