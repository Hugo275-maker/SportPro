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
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel

// ==========================================
// US-009: BIBLIOTECA DE EJERCICIOS (DRILLS)
// ==========================================
@Composable
fun DrillsLibraryScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val drills by viewModel.drills.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }
    var showAddDrillDialog by remember { mutableStateOf(false) }

    val categories = listOf("Todas", "Pase y control", "Definición", "Táctica ofensiva", "Táctica defensiva", "Calentamiento", "Físico")

    val filteredDrills = drills.filter { drill ->
        val matchesQuery = drill.nombre.contains(searchQuery, ignoreCase = true) ||
                drill.objetivo.contains(searchQuery, ignoreCase = true)
        val matchesCat = selectedCategory == "Todas" || drill.categoriaTecnica == selectedCategory
        matchesQuery && matchesCat && drill.activo
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Biblioteca de Ejercicios",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "US-009: Tareas reutilizables para planificación rápida",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (currentUser?.rol == UserRole.DT || currentUser?.rol == UserRole.ADM) {
                    Button(onClick = { showAddDrillDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nuevo")
                    }
                }
            }
        }

        // Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar por nombre u objetivo...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(4).forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filteredDrills) { drill ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = drill.nombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${drill.duracionMin} min",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Categoría: ${drill.categoriaTecnica} • Mín: ${drill.minJugadores} jugadores",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Objetivo: ${drill.objetivo}", fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Materiales: ${drill.materiales}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = drill.descripcion,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }

    // Add Drill Dialog with Character Counter (US-009 criteria: 20-1000 chars)
    if (showAddDrillDialog) {
        var name by remember { mutableStateOf("") }
        var objective by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Pase y control") }
        var duration by remember { mutableStateOf("20") }
        var minPlayers by remember { mutableStateOf("6") }
        var materials by remember { mutableStateOf("Conos, balones, petos") }
        var description by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDrillDialog = false },
            title = { Text("Nuevo Ejercicio de Entrenamiento") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre (3-60 caracteres)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = objective,
                        onValueChange = { objective = it },
                        label = { Text("Objetivo técnico") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duración min (1-120)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minPlayers,
                            onValueChange = { minPlayers = it },
                            label = { Text("Mín jugadores") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción del ejercicio") },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = { Text("${description.length}/1000 caracteres (mín 20)") }
                    )

                    if (errorMsg != null) {
                        Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val dur = duration.toIntOrNull() ?: 20
                        val minP = minPlayers.toIntOrNull() ?: 6
                        if (name.length < 3) {
                            errorMsg = "Nombre muy corto"
                            return@Button
                        }
                        if (description.length < 20) {
                            errorMsg = "La descripción debe tener al menos 20 caracteres"
                            return@Button
                        }
                        viewModel.repository.addDrill(
                            Drill(
                                drillId = "dr_${System.currentTimeMillis()}",
                                nombre = name,
                                objetivo = objective,
                                categoriaTecnica = category,
                                duracionMin = dur,
                                minJugadores = minP,
                                materiales = materials,
                                descripcion = description,
                                activo = true
                            )
                        )
                        showAddDrillDialog = false
                    }
                ) {
                    Text("Guardar Ejercicio")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDrillDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==========================================
// US-010: PLANIFICACIÓN DE ENTRENAMIENTOS
// ==========================================
@Composable
fun TrainingPlanScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.trainingSessions.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelSessionId by remember { mutableStateOf<String?>(null) }
    var cancelReason by remember { mutableStateOf("") }
    var cancelError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sesiones de Entrenamiento",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "US-010: Calendario, ejercicios y asistencia",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (currentUser?.rol == UserRole.DT) {
                    Button(onClick = { viewModel.navigateTo(Screen.DRILLS) }) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ejercicios")
                    }
                }
            }
        }

        items(sessions) { session ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${session.fecha} • ${session.horaInicio}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${session.teamNombre} (${session.duracionTotalMin} min)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (session.estado) {
                                "Programada" -> SportProPitchGreen.copy(alpha = 0.15f)
                                "Realizada" -> Color(0xFF0284C7).copy(alpha = 0.15f)
                                else -> SportProCardRed.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = session.estado,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (session.estado) {
                                    "Programada" -> SportProPitchGreen
                                    "Realizada" -> Color(0xFF0284C7)
                                    else -> SportProCardRed
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Lugar: ${session.lugar}", fontSize = 13.sp)
                    Text(text = "Objetivo: ${session.objetivoGeneral}", fontSize = 13.sp)

                    if (session.motivoCancelacion != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cancelada: ${session.motivoCancelacion}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ejercicios planificados (${session.drills.size}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    session.drills.forEach { drill ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(12.dp), tint = SportProPitchGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${drill.nombre} (${drill.duracionMin} min)", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (currentUser?.rol == UserRole.DT && session.estado == "Programada") {
                            Button(
                                onClick = {
                                    viewModel.selectSession(session.sessionId, Screen.ATTENDANCE)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tomar Asistencia (US-011)")
                            }

                            OutlinedButton(
                                onClick = {
                                    cancelSessionId = session.sessionId
                                    showCancelDialog = true
                                }
                            ) {
                                Text("Cancelar")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(Screen.PARTICIPATION_HISTORY) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ver Historial de Participación (US-012)")
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to Cancel Session with Reason (US-010 criteria: 10-200 chars)
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar Sesión de Entrenamiento") },
            text = {
                Column {
                    Text("Indica el motivo de cancelación (notificará a todos los convocados):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = {
                            cancelReason = it
                            cancelError = null
                        },
                        label = { Text("Motivo de cancelación (10-200 caracteres)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = cancelError != null,
                        supportingText = { cancelError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cancelReason.length < 10) {
                            cancelError = "Mínimo 10 caracteres requeridos"
                            return@Button
                        }
                        cancelSessionId?.let {
                            viewModel.repository.cancelTrainingSession(it, cancelReason)
                        }
                        showCancelDialog = false
                    }
                ) {
                    Text("Confirmar Cancelación")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Volver")
                }
            }
        )
    }
}

// ==========================================
// US-011: REGISTRO DE ASISTENCIA
// ==========================================
@Composable
fun AttendanceScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSessionId by viewModel.selectedSessionId.collectAsState()
    val allAttendance by viewModel.attendanceRecords.collectAsState()
    val records = allAttendance[selectedSessionId] ?: emptyList()
    val isOffline by viewModel.isOffline.collectAsState()

    var showLateDialog by remember { mutableStateOf(false) }
    var showJustifyDialog by remember { mutableStateOf(false) }
    var activeRecord by remember { mutableStateOf<AttendanceRecord?>(null) }
    var lateMinutes by remember { mutableStateOf("15") }
    var justificationReason by remember { mutableStateOf("") }
    var sessionClosedSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Attendance Header & Quick Actions (US-011)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Control de Asistencia (US-011)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (isOffline) "Modo sin conexión activo (sincronizará al volver la red)" else "Sincronizado en tiempo real",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = if (isOffline) SportProCardRed else SportProPitchGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.repository.markAllPresent(selectedSessionId) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Marcar todos presentes", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.repository.closeAttendanceSession(selectedSessionId)
                                sessionClosedSuccess = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SportProPitchGreen)
                        ) {
                            Text("Cerrar Sesión", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        items(records) { record ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("#${record.dorsal}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = record.jugadorNombre,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (record.estado) {
                                AttendanceStatus.PRESENTE -> SportProPitchGreen.copy(alpha = 0.15f)
                                AttendanceStatus.TARDE -> SportProCardYellow.copy(alpha = 0.15f)
                                AttendanceStatus.FALTA_JUSTIFICADA -> Color(0xFF0284C7).copy(alpha = 0.15f)
                                AttendanceStatus.FALTA_INJUSTIFICADA -> SportProCardRed.copy(alpha = 0.15f)
                                AttendanceStatus.SIN_MARCAR -> Color.Gray.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = record.estado.label + if (record.estado == AttendanceStatus.TARDE) " (+${record.minutosTarde}')" else "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (record.estado) {
                                    AttendanceStatus.PRESENTE -> SportProPitchGreen
                                    AttendanceStatus.TARDE -> Color(0xFFB45309)
                                    AttendanceStatus.FALTA_JUSTIFICADA -> Color(0xFF0284C7)
                                    AttendanceStatus.FALTA_INJUSTIFICADA -> SportProCardRed
                                    AttendanceStatus.SIN_MARCAR -> Color.Gray
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (record.motivoJustificacion.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Justificación: ${record.motivoJustificacion}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status selection buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.repository.updateAttendanceRecord(
                                    selectedSessionId,
                                    record.copy(estado = AttendanceStatus.PRESENTE, minutosTarde = 0)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("Presente", fontSize = 10.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                activeRecord = record
                                showLateDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("Tarde", fontSize = 10.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                activeRecord = record
                                showJustifyDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("Justificada", fontSize = 10.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.repository.updateAttendanceRecord(
                                    selectedSessionId,
                                    record.copy(estado = AttendanceStatus.FALTA_INJUSTIFICADA)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("Falta", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }

    // Dialog for Late minutes (US-011 criteria: 1-120 min)
    if (showLateDialog && activeRecord != null) {
        AlertDialog(
            onDismissRequest = { showLateDialog = false },
            title = { Text("Registrar Tardanza") },
            text = {
                Column {
                    Text("Ingresa los minutos de retraso (1-120 min):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = lateMinutes,
                        onValueChange = { lateMinutes = it },
                        label = { Text("Minutos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = lateMinutes.toIntOrNull() ?: 15
                        viewModel.repository.updateAttendanceRecord(
                            selectedSessionId,
                            activeRecord!!.copy(estado = AttendanceStatus.TARDE, minutosTarde = mins)
                        )
                        showLateDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLateDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog for Justification (US-011 criteria: 5-200 chars)
    if (showJustifyDialog && activeRecord != null) {
        AlertDialog(
            onDismissRequest = { showJustifyDialog = false },
            title = { Text("Justificar Falta") },
            text = {
                Column {
                    Text("Ingresa el motivo de la justificación (5-200 caracteres):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = justificationReason,
                        onValueChange = { justificationReason = it },
                        label = { Text("Motivo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.updateAttendanceRecord(
                            selectedSessionId,
                            activeRecord!!.copy(
                                estado = AttendanceStatus.FALTA_JUSTIFICADA,
                                motivoJustificacion = justificationReason
                            )
                        )
                        showJustifyDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJustifyDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (sessionClosedSuccess) {
        AlertDialog(
            onDismissRequest = { sessionClosedSuccess = false },
            title = { Text("Sesión Cerrada con Éxito") },
            text = {
                Text("La sesión ha pasado al estado Realizada. El porcentaje de asistencia del equipo ha sido calculado e incorporado al historial oficial.")
            },
            confirmButton = {
                Button(onClick = { sessionClosedSuccess = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

// ==========================================
// US-012: HISTORIAL DE PARTICIPACIÓN
// ==========================================
@Composable
fun ParticipationHistoryScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Historial de Participación (US-012)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Seguimiento de asistencia y constancia deportiva",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Attendance Metric Cards (Asistencias + Tardanzas / Total)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "91.7%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Porcentaje General de Asistencia",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "(Asistencias + Tardanzas) / Sesiones convocadas",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatPill("Convocadas", "24")
                        StatPill("Asistidas", "20")
                        StatPill("Tardanzas", "2")
                        StatPill("Justificadas", "1")
                        StatPill("Faltas", "1")
                    }
                }
            }
        }

        // 6-Month Attendance Bar Chart (US-012 criteria)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Asistencia Mensual (Últimos 6 meses)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val months = listOf("Abr" to 85, "May" to 90, "Jun" to 95, "Jul" to 88, "Ago" to 100, "Set" to 92)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        months.forEach { (m, pct) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$pct%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height((pct * 0.8).dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(SportProPitchGreen)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(m, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Past Sessions List
        item {
            Text(
                text = "Detalle Cronológico de Sesiones",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        val sessionHistory = listOf(
            Triple("2026-09-24", "Entrenamiento Sub-15", "Presente"),
            Triple("2026-09-22", "Entrenamiento Sub-15", "Tarde (+10 min)"),
            Triple("2026-09-20", "Partido Oficial vs Universitario", "Presente (80 min)"),
            Triple("2026-09-17", "Entrenamiento Sub-15", "Presente"),
            Triple("2026-09-15", "Entrenamiento Sub-15", "Falta Justificada"),
            Triple("2026-09-12", "Partido Amistoso", "Presente (60 min)")
        )

        items(sessionHistory) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = item.second, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = item.first, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (item.third.startsWith("Presente")) SportProPitchGreen.copy(alpha = 0.15f) else SportProAccentGold.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = item.third,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.third.startsWith("Presente")) SportProPitchGreen else SportProAccentGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
