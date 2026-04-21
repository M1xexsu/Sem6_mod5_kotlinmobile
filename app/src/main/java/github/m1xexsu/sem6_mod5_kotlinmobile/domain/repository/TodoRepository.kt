package github.m1xexsu.sem6_mod5_kotlinmobile.domain.repository

import github.m1xexsu.sem6_mod5_kotlinmobile.data.model.TodoEntity
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.model.TodoItem

interface TodoRepository {
    suspend fun ensureSeeded()
    suspend fun getTodos(): List<TodoEntity>
    suspend fun toggleTodo(id: Int)
    suspend fun updateTodo(todo: TodoItem)
    suspend fun deleteTodo(id: Int)
    suspend fun addTodo(title: String, description: String)
}
