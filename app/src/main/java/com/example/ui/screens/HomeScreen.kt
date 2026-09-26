package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.MatchStatus
import com.example.model.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel

@Composable
fun HomeScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val sessions by viewModel.trainingSessions.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val convocatorias by viewModel.convocatorias.collectAsState()

    val nextSession = sessions.firstOrNull { it.estado == "Programada" } ?: sessions.firstOrNull()
    val liveMatch = matches.firstOrNull { it.estado == MatchStatus.EN_CURSO }
    val nextMatch = liveMatch ?: matches.firstOrNull { it.estado == MatchStatus.PROGRAMADO }

    // Urgent announcement (US-027)
    val urgentAnnouncement = announcements.firstOrNull { it.prioridad == "Urgente" && !it.leidoPorCurrentUser }

    // Pending convocatoria for player or parent (US-015)
    val playerMatchConvocatoria = convocatorias["m_next"]?.firstOrNull {
        it.jugadorId == "p1" && it.estado == "Sin responder"
    }

    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }
    var rejectError by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // User Greeting & Club Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.nombres?.take(1) ?: "U",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¡Hola, ${currentUser?.nombres ?: "Usuario"}!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${currentUser?.clubNombre ?: "SportPro Academy"} • ${currentUser?.rol?.label}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.STATISTICS) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Estadísticas",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // US-027: Urgent Announcement Banner
        if (urgentAnnouncement != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SportProCardRed.copy(alpha = 0.12f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SportProCardRed))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = null,
                                tint = SportProCardRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ANUNCIO URGENTE (US-027)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportProCardRed
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = urgentAnnouncement.titulo,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = urgentAnnouncement.mensaje,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.repository.markAnnouncementRead(urgentAnnouncement.anuncioId)
                                }
                            ) {
                                Text("Marcar como leído", color = SportProCardRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // US-015: Pending Convocatoria card (if player/parent has pending match call)
        if (playerMatchConvocatoria != null && (currentUser?.rol == UserRole.JUG || currentUser?.rol == UserRole.PAD)) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SportProAccentGold.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationImportant,
                                contentDescription = null,
                                tint = SportProAccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONVOCATORIA PENDIENTE (US-015)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SportProAccentGold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Partido vs Sporting Cristal Norte",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Fecha: Domingo 04 Oct - 09:00 AM • Sede La Florida",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Límite para responder: 28 Sep 12:00 hrs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.repository.respondConvocatoria("m_next", "p1", true, null)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SportProPitchGreen)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Confirmar")
                            }

                            OutlinedButton(
                                onClick = { showRejectDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("No podré ir")
                            }
                        }
                    }
                }
            }
        }

        // Live Match or Next Match Card (US-003, US-014, US-017, US-021)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (nextMatch?.estado == MatchStatus.EN_CURSO) "Partido en Vivo 🔥" else "Próximo Partido",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { viewModel.navigateTo(Screen.MATCHES) }) {
                        Text("Ver todos")
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (nextMatch?.estado == MatchStatus.EN_CURSO)
                            SportProNavy
                        else
                            MaterialTheme.colorScheme.surface
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
                                color = if (nextMatch?.estado == MatchStatus.EN_CURSO)
                                    SportProCardRed
                                else
                                    MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = if (nextMatch?.estado == MatchStatus.EN_CURSO)
                                        "EN VIVO • ${viewModel.liveMatchMinute.collectAsState().value}'"
                                    else
                                        "${nextMatch?.tipo} • ${nextMatch?.fecha}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (nextMatch?.estado == MatchStatus.EN_CURSO)
                                        Color.White
                                    else
                                        MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = nextMatch?.lugar ?: "Sede Oficial",
                                fontSize = 12.sp,
                                color = if (nextMatch?.estado == MatchStatus.EN_CURSO) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Match Scoreboard display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = nextMatch?.teamNombre ?: "SportPro",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (nextMatch?.estado == MatchStatus.EN_CURSO) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Local",
                                    fontSize = 11.sp,
                                    color = if (nextMatch?.estado == MatchStatus.EN_CURSO) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Score
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (nextMatch?.estado == MatchStatus.EN_CURSO)
                                    Color(0xFF1E293B)
                                else
                                    MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = "${nextMatch?.marcadorLocal ?: 0} - ${nextMatch?.marcadorRival ?: 0}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (nextMatch?.estado == MatchStatus.EN_CURSO)
                                        SportProPitchLight
                                    else
                                        MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = nextMatch?.rivalNombre ?: "Rival",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (nextMatch?.estado == MatchStatus.EN_CURSO) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Visitante",
                                    fontSize = 11.sp,
                                    color = if (nextMatch?.estado == MatchStatus.EN_CURSO) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action button according to role
                        if (currentUser?.rol == UserRole.DT || currentUser?.rol == UserRole.ADM) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.selectMatch(nextMatch?.matchId ?: "m_live", Screen.LIVE_MATCH)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SportProPitchGreen)
                                ) {
                                    Icon(Icons.Default.SportsScore, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Registrar en vivo (US-017)", fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        viewModel.selectMatch(nextMatch?.matchId ?: "m_live", Screen.LINEUP)
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (nextMatch?.estado == MatchStatus.EN_CURSO) Color.White else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.FormatListNumbered, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Alineación", fontSize = 13.sp)
                                }
                            }
                        } else {
                            // Parents or players view as spectator
                            Button(
                                onClick = {
                                    viewModel.selectMatch(nextMatch?.matchId ?: "m_live", Screen.MATCH_SPECTATOR)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = SportProPitchGreen)
                            ) {
                                Icon(Icons.Default.LiveTv, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Seguimiento en Vivo (US-021)")
                            }
                        }
                    }
                }
            }
        }

        // Next Training Session Card (US-003, US-010, US-011)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Próximo Entrenamiento",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { viewModel.navigateTo(Screen.TRAINING) }) {
                        Text("Ver calendario")
                    }
                }

                if (nextSession != null) {
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Event,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${nextSession.fecha} • ${nextSession.horaInicio} (${nextSession.duracionTotalMin} min)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (nextSession.estado == "Programada")
                                        SportProPitchGreen.copy(alpha = 0.15f)
                                    else
                                        Color.Gray.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = nextSession.estado,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (nextSession.estado == "Programada") SportProPitchGreen else Color.Gray,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = nextSession.objetivoGeneral,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = nextSession.lugar,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (currentUser?.rol == UserRole.DT) {
                                    Button(
                                        onClick = {
                                            viewModel.selectSession(nextSession.sessionId, Screen.ATTENDANCE)
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Tomar Asistencia (US-011)")
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.navigateTo(Screen.PARTICIPATION_HISTORY)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Mi Historial de Asistencia (US-012)")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Module Shortcuts
        item {
            Text(
                text = "Accesos Clave de la Academia",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickCard(
                    title = "Resumen IA",
                    subtitle = "US-023 / US-024",
                    icon = Icons.Default.AutoAwesome,
                    color = SportProPitchGreen,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectMatch("m_final", Screen.AI_SUMMARY)
                    }
                )
                QuickCard(
                    title = "Ejercicios",
                    subtitle = "US-009 Biblioteca",
                    icon = Icons.Default.Sports,
                    color = SportProAccentGold,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.navigateTo(Screen.DRILLS)
                    }
                )
                QuickCard(
                    title = "Mensualidades",
                    subtitle = "US-008 Simulado",
                    icon = Icons.Default.Payments,
                    color = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.navigateTo(Screen.MONTHLY_FEES)
                    }
                )
            }
        }
    }

    // Modal to justify rejection (US-015: motivo obligatorio 5-200 caracteres)
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Indicar Motivo de No Asistencia") },
            text = {
                Column {
                    Text(
                        text = "El criterio de aceptación exige un motivo de entre 5 y 200 caracteres para informar al entrenador.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = {
                            rejectReason = it
                            rejectError = ""
                        },
                        label = { Text("Motivo de ausencia") },
                        placeholder = { Text("Ej: Motivo médico, viaje escolar...") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = rejectError.isNotEmpty(),
                        supportingText = {
                            if (rejectError.isNotEmpty()) {
                                Text(rejectError, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("${rejectReason.length}/200 caracteres")
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectReason.trim().length < 5) {
                            rejectError = "El motivo debe tener al menos 5 caracteres"
                        } else {
                            viewModel.repository.respondConvocatoria("m_next", "p1", false, rejectReason)
                            showRejectDialog = false
                        }
                    }
                ) {
                    Text("Enviar Respuesta")
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

@Composable
fun QuickCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
