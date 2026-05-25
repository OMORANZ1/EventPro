package com.eventpro.ui.screens.coordinator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.eventpro.data.MockDataRepository
import com.eventpro.model.Tarea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistTareasScreen(
    onBack: () -> Unit
) {
    // Estado local para la lista de tareas (sincronizada con el repositorio)
    var tareasList by remember { mutableStateOf(MockDataRepository.tareas.toList()) }
    var showAddDialog by remember { mutableStateOf(false) }

    val completadas = tareasList.count { it.completada }
    val total = tareasList.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checklist de Tareas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Tarea")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header con progreso
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = "$completadas de $total tareas completadas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de tareas
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasList) { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onCheckedChange = { isChecked ->
                            // Actualizar en el repositorio
                            val index = MockDataRepository.tareas.indexOfFirst { it.id == tarea.id }
                            if (index != -1) {
                                MockDataRepository.tareas[index] = tarea.copy(completada = isChecked)
                                // Refrescar UI
                                tareasList = MockDataRepository.tareas.toList()
                            }
                        }
                    )
                }
            }
        }
    }

    // Diálogo para agregar nueva tarea
    if (showAddDialog) {
        var titulo by remember { mutableStateOf("") }
        var area by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nueva Tarea") },
            text = {
                Column {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título de la tarea") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Área (ej: Logística)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titulo.isNotBlank()) {
                            val nuevaTarea = Tarea(
                                id = (MockDataRepository.tareas.maxOfOrNull { it.id } ?: 0) + 1,
                                titulo = titulo,
                                area = area,
                                completada = false
                            )
                            MockDataRepository.tareas.add(nuevaTarea)
                            tareasList = MockDataRepository.tareas.toList()
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Agregar")
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
fun TareaItem(
    tarea: Tarea,
    onCheckedChange: (Boolean) -> Unit
) {
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
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = onCheckedChange
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                Text(
                    text = tarea.area,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
