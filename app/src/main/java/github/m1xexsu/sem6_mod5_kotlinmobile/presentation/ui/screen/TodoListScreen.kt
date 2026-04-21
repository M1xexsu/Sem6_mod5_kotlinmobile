package github.m1xexsu.sem6_mod5_kotlinmobile.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.model.TodoItem
import github.m1xexsu.sem6_mod5_kotlinmobile.presentation.ui.component.TodoListElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    todos: State<List<TodoItem>>,
    onTodoClick: (Int) -> Unit,
    onToggle: (Int) -> Unit,
    onTodoRemove: (Int) -> Unit,
    onTodoCreate: () -> Unit,
    isColored: Boolean,
    onIsColoredChange: (Boolean) -> Unit
) {
    val todolist by todos

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Todo List") },
                actions = {
                    Switch(
                        checked = isColored,
                        onCheckedChange = { onIsColoredChange(it) },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onTodoCreate) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(innerPadding)
                .systemBarsPadding()
                .fillMaxSize()
        ) {
            items(
                items = todolist,
                key = { todo -> todo.id }
            ) { todo ->
                TodoListElement(
                    modifier = Modifier.padding(6.dp),
                    head = todo.title,
                    description = todo.description,
                    pass = todo.isCompleted,
                    f = { onTodoClick(todo.id) },
                    checkbox = { onToggle(todo.id) },
                    nuke = { onTodoRemove(todo.id) },
                    isColored = isColored
                )
            }
        }
    }
}

@Preview(device = PIXEL_7, showSystemUi = true)
@Composable
fun PreviewTodoList() {
    TodoListScreen(
        todos = remember { mutableStateOf(listOf(TodoItem(1, "a", "a", false))) },
        onTodoClick = {},
        onToggle = {},
        onTodoCreate = {},
        onTodoRemove = {},
        isColored = true,
        onIsColoredChange = {}
    )
}