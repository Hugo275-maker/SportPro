package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.model.Club
import com.example.model.Team
import com.example.model.UserRole
import com.example.ui.theme.SportProNavy
import com.example.ui.theme.SportProPitchGreen
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel

// ==========================================
// US-004: ACADEMIA Y CLUB
// ==========================================
@Composable
fun AcademyScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val club by viewModel.club.collectAsState()
    val invitationCodes by viewModel.invitationCodes.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showEditClubDialog by remember { mutableStateOf(false) }
    var showGenerateCodeDialog by remember { mutableStateOf(false) }
    var selectedRoleForCode by remember { mutableStateOf(UserRole.JUG) }
    var generatedCodeSuccess by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Club Crest & Info Card
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
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(SportProPitchGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = club.nombre,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${club.distrito}, ${club.ciudad} • Fundado en ${club.anioFundacion}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (currentUser?.rol == UserRole.ADM) {
                            IconButton(onClick = { showEditClubDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar academia")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Dirección: ${club.direccion}", fontSize = 13.sp)
                    Text(text = "Teléfono: ${club.telefono}", fontSize = 13.sp)
                    Text(text = "Correo: ${club.correo}", fontSize = 13.sp)
                }
            }
        }

        // US-004: Códigos de Invitación (8 caracteres)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Códigos de Invitación (US-004)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Códigos de 8 caracteres con validez de 7 días",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (currentUser?.rol == UserRole.ADM) {
                    FilledTonalButton(onClick = { showGenerateCodeDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Generar")
                    }
                }
            }
        }

        items(invitationCodes) { codeItem ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
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
                                text = codeItem.code,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = codeItem.rol.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Expira el: ${codeItem.expiraEn}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (codeItem.estado == "Vigente") SportProPitchGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = codeItem.estado,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (codeItem.estado == "Vigente") SportProPitchGreen else Color.Gray,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (codeItem.estado == "Vigente" && currentUser?.rol == UserRole.ADM) {
                            IconButton(onClick = { viewModel.repository.revokeInvitationCode(codeItem.code) }) {
                                Icon(Icons.Default.Cancel, contentDescription = "Revocar código", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    // Generate Code Dialog
    if (showGenerateCodeDialog) {
        AlertDialog(
            onDismissRequest = {
                showGenerateCodeDialog = false
                generatedCodeSuccess = null
            },
            title = { Text("Generar Código de Invitación") },
            text = {
                Column {
                    if (generatedCodeSuccess == null) {
                        Text(
                            text = "Seleccione el rol al cual se asociará este código de 8 caracteres con validez de 7 días:",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            UserRole.values().forEach { role ->
                                FilterChip(
                                    selected = selectedRoleForCode == role,
                                    onClick = { selectedRoleForCode = role },
                                    label = { Text(role.code) }
                                )
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Código generado exitosamente:", fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = generatedCodeSuccess!!,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Rol asociado: ${selectedRoleForCode.label}", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                if (generatedCodeSuccess == null) {
                    Button(
                        onClick = {
                            generatedCodeSuccess = viewModel.repository.generateInvitationCode(selectedRoleForCode)
                        }
                    ) {
                        Text("Generar")
                    }
                } else {
                    Button(onClick = {
                        showGenerateCodeDialog = false
                        generatedCodeSuccess = null
                    }) {
                        Text("Cerrar")
                    }
                }
            },
            dismissButton = {
                if (generatedCodeSuccess == null) {
                    TextButton(onClick = { showGenerateCodeDialog = false }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }

    // Edit Club Dialog
    if (showEditClubDialog) {
        var clubName by remember { mutableStateOf(club.nombre) }
        var city by remember { mutableStateOf(club.ciudad) }
        var district by remember { mutableStateOf(club.distrito) }
        var address by remember { mutableStateOf(club.direccion) }
        var phone by remember { mutableStateOf(club.telefono) }
        var email by remember { mutableStateOf(club.correo) }
        var foundationYear by remember { mutableStateOf(club.anioFundacion.toString()) }

        AlertDialog(
            onDismissRequest = { showEditClubDialog = false },
            title = { Text("Configuración de la Academia") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = clubName,
                        onValueChange = { clubName = it },
                        label = { Text("Nombre de academia (3-80 caracteres)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Ciudad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("Distrito") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = foundationYear,
                        onValueChange = { foundationYear = it },
                        label = { Text("Año de fundación (1900-2026)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val year = foundationYear.toIntOrNull() ?: 2018
                        viewModel.repository.updateClub(
                            club.copy(
                                nombre = clubName,
                                ciudad = city,
                                distrito = district,
                                direccion = address,
                                anioFundacion = year
                            )
                        )
                        showEditClubDialog = false
                    }
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditClubDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==========================================
// US-005: GESTIÓN DE EQUIPOS POR CATEGORÍAS
// ==========================================
@Composable
fun TeamsScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val teams by viewModel.teams.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val players by viewModel.players.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("Todas") }
    var showAddTeamDialog by remember { mutableStateOf(false) }
    var showCategoryExceptionDialog by remember { mutableStateOf(false) }
    var pendingPlayerAssignment by remember { mutableStateOf<Pair<String, String>?>(null) }

    val categories = listOf("Todas", "Sub-8", "Sub-10", "Sub-12", "Sub-15", "Sub-17", "Sub-20", "Primera")

    val filteredTeams = teams.filter {
        (selectedCategoryFilter == "Todas" || it.categoria == selectedCategoryFilter) && it.activo
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Equipos por Categorías",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "US-005: Rango de edad, DT y control de excepciones",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (currentUser?.rol == UserRole.ADM) {
                    Button(onClick = { showAddTeamDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nuevo Equipo")
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(5).forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat) }
                    )
                }
            }
        }

        items(filteredTeams) { team ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(team.colorUniformeHex))
                                    .border(1.dp, Color.White, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = team.nombre,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = team.categoria,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rango de nacimiento: ${team.anioMin} - ${team.anioMax}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "DT Responsable: ${team.entrenadorNombre}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Plantel activo: ${team.jugadoresCount} jugadores",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.navigateTo(Screen.MY_PROFILE)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ver Jugadores")
                        }

                        if (currentUser?.rol == UserRole.ADM) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.repository.deactivateTeam(team.teamId)
                                }
                            ) {
                                Text("Desactivar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Team Dialog
    if (showAddTeamDialog) {
        var teamName by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Sub-15") }
        var minYear by remember { mutableStateOf("2009") }
        var maxYear by remember { mutableStateOf("2010") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddTeamDialog = false },
            title = { Text("Registrar Nuevo Equipo") },
            text = {
                Column {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Nombre del equipo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría (Sub-8..Sub-20, Primera)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = minYear,
                            onValueChange = { minYear = it },
                            label = { Text("Año Mín") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = maxYear,
                            onValueChange = { maxYear = it },
                            label = { Text("Año Máx") },
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
                        val min = minYear.toIntOrNull() ?: 2009
                        val max = maxYear.toIntOrNull() ?: 2010
                        if (min > max) {
                            errorMsg = "El rango de años es inválido (mínimo debe ser menor o igual al máximo)"
                            return@Button
                        }
                        if (teamName.isBlank()) {
                            errorMsg = "Debe asignar un nombre al equipo"
                            return@Button
                        }
                        viewModel.repository.addTeam(
                            Team(
                                teamId = "team_${System.currentTimeMillis()}",
                                clubId = "club_01",
                                nombre = teamName,
                                categoria = category,
                                anioMin = min,
                                anioMax = max,
                                entrenadorUid = "coach_01",
                                entrenadorNombre = "Carlos Gareca",
                                colorUniformeHex = 0xFF0D9488,
                                activo = true,
                                jugadoresCount = 0
                            )
                        )
                        showAddTeamDialog = false
                    }
                ) {
                    Text("Crear Equipo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTeamDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // US-005: Dialog for Category Range Exception Warning
    if (showCategoryExceptionDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryExceptionDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) },
            title = { Text("Advertencia de Categoría") },
            text = {
                Text("El jugador no cumple el rango de la categoría. ¿Deseas asignarlo de todas formas registrando la excepción técnica?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCategoryExceptionDialog = false
                    }
                ) {
                    Text("Asignar de todas formas")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCategoryExceptionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
