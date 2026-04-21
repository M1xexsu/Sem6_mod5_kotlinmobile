package github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase

import github.m1xexsu.sem6_mod5_kotlinmobile.domain.repository.TodoRepository

class ToggleTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(id: Int): Unit = repository.toggleTodo(id)
}
