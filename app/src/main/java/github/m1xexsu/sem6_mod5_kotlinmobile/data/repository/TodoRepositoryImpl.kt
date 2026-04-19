package github.m1xexsu.sem6_mod5_kotlinmobile.data.repository

import github.m1xexsu.sem6_mod5_kotlinmobile.data.local.TodoDAO
import github.m1xexsu.sem6_mod5_kotlinmobile.data.local.TodoJsonDataSource
import github.m1xexsu.sem6_mod5_kotlinmobile.data.model.TodoEntity
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.model.TodoItem
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.repository.TodoRepository

class TodoRepositoryImpl(
    private val todoDAO: TodoDAO,
    private val jsonDataSource: TodoJsonDataSource
) : TodoRepository {

    override suspend fun ensureSeeded() {
        if (todoDAO.countTodos() > 0) return
        todoDAO.insertAll(jsonDataSource.getTodos())
    }

    override suspend fun getTodos(): List<TodoEntity> {
        ensureSeeded()
        return todoDAO.getAllTodos()
    }

    override suspend fun toggleTodo(id: Int) {
        todoDAO.toggleTodo(id)
    }

    override suspend fun updateTodo(todo: TodoItem) {
        todoDAO.updateTodo(
            TodoEntity(
                id = todo.id,
                title = todo.title,
                description = todo.description,
                isCompleted = todo.isCompleted
            )
        )
    }

    override suspend fun deleteTodo(id: Int) {
        todoDAO.deleteTodo(id)
    }

    override suspend fun addTodo(title: String, description: String) {
        todoDAO.insertTodo(
            TodoEntity(
                title = title,
                description = description,
                isCompleted = false
            )
        )
    }
}