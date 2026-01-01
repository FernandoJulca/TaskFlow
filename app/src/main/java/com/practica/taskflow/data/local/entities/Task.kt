package com.practica.taskflow.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Task(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val createdDate: Long,
    val dueDate: Long? = null,
    val reminderTime: Long? = null,
    val priority: Priority,
    val categoryId: Int,
    val isCompleted: Boolean = false
)
