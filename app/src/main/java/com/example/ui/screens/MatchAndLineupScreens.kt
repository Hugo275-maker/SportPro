package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Match
import com.example.model.MatchStatus
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel

// ==========================================
// US-014: PROGRAMACIÓN DE PARTIDO Y CONVOCATORIA
// ==========================================
@Composable
fun MatchesListScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val matches by viewModel.matches.collectAsState()
    val convocatorias by viewModel.convocatorias.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showNewMatchDialog by remember { mutableStateOf(false) }

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
                        text = "Partidos y Encuentros",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "US-014: Programación, citación y convocatorias",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (currentUser?.rol == UserRole.DT || currentUser?.rol == UserRole.ADM) {
                    Button(onClick = { showNewMatchDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Programar")
                    }
                }
            }
        }

        items(matches) { match ->
            val matchConvocatoria = convocatorias[match.matchId] ?: emptyList()
            val confirmedCount = matchConvocatoria.count { it.estado == "Confirmado" }
            val unavailableCount = matchConvocatoria.count { it.estado == "No disponible" }
            val pendingCount = matchConvocatoria.count { it.estado == "Sin responder" }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (match.estado == MatchStatus.EN_CURSO) SportProNavy else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (match.estado) {
                                MatchStatus.EN_CURSO -> SportProCardRed
                                MatchStatus.FINALIZADO -> SportProPitchGreen
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                        ) {
                            Text(
                                text = match.estado.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (match.estado == MatchStatus.EN_CURSO || match.estado == MatchStatus.FINALIZADO)
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "${match.tipo} • ${match.fecha} ${match.hora}",
                            fontSize = 12.sp,
                            color = if (match.estado == MatchStatus.EN_CURSO) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${match.teamNombre} vs ${match.rivalNombre}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (match.estado == MatchStatus.EN_CURSO) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Lugar: ${match.lugar} (${match.duracionTiempoMin} min por tiempo)",
                        fontSize = 12.sp,
                        color = if (match.estado == MatchStatus.EN_CURSO) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (match.estado != MatchStatus.PROGRAMADO) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Marcador: ${match.marcadorLocal} - ${match.marcadorRival}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (match.estado == MatchStatus.EN_CURSO) SportProPitchLight else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Convocatoria Follow-up (US-014 tracking)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (match.estado == MatchStatus.EN_CURSO) Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Convocatoria (Máx ${match.maxConvocados})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (match.estado == MatchStatus.EN_CURSO) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Total citados: ${matchConvocatoria.size}",
                                    fontSize = 11.sp,
                                    color = if (match.estado == MatchStatus.EN_CURSO) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("✅ Confirmados: $confirmedCount", fontSize = 11.sp, color = SportProPitchGreen, fontWeight = FontWeight.Bold)
                                Text("❌ No van: $unavailableCount", fontSize = 11.sp, color = SportProCardRed)
                                Text("⏳ Pendientes: $pendingCount", fontSize = 11.sp, color = SportProAccentGold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (currentUser?.rol == UserRole.DT || currentUser?.rol == UserRole.ADM) {
                            if (match.estado == MatchStatus.EN_CURSO) {
                                Button(
                                    onClick = { viewModel.selectMatch(match.matchId, Screen.LIVE_MATCH) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportProPitchGreen)
                                ) {
                                    Icon(Icons.Default.SportsScore, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("En Vivo (US-017)")
                                }
                            } else if (match.estado == MatchStatus.PROGRAMADO) {
                                Button(
                                    onClick = { viewModel.selectMatch(match.matchId, Screen.LINEUP) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.FormatListNumbered, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Alineación (US-016)")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.selectMatch(match.matchId, Screen.AI_SUMMARY) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportProPitchGreen)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Resumen IA (US-023)")
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.selectMatch(match.matchId, Screen.MATCH_SPECTATOR)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (match.estado == MatchStatus.EN_CURSO) Color.White else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Ver Ficha")
                            }
                        } else {
                            // Player or parent view
                            Button(
                                onClick = { viewModel.selectMatch(match.matchId, Screen.CONVOCATORIA_RESPONSE) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Mi Convocatoria (US-015)")
                            }
                            OutlinedButton(
                                onClick = { viewModel.selectMatch(match.matchId, Screen.MATCH_SPECTATOR) }
                            ) {
                                Text("En Vivo (US-021)")
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal to schedule new match (US-014 criteria: max 7-30 convocados, duration 10-45 min)
    if (showNewMatchDialog) {
        var rival by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("2026-10-11") }
        var time by remember { mutableStateOf("10:00") }
        var place by remember { mutableStateOf("Complejo Deportivo Surco") }
        var maxPlayers by remember { mutableStateOf("18") }
        var halfDuration by remember { mutableStateOf("40") }
        var matchType by remember { mutableStateOf("Liga") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showNewMatchDialog = false },
            title = { Text("Programar Partido y Convocatoria") },
            text = {
                Column {
                    OutlinedTextField(
                        value = rival,
                        onValueChange = { rival = it },
                        label = { Text("Nombre del rival") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Fecha (AAAA-MM-DD)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Hora") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = place,
                        onValueChange = { place = it },
                        label = { Text("Lugar / Cancha") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = maxPlayers,
                            onValueChange = { maxPlayers = it },
                            label = { Text("Máx Convocados (7-30)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = halfDuration,
                            onValueChange = { halfDuration = it },
                            label = { Text("Minutos Tiempo (10-45)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (errorMsg != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val maxP = maxPlayers.toIntOrNull() ?: 18
                        val dur = halfDuration.toIntOrNull() ?: 40
                        if (maxP < 7 || maxP > 30) {
                            errorMsg = "Valor de convocados debe ser entre 7 y 30"
                            return@Button
                        }
                        if (dur < 10 || dur > 45) {
                            errorMsg = "Duración por tiempo debe ser entre 10 y 45 min"
                            return@Button
                        }
                        if (rival.isBlank()) {
                            errorMsg = "Ingresa el nombre del rival"
                            return@Button
                        }

                        viewModel.repository.addMatch(
                            Match(
                                matchId = "m_${System.currentTimeMillis()}",
                                teamId = "team_u15",
                                teamNombre = "SportPro Sub-15",
                                rivalNombre = rival,
                                fecha = date,
                                hora = time,
                                lugar = place,
                                tipo = matchType,
                                duracionTiempoMin = dur,
                                maxConvocados = maxP,
                                estado = MatchStatus.PROGRAMADO
                            )
                        )
                        showNewMatchDialog = false
                    }
                ) {
                    Text("Crear Partido")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewMatchDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==========================================
// US-015: CONFIRMACIÓN DE DISPONIBILIDAD
// ==========================================
@Composable
fun ConvocatoriaResponseScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMatchId by viewModel.selectedMatchId.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val match = matches.find { it.matchId == selectedMatchId } ?: matches.first()

    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }
    var responseSuccessMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Citación Oficial (US-015)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${match.teamNombre} vs ${match.rivalNombre}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Fecha y Hora: ${match.fecha} a las ${match.hora}", fontSize = 14.sp)
                Text(text = "Sede / Lugar: ${match.lugar}", fontSize = 14.sp)
                Text(text = "Duración: 2 tiempos de ${match.duracionTiempoMin} minutos", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Límite de confirmación: 24 horas antes del partido", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                if (responseSuccessMessage != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SportProPitchGreen.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = responseSuccessMessage!!,
                            color = SportProPitchGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            viewModel.repository.respondConvocatoria(match.matchId, "p1", true, null)
                            responseSuccessMessage = "¡Asistencia confirmada! El entrenador ya puede contar contigo para el once."
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SportProPitchGreen)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirmar Asistencia")
                    }

                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("No podré asistir")
                    }
                }
            }
        }
    }

    if (showRejectDialog) {
        var errorMsg by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Indicar Motivo de No Disponibilidad") },
            text = {
                Column {
                    Text("Conforme a la regla de convocatoria, debes justificar con entre 5 y 200 caracteres:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = {
                            rejectReason = it
                            errorMsg = null
                        },
                        label = { Text("Motivo") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMsg != null,
                        supportingText = { errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectReason.length < 5) {
                            errorMsg = "Mínimo 5 caracteres requeridos"
                            return@Button
                        }
                        viewModel.repository.respondConvocatoria(match.matchId, "p1", false, rejectReason)
                        responseSuccessMessage = "Respuesta registrada: No disponible. Notificado al DT."
                        showRejectDialog = false
                    }
                ) {
                    Text("Enviar Motivo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==========================================
// US-016: ARMADO DE ALINEACIÓN TÁCTICA
// ==========================================
@Composable
fun LineupScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMatchId by viewModel.selectedMatchId.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val formation by viewModel.lineupFormation.collectAsState()
    val lineupPlayers by viewModel.lineupPlayers.collectAsState()

    val match = matches.find { it.matchId == selectedMatchId } ?: matches.first()
    val starters = lineupPlayers.filter { it.titular }
    val substitutes = lineupPlayers.filter { !it.titular }
    val gkCount = starters.count { it.posicion.contains("Arquero", ignoreCase = true) }

    val formations = listOf("4-3-3", "4-4-2", "3-5-2", "4-2-3-1", "5-3-2")
    var confirmMessage by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Formation & Validation Header
        item {
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
                        Column {
                            Text(
                                text = "Alineación Oficial (US-016)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${match.teamNombre} vs ${match.rivalNombre}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Formations selector
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            formations.take(3).forEach { form ->
                                FilterChip(
                                    selected = formation == form,
                                    onClick = { viewModel.repository.setFormation(form) },
                                    label = { Text(form, fontSize = 10.sp) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Validation Counters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Titulares: ${starters.size}/11",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (starters.size == 11) SportProPitchGreen else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Arquero: $gkCount/1",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gkCount == 1) SportProPitchGreen else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Suplentes: ${substitutes.size}/12",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (validationError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = validationError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    if (confirmMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = confirmMessage!!, color = SportProPitchGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Tactical Football Pitch (Visual representation)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF155E38)), // Deep grass green
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.4f)))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Center circle & penalty boxes
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .align(Alignment.Center)
                            .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.5.dp)
                            .align(Alignment.Center)
                            .background(Color.White.copy(alpha = 0.35f))
                    )
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(55.dp)
                            .align(Alignment.BottomCenter)
                            .border(1.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .height(55.dp)
                            .align(Alignment.TopCenter)
                            .border(1.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    )

                    // On-Pitch Player Tokens
                    starters.forEach { player ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(
                                    x = ((player.xRatio * 320)).dp,
                                    y = ((player.yRatio * 280)).dp
                                )
                                .clickable {
                                    viewModel.repository.setCaptain(player.jugadorId)
                                }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (player.posicion.contains("Arquero")) SportProAccentGold else Color.White)
                                        .border(1.5.dp, SportProNavy, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${player.dorsal}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SportProNavy
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = player.nombre.split(" ")[0],
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (player.esCapitan) {
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = SportProAccentGold,
                                            modifier = Modifier.size(12.dp)
                                        ) {
                                            Text(
                                                text = "C",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.Black,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Substitutes Bench (US-016 criteria)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Banca de Suplentes (${substitutes.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    substitutes.forEach { sub ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("#${sub.dorsal}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(sub.nombre, fontSize = 13.sp)
                            }
                            Text(sub.posicion, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Action: Confirm Lineup (Locks XI and unlocks match start)
        item {
            Button(
                onClick = {
                    if (starters.size != 11) {
                        validationError = "Debes asignar exactamente 11 titulares"
                        return@Button
                    }
                    if (gkCount != 1) {
                        validationError = "Debes incluir exactamente un arquero en cancha"
                        return@Button
                    }
                    viewModel.repository.confirmLineup(match.matchId)
                    confirmMessage = "¡Alineación confirmada con éxito! Partido listo para inicio oficial."
                    validationError = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SportProPitchGreen)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirmar Alineación Oficial (US-016)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
