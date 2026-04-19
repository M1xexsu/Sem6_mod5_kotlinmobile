package github.m1xexsu.sem6_mod5_kotlinmobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import github.m1xexsu.sem6_mod5_kotlinmobile.data.model.TodoEntity

@Dao
interface TodoDAO {
    @Query("select * from todos order by id asc")
    suspend fun getAllTodos(): List<TodoEntity>

    @Query("select * from todos where id = :id")
    suspend fun getTodoById(id: Int): TodoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todos: List<TodoEntity>)

    @Insert
    suspend fun insertTodo(todo: TodoEntity): Long

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Query("select count(*) from todos")
    suspend fun countTodos(): Int

    @Query("update todos set isCompleted = not isCompleted where id = :id")
    suspend fun toggleTodo(id: Int)

    @Query("delete from todos where id = :id")
    suspend fun deleteTodo(id: Int)
}