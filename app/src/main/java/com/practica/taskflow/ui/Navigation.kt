package com.practica.taskflow.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.practica.taskflow.ui.screens.home.HomeScreen
import com.practica.taskflow.ui.screens.taskdetail.TaskDetailScreen
import com.practica.taskflow.viewmodel.TaskViewModel

// Rutas de navegación
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Int = 0) = "task_detail/$taskId"
    }
}

@Composable
fun TaskFlowNavigation(
    viewModel: TaskViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Pantalla Home
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onTaskClick = { task ->
                    // Navegar a editar tarea
                    navController.navigate(Screen.TaskDetail.createRoute(task.id))
                },
                onAddTaskClick = {
                    // Navegar a crear nueva tarea
                    navController.navigate(Screen.TaskDetail.createRoute(0))
                }
            )
        }

        // Pantalla TaskDetail
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            TaskDetailScreen(
                taskId = taskId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}