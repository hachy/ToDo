package io.github.hachy.android.todo.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class Task(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        var content: String,
        var completed: Boolean = false,
        var created_at: Date
)
