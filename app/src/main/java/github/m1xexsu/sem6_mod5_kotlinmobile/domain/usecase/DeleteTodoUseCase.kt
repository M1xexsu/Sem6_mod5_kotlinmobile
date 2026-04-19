package github.m1xexsu.sem6_mod5_kotlinmobile.domain.usecase


import github.m1xexsu.sem6_mod5_kotlinmobile.domain.repository.TodoRepository

class DeleteTodoUseCase(private val repository: TodoRepository)
{
    suspend operator fun invoke(id: Int): Unit = repository.deleteTodo(id)
}