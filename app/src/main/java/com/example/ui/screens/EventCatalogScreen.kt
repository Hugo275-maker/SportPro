package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EventDefinition
import com.example.model.UserRole
import com.example.ui.theme.SportProPitchGreen
import com.example.viewmodel.SportProViewModel

@Composable
fun EventCatalogScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val eventDefs by viewModel.eventDefinitions.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showNewEventDialog by remember { mutableStateOf(false) }
    var newEventName by remember { mutableStateOf("") }
    var newEventScope by remember { mutableStateOf("Jugador") }
    var newEventGoal by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Version and sync badge (US-013)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Catálogo Dinámico de Eventos",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "v1.2 Activo",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "US-013: Sincronización en tiempo real con operadores en cancha",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    if (currentUser?.rol == UserRole.ADM) {
                        Button(onClick = { showNewEventDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nuevo")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Eventos Configurados (${eventDefs.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(eventDefs) { def ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(def.colorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (def.nombre) {
                                    "Gol" -> Icons.Default.SportsSoccer
                                    "Tarjeta amarilla", "Tarjeta roja" -> Icons.Default.Warning
                                    "Cambio" -> Icons.Default.SwapHoriz
                                    "Penal" -> Icons.Default.SportsFootball
                                    else -> Icons.Default.Sports
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = def.nombre,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (def.esPersonalizado) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = "Personalizado",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Ámbito: ${def.ambito} • Campos: ${def.camposObligatorios}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (def.sumaMarcador) {
                                Text(
                                    text = "Suma al marcador oficial",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SportProPitchGreen
                                )
                            }
                        }
                    }

                    if (currentUser?.rol == UserRole.ADM) {
                        Switch(
                            checked = def.activo,
                            onCheckedChange = { viewModel.repository.toggleEventDefinition(def.typeId) }
                        )
                    }
                }
            }
        }
    }

    // Modal to create custom event (US-013 criteria: name 3-30 chars, scope, sum score)
    if (showNewEventDialog) {
        AlertDialog(
            onDismissRequest = { showNewEventDialog = false },
            title = { Text("Crear Evento Personalizado") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newEventName,
                        onValueChange = {
                            newEventName = it
                            nameError = if (it.length < 3 || it.length > 30) "Nombre debe tener entre 3 y 30 caracteres" else null
                        },
                        label = { Text("Nombre del evento") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError != null,
                        supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Ámbito:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = newEventScope == "Jugador",
                            onClick = { newEventScope = "Jugador" },
                            label = { Text("Jugador") }
                        )
                        FilterChip(
                            selected = newEventScope == "Equipo",
                            onClick = { newEventScope = "Equipo" },
                            label = { Text("Equipo") }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = newEventGoal, onCheckedChange = { newEventGoal = it })
                        Text("¿Suma al marcador oficial del partido?", fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newEventName.length < 3 || newEventName.length > 30) {
                            nameError = "Nombre inválido"
                            return@Button
                        }
                        viewModel.repository.addEventDefinition(
                            EventDefinition(
                                typeId = "ev_cust_${System.currentTimeMillis()}",
                                nombre = newEventName,
                                icono = "sports",
                                colorHex = 0xFF6366F1,
                                ambito = newEventScope,
                                camposObligatorios = "Minuto, $newEventScope",
                                sumaMarcador = newEventGoal,
                                activo = true,
                                esPersonalizado = true
                            )
                        )
                        showNewEventDialog = false
                    }
                ) {
                    Text("Crear Evento")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewEventDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
