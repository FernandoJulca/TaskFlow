package com.practica.taskflow.ui.screens.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practica.taskflow.data.local.entities.Category
import com.practica.taskflow.data.local.entities.Task
import com.practica.taskflow.ui.components.TaskCard
import com.practica.taskflow.ui.theme.PrimaryCyan
import com.practica.taskflow.viewmodel.TaskViewModel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados para filtros
    var selectedFilter by remember { mutableStateOf(TaskFilter.ALL) }
    var selectedCategoryId by remember { mutableIntStateOf(0) }

    // Observar datos del ViewModel
    val allTasks by viewModel.allTask.collectAsState(initial = emptyList())
    val pendingTasks by viewModel.pendingTask.collectAsState(initial = emptyList())
    val completedTasks by viewModel.completedTask.collectAsState(initial = emptyList())
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())

    // Determinar qué tareas mostrar según el filtro
    val tasksToShow = when (selectedFilter) {
        TaskFilter.ALL -> allTasks
        TaskFilter.PENDING -> pendingTasks
        TaskFilter.COMPLETED -> completedTasks
    }

    // Filtrar por categoría si hay una seleccionada
    val filteredTasks = if (selectedCategoryId != 0) {
        tasksToShow.filter { it.categoryId == selectedCategoryId }
    } else {
        tasksToShow
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryCyan,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TaskFlow",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Configuración */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = PrimaryCyan
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar tarea",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtros (Todas, Pendientes, Completadas)
            FilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = {
                    selectedFilter = it
                    selectedCategoryId = 0 // Reset categoría al cambiar filtro
                }
            )

            // Categorías horizontales
            if (categories.isNotEmpty()) {
                CategoryRow(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelected = { selectedCategoryId = it }
                )
            }

            // Título de sección
            SectionHeader(
                title = when (selectedFilter) {
                    TaskFilter.ALL -> "Todas"
                    TaskFilter.PENDING -> "Pendientes"
                    TaskFilter.COMPLETED -> "Completadas"
                },
                count = filteredTasks.size
            )

            // Lista de tareas
            if (filteredTasks.isEmpty()) {
                EmptyState(filter = selectedFilter)
            } else {
                TaskList(
                    tasks = filteredTasks,
                    categories = categories,
                    onTaskClick = onTaskClick,
                    onCheckChange = { task, isChecked ->
                        viewModel.updateCompletedTask(task.id, isChecked)
                    },
                    onDeleteTask = { task -> viewModel.deleteTask(task) }
                )
            }
        }
    }
}

// Enum para los filtros
enum class TaskFilter {
    ALL, PENDING, COMPLETED
}

@Composable
private fun FilterChips(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == TaskFilter.ALL,
            onClick = { onFilterSelected(TaskFilter.ALL) },
            label = { Text("Todas") }
        )

        FilterChip(
            selected = selectedFilter == TaskFilter.PENDING,
            onClick = { onFilterSelected(TaskFilter.PENDING) },
            label = { Text("Pendientes") }
        )

        FilterChip(
            selected = selectedFilter == TaskFilter.COMPLETED,
            onClick = { onFilterSelected(TaskFilter.COMPLETED) },
            label = { Text("Completadas") }
        )
    }
}

@Composable
private fun CategoryRow(
    categories: List<Category>,
    selectedCategoryId: Int,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "CATEGORÍAS",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chip "Todas" para limpiar filtro de categoría
            item {
                FilterChip(
                    selected = selectedCategoryId == 0,
                    onClick = { onCategorySelected(0) },
                    label = { Text("Todas") }
                )
            }

            // Chips de categorías
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { onCategorySelected(category.id) },
                    label = { Text(category.name) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = category.color?.let { Color(android.graphics.Color.parseColor(it)) }
                                        ?: MaterialTheme.colorScheme.primary,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$count tareas",
            style = MaterialTheme.typography.bodyMedium,
            color = PrimaryCyan,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    categories: List<Category>,
    onTaskClick: (Task) -> Unit,
    onCheckChange: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(tasks, key = { it.id }) { task ->
            // Buscar la categoría de esta tarea
            val category = categories.find { it.id == task.categoryId }

            TaskCard(
                task = task,
                categoryName = category?.name ?: "Sin categoría",
                categoryColor = category?.color?.let {
                    Color(android.graphics.Color.parseColor(it))
                } ?: MaterialTheme.colorScheme.primary,
                onTaskClick = { onTaskClick(task) },
                onCheckChange = { isChecked -> onCheckChange(task, isChecked) },
                onDeleteClick = { taskToDelete = task }
            )
        }
    }
}

@Composable
private fun EmptyState(
    filter: TaskFilter,
    modifier: Modifier = Modifier
) {
    val message = when (filter) {
        TaskFilter.ALL -> "No tienes tareas.\n¡Comienza agregando una!"
        TaskFilter.PENDING -> "¡Todo completado!\nNo tienes tareas pendientes."
        TaskFilter.COMPLETED -> "Aún no has completado ninguna tarea."
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}