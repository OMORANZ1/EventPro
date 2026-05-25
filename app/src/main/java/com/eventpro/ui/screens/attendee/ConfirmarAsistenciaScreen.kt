package com.eventpro.ui.screens.attendee

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eventpro.data.MockDataRepository
import com.eventpro.model.EstadoAsistente
import com.eventpro.util.PreferenciasUsuario
import com.eventpro.util.SesionActual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmarAsistenciaScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { PreferenciasUsuario(context) }
    val asistente = remember { SesionActual.obtenerAsistenteActual() }
    val evento = MockDataRepository.eventoActual

    var estadoConfirmacion by remember {
        mutableStateOf(prefs.obtenerEstadoConfirmacion(asistente?.id ?: 0))
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Asistencia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = evento.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${evento.fecha} - ${evento.hora}")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(evento.lugar)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tu estado actual:",
                style = MaterialTheme.typography.titleMedium
            )
            
            val (statusText, statusColor) = when (estadoConfirmacion) {
                EstadoAsistente.CONFIRMADO -> "✓ ASISTENCIA CONFIRMADA" to Color(0xFF2E7D32)
                EstadoAsistente.CANCELADO -> "✗ ASISTENCIA CANCELADA" to Color(0xFFC62828)
                EstadoAsistente.PENDIENTE -> "PENDIENTE DE CONFIRMACIÓN" to Color.Gray
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.headlineSmall,
                color = statusColor,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (estadoConfirmacion) {
                EstadoAsistente.PENDIENTE -> {
                    Button(
                        onClick = { 
                            actualizarEstado(EstadoAsistente.CONFIRMADO, asistente?.id, prefs, { estadoConfirmacion = it }, snackbarHostState)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirmar Asistencia")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { 
                            actualizarEstado(EstadoAsistente.CANCELADO, asistente?.id, prefs, { estadoConfirmacion = it }, snackbarHostState)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("No Podré Asistir")
                    }
                }
                EstadoAsistente.CONFIRMADO -> {
                    OutlinedButton(
                        onClick = { 
                            actualizarEstado(EstadoAsistente.CANCELADO, asistente?.id, prefs, { estadoConfirmacion = it }, snackbarHostState)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Cancelar mi asistencia")
                    }
                }
                EstadoAsistente.CANCELADO -> {
                    Button(
                        onClick = { 
                            actualizarEstado(EstadoAsistente.CONFIRMADO, asistente?.id, prefs, { estadoConfirmacion = it }, snackbarHostState)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Volver a confirmar asistencia")
                    }
                }
            }
        }
    }
}

private fun actualizarEstado(
    nuevoEstado: EstadoAsistente,
    asistenteId: Int?,
    prefs: PreferenciasUsuario,
    onUpdate: (EstadoAsistente) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    if (asistenteId == null) return
    
    // Persistir en SharedPreferences
    prefs.guardarEstadoConfirmacion(asistenteId, nuevoEstado)
    
    // Actualizar en el repositorio (memoria)
    val index = MockDataRepository.asistentes.indexOfFirst { it.id == asistenteId }
    if (index != -1) {
        val asistenteActual = MockDataRepository.asistentes[index]
        MockDataRepository.asistentes[index] = asistenteActual.copy(estado = nuevoEstado)
    }
    
    onUpdate(nuevoEstado)
    
    // Feedback al usuario
    // Nota: en una app real esto sería un suspend fun en un CoroutineScope
}
