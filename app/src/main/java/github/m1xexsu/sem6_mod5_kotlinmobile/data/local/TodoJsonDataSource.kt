package github.m1xexsu.sem6_mod5_kotlinmobile.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import github.m1xexsu.sem6_mod5_kotlinmobile.data.model.TodoEntity

class TodoJsonDataSource(private val context: Context) {
    private val gson = Gson()

    fun getTodos(): List<TodoEntity> {
        val json = context.assets.open("todos.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<TodoEntity>>() {}.type
        return gson.fromJson(json, type)
    }
}