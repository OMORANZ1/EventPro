package com.eventpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eventpro.navigation.EventProRoutes
import com.eventpro.ui.screens.login.LoginScreen
import com.eventpro.ui.screens.scanner.ScannerScreen
import com.eventpro.ui.theme.EventProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventProTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = EventProRoutes.LOGIN,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        composable(EventProRoutes.LOGIN) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(EventProRoutes.SCANNER) {
                                        popUpTo(EventProRoutes.LOGIN) { inclusive = true }
                                    }
                                },
                            )
                        }
                        composable(EventProRoutes.SCANNER) {
                            ScannerScreen()
                        }
                    }
                }
            }
        }
    }
}
