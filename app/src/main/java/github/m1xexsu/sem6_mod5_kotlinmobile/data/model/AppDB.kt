package github.m1xexsu.sem6_mod5_kotlinmobile.data.model

import androidx.room.Database
import androidx.room.RoomDatabase
import github.m1xexsu.sem6_mod5_kotlinmobile.data.local.TodoDAO

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)

abstract class AppDB: RoomDatabase() {
    abstract fun todoDAO(): TodoDAO
}