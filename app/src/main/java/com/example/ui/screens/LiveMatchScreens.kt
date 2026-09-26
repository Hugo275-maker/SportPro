package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MatchEvent
import com.example.model.MatchStatus
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel

// ==========================================
// US-017, US-018, US-019, US-020: REGISTRO EN VIVO
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMatchRecordingScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMatchId by viewModel.selectedMatchId.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val allEvents by viewModel.matchEvents.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val liveMinute by viewModel.liveMatchMinute.collectAsState()
    val isTimerRunning by viewModel.liveMatchTimerRunning.collectAsState()
    val lineupPlayers by viewModel.lineupPlayers.collectAsState()

    val match = matches.find { it.matchId == selectedMatchId } ?: matches.first()
    val events = allEvents[selectedMatchId] ?: emptyList()
    val incompleteEvents = events.filter { it.esIncompleto && it.estado == "Vigente" }

    // Bottom sheet & modal states
    var activeSheetEvent by remember { mutableStateOf<String?>(null) }
    var selectedEventForAction by remember { mutableStateOf<MatchEvent?>(null) }
    var showAnnulDialog by remember { mutableStateOf(false) }
    var annulReason by remember { mutableStateOf("") }
    var showAuditHistoryDialog by remember { mutableStateOf(false) }
    var showOutOfOrderAlert by remember { mutableStateOf(false) }
    var showCompletePendingDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Live Scoreboard & Timer Header (US-017)
        Surface(
            color = SportProNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = SportProCardRed,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EN VIVO",
                            color = SportProCardRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Timer controller
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$liveMinute'",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        IconButton(onClick = { viewModel.toggleMatchTimer() }) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Pausar/Reanudar",
                                tint = Color.White
                            )
                        }
                    }

                    // Button to Match Closure (US-022)
                    FilledTonalButton(
                        onClick = { viewModel.navigateTo(Screen.MATCH_CLOSURE) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Acta y Cierre (US-022)", fontSize = 11.sp)
                    }
                }

                // Teams Score
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(match.teamNombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Local", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = "${match.marcadorLocal} - ${match.marcadorRival}",
                            color = SportProPitchLight,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(match.rivalNombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Rival", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }

        // US-019: Incomplete events banner
        if (incompleteEvents.isNotEmpty()) {
            Surface(
                color = SportProAccentGold.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️ ${incompleteEvents.size} evento(s) incompletos (Jugador no identificado)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SportProAccentGold
                    )
                    TextButton(onClick = { showCompletePendingDialog = true }) {
                        Text("Completar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Event Palette (US-017 - Min touch target 48dp for fast pitch action)
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Paleta Rápida de Eventos en Cancha (US-017)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EventPaletteButton("Gol", Icons.Default.SportsSoccer, SportProPitchGreen, Modifier.weight(1f)) {
                        activeSheetEvent = "Gol"
                    }
                    EventPaletteButton("Amarilla", Icons.Default.Warning, SportProCardYellow, Modifier.weight(1f)) {
                        activeSheetEvent = "Tarjeta amarilla"
                    }
                    EventPaletteButton("Roja", Icons.Default.Error, SportProCardRed, Modifier.weight(1f)) {
                        activeSheetEvent = "Tarjeta roja"
                    }
                    EventPaletteButton("Cambio", Icons.Default.SwapHoriz, Color(0xFF0284C7), Modifier.weight(1f)) {
                        activeSheetEvent = "Cambio"
                    }
                    EventPaletteButton("Penal", Icons.Default.SportsFootball, Color(0xFF8B5CF6), Modifier.weight(1f)) {
                        activeSheetEvent = "Penal"
                    }
                }
            }
        }

        // Live Events Chronology (US-017, US-018, US-019, US-020)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Cronología del Partido (${events.size} registros)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(events.reversed()) { event ->
                val isAnnulled = event.estado == "Anulado"

                Card(
                    onClick = {
                        selectedEventForAction = event
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAnnulled)
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isAnnulled) 0.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${event.minuto}'",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = event.tipo,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = if (isAnnulled) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    if (isAnnulled) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = SportProCardRed.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "ANULADO (US-018)",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SportProCardRed,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    if (event.esIncompleto && !isAnnulled) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = SportProAccentGold.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Incompleto (US-019)",
                                                fontSize = 9.sp,
                                                color = SportProAccentGold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${event.jugadorNombre}${if (event.jugadorSecundarioNombre != null) " (Asist: ${event.jugadorSecundarioNombre})" else ""}",
                                    fontSize = 12.sp,
                                    textDecoration = if (isAnnulled) TextDecoration.LineThrough else TextDecoration.None
                                )

                                if (event.observacion.isNotBlank()) {
                                    Text(
                                        text = event.observacion,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Offline sync status icon (US-020)
                        Icon(
                            imageVector = if (isOffline) Icons.Default.Schedule else Icons.Default.Check,
                            contentDescription = if (isOffline) "Pendiente de sincronizar" else "Sincronizado",
                            tint = if (isOffline) SportProAccentGold else SportProPitchGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // BottomSheet for Event Recording (Gol, Cambio, Tarjetas, Penal)
    if (activeSheetEvent != null) {
        val eventType = activeSheetEvent!!
        var scorerTeam by remember { mutableStateOf("Local") }
        var selectedScorerName by remember { mutableStateOf("Mateo Flores") }
        var isUnidentifiedPlayer by remember { mutableStateOf(false) }
        var observation by remember { mutableStateOf("") }
        var penaltyResult by remember { mutableStateOf("Convertido") }

        ModalBottomSheet(
            onDismissRequest = { activeSheetEvent = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Registrar $eventType (${liveMinute}')",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Team selector
                Text("Equipo:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = scorerTeam == "Local",
                        onClick = { scorerTeam = "Local" },
                        label = { Text("SportPro (Local)") }
                    )
                    FilterChip(
                        selected = scorerTeam == "Rival",
                        onClick = { scorerTeam = "Rival" },
                        label = { Text("Rival") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Player selection with "Jugador no identificado" (US-019)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isUnidentifiedPlayer,
                        onCheckedChange = {
                            isUnidentifiedPlayer = it
                            if (it) selectedScorerName = "Jugador no identificado"
                        }
                    )
                    Text("Marcar como 'Jugador no identificado' (US-019)", fontSize = 13.sp)
                }

                if (!isUnidentifiedPlayer) {
                    OutlinedTextField(
                        value = selectedScorerName,
                        onValueChange = { selectedScorerName = it },
                        label = { Text("Jugador en cancha") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (eventType == "Penal") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Resultado del Penal:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = penaltyResult == "Convertido",
                            onClick = { penaltyResult = "Convertido" },
                            label = { Text("Convertido (Suma Gol)") }
                        )
                        FilterChip(
                            selected = penaltyResult == "Fallado",
                            onClick = { penaltyResult = "Fallado" },
                            label = { Text("Fallado") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = observation,
                    onValueChange = { observation = it },
                    label = { Text("Observación de la jugada (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val finalPlayer = if (scorerTeam == "Rival") "Jugador del rival" else selectedScorerName
                        viewModel.repository.addMatchEvent(
                            matchId = selectedMatchId,
                            event = MatchEvent(
                                eventId = "ev_${System.currentTimeMillis()}",
                                matchId = selectedMatchId,
                                tipo = eventType,
                                minuto = liveMinute,
                                equipo = scorerTeam,
                                jugadorId = if (isUnidentifiedPlayer || scorerTeam == "Rival") null else "p1",
                                jugadorNombre = finalPlayer,
                                observacion = observation,
                                esIncompleto = isUnidentifiedPlayer
                            )
                        )

                        // If Penalty converted, also score goal
                        if (eventType == "Penal" && penaltyResult == "Convertido") {
                            viewModel.repository.addMatchEvent(
                                matchId = selectedMatchId,
                                event = MatchEvent(
                                    eventId = "ev_pen_goal_${System.currentTimeMillis()}",
                                    matchId = selectedMatchId,
                                    tipo = "Gol",
                                    minuto = liveMinute,
                                    equipo = scorerTeam,
                                    jugadorId = if (isUnidentifiedPlayer || scorerTeam == "Rival") null else "p1",
                                    jugadorNombre = finalPlayer,
                                    observacion = "Gol de penal",
                                    esIncompleto = isUnidentifiedPlayer
                                )
                            )
                        }

                        activeSheetEvent = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Registrar Evento")
                }
            }
        }
    }

    // US-018: Action Sheet on registered event (Editar / Anular / Ver historial)
    if (selectedEventForAction != null) {
        val ev = selectedEventForAction!!
        AlertDialog(
            onDismissRequest = { selectedEventForAction = null },
            title = { Text("Opciones de Evento: ${ev.tipo} (${ev.minuto}')") },
            text = {
                Column {
                    Text("Trazabilidad y control del evento registrado (US-018):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            showAuditHistoryDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver Historial de Versiones")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (ev.estado == "Vigente") {
                        OutlinedButton(
                            onClick = {
                                showAnnulDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SportProCardRed)
                        ) {
                            Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Anular Evento (con motivo)")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedEventForAction = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // US-018: Dialog to Annul Event with Mandatory Reason
    if (showAnnulDialog && selectedEventForAction != null) {
        var errorMsg by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { showAnnulDialog = false },
            title = { Text("Anular Evento (RN-04 / US-018)") },
            text = {
                Column {
                    Text(
                        text = "El criterio de aceptación exige un motivo obligatorio. El evento quedará tachado y el marcador se recalculará automáticamente sin borrado físico:",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = annulReason,
                        onValueChange = {
                            annulReason = it
                            errorMsg = null
                        },
                        label = { Text("Motivo de anulación") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMsg != null,
                        supportingText = { errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (annulReason.trim().length < 5) {
                            errorMsg = "Mínimo 5 caracteres de justificación requeridos"
                            return@Button
                        }
                        viewModel.repository.annulMatchEvent(selectedMatchId, selectedEventForAction!!.eventId, annulReason)
                        showAnnulDialog = false
                        selectedEventForAction = null
                    }
                ) {
                    Text("Confirmar Anulación")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAnnulDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // US-018: Dialog with Version Audit History
    if (showAuditHistoryDialog && selectedEventForAction != null) {
        val ev = selectedEventForAction!!
        AlertDialog(
            onDismissRequest = { showAuditHistoryDialog = false },
            title = { Text("Auditoría del Evento (${ev.tipo})") },
            text = {
                Column {
                    Text("Trazabilidad completa en Firestore:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ID: ${ev.eventId}")
                    Text("Operador: ${ev.operadorUid}")
                    Text("Versión: v${ev.version}")
                    Text("Estado actual: ${ev.estado}")
                    if (ev.motivoCorreccion != null) {
                        Text("Motivo registrado: ${ev.motivoCorreccion}", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showAuditHistoryDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }

    // US-019: Dialog to complete pending incomplete events
    if (showCompletePendingDialog) {
        var assignedPlayer by remember { mutableStateOf("Mateo Flores") }
        AlertDialog(
            onDismissRequest = { showCompletePendingDialog = false },
            title = { Text("Completar Eventos Pendientes (US-019)") },
            text = {
                Column {
                    Text("Asignar jugador faltante al primer evento incompleto:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = assignedPlayer,
                        onValueChange = { assignedPlayer = it },
                        label = { Text("Nombre del jugador") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val firstInc = incompleteEvents.firstOrNull()
                        if (firstInc != null) {
                            viewModel.repository.editMatchEvent(
                                selectedMatchId,
                                firstInc.eventId,
                                firstInc.minuto,
                                assignedPlayer,
                                "Completado post-jugada"
                            )
                        }
                        showCompletePendingDialog = false
                    }
                ) {
                    Text("Guardar Asignación")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompletePendingDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EventPaletteButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp), // Area táctil mínima >= 48dp (US-017)
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(2.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// US-021: SEGUIMIENTO EN VIVO PARA ESPECTADOR
// ==========================================
@Composable
fun SpectatorMatchScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMatchId by viewModel.selectedMatchId.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val allEvents by viewModel.matchEvents.collectAsState()
    val liveMinute by viewModel.liveMatchMinute.collectAsState()
    val match = matches.find { it.matchId == selectedMatchId } ?: matches.first()
    val validEvents = (allEvents[selectedMatchId] ?: emptyList()).filter { it.estado == "Vigente" }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Cronología", "Alineación", "Estadísticas")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Spectator Scoreboard Banner
        Surface(color = SportProNavy, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ESPECTADOR EN VIVO (US-021)", color = SportProPitchLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("$liveMinute'", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(match.teamNombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        text = "${match.marcadorLocal} - ${match.marcadorRival}",
                        color = SportProPitchLight,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(match.rivalNombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Cronología limpia sin acciones de edición (US-021)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(validEvents.reversed()) { event ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${event.minuto}'",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${event.tipo} • ${event.jugadorNombre}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (event.observacion.isNotBlank()) {
                                        Text(text = event.observacion, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Alineación Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Alineación Titular del Equipo (US-021)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Formación: 4-3-3 • DT: Carlos Gareca",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    val starterList = listOf(
                        Triple(1, "Lucas Mendoza", "Arquero"),
                        Triple(2, "Gabriel Vargas", "Lateral derecho"),
                        Triple(4, "Sebastián Pérez", "Defensa central"),
                        Triple(5, "Rodrigo Navarro", "Defensa central"),
                        Triple(3, "Joaquín Sánchez", "Lateral izquierdo"),
                        Triple(6, "Diego Quispe", "Volante defensivo"),
                        Triple(8, "Álvaro Gutiérrez", "Volante mixto"),
                        Triple(10, "Mateo Flores (Capitán)", "Volante ofensivo"),
                        Triple(7, "Nicolás Chávez", "Extremo"),
                        Triple(9, "Franco Alarcón", "Delantero"),
                        Triple(11, "Alejandro Ruiz", "Extremo")
                    )

                    items(starterList) { (dorsal, name, position) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (position == "Arquero") SportProAccentGold else MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "#$dorsal",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (position == "Arquero") Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = position,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Match Stats Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Estadísticas del Partido en Tiempo Real", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    StatComparisonBar("Posesión de eventos", "58%", "42%", 0.58f)
                    StatComparisonBar("Tiros al arco", "8", "4", 0.67f)
                    StatComparisonBar("Faltas", "6", "9", 0.40f)
                    StatComparisonBar("Tarjetas amarillas", "2", "3", 0.40f)
                }
            }
        }
    }
}

@Composable
fun StatComparisonBar(label: String, val1: String, val2: String, ratio: Float = 0.5f) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(val1, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(val2, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .weight(ratio)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .weight(1f - ratio)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

// ==========================================
// US-022: CIERRE DEL PARTIDO Y ACTA FINAL
// ==========================================
@Composable
fun MatchClosureScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMatchId by viewModel.selectedMatchId.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val allEvents by viewModel.matchEvents.collectAsState()
    val match = matches.find { it.matchId == selectedMatchId } ?: matches.first()
    val events = allEvents[selectedMatchId] ?: emptyList()
    val validEvents = events.filter { it.estado == "Vigente" }
    val annulledEvents = events.filter { it.estado == "Anulado" }
    val incompleteEvents = events.filter { it.esIncompleto && it.estado == "Vigente" }
    val context = LocalContext.current

    var showClosedDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Acta Oficial de Partido (US-022)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Partido: ${match.teamNombre} vs ${match.rivalNombre}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Marcador Final: ${match.marcadorLocal} - ${match.marcadorRival}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SportProPitchGreen)
                    Text(text = "Fecha: ${match.fecha} • Sede: ${match.lugar}", fontSize = 13.sp)
                    Text(text = "Versión del catálogo: ${match.versionCatalogo}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Operador del registro: coach_01 (Carlos Gareca)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Resumen de Eventos Registrados:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Eventos Vigentes: ${validEvents.size}")
                    Text("• Eventos Anulados: ${annulledEvents.size}")
                    Text("• Eventos Incompletos: ${incompleteEvents.size}")

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Minutos Calculados por Jugador Titular:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Mateo Flores (#10): 80 minutos")
                    Text("• Lucas Mendoza (#1): 80 minutos")
                    Text("• Franco Alarcón (#9): 80 minutos")
                    Text("• Diego Quispe (#6): 60 minutos (Sustituido)")
                    Text("• Christian Hidalgo (#14): 20 minutos (Ingresó)")

                    Spacer(modifier = Modifier.height(18.dp))

                    if (!match.actaCerrada) {
                        Button(
                            onClick = {
                                viewModel.repository.closeMatch(match.matchId)
                                showClosedDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SportProPitchGreen)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirmar Cierre de Partido y Generar Acta")
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SportProPitchGreen.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Acta Oficial Cerrada y Firmada. Resumen narrativo con IA habilitado.",
                                color = SportProPitchGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val shareText = "Acta Oficial SportPro: ${match.teamNombre} ${match.marcadorLocal} - ${match.marcadorRival} ${match.rivalNombre}. Fecha: ${match.fecha}."
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Compartir Acta"))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Compartir Acta")
                            }

                            Button(
                                onClick = {
                                    viewModel.generateAISummary(match.matchId)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SportProAccentGold)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Generar Resumen IA")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClosedDialog) {
        AlertDialog(
            onDismissRequest = { showClosedDialog = false },
            title = { Text("Partido Finalizado") },
            text = {
                Text("El acta ha sido consolidada. Se ha habilitado la generación del resumen narrativo con IA.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClosedDialog = false
                        viewModel.generateAISummary(match.matchId)
                    }
                ) {
                    Text("Ir al Resumen Narrativo con IA (US-023)")
                }
            }
        )
    }
}
