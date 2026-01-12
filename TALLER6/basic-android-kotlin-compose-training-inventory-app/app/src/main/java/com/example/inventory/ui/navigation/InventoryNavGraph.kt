package com.example.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.inventory.ui.home.HomeDestination
import com.example.inventory.ui.home.HomeScreen
import com.example.inventory.ui.item.ItemDetailsDestination
import com.example.inventory.ui.item.ItemDetailsScreen
import com.example.inventory.ui.item.ItemEditDestination
import com.example.inventory.ui.item.ItemEditScreen
import com.example.inventory.ui.item.ItemEntryDestination
import com.example.inventory.ui.item.ItemEntryScreen
import com.example.inventory.ui.login.LoginScreen
import com.example.inventory.ui.splash.SplashScreen

@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {

        // 🔹 SPLASH (logo centrado)
        composable(route = "splash") {
            SplashScreen {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        // 🔹 LOGIN
        composable(route = "login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeDestination.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 🔹 HOME
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToItemEntry = {
                    navController.navigate(ItemEntryDestination.route)
                },
                navigateToItemUpdate = {
                    navController.navigate("${ItemDetailsDestination.route}/${it}")
                }
            )
        }

        // 🔹 AGREGAR ITEM
        composable(route = ItemEntryDestination.route) {
            ItemEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        // 🔹 DETALLES ITEM
        composable(
            route = ItemDetailsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ItemDetailsDestination.itemIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            ItemDetailsScreen(
                navigateToEditItem = {
                    navController.navigate("${ItemEditDestination.route}/$it")
                },
                navigateBack = { navController.navigateUp() }
            )
        }

        // 🔹 EDITAR ITEM
        composable(
            route = ItemEditDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ItemEditDestination.itemIdArg) {
                    type = NavType.IntType
                }
            )
        ) {
            ItemEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
