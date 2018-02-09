package io.github.hachy.android.todo

import android.app.Application
import android.arch.persistence.room.Room
import io.github.hachy.android.todo.room.TaskDatabase

class MyApplication : Application() {
    companion object {
        lateinit var database: TaskDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, TaskDatabase::class.java, "ToDoDb").build()
    }
}

