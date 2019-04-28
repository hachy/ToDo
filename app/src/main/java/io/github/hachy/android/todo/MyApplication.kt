package io.github.hachy.android.todo

import android.app.Application
import androidx.room.Room
import android.content.Context
import io.github.hachy.android.todo.room.TaskDatabase

class MyApplication : Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var database: TaskDatabase
        lateinit var instance: MyApplication
        fun getContext(): Context {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, TaskDatabase::class.java, "ToDoDb").build()
        Prefs.init(this)
    }
}

