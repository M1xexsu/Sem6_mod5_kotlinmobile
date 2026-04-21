package github.m1xexsu.sem6_mod5_kotlinmobile.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.model.TodoItem
import github.m1xexsu.sem6_mod5_kotlinmobile.presentation.ui.screen.TodoDetailScreen
import github.m1xexsu.sem6_mod5_kotlinmobile.presentation.ui.screen.TodoListScreen
import github.m1xexsu.sem6_mod5_kotlinmobile.presentation.viewmodel.LoadItems

@Composable
fun NavController(
    navController: NavHostController,
    viewModel: LoadItems
) {
    NavHost(navController = navController, startDestination = "TLS", modifier = Modifier) {
        composable("TLS") {
            TodoListScreen(
                todos = viewModel.todos,
                onTodoClick = { id -> navController.navigate("detail/$id") },
                onToggle = viewModel::toggletodo,
                onTodoRemove = viewModel::nuketodo,
                onTodoCreate = { navController.navigate("NEW") },
                isColored = viewModel::state.get().value,
                onIsColoredChange = viewModel::setpreferences,
            )
        }

        composable("detail/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { b ->
            val todoId = b.arguments?.getInt("id") ?: return@composable
            Log.d("RRR", "$todoId")
            val todo = viewModel.todos.value.find { it.id == todoId }

            todo?.let { it ->
                var title by rememberSaveable { mutableStateOf(it.title) }
                var description by rememberSaveable { mutableStateOf(it.description) }
                var isCompleted by rememberSaveable { mutableStateOf(it.isCompleted) }
                TodoDetailScreen(
                    modifier = Modifier,
                    todoItem = it,
                    onBack = {  viewModel.edittodo(todoItem = TodoItem(todoId, title, description, it.isCompleted));
                                navController.popBackStack() },
                    onChangeState = { isCompleted = !isCompleted },
                    onTitleChange = { title = it },
                    onDescriptionChange = { description = it }
                )
            }
        }

        composable("NEW") {
            var title by rememberSaveable { mutableStateOf("") }
            var description by rememberSaveable { mutableStateOf("") }
            var isCompleted by rememberSaveable { mutableStateOf(false) }

            TodoDetailScreen(
                modifier = Modifier,
                todoItem = TodoItem(0, title, description, isCompleted),
                onBack = {
                    viewModel.addtodo(title, description)
                    navController.popBackStack()
                },
                onChangeState = { isCompleted = !isCompleted },
                onTitleChange = { title = it },
                onDescriptionChange = { description = it }
            )
        }
    }
}