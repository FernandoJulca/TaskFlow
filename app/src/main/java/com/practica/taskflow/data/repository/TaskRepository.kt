package com.practica.taskflow.data.repository

import com.practica.taskflow.data.local.dao.CategoryDao
import com.practica.taskflow.data.local.dao.TaskDao
import com.practica.taskflow.data.local.entities.Category
import com.practica.taskflow.data.local.entities.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository (
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao
){
    //TASK
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<Task>> = taskDao.getPendingTask()
    val completedTasks: Flow<List<Task>> = taskDao.getCompletedTask()
    fun getTaskByTitle(title: String): Flow<List<Task>> = taskDao.getTaskByTitle(title)
    fun getTaskById(taskId: Int): Flow<Task?> = taskDao.getTaskById(taskId)
    fun getTaskByCategory(categoryId: Int): Flow<List<Task>> = taskDao.getTaskByCategory(categoryId)

    suspend fun insertTask(task: Task){
        taskDao.insert(task)
    }

    suspend fun updateTask(task: Task){
        taskDao.update(task)
    }

    suspend fun deleteTask(task: Task){
        taskDao.delete(task)
    }

    suspend fun updateCompletedTask(taskId: Int, isCompleted: Boolean){
        taskDao.updateTaskCompletion(taskId, isCompleted)
    }

    //CATEGORY

    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    fun getCategoryById(categoryId: Int): Flow<Category?> = categoryDao.getCategoryById(categoryId)

    suspend fun insertCategory(category: Category){
        categoryDao.insert(category)
    }

    suspend fun updateCategory(category: Category){
        categoryDao.update(category)
    }

    suspend fun deleteCategory(category: Category){
        categoryDao.delete(category)
    }

}