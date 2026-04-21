package github.m1xexsu.sem6_mod5_kotlinmobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.todolist.ui.theme.TodolistTheme
import github.m1xexsu.sem6_mod5_kotlinmobile.data.local.TodoJsonDataSource
import github.m1xexsu.sem6_mod5_kotlinmobile.data.model.AppDB
import github.m1xexsu.sem6_mod5_kotlinmobile.data.preferences.dataStore
import github.m1xexsu.sem6_mod5_kotlinmobile.data.repository.DataStoreRepositoryImpl
import github.m1xexsu.sem6_mod5_kotlinmobile.data.repository.TodoRepositoryImpl
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.AddTodoUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.DeleteTodoUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.EditTodoUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.GetDataUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.GetTodosUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.SetDataUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.ToggleTodoUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.presentation.navigation.NavController
import github.m1xexsu.sem6_mod5_kotlinmobile.presentation.viewmodel.LoadItems

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDB::class.java,
            "todo.db"
        ).build()

        val dataStoreRepository = DataStoreRepositoryImpl(applicationContext)

        val repository = TodoRepositoryImpl(
            todoDAO = db.todoDAO(),
            jsonDataSource = TodoJsonDataSource(this)
        )

        val viewModel = LoadItems(
            getTodosUseCase = GetTodosUseCase(repository),
            toggleTodoUseCase = ToggleTodoUseCase(repository),
            addTodoUseCase = AddTodoUseCase(repository),
            deleteTodoUseCase = DeleteTodoUseCase(repository),
            editTodoUseCase = EditTodoUseCase(repository),
            setDataUseCase = SetDataUseCase(dataStoreRepository),
            getDataUseCase = GetDataUseCase(dataStoreRepository)
        )

        setContent {
            val navController = rememberNavController()
            TodolistTheme {
                NavController(navController, viewModel)
            }
        }
    }
}
