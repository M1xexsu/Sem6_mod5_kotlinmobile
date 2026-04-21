package github.m1xexsu.sem6_mod5_kotlinmobile.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.m1xexsu.sem6_mod5_kotlinmobile.data.model.toTodoItem
import github.m1xexsu.sem6_mod5_kotlinmobile.data.preferences.Datastore
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.model.TodoItem
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.AddTodoUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.DeleteTodoUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.EditTodoUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.GetDataUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.GetTodosUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.SetDataUseCase
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase.ToggleTodoUseCase
import kotlinx.coroutines.launch

class LoadItems(
    private val getTodosUseCase: GetTodosUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase,
    private val editTodoUseCase: EditTodoUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val setDataUseCase: SetDataUseCase,
    private val getDataUseCase: GetDataUseCase
) : ViewModel() {

    private val _todos = mutableStateOf<List<TodoItem>>(emptyList())
    val todos: State<List<TodoItem>> = _todos
    val _state = mutableStateOf<Boolean>(false)
    val state: State<Boolean> = _state

    init {
        loadtodos()
    }

    fun loadtodos() {
        viewModelScope.launch {
            _todos.value = getTodosUseCase().map { it.toTodoItem() }
        }
    }

    fun addtodo(title: String, description: String) {
        if (title.isBlank() && description.isBlank()) return
        viewModelScope.launch {
            addTodoUseCase(title.trim(), description.trim())
            loadtodos()
        }
    }

     fun edittodo(todoItem: TodoItem) {
         if (todoItem.title.isBlank() && todoItem.description.isBlank()) return
         viewModelScope.launch {
             editTodoUseCase(todoItem)
             loadtodos()
         }
     }

    fun toggletodo(id: Int) {
        viewModelScope.launch {
            toggleTodoUseCase(id)
            loadtodos()
        }
    }

    fun nuketodo(id: Int) {
        viewModelScope.launch {
            deleteTodoUseCase(id)
            loadtodos()
        }
    }

    fun setpreferences(i: Boolean)
    {
        viewModelScope.launch {
            setDataUseCase(i)
            _state.value = getDataUseCase()
        }
    }
}
