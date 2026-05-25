package com.eventpro.ui.screens.coordinator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventpro.data.MockDataRepository
import com.eventpro.model.Incidencia
import com.eventpro.model.Prioridad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidenciasScreen(
    onBack: () -> Unit
) {
    var incidenciasList by remember { mutableStateOf(MockDataRepository.incidencias.toList()) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Activas, 1: Historial
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedIncidencia by remember { mutableStateOf<Incidencia?>(null) }

    val filteredIncidencias = if (selectedTab == 0) {
        incidenciasList.filter { !it.resuelta }
    } else {
        incidenciasList.filter { it.resuelta }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Incidencias") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Reportar Incidencia")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Activas") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Historial") }
                )
            }

            if (filteredIncidencias.isEmpty()) {
                EmptyIncidenciasContent(selectedTab)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredIncidencias) { incidencia ->
                        IncidenciaItem(
                            incidencia = incidencia,
                            onClick = { selectedIncidencia = incidencia }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de Detalles / Resolver
    selectedIncidencia?.let { incidencia ->
        AlertDialog(
            onDismissRequest = { selectedIncidencia = null },
            title = { Text(incidencia.titulo) },
            text = {
                Column {
                    Text("Prioridad: ${incidencia.prioridad}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(incidencia.descripcion)
                }
            },
            confirmButton = {
                if (!incidencia.resuelta) {
                    Button(onClick = {
                        val index = MockDataRepository.incidencias.indexOfFirst { it.id == incidencia.id }
                        if (index != -1) {
                            MockDataRepository.incidencias[index] = incidencia.copy(resuelta = true)
                            incidenciasList = MockDataRepository.incidencias.toList()
                        }
                        selectedIncidencia = null
                    }) {
                        Text("Marcar como resuelta")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedIncidencia = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Diálogo para nueva incidencia
    if (showAddDialog) {
        var titulo by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var prioridad by remember { mutableStateOf(Prioridad.MEDIA) }
        var expanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Reportar Incidencia") },
            text = {
                Column {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Prioridad", style = MaterialTheme.typography.labelMedium)
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(prioridad.name)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            Prioridad.entries.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.name) },
                                    onClick = {
                                        prioridad = p
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (titulo.isNotBlank()) {
                        val nueva = Incidencia(
                            id = (MockDataRepository.incidencias.maxOfOrNull { it.id } ?: 0) + 1,
                            titulo = titulo,
                            descripcion = descripcion,
                            prioridad = prioridad
                        )
                        MockDataRepository.incidencias.add(nueva)
                        incidenciasList = MockDataRepository.incidencias.toList()
                        showAddDialog = false
                    }
                }) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun IncidenciaItem(incidencia: Incidencia, onClick: () -> Unit) {
    val badgeColor = when (incidencia.prioridad) {
        Prioridad.ALTA -> Color(0xFFE57373) // Rojo
        Prioridad.MEDIA -> Color(0xFFFFB74D) // Naranja
        Prioridad.BAJA -> Color(0xFFFFF176) // Amarillo
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = incidencia.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = incidencia.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Surface(
                color = badgeColor,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = incidencia.prioridad.name,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun EmptyIncidenciasContent(tabIndex: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (tabIndex == 0) "No hay incidencias activas" else "Historial vacío",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
