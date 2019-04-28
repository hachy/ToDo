package io.github.hachy.android.todo.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Task(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        var content: String,
        var completed: Boolean = false,
        var viewType: Int = 0,
        var created_at: Date
)
