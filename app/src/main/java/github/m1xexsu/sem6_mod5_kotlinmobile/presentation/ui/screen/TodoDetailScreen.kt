package github.m1xexsu.sem6_mod5_kotlinmobile.presentation.ui.screen

import android.accessibilityservice.GestureDescription
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.model.TodoItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    modifier: Modifier = Modifier,
    todoItem: TodoItem,
    onBack: () -> Unit,
    onChangeState: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    val name = remember { mutableStateOf(todoItem.title) }
    val description = remember { mutableStateOf(todoItem.description) }
    val isCompleted = remember { mutableStateOf(todoItem.isCompleted) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = {
                            name.value = it
                            onTitleChange(it)
                        },
                        maxLines = 1,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    Switch(
                        onCheckedChange = { isCompleted.value = it
                            onChangeState() },
                        checked = isCompleted.value,
                        modifier = Modifier.padding(6.dp, 0.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextField(
                value = description.value,
                onValueChange = {
                    description.value = it
                    onDescriptionChange(it)
                },
                label = { Text("Описание:") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp)
            )
        }
    }
}

@Preview(showSystemUi = true, device = PIXEL_7)
@Composable
fun Preview() {
    TodoDetailScreen(
        todoItem = TodoItem(0, "d", "d", false),
        onBack = {},
        onChangeState = {},
        onTitleChange = {},
        onDescriptionChange = {},
    )
}