package com.example.ecommerceapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecommerceapp.ui.AppState
import com.example.ecommerceapp.ui.screens.business.AddPointsScreen
import com.example.ecommerceapp.ui.screens.business.BusinessListScreen
import com.example.ecommerceapp.ui.screens.business.BusinessRewardsScreen
import com.example.ecommerceapp.ui.screens.business.BusinessSettingsScreen
import com.example.ecommerceapp.ui.screens.business.RedeemRewardScreen
import com.example.ecommerceapp.ui.screens.customer.CustomerProfileScreen
import com.example.ecommerceapp.ui.screens.enrollment.EnrollmentsScreen
import com.example.ecommerceapp.ui.screens.login.LoginScreen
import com.example.ecommerceapp.ui.screens.register.RegisterScreen

// ui/navigation/NavGraph.kt
object NavRoutes {
    const val Login = "login"
    const val Register = "register"
    const val BusinessList = "business_list"
    const val Enrollments = "enrollments"
    const val CustomerProfile = "customer_profile"
    const val BusinessSettings = "business_settings"
    const val BusinessRewards = "business_rewards"
    const val RedeemReward = "redeem_reward"
    const val AddPoints = "add_points"
}

@Composable
fun AppNavigation(appState: AppState) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (appState.isLoggedIn) {
            if (appState.isBusinessOwner) NavRoutes.BusinessSettings
            else NavRoutes.BusinessList
        } else NavRoutes.Login
    ) {
        // Auth flows
        composable(NavRoutes.Login) {
            LoginScreen(
                appState = appState,
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.Register)
                }
            )
        }

        composable(NavRoutes.Register) {
            RegisterScreen(
                appState = appState,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Customer flows
        composable(NavRoutes.BusinessList) {
            BusinessListScreen(
                appState = appState,
                onNavigateToEnrollments = {
                    navController.navigate(NavRoutes.Enrollments)
                },
                onNavigateToProfile = {
                    navController.navigate(NavRoutes.CustomerProfile)
                }
            )
        }

        composable(NavRoutes.Enrollments) {
            EnrollmentsScreen(
                appState = appState,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.CustomerProfile) {
            CustomerProfileScreen(
                appState = appState,
                onNavigateToEnrollments = {
                    navController.navigate(NavRoutes.Enrollments)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Business owner flows
        composable(NavRoutes.BusinessSettings) {
            BusinessSettingsScreen(
                appState = appState,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.BusinessRewards) {
            BusinessRewardsScreen(
                appState = appState,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.RedeemReward) {
            RedeemRewardScreen(
                appState = appState,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.AddPoints) {
            AddPointsScreen(
                appState = appState,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
