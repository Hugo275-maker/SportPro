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
import com.example.model.MonthlyFee
import com.example.model.ParentLinkRequest
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel

// ==========================================
// US-006: PERFIL DEL JUGADOR
// ==========================================
@Composable
fun PlayerProfileScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val players by viewModel.players.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedPlayerId by viewModel.selectedPlayerId.collectAsState()

    val player = players.find { it.jugadorId == selectedPlayerId } ?: players.first()

    // Privacy Masking Rule (US-006 / Matrix 4.3):
    // DT (su equipo), ADM, el propio JUG, o PAD vinculado tienen acceso.
    val hasFullAccess = currentUser?.rol == UserRole.DT ||
            currentUser?.rol == UserRole.ADM ||
            (currentUser?.rol == UserRole.JUG && player.jugadorId == "p1") ||
            (currentUser?.rol == UserRole.PAD && player.apoderadosUids.contains("parent_01"))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player Identity Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${player.dorsal}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${player.nombres} ${player.apellidos}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${player.posicionPrincipal} • Pie ${player.pieHabil}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (player.esMenor) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = SportProAccentGold.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "Menor de edad (Sub-15)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SportProAccentGold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Season Stats Summary (US-006 criteria)
                    Text(
                        text = "Rendimiento en Temporada",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatPill("Partidos", "${player.partidosJugados}")
                        StatPill("Minutos", "${player.minutosJugados}'")
                        StatPill("Goles", "${player.goles}")
                        StatPill("Asist.", "${player.asistencias}")
                        StatPill("Amarillas", "${player.tarjetasAmarillas}")
                        StatPill("Rojas", "${player.tarjetasRojas}")
                    }
                }
            }
        }

        // Physical Data & Emergency Contact (with Privacy Masking US-006)
        item {
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
                        Text(
                            text = "Datos Físicos y Médicos",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (!hasFullAccess) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Protegido", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (hasFullAccess) {
                        Text(text = "Estatura: ${player.estatura} m", fontSize = 13.sp)
                        Text(text = "Peso: ${player.peso} kg", fontSize = 13.sp)
                        Text(text = "DNI: ${player.dni}", fontSize = 13.sp)
                        Text(text = "Fecha Nacimiento: ${player.fechaNacimiento}", fontSize = 13.sp)
                        Text(text = "Teléfono Jugador: ${player.telefono}", fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Contacto de Emergencia (Obligatorio)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(text = "Nombre: ${player.contactoEmergenciaNombre} (${player.contactoEmergenciaParentesco})", fontSize = 13.sp)
                        Text(text = "Teléfono de Emergencia: ${player.contactoEmergenciaTelefono}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    } else {
                        // Masked sensitive view (US-006 criterion)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Información restringida",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Los datos físicos, documento y contactos de emergencia solo son visibles para el entrenador, el apoderado vinculado o el administrador del club.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Link with Parent action / status (US-007)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Vinculación con Apoderado (US-007)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Código único de jugador: SP-P10",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Apoderados vinculados actuales: ${player.apoderadosUids.size}/2 (Máximo 2 apoderados)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.PARENT_LINK_REQUESTS) }
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gestionar Solicitudes de Vinculación")
                    }
                }
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ==========================================
// US-007: VINCULACIÓN PADRE - JUGADOR
// ==========================================
@Composable
fun ParentLinkRequestsScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val requests by viewModel.parentLinkRequests.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showRequestDialog by remember { mutableStateOf(false) }
    var playerCodeInput by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf<String?>(null) }
    var rejectReason by remember { mutableStateOf("") }
    var rejectingRequestId by remember { mutableStateOf<String?>(null) }

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
                        text = "Vinculación de Apoderados",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "US-007: Solicitudes de seguimiento de hijos menores",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (currentUser?.rol == UserRole.PAD) {
                    Button(onClick = { showRequestDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vincular")
                    }
                }
            }
        }

        items(requests) { req ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jugador: ${req.jugadorNombre}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (req.estado) {
                                "Aprobada" -> SportProPitchGreen.copy(alpha = 0.15f)
                                "Pendiente" -> SportProAccentGold.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.errorContainer
                            }
                        ) {
                            Text(
                                text = req.estado,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (req.estado) {
                                    "Aprobada" -> SportProPitchGreen
                                    "Pendiente" -> SportProAccentGold
                                    else -> MaterialTheme.colorScheme.onErrorContainer
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Padre solicitante: ${req.padreNombre}", fontSize = 13.sp)

                    if (req.motivoRechazo != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Motivo de rechazo: ${req.motivoRechazo}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                    }

                    // Coach/Admin can approve or reject
                    if (req.estado == "Pendiente" && (currentUser?.rol == UserRole.DT || currentUser?.rol == UserRole.ADM)) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.repository.resolveParentLink(req.requestId, true) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Aprobar")
                            }
                            OutlinedButton(
                                onClick = { rejectingRequestId = req.requestId },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Rechazar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to request link
    if (showRequestDialog) {
        AlertDialog(
            onDismissRequest = { showRequestDialog = false },
            title = { Text("Solicitar Vinculación de Hijo") },
            text = {
                Column {
                    Text(
                        text = "Ingresa el código único de 6 caracteres entregado por la academia:",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = playerCodeInput,
                        onValueChange = {
                            playerCodeInput = it
                            codeError = null
                        },
                        placeholder = { Text("Ej: SP-P10") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = codeError != null,
                        supportingText = { codeError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (playerCodeInput.isBlank()) {
                            codeError = "Ingresa un código válido"
                            return@Button
                        }
                        viewModel.repository.requestParentLink("p1", "Mateo Flores")
                        showRequestDialog = false
                    }
                ) {
                    Text("Enviar Solicitud")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRequestDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal to reject with mandatory reason (10-200 chars)
    if (rejectingRequestId != null) {
        var rejectError by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { rejectingRequestId = null },
            title = { Text("Motivo de Rechazo") },
            text = {
                Column {
                    Text("El criterio de aceptación exige un motivo de entre 10 y 200 caracteres:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = {
                            rejectReason = it
                            rejectError = null
                        },
                        label = { Text("Motivo") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = rejectError != null,
                        supportingText = { rejectError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectReason.length < 10) {
                            rejectError = "Mínimo 10 caracteres requeridos"
                            return@Button
                        }
                        viewModel.repository.resolveParentLink(rejectingRequestId!!, false, rejectReason)
                        rejectingRequestId = null
                    }
                ) {
                    Text("Confirmar Rechazo")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingRequestId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==========================================
// US-008: REGISTRO SIMULADO DE MENSUALIDADES
// ==========================================
@Composable
fun MonthlyFeesScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val fees by viewModel.monthlyFees.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showRegisterPaymentDialog by remember { mutableStateOf(false) }
    var selectedMonthFilter by remember { mutableStateOf("Todos") }

    // Parent sees only linked child; Admin sees all; DT sees status without amounts
    val visibleFees = if (currentUser?.rol == UserRole.PAD) {
        fees.filter { it.jugadorId == "p1" }
    } else {
        fees
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // MANDATORY PERMANENT DISCLAIMER (US-008 & RN-06)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SportProAccentGold.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SportProAccentGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Registro simulado. No se procesan pagos reales",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SportProAccentGold
                        )
                        Text(
                            text = "Módulo administrativo informativo para seguimiento de cuotas de la academia.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Header & Register Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Control de Mensualidades",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (currentUser?.rol == UserRole.ADM) {
                    Button(onClick = { showRegisterPaymentDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Registrar Pago")
                    }
                }
            }
        }

        items(visibleFees) { fee ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fee.jugadorNombre,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${fee.mes} ${fee.anio} • Registrado el ${fee.fechaRegistro}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (currentUser?.rol != UserRole.DT) {
                            Text(
                                text = "Monto referencial: S/ ${fee.montoReferencial} (${fee.medioDeclarado})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "Estado administrativo del alumno",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Status Pill (US-008: Pagado, Pendiente, Vencido, Exonerado)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (fee.estado) {
                            "Pagado" -> SportProPitchGreen.copy(alpha = 0.15f)
                            "Pendiente" -> SportProAccentGold.copy(alpha = 0.15f)
                            "Vencido" -> SportProCardRed.copy(alpha = 0.15f)
                            else -> Color(0xFF0284C7).copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = fee.estado,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (fee.estado) {
                                "Pagado" -> SportProPitchGreen
                                "Pendiente" -> SportProAccentGold
                                "Vencido" -> SportProCardRed
                                else -> Color(0xFF0284C7)
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }

    // Register simulated payment modal
    if (showRegisterPaymentDialog) {
        var playerName by remember { mutableStateOf("Mateo Flores") }
        var month by remember { mutableStateOf("Octubre") }
        var amount by remember { mutableStateOf("250.00") }
        var method by remember { mutableStateOf("Transferencia") }
        var obs by remember { mutableStateOf("Pago simulado mensualidad regular") }

        AlertDialog(
            onDismissRequest = { showRegisterPaymentDialog = false },
            title = { Text("Registrar Pago Simulado (US-008)") },
            text = {
                Column {
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        label = { Text("Jugador") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = it },
                        label = { Text("Mes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Monto referencial (0 a 5000)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = method,
                        onValueChange = { method = it },
                        label = { Text("Medio declarado (Efectivo / Transferencia)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull() ?: 250.0
                        viewModel.repository.registerMonthlyFeePayment(
                            MonthlyFee(
                                feeId = "fee_${System.currentTimeMillis()}",
                                jugadorId = "p1",
                                jugadorNombre = playerName,
                                mes = month,
                                anio = 2026,
                                estado = "Pagado",
                                montoReferencial = amt,
                                fechaRegistro = "2026-09-25",
                                medioDeclarado = method,
                                observacion = obs
                            )
                        )
                        showRegisterPaymentDialog = false
                    }
                ) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterPaymentDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
