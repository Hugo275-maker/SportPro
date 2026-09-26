package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SportProRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen(val title: String) {
    LOGIN("Iniciar Sesión"),
    REGISTER("Registro de Usuario"),
    FORGOT_PASSWORD("Recuperar Contraseña"),
    HOME("Inicio"),
    TEAMS("Gestión de Equipos"),
    TRAINING("Entrenamientos"),
    MATCHES("Partidos y Convocatorias"),
    COMMUNITY("Comunidad"),
    MY_PROFILE("Mi Perfil"),
    MY_CHILD("Perfil de Mi Hijo"),
    MONTHLY_FEES("Mensualidades (Simulado)"),
    ACADEMY("Academia y Club"),
    USERS_TEAMS("Usuarios y Equipos"),
    EVENT_CATALOG("Catálogo de Eventos"),
    LIVE_MATCH("Registro en Vivo"),
    MATCH_SPECTATOR("Seguimiento en Vivo"),
    MATCH_CLOSURE("Cierre y Acta de Partido"),
    AI_SUMMARY("Resumen Narrativo IA"),
    AI_EVALUATION("Evaluación de IA"),
    STATISTICS("Estadísticas Acumuladas"),
    ANNOUNCEMENTS("Anuncios del Club"),
    SCOUTING("Avisos de Pruebas"),
    MODERATION("Bandeja de Moderación"),
    DRILLS("Biblioteca de Ejercicios"),
    ATTENDANCE("Registro de Asistencia"),
    LINEUP("Alineación Táctica"),
    CONVOCATORIA_RESPONSE("Confirmar Convocatoria"),
    PARTICIPATION_HISTORY("Historial de Participación"),
    PARENT_LINK_REQUESTS("Solicitudes de Vinculación"),
    INVITATION_CODES("Códigos de Invitación")
}

data class UserStoryItem(
    val id: String,
    val code: String,
    val name: String,
    val role: UserRole,
    val targetScreen: Screen,
    val summary: String,
    val criteria: List<String>
)

