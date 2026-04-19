package github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase

import github.m1xexsu.sem6_mod5_kotlinmobile.domain.repository.TodoRepository

class AddTodoUseCase(private val repository: TodoRepository)
{
    suspend operator fun invoke(title: String, description: String): Unit = repository.addTodo(title, description)
}