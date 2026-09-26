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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.SportProViewModel

// ==========================================
// US-027: ANUNCIOS INTERNOS
// ==========================================
@Composable
fun AnnouncementsScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val announcements by viewModel.announcements.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showNewAnnouncementDialog by remember { mutableStateOf(false) }

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
                    Text("Anuncios del Club", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("US-027: Comunicación interna y notificaciones", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (currentUser?.rol == UserRole.DT || currentUser?.rol == UserRole.ADM) {
                    Button(onClick = { showNewAnnouncementDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nuevo")
                    }
                }
            }
        }

        items(announcements) { an ->
            val isUrgent = an.prioridad == "Urgente"
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUrgent) SportProCardRed.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isUrgent) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SportProCardRed)) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isUrgent) SportProCardRed else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = an.prioridad.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUrgent) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(an.fechaPublicacion, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(an.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(an.mensaje, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Destinatarios: ${an.destinatarios}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        Text("Leído por ${an.lecturasCount} personas", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showNewAnnouncementDialog) {
        var title by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var audience by remember { mutableStateOf("Todo el club") }
        var priority by remember { mutableStateOf("Informativa") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showNewAnnouncementDialog = false },
            title = { Text("Publicar Anuncio Interno") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título (5-80 caracteres)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Mensaje (10-500 caracteres)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        supportingText = { Text("${message.length}/500 caracteres") }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Prioridad:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = priority == "Informativa", onClick = { priority = "Informativa" }, label = { Text("Informativa") })
                        FilterChip(selected = priority == "Urgente", onClick = { priority = "Urgente" }, label = { Text("Urgente") })
                    }
                    if (errorMsg != null) {
                        Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.length < 5) {
                            errorMsg = "Título demasiado corto"
                            return@Button
                        }
                        if (message.length < 10) {
                            errorMsg = "Mensaje debe tener al menos 10 caracteres"
                            return@Button
                        }
                        viewModel.repository.addAnnouncement(
                            Announcement(
                                anuncioId = "an_${System.currentTimeMillis()}",
                                titulo = title,
                                mensaje = message,
                                destinatarios = audience,
                                prioridad = priority,
                                fechaPublicacion = "Ahora",
                                autorNombre = currentUser?.nombres ?: "DT",
                                lecturasCount = 1
                            )
                        )
                        showNewAnnouncementDialog = false
                    }
                ) {
                    Text("Publicar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewAnnouncementDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==========================================
// US-028: COMUNIDAD, PUBLICACIONES Y REACCIONES
// ==========================================
@Composable
fun CommunityScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.communityPosts.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showNewPostDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportedPostId by remember { mutableStateOf<String?>(null) }
    var reportedAuthorName by remember { mutableStateOf("") }
    var activeCommentPostId by remember { mutableStateOf<String?>(null) }
    var commentText by remember { mutableStateOf("") }

    val visiblePosts = posts.filter { it.estado != "Oculto" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Community Header & Quick Post / Navigation Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Comunidad SportPro", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("US-028: Muro, comentarios, reacciones y seguridad", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(onClick = { viewModel.navigateTo(Screen.SCOUTING) }) {
                        Text("Pruebas (US-029)", fontSize = 11.sp)
                    }
                    if (currentUser?.rol == UserRole.ADM) {
                        Button(
                            onClick = { viewModel.navigateTo(Screen.MODERATION) },
                            colors = ButtonDefaults.buttonColors(containerColor = SportProCardRed)
                        ) {
                            Text("Moderar (US-030)", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Create Post Card Prompt
        item {
            Card(
                onClick = { showNewPostDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("¿Qué deseas compartir hoy con la academia?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        }

        items(visiblePosts) { post ->
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SportProPitchGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = post.autorNombre.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(post.autorNombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${post.autorRol.label} • ${post.fechaPublicacion}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = post.visibilidad,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            IconButton(onClick = {
                                reportedPostId = post.postId
                                reportedAuthorName = post.autorNombre
                                showReportDialog = true
                            }) {
                                Icon(Icons.Default.Flag, contentDescription = "Reportar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = post.texto, fontSize = 13.sp, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Reactions: Me gusta, Aplausos, Fuerza (US-028)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ReactionButton(
                            label = "👍 Me gusta (${post.likesCount})",
                            isSelected = post.myReaction == "likes"
                        ) {
                            viewModel.repository.toggleReaction(post.postId, "likes")
                        }

                        ReactionButton(
                            label = "👏 Aplausos (${post.aplausosCount})",
                            isSelected = post.myReaction == "aplausos"
                        ) {
                            viewModel.repository.toggleReaction(post.postId, "aplausos")
                        }

                        ReactionButton(
                            label = "💪 Fuerza (${post.fuerzaCount})",
                            isSelected = post.myReaction == "fuerza"
                        ) {
                            viewModel.repository.toggleReaction(post.postId, "fuerza")
                        }
                    }

                    // Comments section
                    if (post.comentarios.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        post.comentarios.forEach { cm ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(cm.autorNombre, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text(cm.texto, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Add comment field
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = if (activeCommentPostId == post.postId) commentText else "",
                            onValueChange = {
                                activeCommentPostId = post.postId
                                commentText = it
                            },
                            placeholder = { Text("Escribe un comentario...", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.repository.addComment(post.postId, commentText)
                                commentText = ""
                            }
                        }) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    // Modal to create post with phone-leak filter & minor review rule (US-028)
    if (showNewPostDialog) {
        var postContent by remember { mutableStateOf("") }
        var visibility by remember { mutableStateOf("Solo mi academia") }
        var warningPhoneLeak by remember { mutableStateOf<String?>(null) }

        val isMinor = currentUser?.rol == UserRole.JUG && (currentUser?.esMenor == true)

        AlertDialog(
            onDismissRequest = { showNewPostDialog = false },
            title = { Text("Nueva Publicación") },
            text = {
                Column {
                    OutlinedTextField(
                        value = postContent,
                        onValueChange = {
                            postContent = it
                            // Phone number regex detector (9 digits, Peruvian format)
                            val phoneRegex = Regex(".*(9\\d{8}|\\+51\\s?9\\d{8}).*")
                            if (phoneRegex.matches(it)) {
                                warningPhoneLeak = "No compartas números de contacto en la comunidad (US-028)"
                            } else {
                                warningPhoneLeak = null
                            }
                        },
                        label = { Text("¿Qué deseas compartir? (1-1000 caracteres)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        supportingText = {
                            warningPhoneLeak?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            } ?: Text("${postContent.length}/1000")
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Visibilidad:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = visibility == "Solo mi academia",
                            onClick = { visibility = "Solo mi academia" },
                            label = { Text("Solo mi academia") }
                        )
                        FilterChip(
                            selected = visibility == "Comunidad abierta",
                            onClick = {
                                if (isMinor) {
                                    warningPhoneLeak = "Las cuentas de menores de edad no pueden publicar en Comunidad abierta sin aprobación previa del DT (US-028)."
                                } else {
                                    visibility = "Comunidad abierta"
                                }
                            },
                            label = { Text("Comunidad abierta") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (warningPhoneLeak != null) return@Button
                        if (postContent.isBlank()) return@Button

                        val state = if (isMinor && visibility == "Comunidad abierta") "En revision" else "Publicado"

                        viewModel.repository.addCommunityPost(
                            CommunityPost(
                                postId = "cp_${System.currentTimeMillis()}",
                                autorUid = currentUser?.uid ?: "usr",
                                autorNombre = "${currentUser?.nombres} ${currentUser?.apellidos}",
                                autorRol = currentUser?.rol ?: UserRole.DT,
                                esMenor = isMinor,
                                texto = postContent,
                                visibilidad = visibility,
                                estado = state,
                                fechaPublicacion = "Ahora"
                            )
                        )
                        showNewPostDialog = false
                    }
                ) {
                    Text("Publicar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewPostDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal to Report Content (US-030)
    if (showReportDialog && reportedPostId != null) {
        var reportReason by remember { mutableStateOf("Contenido ofensivo") }
        var reportDetail by remember { mutableStateOf("") }
        val reasons = listOf("Contenido ofensivo", "Acoso", "Datos personales expuestos", "Convocatoria engañosa", "Suplantación", "Otro")

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Reportar Contenido (US-030)") },
            text = {
                Column {
                    Text("Selecciona el motivo del reporte:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    reasons.forEach { r ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = reportReason == r, onClick = { reportReason = r })
                            Text(r, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reportDetail,
                        onValueChange = { reportDetail = it },
                        label = { Text("Detalle opcional (máx 300 caracteres)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.reportContent(
                            targetId = reportedPostId!!,
                            type = "Publicación",
                            reason = reportReason,
                            detail = reportDetail,
                            autorNombre = reportedAuthorName,
                            isMinor = false
                        )
                        showReportDialog = false
                    }
                ) {
                    Text("Enviar Reporte")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ReactionButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

// ==========================================
// US-029: AVISOS DE PRUEBAS Y CONVOCATORIAS ABIERTAS
// ==========================================
@Composable
fun ScoutingNoticesScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val notices by viewModel.scoutingNotices.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var categoryFilter by remember { mutableStateOf("Todas") }
    var showNewNoticeDialog by remember { mutableStateOf(false) }

    val filteredNotices = notices.filter {
        (categoryFilter == "Todas" || it.categoria == categoryFilter) && it.reportesCount < 3
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
                    Text("Avisos de Pruebas", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("US-029: Oportunidades deportivas y captación verificada", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (currentUser?.rol == UserRole.DT || currentUser?.rol == UserRole.ADM) {
                    Button(onClick = { showNewNoticeDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Publicar")
                    }
                }
            }
        }

        // Category filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Todas", "Sub-15", "Sub-17", "Sub-20").forEach { cat ->
                    FilterChip(
                        selected = categoryFilter == cat,
                        onClick = { categoryFilter = cat },
                        label = { Text(cat) }
                    )
                }
            }
        }

        items(filteredNotices) { notice ->
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
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = notice.categoria,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text("Prueba: ${notice.fechaPrueba}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SportProPitchGreen)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(notice.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Club: ${notice.clubNombre}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Ubicación: ${notice.ubicacion} (${notice.distrito})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Posiciones buscadas: ${notice.posiciones.joinToString(", ")}", fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Requisitos: ${notice.requisitos}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Contacto institucional oficial: ${notice.contactoInstitucional}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    if (showNewNoticeDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Sub-15") }
        var date by remember { mutableStateOf("2026-10-20") }
        var place by remember { mutableStateOf("Complejo Deportivo Surco") }
        var district by remember { mutableStateOf("Surco") }
        var reqs by remember { mutableStateOf("Traer DNI y ropa deportiva") }
        var contact by remember { mutableStateOf("pruebas@sportprolima.pe") }

        AlertDialog(
            onDismissRequest = { showNewNoticeDialog = false },
            title = { Text("Publicar Aviso de Prueba Abierta (US-029)") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título de la convocatoria") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría (Sub-15, Sub-17...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Fecha de la prueba (posterior a hoy)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("Contacto institucional (correo/teléfono del club)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.addScoutingNotice(
                            ScoutingNotice(
                                noticeId = "sc_${System.currentTimeMillis()}",
                                titulo = title,
                                clubNombre = "SportPro Academy Lima",
                                categoria = category,
                                posiciones = listOf("Arquero", "Defensa central", "Volante"),
                                fechaPrueba = date,
                                hora = "08:30 AM",
                                distrito = district,
                                ubicacion = place,
                                requisitos = reqs,
                                contactoInstitucional = contact
                            )
                        )
                        showNewNoticeDialog = false
                    }
                ) {
                    Text("Publicar Aviso")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewNoticeDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==========================================
// US-030: BANDEJA DE MODERACIÓN DE LA COMUNIDAD
// ==========================================
@Composable
fun ModerationQueueScreen(
    viewModel: SportProViewModel,
    modifier: Modifier = Modifier
) {
    val reports by viewModel.moderationReports.collectAsState()
    var selectedReportForAction by remember { mutableStateOf<ModerationReport?>(null) }
    var resolutionReason by remember { mutableStateOf("") }
    var reasonError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Bandeja de Moderación (US-030)", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Espacio seguro y prioritario para protección de menores de edad", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(reports.filter { it.estado == "Pendiente" }) { report ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (report.involucraMenor) SportProCardRed.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (report.involucraMenor) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SportProCardRed)) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (report.involucraMenor) SportProCardRed else MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = if (report.involucraMenor) "⚠️ ATENCIÓN MENOR DE EDAD (PRIORITARIO)" else "Reporte de ${report.tipoContenido}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (report.involucraMenor) Color.White else MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Text("Reportes: ${report.reportesAcumulados}", fontWeight = FontWeight.Bold, color = SportProCardRed, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Motivo: ${report.motivo}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Autor del contenido: ${report.autorNombre}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Detalle: ${report.detalle}", fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { selectedReportForAction = report },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Resolver Reporte (US-030)")
                    }
                }
            }
        }
    }

    if (selectedReportForAction != null) {
        val rep = selectedReportForAction!!
        AlertDialog(
            onDismissRequest = { selectedReportForAction = null },
            title = { Text("Resolver Reporte: ${rep.motivo}") },
            text = {
                Column {
                    Text("El criterio de aceptación exige un motivo obligatorio (10-300 caracteres):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resolutionReason,
                        onValueChange = {
                            resolutionReason = it
                            reasonError = null
                        },
                        label = { Text("Motivo de la resolución") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = reasonError != null,
                        supportingText = { reasonError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Acciones:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (resolutionReason.length < 10) {
                                reasonError = "Mínimo 10 caracteres"
                                return@Button
                            }
                            viewModel.repository.resolveModerationReport(rep.reportId, "Mantener", resolutionReason)
                            selectedReportForAction = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mantener publicado")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            if (resolutionReason.length < 10) {
                                reasonError = "Mínimo 10 caracteres"
                                return@Button
                            }
                            viewModel.repository.resolveModerationReport(rep.reportId, "Ocultar", resolutionReason)
                            selectedReportForAction = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SportProCardRed)
                    ) {
                        Text("Ocultar y suspender cuenta por 7 días")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedReportForAction = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
