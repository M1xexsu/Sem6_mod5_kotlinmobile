package github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase

import github.m1xexsu.sem6_mod5_kotlinmobile.domain.model.TodoItem
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.repository.TodoRepository

class EditTodoUseCase(private val repository: TodoRepository)
{
    suspend operator fun invoke(todoItem: TodoItem): Unit = repository.updateTodo(todoItem)
}