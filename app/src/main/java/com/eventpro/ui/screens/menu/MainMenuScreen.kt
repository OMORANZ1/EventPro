package com.eventpro.ui.screens.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

/**
 * Representa una opción individual en el menú principal.
 */
data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToScanner: () -> Unit,
    onNavigateToPanel: () -> Unit,
    onNavigateToChecklist: () -> Unit,
    onNavigateToIncidencias: () -> Unit,
    onNavigateToCronograma: () -> Unit,
    onNavigateToAsistentes: () -> Unit,
    onNavigateToMiQR: () -> Unit,
    onNavigateToConfirmarAsistencia: () -> Unit,
    onNavigateToDetallesEvento: () -> Unit,
    onNavigateToItinerario: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Definición de las opciones del menú según los requerimientos
    val menuItems = listOf(
        // COORDINADOR DE EVENTOS
        MenuItem("Checklist de Tareas", Icons.Default.Checklist) { 
            onNavigateToChecklist()
        },
        MenuItem("Gestión de Incidencias", Icons.Default.Warning) { 
            onNavigateToIncidencias()
        },
        MenuItem("Cronograma", Icons.Default.Schedule) { 
            onNavigateToCronograma()
        },
        MenuItem("Lista de Asistentes", Icons.Default.People) { 
            onNavigateToAsistentes()
        },
        
        // CONTROLADOR DE ACCESO
        MenuItem("Escanear QR", Icons.Default.QrCodeScanner) { 
            onNavigateToScanner() 
        },
        MenuItem("Asistencia en Vivo", Icons.Default.Dashboard) { 
            onNavigateToPanel()
        },
        
        // ASISTENTE
        MenuItem("Mi Código QR", Icons.Default.QrCode) { 
            onNavigateToMiQR() 
        },
        MenuItem("Confirmar Asistencia", Icons.Default.CheckCircle) { 
            onNavigateToConfirmarAsistencia() 
        },
        MenuItem("Detalles del Evento", Icons.Default.Info) { 
            onNavigateToDetallesEvento() 
        },
        MenuItem("Ver Itinerario", Icons.Default.EventNote) { 
            onNavigateToItinerario()
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("EventPro", style = MaterialTheme.typography.titleLarge)
                        Text("Bienvenido, Personal", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Menú Principal",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(menuItems) { item ->
                    MenuCard(item)
                }
            }
        }
    }
}

@Composable
fun MenuCard(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { item.onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
