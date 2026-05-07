package com.example.mchavezmusicapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mchavezmusicapp.ui.screens.HomeScreen
import com.example.mchavezmusicapp.ui.screens.DetailScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{albumId}") {
        fun createRoute(albumId: String) = "detail/$albumId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Detail.route) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
            DetailScreen(navController = navController, albumId = albumId)
        }
    }
}