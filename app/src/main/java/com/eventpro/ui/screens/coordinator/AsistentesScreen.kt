package com.eventpro.ui.screens.coordinator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpro.data.MockDataRepository
import com.eventpro.model.Asistente
import com.eventpro.model.EstadoAsistente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsistentesScreen(
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val asistentesList = MockDataRepository.asistentes

    val filteredAsistentes = asistentesList.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    val confirmados = asistentesList.count { it.estado == EstadoAsistente.CONFIRMADO }
    val pendientes = asistentesList.count { it.estado == EstadoAsistente.PENDIENTE }
    val cancelados = asistentesList.count { it.estado == EstadoAsistente.CANCELADO }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Asistentes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Buscar por nombre o email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // Contadores
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CounterItem("Conf.", confirmados, Color(0xFF4CAF50))
                    CounterItem("Pend.", pendientes, Color(0xFF9E9E9E))
                    CounterItem("Canc.", cancelados, Color(0xFFF44336))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredAsistentes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron asistentes", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredAsistentes) { asistente ->
                        AsistenteItem(asistente)
                    }
                }
            }
        }
    }
}

@Composable
fun CounterItem(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun AsistenteItem(asistente: Asistente) {
    val statusColor = when (asistente.estado) {
        EstadoAsistente.CONFIRMADO -> Color(0xFF4CAF50)
        EstadoAsistente.PENDIENTE -> Color(0xFF9E9E9E)
        EstadoAsistente.CANCELADO -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = asistente.nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = asistente.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                color = statusColor,
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = asistente.estado.name,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}
