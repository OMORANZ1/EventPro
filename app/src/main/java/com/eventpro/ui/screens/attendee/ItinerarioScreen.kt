package com.eventpro.ui.screens.attendee

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eventpro.data.MockDataRepository
import com.eventpro.model.ActividadCronograma
import com.eventpro.model.EstadoActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItinerarioScreen(onNavigateBack: () -> Unit) {
    val actividades = MockDataRepository.actividades

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itinerario del Evento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(actividades) { actividad ->
                ItinerarioItem(actividad)
            }
        }
    }
}

@Composable
fun ItinerarioItem(actividad: ActividadCronograma) {
    val esEnCurso = actividad.estado == EstadoActividad.EN_CURSO
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (esEnCurso) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (esEnCurso) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esEnCurso) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = actividad.horaInicio,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = actividad.horaFin,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = actividad.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                BadgeEstado(actividad.estado)
            }
        }
    }
}

@Composable
fun BadgeEstado(estado: EstadoActividad) {
    val (texto, color) = when (estado) {
        EstadoActividad.EN_CURSO -> "AHORA" to Color(0xFF1976D2)
        EstadoActividad.PROXIMA -> "PRÓXIMO" to Color(0xFFF57C00)
        EstadoActividad.COMPLETADA -> "FINALIZADO" to Color(0xFF388E3C)
        EstadoActividad.RETRASADA -> "RETRASADO" to Color(0xFFD32F2F)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