class SportProViewModel(
    val repository: SportProRepository = SportProRepository()
) : ViewModel() {

    val currentUser = repository.currentUser
    val isOffline = repository.isOffline
    val club = repository.club
    val invitationCodes = repository.invitationCodes
    val teams = repository.teams
    val players = repository.players
    val parentLinkRequests = repository.parentLinkRequests
    val monthlyFees = repository.monthlyFees
    val drills = repository.drills
    val trainingSessions = repository.trainingSessions
    val attendanceRecords = repository.attendanceRecords
    val eventDefinitions = repository.eventDefinitions
    val matches = repository.matches
    val convocatorias = repository.convocatorias
    val lineupFormation = repository.lineupFormation
    val lineupPlayers = repository.lineupPlayers
    val matchEvents = repository.matchEvents
    val aiSummaries = repository.aiSummaries
    val summaryEvaluations = repository.summaryEvaluations
    val announcements = repository.announcements
    val communityPosts = repository.communityPosts
    val scoutingNotices = repository.scoutingNotices
    val moderationReports = repository.moderationReports
    val blockedUsers = repository.blockedUsers

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenBackStack = MutableStateFlow<List<Screen>>(listOf(Screen.HOME))
    val screenBackStack: StateFlow<List<Screen>> = _screenBackStack.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
    }

    private val _selectedMatchId = MutableStateFlow("m_live")
    val selectedMatchId: StateFlow<String> = _selectedMatchId.asStateFlow()

    private val _selectedSessionId = MutableStateFlow("ts_101")
    val selectedSessionId: StateFlow<String> = _selectedSessionId.asStateFlow()

    private val _selectedPlayerId = MutableStateFlow("p1")
    val selectedPlayerId: StateFlow<String> = _selectedPlayerId.asStateFlow()

    private val _showStoryGuide = MutableStateFlow(false)
    val showStoryGuide: StateFlow<Boolean> = _showStoryGuide.asStateFlow()

    private val _liveMatchTimerRunning = MutableStateFlow(true)
    val liveMatchTimerRunning: StateFlow<Boolean> = _liveMatchTimerRunning.asStateFlow()

    private val _liveMatchMinute = MutableStateFlow(54)
    val liveMatchMinute: StateFlow<Int> = _liveMatchMinute.asStateFlow()

    private val _incompleteEventsCount = MutableStateFlow(0)
    val incompleteEventsCount: StateFlow<Int> = _incompleteEventsCount.asStateFlow()

    private val _offlinePendingCount = MutableStateFlow(0)
    val offlinePendingCount: StateFlow<Int> = _offlinePendingCount.asStateFlow()

    private val _aiGenerationLoading = MutableStateFlow(false)
    val aiGenerationLoading: StateFlow<Boolean> = _aiGenerationLoading.asStateFlow()

    private val _aiGenerationError = MutableStateFlow<String?>(null)
    val aiGenerationError: StateFlow<String?> = _aiGenerationError.asStateFlow()

    private var timerJob: Job? = null

    init {
        startMatchTimer()
    }

    private fun startMatchTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(60000) // update minute
                if (_liveMatchTimerRunning.value && _currentScreen.value == Screen.LIVE_MATCH) {
                    _liveMatchMinute.value += 1
                }
            }
        }
    }

    fun toggleMatchTimer() {
        _liveMatchTimerRunning.value = !_liveMatchTimerRunning.value
    }

    fun navigateTo(screen: Screen) {
        val stack = _screenBackStack.value.toMutableList()
        stack.add(screen)
        _screenBackStack.value = stack
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        val stack = _screenBackStack.value.toMutableList()
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            _screenBackStack.value = stack
            _currentScreen.value = stack.last()
            return true
        }
        return false
    }

    fun switchRole(role: UserRole) {
        repository.switchUserRole(role)
        // Reset to Home
        _screenBackStack.value = listOf(Screen.HOME)
        _currentScreen.value = Screen.HOME
    }

    fun openStoryGuide(show: Boolean) {
        _showStoryGuide.value = show
    }

    fun selectMatch(matchId: String, screen: Screen? = null) {
        _selectedMatchId.value = matchId
        if (screen != null) navigateTo(screen)
    }

    fun selectSession(sessionId: String, screen: Screen? = null) {
        _selectedSessionId.value = sessionId
        if (screen != null) navigateTo(screen)
    }

    fun selectPlayer(playerId: String, screen: Screen? = null) {
        _selectedPlayerId.value = playerId
        if (screen != null) navigateTo(screen)
    }

    fun toggleOffline() {
        val next = !repository.isOffline.value
        repository.setOfflineMode(next)
        if (next) {
            _offlinePendingCount.value = 2
        } else {
            _offlinePendingCount.value = 0
        }
    }

    // US-023: AI Summary generation with verification check
    fun generateAISummary(matchId: String) {
        viewModelScope.launch {
            _aiGenerationLoading.value = true
            _aiGenerationError.value = null
            delay(1200) // realistic AI generation time

            val match = matches.value.find { it.matchId == matchId }
            val events = matchEvents.value[matchId]?.filter { it.estado == "Vigente" } ?: emptyList()

            if (events.size < 3) {
                _aiGenerationLoading.value = false
                _aiGenerationError.value = "No hay suficientes eventos registrados para generar el resumen"
                return@launch
            }

            val scorerList = events.filter { it.tipo.equals("Gol", ignoreCase = true) }
                .map { "${it.jugadorNombre} (${it.minuto}')" }.joinToString(", ")
            val cardsList = events.filter { it.tipo.contains("Tarjeta", ignoreCase = true) }
                .map { "${it.jugadorNombre} (${it.tipo} al ${it.minuto}')" }.joinToString(", ")

            val generatedSummary = AISummary(
                summaryId = "ais_${System.currentTimeMillis()}",
                matchId = matchId,
                version = 1,
                hechosRegistrados = "Encuentro disputado entre ${match?.teamNombre ?: "SportPro"} y ${match?.rivalNombre ?: "Rival"}. Marcador final: ${match?.marcadorLocal} - ${match?.marcadorRival}. Goles registrados: $scorerList. Disciplina: $cardsList.",
                desarrolloPartido = "El equipo mantuvo un esquema ordenado con buena circulación en el mediocampo. Se aprovecharon las transiciones ofensivas para generar llegadas de peligro, neutralizando las arremetidas del rival.",
                interpretacionEntrenador = "",
                estado = "Borrador",
                datosNoVerificados = emptyList()
            )

            repository.saveAISummary(generatedSummary)
            _aiGenerationLoading.value = false
            navigateTo(Screen.AI_SUMMARY)
        }
    }

    // 30 User Stories Catalog for rapid testing
    val allUserStories = listOf(
        UserStoryItem(
            "1", "US-001", "Registro con selección de rol", UserRole.DT, Screen.REGISTER,
            "Campos completos, selector de rol (DT/JUG/PAD/ADM), menor exige apoderado, admin código 8 caracteres.",
            listOf("Validación nombres/apellidos", "Regla de menores con correo de apoderado", "Código de invitación admin")
        ),
        UserStoryItem(
            "2", "US-002", "Inicio de sesión y recuperación", UserRole.DT, Screen.LOGIN,
            "Validación obligatoria, toggle ver contraseña, bloqueo por 3 intentos fallidos (30s) y recuperación.",
            listOf("Visibilidad toggle", "Bloqueo 30s tras 3 intentos", "Recuperación de contraseña")
        ),
        UserStoryItem(
            "3", "US-003", "Navegación diferenciada por rol", UserRole.DT, Screen.HOME,
            "Barra inferior cambia según DT, JUG, PAD o ADM. Home con próximo entreno, partido y convocatorias.",
            listOf("4 variantes de navegación", "Próximo entrenamiento y partido", "Bandeja de convocatorias pendientes")
        ),
        UserStoryItem(
            "4", "US-004", "Configuración de academia y códigos", UserRole.ADM, Screen.ACADEMY,
            "Datos del club, logo, año fundación y generación de códigos de invitación de 8 caracteres con revocación.",
            listOf("Datos del club auditables", "Generador de códigos 8 caracteres", "Estados de códigos vigente/expirado")
        ),
        UserStoryItem(
            "5", "US-005", "Gestión de equipos por categorías", UserRole.ADM, Screen.TEAMS,
            "Sub-8 a Sub-20, Primera y Otra. DT asignado, rango de años y advertencia de excepción de categoría.",
            listOf("Filtros por categoría", "Validación año nacimiento", "Advertencia de categoría y desactivar")
        ),
        UserStoryItem(
            "6", "US-006", "Perfil de jugador y privacidad", UserRole.DT, Screen.MY_PROFILE,
            "Dorsal 1-99, posiciones, físico, contacto emergencia obligatorio para menores. Máscara de privacidad.",
            listOf("Campos deportivos y físicos", "Contacto de emergencia menores", "Máscara de privacidad para terceros")
        ),
        UserStoryItem(
            "7", "US-007", "Vinculación padre y jugador", UserRole.PAD, Screen.PARENT_LINK_REQUESTS,
            "Solicitud con código de 6 caracteres, estado pendiente, aprobación/rechazo con motivo, máx 2 apoderados.",
            listOf("Código de 6 caracteres", "Aprobación/Rechazo con motivo", "Límite 2 apoderados por menor")
        ),
        UserStoryItem(
            "8", "US-008", "Registro simulado de mensualidades", UserRole.ADM, Screen.MONTHLY_FEES,
            "Grilla por mes con estados Pagado, Pendiente, Vencido, Exonerado. Leyenda permanente de registro simulado.",
            listOf("Leyenda permanente obligatoria", "Grilla mensual y estados", "Modal registrar pago referencial")
        ),
        UserStoryItem(
            "9", "US-009", "Biblioteca de ejercicios", UserRole.DT, Screen.DRILLS,
            "Técnica individual, rondos, duración 1-120 min, materiales, contador de caracteres en descripción.",
            listOf("Filtros por técnica y duración", "Contador caracteres restantes", "Desactivar ejercicio en lugar de borrar")
        ),
        UserStoryItem(
            "10", "US-010", "Planificación de sesiones", UserRole.DT, Screen.TRAINING,
            "Equipo, fecha, hora, duración, selección y orden de ejercicios, advertencia de exceso de duración.",
            listOf("Reordenar ejercicios", "Advertencia de duración excedida", "Cancelar sesión con motivo obligatorio")
        ),
        UserStoryItem(
            "11", "US-011", "Registro de asistencia a entrenamiento", UserRole.DT, Screen.ATTENDANCE,
            "Presente, Tarde (minutos), Falta justificada (motivo), Injustificada, Marcar todos, modo offline.",
            listOf("Minutos de tardanza y justificación", "Acción Marcar todos como presentes", "Persistencia offline")
        ),
        UserStoryItem(
            "12", "US-012", "Historial de participación", UserRole.JUG, Screen.PARTICIPATION_HISTORY,
            "Porcentaje de asistencia (asistencias+tardanzas)/total, lista cronológica y gráfico de barras 6 meses.",
            listOf("Cálculo porcentual con 1 decimal", "Gráfico de barras mensual", "Filtros por rango y tipo")
        ),
        UserStoryItem(
            "13", "US-013", "Catálogo dinámico de eventos", UserRole.ADM, Screen.EVENT_CATALOG,
            "14 eventos base + creación de eventos personalizados, ámbitos equipo/jugador, versión de catálogo.",
            listOf("Catálogo extensible", "Eventos personalizados", "Versionamiento v1.2")
        ),
        UserStoryItem(
            "14", "US-014", "Programar partido y convocatoria", UserRole.DT, Screen.MATCHES,
            "Rival, fecha, tiempos (10-45 min), convocados (7-30 jugadores), fecha límite y botón Recordar.",
            listOf("Rango de convocados 7-30", "Seguimiento en tiempo real", "Acción Recordar a sin responder")
        ),
        UserStoryItem(
            "15", "US-015", "Confirmación de disponibilidad", UserRole.JUG, Screen.CONVOCATORIA_RESPONSE,
            "Botones Confirmar y No podré asistir (motivo obligatorio). Registro con autor y alerta si vence fecha.",
            listOf("Motivo obligatorio si rechaza", "Registro de quién respondió", "Aviso destacado en Home")
        ),
        UserStoryItem(
            "16", "US-016", "Armado de alineación titular y suplentes", UserRole.DT, Screen.LINEUP,
            "Campo vertical, 4-4-2 / 4-3-3 / 3-5-2, 11 titulares con 1 arquero obligatorio, suplentes y capitán.",
            listOf("Cancha interactiva con tácticas", "Validación 11 titulares y 1 arquero", "Designación de capitán única")
        ),
        UserStoryItem(
            "17", "US-017", "Registro dinámico de eventos en vivo", UserRole.DT, Screen.LIVE_MATCH,
            "Cronómetro servidor, marcador, paleta de eventos rápida (>=48dp), BottomSheet Gol, Cambio, Tarjetas.",
            listOf("Requiere alineación confirmada", "Gol suma marcador inmediato", "Tarjeta roja retira de cancha")
        ),
        UserStoryItem(
            "18", "US-018", "Corrección y anulación con trazabilidad", UserRole.DT, Screen.LIVE_MATCH,
            "Editar o anular evento con motivo obligatorio, evento tachado con etiqueta Anulado, recálculo de goles.",
            listOf("Historial de versiones", "Anulación tachada sin borrado físico", "Recálculo automático de marcador")
        ),
        UserStoryItem(
            "19", "US-019", "Registro tolerante a datos incompletos", UserRole.DT, Screen.LIVE_MATCH,
            "Opción Jugador no identificado [Incompleto], alerta fuera de orden, diálogo para completar pendientes.",
            listOf("Etiqueta Incompleto", "Alerta de fuera de orden", "Completar pendientes antes de cerrar")
        ),
        UserStoryItem(
            "20", "US-020", "Operación sin conexión y resincronización", UserRole.DT, Screen.LIVE_MATCH,
            "Franja superior 'Sin conexión', íconos de reloj pendientes que pasan a check al resincronizar.",
            listOf("Banner offline visible", "Cola de sincronización con íconos", "Detección de duplicados")
        ),
        UserStoryItem(
            "21", "US-021", "Seguimiento en vivo para espectador", UserRole.PAD, Screen.MATCH_SPECTATOR,
            "Vista en tiempo real para padres/jugadores sin botones de edición, tabs Cronología, Alineación y Stats.",
            listOf("Marcador y cronología viva", "Tabs Cronología / Alineación / Stats", "Sin acciones de edición")
        ),
        UserStoryItem(
            "22", "US-022", "Cierre de partido y acta final", UserRole.DT, Screen.MATCH_CLOSURE,
            "Exige Fin de partido, cálculo de minutos por jugador, acta oficial compartible, reapertura 48h.",
            listOf("Resumen y minutos por jugador", "Estado Finalizado", "Compartir acta como texto/reporte")
        ),
        UserStoryItem(
            "23", "US-023", "Generación de resumen narrativo con IA", UserRole.DT, Screen.AI_SUMMARY,
            "Solo eventos vigentes, 3 bloques (Hechos, Desarrollo, Interpretación), alerta 'Dato no verificado'.",
            listOf("3 bloques estructurados", "Exclusión de datos sensibles", "Detección de datos no verificados")
        ),
        UserStoryItem(
            "24", "US-024", "Revisión y aprobación de resumen narrativo", UserRole.DT, Screen.AI_SUMMARY,
            "Editor lado a lado con cronología, bloqueo si hay datos no verificados, confirmación obligatoria.",
            listOf("Comparación lado a lado", "Validación previa a aprobación", "Leyenda oficial permanente")
        ),
        UserStoryItem(
            "25", "US-025", "Evaluación documentada de IA", UserRole.DT, Screen.AI_EVALUATION,
            "Escala 1 a 5 estrellas, conteo de omisiones, errores e invenciones, diferencias y consolidado exportable.",
            listOf("Escala de utilidad 1-5", "Métricas de alucinaciones/omisiones", "Consolidado exportable")
        ),
        UserStoryItem(
            "26", "US-026", "Estadísticas acumuladas", UserRole.DT, Screen.STATISTICS,
            "PJ, Minutos, Goles, Asistencias, Faltas, Asistencia %. Gráfico de goles últimos 10 partidos y filtros.",
            listOf("Tabla de jugadores ordenable", "Fila Sin asignar", "Gráfico de goles de últimos 10 partidos")
        ),
        UserStoryItem(
            "27", "US-027", "Anuncios internos y notificaciones", UserRole.DT, Screen.ANNOUNCEMENTS,
            "Prioridad Informativa o Urgente. Anuncios urgentes fijados en Home, contador de lecturas y destinatarios.",
            listOf("Banner urgente en Home", "Contador de leídos", "Destinatarios por rol o equipo")
        ),
        UserStoryItem(
            "28", "US-028", "Comunidad, publicaciones y reacciones", UserRole.DT, Screen.COMMUNITY,
            "Publicaciones con fotos, comentarios, reacciones (👍, 👏, 💪), filtro antiteléfonos y regla de menores.",
            listOf("Reacciones Me gusta/Aplausos/Fuerza", "Bloqueo de números de teléfono", "Menor de edad En revisión")
        ),
        UserStoryItem(
            "29", "US-029", "Avisos de pruebas con filtros", UserRole.JUG, Screen.SCOUTING,
            "Buscador con filtros por categoría, posición y distrito. Contacto institucional obligatorio.",
            listOf("Filtros combinables", "Solo contacto institucional", "Reporte de convocatoria engañosa")
        ),
        UserStoryItem(
            "30", "US-030", "Reporte, moderación y bloqueo", UserRole.ADM, Screen.MODERATION,
            "Bandeja de moderación para admin, prioridad menores, ocultación automática con >=3 reportes, bloqueo.",
            listOf("Prioridad de reportes de menores", "Acciones advertir/suspender cuenta", "Ocultación con 3 reportes")
        )
    )

    fun testUserStory(story: UserStoryItem) {
        // 1. Switch to the required role
        switchRole(story.role)
        // 2. Direct to target screen
        _currentScreen.value = story.targetScreen
        _screenBackStack.value = listOf(Screen.HOME, story.targetScreen)
        // 3. Close guide
        _showStoryGuide.value = false
    }
}
