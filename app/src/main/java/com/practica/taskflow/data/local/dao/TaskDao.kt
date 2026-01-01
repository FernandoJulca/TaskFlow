package com.practica.taskflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practica.taskflow.data.local.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasks() :Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = false")
    fun getPendingTask() :Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = true")
    fun getCompletedTask() :Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :title || '%'")
    fun getTaskByTitle(title: String) :Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId")
    fun getTaskByCategory(categoryId: Int) :Flow<List<Task>>

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Int, isCompleted: Boolean)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int) :Flow<Task?>
}