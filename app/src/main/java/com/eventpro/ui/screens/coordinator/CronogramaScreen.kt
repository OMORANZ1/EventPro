package com.eventpro.ui.screens.coordinator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpro.data.MockDataRepository
import com.eventpro.model.ActividadCronograma
import com.eventpro.model.EstadoActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CronogramaScreen(
    onBack: () -> Unit
) {
    var actividadesList by remember { mutableStateOf(MockDataRepository.actividades.toList()) }
    var selectedActividad by remember { mutableStateOf<ActividadCronograma?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronograma del Evento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(actividadesList) { actividad ->
                ActividadItem(
                    actividad = actividad,
                    onClick = { selectedActividad = actividad }
                )
            }
        }
    }

    // Diálogo para cambiar estado
    selectedActividad?.let { actividad ->
        AlertDialog(
            onDismissRequest = { selectedActividad = null },
            title = { Text("Cambiar estado: ${actividad.nombre}") },
            text = {
                Column {
                    EstadoActividad.entries.forEach { estado ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = actividad.estado == estado,
                                onClick = {
                                    val index = MockDataRepository.actividades.indexOfFirst { it.id == actividad.id }
                                    if (index != -1) {
                                        MockDataRepository.actividades[index] = actividad.copy(estado = estado)
                                        actividadesList = MockDataRepository.actividades.toList()
                                    }
                                    selectedActividad = null
                                }
                            )
                            Text(text = estado.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedActividad = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ActividadItem(actividad: ActividadCronograma, onClick: () -> Unit) {
    val isEnCurso = actividad.estado == EstadoActividad.EN_CURSO
    val badgeColor = when (actividad.estado) {
        EstadoActividad.EN_CURSO -> Color(0xFF4CAF50) // Verde
        EstadoActividad.PROXIMA -> Color(0xFF2196F3) // Azul
        EstadoActividad.COMPLETADA -> Color(0xFF9E9E9E) // Gris
        EstadoActividad.RETRASADA -> Color(0xFFF44336) // Rojo
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        border = if (isEnCurso) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isEnCurso) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(80.dp)) {
                Text(text = actividad.horaInicio, fontWeight = FontWeight.Bold)
                Text(text = actividad.horaFin, style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = actividad.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Surface(
                    color = badgeColor,
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = actividad.estado.name,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}
