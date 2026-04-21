package github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase

import github.m1xexsu.sem6_mod5_kotlinmobile.data.model.TodoEntity
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.model.TodoItem
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.repository.TodoRepository

class GetTodosUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(): List<TodoEntity> = repository.getTodos()
}

