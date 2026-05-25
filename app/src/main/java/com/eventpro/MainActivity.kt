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
import com.eventpro.ui.screens.menu.MainMenuScreen
import com.eventpro.ui.screens.scanner.ScannerScreen
import com.eventpro.ui.screens.coordinator.ChecklistTareasScreen
import com.eventpro.ui.screens.coordinator.IncidenciasScreen
import com.eventpro.ui.screens.coordinator.CronogramaScreen
import com.eventpro.ui.screens.coordinator.AsistentesScreen
import com.eventpro.ui.screens.access.PanelAsistenciaScreen
import com.eventpro.ui.screens.attendee.*
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
                                    navController.navigate(EventProRoutes.MAIN_MENU) {
                                        popUpTo(EventProRoutes.LOGIN) { inclusive = true }
                                    }
                                },
                            )
                        }
                        composable(EventProRoutes.MAIN_MENU) {
                            MainMenuScreen(
                                onNavigateToScanner = {
                                    navController.navigate(EventProRoutes.SCANNER)
                                },
                                onNavigateToPanel = {
                                    navController.navigate(EventProRoutes.PANEL_ASISTENCIA)
                                },
                                onNavigateToChecklist = {
                                    navController.navigate(EventProRoutes.CHECKLIST)
                                },
                                onNavigateToIncidencias = {
                                    navController.navigate(EventProRoutes.INCIDENCIAS)
                                },
                                onNavigateToCronograma = {
                                    navController.navigate(EventProRoutes.CRONOGRAMA)
                                },
                                onNavigateToAsistentes = {
                                    navController.navigate(EventProRoutes.ASISTENTES)
                                },
                                onNavigateToMiQR = {
                                    navController.navigate(EventProRoutes.MI_QR)
                                },
                                onNavigateToConfirmarAsistencia = {
                                    navController.navigate(EventProRoutes.CONFIRMAR_ASISTENCIA)
                                },
                                onNavigateToDetallesEvento = {
                                    navController.navigate(EventProRoutes.DETALLES_EVENTO)
                                },
                                onNavigateToItinerario = {
                                    navController.navigate(EventProRoutes.ITINERARIO)
                                },
                                onLogout = {
                                    navController.navigate(EventProRoutes.LOGIN) {
                                        popUpTo(EventProRoutes.MAIN_MENU) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(EventProRoutes.SCANNER) {
                            ScannerScreen(
                                onNavigateToPanel = { navController.navigate(EventProRoutes.PANEL_ASISTENCIA) },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(EventProRoutes.PANEL_ASISTENCIA) {
                            PanelAsistenciaScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(EventProRoutes.CHECKLIST) {
                            ChecklistTareasScreen(onBack = { navController.popBackStack() })
                        }
                        composable(EventProRoutes.INCIDENCIAS) {
                            IncidenciasScreen(onBack = { navController.popBackStack() })
                        }
                        composable(EventProRoutes.CRONOGRAMA) {
                            CronogramaScreen(onBack = { navController.popBackStack() })
                        }
                        composable(EventProRoutes.ASISTENTES) {
                            AsistentesScreen(onBack = { navController.popBackStack() })
                        }
                        composable(EventProRoutes.MI_QR) {
                            MiQRScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToConfirm = { navController.navigate(EventProRoutes.CONFIRMAR_ASISTENCIA) }
                            )
                        }
                        composable(EventProRoutes.CONFIRMAR_ASISTENCIA) {
                            ConfirmarAsistenciaScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(EventProRoutes.DETALLES_EVENTO) {
                            DetallesEventoScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToConfirm = { navController.navigate(EventProRoutes.CONFIRMAR_ASISTENCIA) }
                            )
                        }
                        composable(EventProRoutes.ITINERARIO) {
                            ItinerarioScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
