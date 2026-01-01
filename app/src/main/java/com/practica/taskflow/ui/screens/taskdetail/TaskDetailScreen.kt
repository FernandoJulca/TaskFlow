package com.practica.taskflow.ui.screens.taskdetail

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.practica.taskflow.data.local.entities.Category
import com.practica.taskflow.data.local.entities.Priority
import com.practica.taskflow.data.local.entities.Task
import com.practica.taskflow.data.local.entities.TaskDetailState
import com.practica.taskflow.ui.theme.PrimaryCyan
import com.practica.taskflow.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int?,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado del formulario
    var state by remember { mutableStateOf(TaskDetailState()) }

    // Observar categorías
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())

    // Si es edición, cargar la tarea
    LaunchedEffect(taskId) {
        if (taskId != null && taskId != 0) {
            viewModel.getTaskById(taskId).collect { task ->
                task?.let {
                    state = TaskDetailState(
                        id = it.id,
                        title = it.title,
                        description = it.description ?: "",
                        categoryId = it.categoryId,
                        dueDate = it.dueDate,
                        reminderTime = it.reminderTime,
                        priority = it.priority,
                        isCompleted = it.isCompleted,
                        isEditMode = true
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditMode) "Editar Tarea" else "Nueva Tarea",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de título
            TitleField(
                value = state.title,
                onValueChange = { state = state.copy(title = it) }
            )

            // Campo de descripción
            DescriptionField(
                value = state.description,
                onValueChange = { state = state.copy(description = it) }
            )

            // Selector de categoría
            CategorySelector(
                categories = categories,
                selectedCategoryId = state.categoryId,
                onCategorySelected = { state = state.copy(categoryId = it) }
            )

            // Selector de fecha
            DateSelector(
                selectedDate = state.dueDate,
                onDateSelected = { state = state.copy(dueDate = it) }
            )

            // Selector de recordatorio
            ReminderSelector(
                reminderTime = state.reminderTime,
                onReminderSelected = { state = state.copy(reminderTime = it) }
            )

            // Selector de prioridad
            PrioritySelector(
                selectedPriority = state.priority,
                onPrioritySelected = { state = state.copy(priority = it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de guardar
            Button(
                onClick = {
                    val task = Task(
                        id = state.id,
                        title = state.title,
                        description = state.description.ifBlank { null },
                        createdDate = System.currentTimeMillis(),
                        dueDate = state.dueDate,
                        reminderTime = state.reminderTime,
                        priority = state.priority,
                        categoryId = state.categoryId,
                        isCompleted = state.isCompleted
                    )

                    if (state.isEditMode) {
                        viewModel.updateTask(task)
                    } else {
                        viewModel.insertTask(task)
                    }

                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryCyan
                ),
                enabled = state.title.isNotBlank() && state.categoryId != 0
            ) {
                Text(
                    text = if (state.isEditMode) "Actualizar Tarea" else "Guardar Tarea",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            // Botón de cancelar
            if (state.isEditMode) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@Composable
private fun TitleField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("¿Qué necesitas hacer?") },
        placeholder = { Text("Título de la tarea") },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryCyan,
            focusedLabelColor = PrimaryCyan
        )
    )
}

@Composable
private fun DescriptionField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Descripción") },
        placeholder = { Text("Agregar detalles, notas o subtareas...") },
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        maxLines = 5,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryCyan,
            focusedLabelColor = PrimaryCyan
        )
    )
}

@Composable
private fun CategorySelector(
    categories: List<Category>,
    selectedCategoryId: Int,
    onCategorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "CATEGORÍA",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(3).forEach { category ->
                val isSelected = selectedCategoryId == category.id
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category.id) },
                    label = { Text(category.name) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        color = category.color?.let {
                                            Color(android.graphics.Color.parseColor(it))
                                        } ?: MaterialTheme.colorScheme.primary,
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateSelector(
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis()
    )

    Column(modifier = modifier) {
        Text(
            text = "PROGRAMAR",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedCard(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = PrimaryCyan
                    )
                    Column {
                        Text(
                            text = "Fecha límite",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        selectedDate?.let {
                            Text(
                                text = formatDateLong(it),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                if (selectedDate != null) {
                    TextButton(onClick = { onDateSelected(null) }) {
                        Text("Limpiar")
                    }
                }
            }
        }
    }


    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(millis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false}) {
                    Text("Cancelar")
                }
            }
        ){
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderSelector(
    reminderTime: Long?,
    onReminderSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var isReminderEnabled by remember { mutableStateOf(reminderTime != null) }
    var showTimePicker by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    reminderTime?.let { calendar.timeInMillis = it }

    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    Column(modifier = modifier) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = if (isReminderEnabled) PrimaryCyan else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Column {
                        Text(
                            text = "Recordatorio",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (isReminderEnabled && reminderTime != null) {
                            Text(
                                text = formatTime(reminderTime),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Switch(
                    checked = isReminderEnabled,
                    onCheckedChange = { enabled ->
                        isReminderEnabled = enabled
                        if (enabled) {
                            // Establecer recordatorio para dentro de 1 hora por defecto
                            onReminderSelected(System.currentTimeMillis() + 3600000)
                        } else {
                            onReminderSelected(null)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryCyan
                    )
                )
            }
        }
    }

    if(showTimePicker) {
        AlertDialog(
            onDismissRequest = {
                showTimePicker = false
                if(reminderTime == null){
                    isReminderEnabled = false
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newCalendar = Calendar.getInstance()
                        newCalendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        newCalendar.set(Calendar.MINUTE, timePickerState.minute)
                        newCalendar.set(Calendar.SECOND, 0)
                        onReminderSelected(newCalendar.timeInMillis)
                        showTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                        if (reminderTime == null) {
                            isReminderEnabled = false
                        }
                    }
                ){
                    Text("Cancelar")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
private fun PrioritySelector(
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "PRIORIDAD",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PriorityButton(
                priority = Priority.LOW,
                text = "Baja",
                color = com.practica.taskflow.ui.theme.PriorityLow,
                isSelected = selectedPriority == Priority.LOW,
                onClick = { onPrioritySelected(Priority.LOW) },
                modifier = Modifier.weight(1f)
            )

            PriorityButton(
                priority = Priority.MEDIUM,
                text = "Media",
                color = com.practica.taskflow.ui.theme.PriorityMedium,
                isSelected = selectedPriority == Priority.MEDIUM,
                onClick = { onPrioritySelected(Priority.MEDIUM) },
                modifier = Modifier.weight(1f)
            )

            PriorityButton(
                priority = Priority.HIGH,
                text = "Alta",
                color = com.practica.taskflow.ui.theme.PriorityHigh,
                isSelected = selectedPriority == Priority.HIGH,
                onClick = { onPrioritySelected(Priority.HIGH) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PriorityButton(
    priority: Priority,
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) color else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatDateLong(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    return java.text.SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale("es", "ES")).format(date)
}

private fun formatTime(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    return java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(date)
}