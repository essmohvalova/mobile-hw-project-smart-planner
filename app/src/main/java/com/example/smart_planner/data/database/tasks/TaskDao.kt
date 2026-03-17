package com.example.smart_planner.data.database.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.smart_planner.data.models.tasks.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY CASE WHEN isCompleted = 0 THEN 0 ELSE 1 END, createdAt DESC")
        fun getAllTasks(): Flow<List<TaskEntity>>

        @Query("SELECT * FROM tasks WHERE id = :taskId")
        suspend fun getTaskById(taskId: Long): TaskEntity?

        @Insert
        suspend fun insertTask(task: TaskEntity)

        @Insert
        suspend fun insertAllTasks(tasks: List<TaskEntity>)

        @Update
        suspend fun updateTask(task: TaskEntity)

        @Delete
        suspend fun deleteTask(task: TaskEntity)

        @Query("DELETE FROM tasks WHERE isCompleted = 1 AND createdAt < :threshold")
        suspend fun deleteOldCompletedTasks(threshold: Long)
}