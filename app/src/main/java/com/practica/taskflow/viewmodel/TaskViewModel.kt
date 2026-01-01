package com.practica.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.taskflow.data.local.entities.Category
import com.practica.taskflow.data.local.entities.Task
import com.practica.taskflow.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    //TASK
    val allTask: Flow<List<Task>> = repository.allTasks
    val pendingTask: Flow<List<Task>> = repository.pendingTasks
    val completedTask: Flow<List<Task>> = repository.completedTasks
    fun getTaskByTitle(title: String) :Flow<List<Task>> = repository.getTaskByTitle(title)
    fun getTaskById(taskId: Int) :Flow<Task?> = repository.getTaskById(taskId)
    fun getTaskByCategory(categoryId: Int) :Flow<List<Task>> = repository.getTaskByCategory(categoryId)

    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    fun updateCompletedTask(taskId: Int, isCompleted: Boolean) = viewModelScope.launch {
        repository.updateCompletedTask(taskId, isCompleted)
    }

    //CATEGORY
    val allCategories: Flow<List<Category>> = repository.allCategories
    fun getCategoryById(categoryId: Int): Flow<Category?> = repository.getCategoryById(categoryId)

    fun insertCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch {
        repository.updateCategory(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
    }
}