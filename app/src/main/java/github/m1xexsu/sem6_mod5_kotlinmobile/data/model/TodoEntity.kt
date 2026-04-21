package github.m1xexsu.sem6_mod5_kotlinmobile.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import github.m1xexsu.sem6_mod5_kotlinmobile.domain.model.TodoItem

@Entity(tableName = "todos")
data class TodoEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)

fun TodoEntity.toTodoItem(): TodoItem {
    return TodoItem(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted
    )
}