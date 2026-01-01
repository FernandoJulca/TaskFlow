package com.practica.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.practica.taskflow.data.local.database.TaskDatabase
import com.practica.taskflow.data.local.entities.Category
import com.practica.taskflow.data.local.entities.Priority
import com.practica.taskflow.data.local.entities.Task
import com.practica.taskflow.data.repository.TaskRepository
import com.practica.taskflow.ui.TaskFlowNavigation
import com.practica.taskflow.ui.screens.home.HomeScreen
import com.practica.taskflow.ui.theme.TaskFlowTheme
import com.practica.taskflow.viewmodel.TaskViewModel
import com.practica.taskflow.viewmodel.TaskViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    //Inicializar Database, Repository y ViewModel
    private val database by lazy { TaskDatabase.getDatabase(this) }
    private val repository by lazy {
        TaskRepository(
            taskDao = database.taskDao(),
            categoryDao = database.categoryDao()
        )
    }

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        insertSampleData()
        setContent {
            TaskFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    HomeScreen(
                        viewModel = viewModel,
                        onTaskClick = { task ->
                            println("Click en tarea: ${task.title}")
                        },
                        onAddTaskClick = {
                            println("Click en agregar tarea")

                        }
                    )
                    TaskFlowNavigation(viewModel = viewModel)
                }
            }
        }

    }

    private fun insertSampleData() {
        // Solo insertar si la BD está vacía (primera vez)
        lifecycleScope.launch {
            val hasData = repository.allCategories.first().isNotEmpty()
            if (!hasData) {
                // Insertar categorías
                repository.insertCategory(Category(name = "Trabajo", color = "#00BCD4"))
                repository.insertCategory(Category(name = "Personal", color = "#795548"))
                repository.insertCategory(Category(name = "Hogar", color = "#4CAF50"))
                repository.insertCategory(Category(name = "Salud", color = "#9C27B0"))

                delay(100) // Esperar a que se inserten

                // Obtener IDs de categorías
                val categories = repository.allCategories.first()
                val workId = categories.find { it.name == "Trabajo" }?.id ?: 1
                val homeId = categories.find { it.name == "Hogar" }?.id ?: 2

                // Insertar tareas de ejemplo
                repository.insertTask(
                    Task(
                        title = "Enviar reporte mensual",
                        description = "Reporte de ventas Q4",
                        createdDate = System.currentTimeMillis(),
                        dueDate = System.currentTimeMillis() + 3600000, // +1 hora
                        priority = Priority.HIGH,
                        categoryId = workId,
                        isCompleted = false
                    )
                )

                repository.insertTask(
                    Task(
                        title = "Comprar despensa",
                        description = "Leche, pan, huevos",
                        createdDate = System.currentTimeMillis(),
                        dueDate = System.currentTimeMillis() + 86400000, // +1 día
                        priority = Priority.MEDIUM,
                        categoryId = homeId,
                        isCompleted = false
                    )
                )

                repository.insertTask(
                    Task(
                        title = "Revisar facturas pendientes",
                        description = "",
                        createdDate = System.currentTimeMillis(),
                        priority = Priority.HIGH,
                        categoryId = workId,
                        isCompleted = true
                    )
                )
            }
        }
    }
}