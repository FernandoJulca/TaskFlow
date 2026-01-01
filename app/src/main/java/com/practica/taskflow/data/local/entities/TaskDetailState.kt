package com.practica.taskflow.data.local.entities

data class TaskDetailState(

    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val categoryId: Int = 0,
    val dueDate: Long? = null,
    val reminderTime: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val isEditMode: Boolean = false
)
