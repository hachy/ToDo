package io.github.hachy.android.todo.room

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import io.reactivex.Maybe

@Dao
interface TaskDao {
    @Query("SELECT * FROM task ORDER BY created_at")
    fun loadAll(): Maybe<List<Task>>

    @Insert(onConflict = REPLACE)
    fun insertTask(task: Task)

    @Update(onConflict = REPLACE)
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)
}
