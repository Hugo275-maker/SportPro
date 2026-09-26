package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class SportProRepository {

    // Current User & Session State (US-001, US-002, US-003)
    val demoUsers = listOf(
        UserProfile(
            uid = "coach_01",
            nombres = "Carlos",
            apellidos = "Gareca Lima",
            correo = "carlos.dt@sportpro.com",
            telefono = "998877665",
            fechaNacimiento = "1982-05-10",
            rol = UserRole.DT,
            esMenor = false,
            clubId = "club_01",
            clubNombre = "SportPro Academy Lima"
        ),
        UserProfile(
            uid = "player_01",
            nombres = "Mateo",
            apellidos = "Flores Silva",
            correo = "mateo.jug@sportpro.com",
            telefono = "987654321",
            fechaNacimiento = "2009-04-15",
            rol = UserRole.JUG,
            esMenor = true,
            correoApoderado = "rosa.pad@sportpro.com",
            clubId = "club_01",
            clubNombre = "SportPro Academy Lima"
        ),
        UserProfile(
            uid = "parent_01",
            nombres = "Rosa",
            apellidos = "Flores Vargas",
            correo = "rosa.pad@sportpro.com",
            telefono = "987112233",
            fechaNacimiento = "1984-11-20",
            rol = UserRole.PAD,
            esMenor = false,
            clubId = "club_01",
            clubNombre = "SportPro Academy Lima"
        ),
        UserProfile(
            uid = "admin_01",
            nombres = "Gabriela",
            apellidos = "Marín Soto",
            correo = "admin@sportpro.com",
            telefono = "994433221",
            fechaNacimiento = "1988-08-30",
            rol = UserRole.ADM,
            esMenor = false,
            clubId = "club_01",
            clubNombre = "SportPro Academy Lima"
        )
    )

    private val _currentUser = MutableStateFlow<UserProfile?>(demoUsers[0]) // Default logged as DT
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _loginAttempts = MutableStateFlow(0)
    val loginAttempts: StateFlow<Int> = _loginAttempts.asStateFlow()

    private val _lockoutSeconds = MutableStateFlow(0)
    val lockoutSeconds: StateFlow<Int> = _lockoutSeconds.asStateFlow()

    // Club (US-004)
    private val _club = MutableStateFlow(
        Club(
            clubId = "club_01",
            nombre = "SportPro Academy Lima",
            ciudad = "Lima",
            distrito = "Miraflores",
            direccion = "Av. Benavides 1540",
            telefono = "01-4458920",
            correo = "contacto@sportprolima.pe",
            anioFundacion = 2018,
            adminUid = "admin_01"
        )
    )
    val club: StateFlow<Club> = _club.asStateFlow()

    private val _invitationCodes = MutableStateFlow(
        listOf(
            InvitationCode("SP-DT901", UserRole.DT, "Vigente", "2026-10-02"),
            InvitationCode("SP-JG442", UserRole.JUG, "Vigente", "2026-10-02"),
            InvitationCode("SP-PD338", UserRole.PAD, "Usado", "2026-09-20"),
            InvitationCode("SP-AD881", UserRole.ADM, "Vigente", "2026-10-05")
        )
    )
    val invitationCodes: StateFlow<List<InvitationCode>> = _invitationCodes.asStateFlow()

    // Teams (US-005)
    private val _teams = MutableStateFlow(
        listOf(
            Team("team_u15", "club_01", "Sub-15 Competitivo", "Sub-15", 2009, 2010, "coach_01", "Carlos Gareca", 0xFF0D9488, true, 16),
            Team("team_u17", "club_01", "Sub-17 Élite", "Sub-17", 2007, 2008, "coach_01", "Carlos Gareca", 0xFF0369A1, true, 18),
            Team("team_u12", "club_01", "Sub-12 Semillero", "Sub-12", 2012, 2013, "coach_02", "Alonso Vidal", 0xFFD97706, true, 14)
        )
    )
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    // Players (US-006)
    private val _players = MutableStateFlow(
        listOf(
            Player("p1", "team_u15", "Mateo", "Flores Silva", 10, "Volante ofensivo", "Extremo", "Derecho", 1.74f, 66f, "987654321", "74581230", "2009-04-15", "Rosa Flores", "Madre", "987112233", true, listOf("parent_01"), false, 12, 890, 7, 5, 2, 0),
            Player("p2", "team_u15", "Lucas", "Mendoza Ríos", 1, "Arquero", "", "Derecho", 1.82f, 74f, "987112201", "74581231", "2009-02-10", "Juan Mendoza", "Padre", "987001122", true, emptyList(), false, 12, 1080, 0, 0, 1, 0),
            Player("p3", "team_u15", "Sebastián", "Pérez Castro", 4, "Defensa central", "Lateral derecho", "Derecho", 1.78f, 70f, "987112202", "74581232", "2009-06-22", "Ana Castro", "Madre", "987001123", true, emptyList(), false, 11, 860, 1, 1, 3, 0),
            Player("p4", "team_u15", "Joaquín", "Sánchez Vega", 3, "Lateral izquierdo", "Extremo", "Izquierdo", 1.71f, 63f, "987112203", "74581233", "2009-08-11", "Luis Sánchez", "Padre", "987001124", true, emptyList(), false, 10, 740, 2, 2, 0, 0),
            Player("p5", "team_u15", "Diego", "Quispe Morales", 6, "Volante defensivo", "Defensa central", "Derecho", 1.76f, 68f, "987112204", "74581234", "2009-01-05", "Carmen Morales", "Madre", "987001125", true, emptyList(), false, 12, 920, 0, 3, 4, 1),
            Player("p6", "team_u15", "Franco", "Alarcón Ramos", 9, "Delantero", "Extremo", "Derecho", 1.77f, 69f, "987112205", "74581235", "2009-09-18", "Hugo Alarcón", "Padre", "987001126", true, emptyList(), false, 11, 790, 8, 2, 1, 0),
            Player("p7", "team_u15", "Álvaro", "Gutiérrez Bellido", 8, "Volante mixto", "Volante ofensivo", "Ambidiestro", 1.73f, 65f, "987112206", "74581236", "2009-03-30", "Sonia Bellido", "Madre", "987001127", true, emptyList(), false, 12, 850, 3, 6, 2, 0),
            Player("p8", "team_u15", "Nicolás", "Chávez Torres", 7, "Extremo", "Delantero", "Derecho", 1.72f, 64f, "987112207", "74581237", "2009-07-04", "Pedro Chávez", "Padre", "987001128", true, emptyList(), false, 9, 610, 4, 3, 1, 0),
            Player("p9", "team_u15", "Gabriel", "Vargas Pinto", 2, "Lateral derecho", "Defensa central", "Derecho", 1.75f, 67f, "987112208", "74581238", "2009-05-19", "Elena Pinto", "Madre", "987001129", true, emptyList(), false, 10, 720, 0, 1, 2, 0),
            Player("p10", "team_u15", "Rodrigo", "Navarro Luna", 5, "Defensa central", "Volante defensivo", "Derecho", 1.80f, 72f, "987112209", "74581239", "2009-10-12", "César Navarro", "Padre", "987001130", true, emptyList(), false, 11, 880, 1, 0, 3, 0),
            Player("p11", "team_u15", "Alejandro", "Ruiz Paz", 11, "Extremo", "Delantero", "Izquierdo", 1.70f, 62f, "987112210", "74581240", "2009-12-01", "Lucía Paz", "Madre", "987001131", true, emptyList(), false, 10, 680, 3, 4, 1, 0),
            // Substitutes
            Player("p12", "team_u15", "Emilio", "Campos Ortiz", 12, "Arquero", "", "Derecho", 1.79f, 71f, "987112211", "74581241", "2009-04-20", "Gloria Ortiz", "Madre", "987001132", true, emptyList(), false, 3, 180, 0, 0, 0, 0),
            Player("p13", "team_u15", "Christian", "Hidalgo Soto", 14, "Volante mixto", "Volante defensivo", "Derecho", 1.72f, 65f, "987112212", "74581242", "2009-08-25", "Raúl Hidalgo", "Padre", "987001133", true, emptyList(), false, 7, 310, 1, 1, 1, 0),
            Player("p14", "team_u15", "Matías", "Cruz Aguilar", 15, "Delantero", "Extremo", "Derecho", 1.75f, 66f, "987112213", "74581243", "2009-03-14", "Patricia Aguilar", "Madre", "987001134", true, emptyList(), false, 6, 250, 2, 0, 0, 0),
            Player("p15", "team_u15", "Felipe", "Lozano Paredes", 16, "Defensa central", "Lateral izquierdo", "Izquierdo", 1.76f, 68f, "987112214", "74581244", "2009-11-09", "Jorge Lozano", "Padre", "987001135", true, emptyList(), false, 5, 220, 0, 0, 1, 0)
        )
    )
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    // Parent Link Requests (US-007)
    private val _parentLinkRequests = MutableStateFlow(
        listOf(
            ParentLinkRequest("req_01", "parent_01", "Rosa Flores", "p1", "Mateo Flores", "Aprobada", null),
            ParentLinkRequest("req_02", "parent_02", "Juan Mendoza", "p2", "Lucas Mendoza", "Pendiente", null)
        )
    )
    val parentLinkRequests: StateFlow<List<ParentLinkRequest>> = _parentLinkRequests.asStateFlow()

    // Simulated Monthly Fees (US-008)
    private val _monthlyFees = MutableStateFlow(
        listOf(
            MonthlyFee("fee_101", "p1", "Mateo Flores", "team_u15", "Septiembre", 2026, "Pagado", 250.0, "2026-09-05", "Transferencia", "Cuota regular al día"),
            MonthlyFee("fee_102", "p1", "Mateo Flores", "team_u15", "Octubre", 2026, "Pendiente", 250.0, "2026-09-25", "Efectivo", "Vence el 10 de octubre"),
            MonthlyFee("fee_103", "p2", "Lucas Mendoza", "team_u15", "Septiembre", 2026, "Pagado", 250.0, "2026-09-03", "Efectivo"),
            MonthlyFee("fee_104", "p3", "Sebastián Pérez", "team_u15", "Septiembre", 2026, "Vencido", 250.0, "2026-09-12", "Transferencia", "Día de corte superado"),
            MonthlyFee("fee_105", "p4", "Joaquín Sánchez", "team_u15", "Septiembre", 2026, "Exonerado", 0.0, "2026-09-01", "Otro", "Beca deportiva mérito 100%")
        )
    )
    val monthlyFees: StateFlow<List<MonthlyFee>> = _monthlyFees.asStateFlow()

    // Drills Library (US-009)
    private val _drills = MutableStateFlow(
        listOf(
            Drill("dr_1", "Rondos 4v2 con presión alta", "Mejorar pase en espacio reducido y recuperación rápida", "Pase y control", 20, 6, "10 conos, 3 balones, petos", "Los atacantes juegan a dos toques buscando filtrar balones mientras dos defensores ejercen presión continua.", true),
            Drill("dr_2", "Circuito de finalización frontal", "Potencia y precisión en remate al borde del área", "Definición", 25, 8, "4 estacas, balones, arco", "Pase hacia atrás para remate de primera intención desde el semicírculo con marca pasiva.", true),
            Drill("dr_3", "Salida limpia bajo presión 3+1", "Automatizar desmarques y apoyos en salida defensiva", "Táctica ofensiva", 30, 11, "Cintas demarcatorias, petos, 6 balones", "Defensa de 4 más volante central enfrentando repliegue y basculación.", true),
            Drill("dr_4", "Transición defensiva y repliegue veloz", "Cierre de líneas interiores y reducción de espacios", "Táctica defensiva", 20, 10, "Conos, silbato, petos", "Simulación de pérdida en campo rival y retorno en 5 segundos.", true),
            Drill("dr_5", "Activación miofascial y movilidad articular", "Preparación muscular y prevención de lesiones", "Calentamiento", 15, 12, "Colchonetas, vallas bajas", "Ejercicios dinámicos y sprints progresivos.", true)
        )
    )
    val drills: StateFlow<List<Drill>> = _drills.asStateFlow()

    // Training Sessions (US-010)
    private val _trainingSessions = MutableStateFlow(
        listOf(
            TrainingSession(
                sessionId = "ts_101",
                teamId = "team_u15",
                teamNombre = "Sub-15 Competitivo",
                fecha = "2026-09-26",
                horaInicio = "16:30",
                duracionTotalMin = 90,
                lugar = "Campo Deportivo San Borja - Cancha 2",
                objetivoGeneral = "Automatización de repliegue defensivo y transición ofensiva rápida",
                estado = "Programada",
                drills = listOf(
                    SessionDrill("dr_5", "Activación miofascial", 15),
                    SessionDrill("dr_1", "Rondos 4v2 con presión", 25),
                    SessionDrill("dr_4", "Transición defensiva", 30),
                    SessionDrill("dr_2", "Circuito de finalización", 20)
                )
            ),
            TrainingSession(
                sessionId = "ts_100",
                teamId = "team_u15",
                teamNombre = "Sub-15 Competitivo",
                fecha = "2026-09-24",
                horaInicio = "16:30",
                duracionTotalMin = 90,
                lugar = "Campo Deportivo San Borja - Cancha 1",
                objetivoGeneral = "Trabajo de finalización y táctica fija",
                estado = "Realizada",
                drills = listOf(
                    SessionDrill("dr_5", "Activación miofascial", 15),
                    SessionDrill("dr_2", "Circuito de finalización", 40)
                )
            )
        )
    )
    val trainingSessions: StateFlow<List<TrainingSession>> = _trainingSessions.asStateFlow()

    // Attendance (US-011, US-012)
    private val _attendanceRecords = MutableStateFlow<Map<String, List<AttendanceRecord>>>(
        mapOf(
            "ts_101" to listOf(
                AttendanceRecord("att_1", "ts_101", "p1", "Mateo Flores", 10, AttendanceStatus.PRESENTE),
                AttendanceRecord("att_2", "ts_101", "p2", "Lucas Mendoza", 1, AttendanceStatus.PRESENTE),
                AttendanceRecord("att_3", "ts_101", "p3", "Sebastián Pérez", 4, AttendanceStatus.TARDE, 12, "Tráfico pesado en Javier Prado"),
                AttendanceRecord("att_4", "ts_101", "p4", "Joaquín Sánchez", 3, AttendanceStatus.FALTA_JUSTIFICADA, 0, "Cita médica odontológica"),
                AttendanceRecord("att_5", "ts_101", "p5", "Diego Quispe", 6, AttendanceStatus.PRESENTE),
                AttendanceRecord("att_6", "ts_101", "p6", "Franco Alarcón", 9, AttendanceStatus.PRESENTE),
                AttendanceRecord("att_7", "ts_101", "p7", "Álvaro Gutiérrez", 8, AttendanceStatus.PRESENTE),
                AttendanceRecord("att_8", "ts_101", "p8", "Nicolás Chávez", 7, AttendanceStatus.PRESENTE),
                AttendanceRecord("att_9", "ts_101", "p9", "Gabriel Vargas", 2, AttendanceStatus.FALTA_INJUSTIFICADA),
                AttendanceRecord("att_10", "ts_101", "p10", "Rodrigo Navarro", 5, AttendanceStatus.PRESENTE),
                AttendanceRecord("att_11", "ts_101", "p11", "Alejandro Ruiz", 11, AttendanceStatus.PRESENTE)
            )
        )
    )
    val attendanceRecords: StateFlow<Map<String, List<AttendanceRecord>>> = _attendanceRecords.asStateFlow()

    // Dynamic Event Catalog (US-013)
    private val _eventDefinitions = MutableStateFlow(
        listOf(
            EventDefinition("ev_inicio", "Inicio de partido", "play_arrow", 0xFF10B981, "Partido", "Minuto", false),
            EventDefinition("ev_gol", "Gol", "sports_soccer", 0xFF0D9488, "Jugador", "Minuto, equipo, jugador", true),
            EventDefinition("ev_amarilla", "Tarjeta amarilla", "warning", 0xFFEAB308, "Jugador", "Minuto, equipo, jugador", false),
            EventDefinition("ev_roja", "Tarjeta roja", "error", 0xFFEF4444, "Jugador", "Minuto, equipo, jugador", false),
            EventDefinition("ev_cambio", "Cambio", "swap_horiz", 0xFF0284C7, "Jugador", "Minuto, jugador sale, jugador entra", false),
            EventDefinition("ev_penal", "Penal", "sports_football", 0xFF8B5CF6, "Jugador", "Minuto, jugador, resultado", false),
            EventDefinition("ev_falta", "Falta", "front_hand", 0xFF64748B, "Jugador", "Minuto, equipo, jugador", false),
            EventDefinition("ev_corner", "Tiro de esquina", "flag", 0xFF0369A1, "Equipo", "Minuto, equipo", false),
            EventDefinition("ev_lateral", "Saque lateral", "pan_tool", 0xFF94A3B8, "Equipo", "Minuto, equipo", false),
            EventDefinition("ev_meta", "Saque de meta", "sports_handball", 0xFF64748B, "Equipo", "Minuto, equipo", false),
            EventDefinition("ev_offside", "Fuera de juego", "not_interested", 0xFFF97316, "Equipo", "Minuto, equipo", false),
            EventDefinition("ev_fin_1t", "Fin 1T", "pause", 0xFF64748B, "Partido", "Minuto", false),
            EventDefinition("ev_ini_2t", "Inicio 2T", "play_arrow", 0xFF10B981, "Partido", "Minuto", false),
            EventDefinition("ev_fin_partido", "Fin de partido", "stop", 0xFFDC2626, "Partido", "Minuto", false),
            EventDefinition("ev_atajada", "Atajada clave", "shield", 0xFF3B82F6, "Jugador", "Minuto, arquero", false, esPersonalizado = true)
        )
    )
    val eventDefinitions: StateFlow<List<EventDefinition>> = _eventDefinitions.asStateFlow()

    // Matches (US-014, US-017, US-021, US-022)
    private val _matches = MutableStateFlow(
        listOf(
            Match(
                matchId = "m_live",
                teamId = "team_u15",
                teamNombre = "SportPro Sub-15",
                rivalNombre = "Alianza Juvenil",
                fecha = "2026-09-27",
                hora = "10:30",
                lugar = "Estadio Municipal de Barranco",
                tipo = "Liga",
                duracionTiempoMin = 40,
                maxConvocados = 18,
                estado = MatchStatus.EN_CURSO,
                marcadorLocal = 2,
                marcadorRival = 1,
                minutoActual = 54,
                alineacionConfirmada = true,
                versionCatalogo = "v1.2",
                actaCerrada = false
            ),
            Match(
                matchId = "m_final",
                teamId = "team_u15",
                teamNombre = "SportPro Sub-15",
                rivalNombre = "Universitario Filial",
                fecha = "2026-09-20",
                hora = "11:00",
                lugar = "Complejo FPF Videna",
                tipo = "Torneo",
                duracionTiempoMin = 40,
                maxConvocados = 18,
                estado = MatchStatus.FINALIZADO,
                marcadorLocal = 3,
                marcadorRival = 1,
                minutoActual = 80,
                alineacionConfirmada = true,
                versionCatalogo = "v1.2",
                actaCerrada = true,
                fechaCierre = "2026-09-20 12:45"
            ),
            Match(
                matchId = "m_next",
                teamId = "team_u15",
                teamNombre = "SportPro Sub-15",
                rivalNombre = "Sporting Cristal Norte",
                fecha = "2026-10-04",
                hora = "09:00",
                lugar = "Sede La Florida, Rímac",
                tipo = "Liga",
                duracionTiempoMin = 40,
                maxConvocados = 18,
                estado = MatchStatus.PROGRAMADO,
                marcadorLocal = 0,
                marcadorRival = 0,
                minutoActual = 0,
                alineacionConfirmada = false,
                versionCatalogo = "v1.2",
                actaCerrada = false
            )
        )
    )
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    // Convocatoria (US-014, US-015)
    private val _convocatorias = MutableStateFlow<Map<String, List<ConvocatoriaItem>>>(
        mapOf(
            "m_live" to _players.value.mapIndexed { i, p ->
                ConvocatoriaItem(
                    convocatoriaId = "conv_${p.jugadorId}",
                    matchId = "m_live",
                    jugadorId = p.jugadorId,
                    jugadorNombre = "${p.nombres} ${p.apellidos}",
                    dorsal = p.dorsal,
                    estado = if (i == 4) "No disponible" else if (i > 13) "Sin responder" else "Confirmado",
                    respondidoPor = if (i == 0) "Mateo Flores (Jugador)" else "Rosa Flores (Apoderado)",
                    motivoRechazo = if (i == 4) "Compromiso académico escolar" else null
                )
            },
            "m_next" to _players.value.take(16).mapIndexed { i, p ->
                ConvocatoriaItem(
                    convocatoriaId = "conv_next_${p.jugadorId}",
                    matchId = "m_next",
                    jugadorId = p.jugadorId,
                    jugadorNombre = "${p.nombres} ${p.apellidos}",
                    dorsal = p.dorsal,
                    estado = if (i == 0) "Sin responder" else if (i < 6) "Confirmado" else "Sin responder"
                )
            }
        )
    )
    val convocatorias: StateFlow<Map<String, List<ConvocatoriaItem>>> = _convocatorias.asStateFlow()

    // Lineup (US-016)
    private val _lineupFormation = MutableStateFlow("4-3-3")
    val lineupFormation: StateFlow<String> = _lineupFormation.asStateFlow()

    private val _lineupPlayers = MutableStateFlow<List<LineupPlayer>>(
        listOf(
            LineupPlayer("p2", "Lucas Mendoza", 1, "Arquero", 0.5f, 0.88f, true),
            LineupPlayer("p9", "Gabriel Vargas", 2, "Lateral derecho", 0.85f, 0.72f, true),
            LineupPlayer("p3", "Sebastián Pérez", 4, "Defensa central", 0.62f, 0.74f, true),
            LineupPlayer("p10", "Rodrigo Navarro", 5, "Defensa central", 0.38f, 0.74f, true),
            LineupPlayer("p4", "Joaquín Sánchez", 3, "Lateral izquierdo", 0.15f, 0.72f, true),
            LineupPlayer("p5", "Diego Quispe", 6, "Volante defensivo", 0.50f, 0.54f, true),
            LineupPlayer("p7", "Álvaro Gutiérrez", 8, "Volante mixto", 0.72f, 0.44f, true),
            LineupPlayer("p1", "Mateo Flores", 10, "Volante ofensivo", 0.30f, 0.40f, true, esCapitan = true),
            LineupPlayer("p8", "Nicolás Chávez", 7, "Extremo", 0.82f, 0.22f, true),
            LineupPlayer("p6", "Franco Alarcón", 9, "Delantero", 0.50f, 0.16f, true),
            LineupPlayer("p11", "Alejandro Ruiz", 11, "Extremo", 0.18f, 0.22f, true),
            // Bench
            LineupPlayer("p12", "Emilio Campos", 12, "Arquero", 0f, 0f, false),
            LineupPlayer("p13", "Christian Hidalgo", 14, "Volante mixto", 0f, 0f, false),
            LineupPlayer("p14", "Matías Cruz", 15, "Delantero", 0f, 0f, false),
            LineupPlayer("p15", "Felipe Lozano", 16, "Defensa central", 0f, 0f, false)
        )
    )
    val lineupPlayers: StateFlow<List<LineupPlayer>> = _lineupPlayers.asStateFlow()

    // Match Events (US-017, US-018, US-019, US-020)
    private val _matchEvents = MutableStateFlow<Map<String, List<MatchEvent>>>(
        mapOf(
            "m_live" to listOf(
                MatchEvent("ev_1", "m_live", "Inicio de partido", 0, "Local", null, "Árbitro", null, null, "Inicio del primer tiempo", "Vigente"),
                MatchEvent("ev_2", "m_live", "Gol", 14, "Local", "p1", "Mateo Flores", "p7", "Álvaro Gutiérrez", "Definición cruzada al palo derecho", "Vigente"),
                MatchEvent("ev_3", "m_live", "Tarjeta amarilla", 22, "Rival", null, "Jugador del rival", null, null, "Falta táctica por detrás", "Vigente"),
                MatchEvent("ev_4", "m_live", "Gol", 31, "Rival", null, "Jugador del rival", null, null, "Cabezazo tras tiro de esquina", "Vigente"),
                MatchEvent("ev_5", "m_live", "Fin 1T", 40, "Local", null, "Árbitro", null, null, "Descanso reglamentario", "Vigente"),
                MatchEvent("ev_6", "m_live", "Inicio 2T", 41, "Local", null, "Árbitro", null, null, "Reanudación del juego", "Vigente"),
                MatchEvent("ev_7", "m_live", "Gol", 49, "Local", "p6", "Franco Alarcón", "p1", "Mateo Flores", "Remate potente de primera intención", "Vigente"),
                MatchEvent("ev_8", "m_live", "Tarjeta amarilla", 52, "Local", "p5", "Diego Quispe", null, null, "Reclamo reiterado", "Vigente")
            ),
            "m_final" to listOf(
                MatchEvent("ev_f1", "m_final", "Inicio de partido", 0, "Local", null, "Árbitro", null, null, "Inicio de partido", "Vigente"),
                MatchEvent("ev_f2", "m_final", "Gol", 8, "Local", "p1", "Mateo Flores", "p8", "Nicolás Chávez", "Tiro colocado al ángulo", "Vigente"),
                MatchEvent("ev_f3", "m_final", "Gol", 27, "Local", "p6", "Franco Alarcón", "p1", "Mateo Flores", "Anticipo al arquero", "Vigente"),
                MatchEvent("ev_f4", "m_final", "Fin 1T", 40, "Local", null, "Árbitro", null, null, "Fin del primer tiempo", "Vigente"),
                MatchEvent("ev_f5", "m_final", "Inicio 2T", 41, "Local", null, "Árbitro", null, null, "Inicio del segundo tiempo", "Vigente"),
                MatchEvent("ev_f6", "m_final", "Gol", 55, "Rival", null, "Rival Delantero", null, null, "Descuento del visitante", "Vigente"),
                MatchEvent("ev_f7", "m_final", "Cambio", 60, "Local", "p5", "Diego Quispe", "p13", "Christian Hidalgo", "Sustitución táctica", "Vigente"),
                MatchEvent("ev_f8", "m_final", "Tarjeta amarilla", 68, "Local", "p3", "Sebastián Pérez", null, null, "Falta al borde del área", "Vigente"),
                MatchEvent("ev_f9", "m_final", "Gol", 76, "Local", "p1", "Mateo Flores", null, null, "Tiro libre directo exquisito", "Vigente"),
                MatchEvent("ev_f10", "m_final", "Fin de partido", 80, "Local", null, "Árbitro", null, null, "Victoria contundente", "Vigente")
            )
        )
    )
    val matchEvents: StateFlow<Map<String, List<MatchEvent>>> = _matchEvents.asStateFlow()

    // AI Narrative Summaries (US-023, US-024)
    private val _aiSummaries = MutableStateFlow<Map<String, AISummary>>(
        mapOf(
            "m_final" to AISummary(
                summaryId = "ais_01",
                matchId = "m_final",
                version = 1,
                hechosRegistrados = "SportPro Sub-15 se impuso 3-1 ante Universitario Filial en Videna. Los goles locales fueron anotados por Mateo Flores (#10) a los minutos 8' y 76', y Franco Alarcón (#9) al minuto 27'. El descuento rival llegó al minuto 55'. Se registraron amonestaciones para Sebastián Pérez (#4) al 68' y una sustitución al 60' con el ingreso de Christian Hidalgo.",
                desarrolloPartido = "El conjunto de SportPro dominó los tiempos desde el inicio con un juego asociado rápido. La apertura temprana facilitó el control territorial. Universitario descontó en el complemento con balón parado, pero la respuesta táctica selló el triunfo.",
                interpretacionEntrenador = "Gran desempeño colectivo y disciplina en la presión tras pérdida. Destaco la madurez del equipo para manejar los momentos difíciles y liquidar el partido.",
                estado = "Aprobado",
                datosNoVerificados = emptyList(),
                aprobadoPorUid = "coach_01",
                fechaAprobacion = "2026-09-20 13:10"
            )
        )
    )
    val aiSummaries: StateFlow<Map<String, AISummary>> = _aiSummaries.asStateFlow()

    // Summary Evaluations (US-025)
    private val _summaryEvaluations = MutableStateFlow(
        listOf(
            SummaryEvaluation("eval_01", "m_final", 5, 0, 0, 0, "Resumen conciso y muy fiel a la cronología de eventos registrados.", "Se precisó la sustitución de Hidalgo.", "coach_01")
        )
    )
    val summaryEvaluations: StateFlow<List<SummaryEvaluation>> = _summaryEvaluations.asStateFlow()

    // Announcements (US-027)
    private val _announcements = MutableStateFlow(
        listOf(
            Announcement(
                anuncioId = "an_1",
                titulo = "¡Urgente! Cambio de sede para el entrenamiento del sábado",
                mensaje = "Por mantenimiento en el césped de San Borja, la sesión de las 16:30 se traslada al Campo Deportivo de Miraflores Cancha 2. Llegar con 15 minutos de anticipación.",
                destinatarios = "Sub-15",
                prioridad = "Urgente",
                fechaPublicacion = "2026-09-25 08:30",
                autorNombre = "Carlos Gareca (DT)",
                lecturasCount = 14,
                leidoPorCurrentUser = false
            ),
            Announcement(
                anuncioId = "an_2",
                titulo = "Convocatoria confirmada para el fin de semana",
                mensaje = "Ya pueden revisar la lista de jugadores citados para el partido contra Alianza Juvenil. Favor de confirmar antes de las 20:00 hrs de hoy.",
                destinatarios = "Todo el club",
                prioridad = "Informativa",
                fechaPublicacion = "2026-09-24 14:00",
                autorNombre = "Carlos Gareca (DT)",
                lecturasCount = 38,
                leidoPorCurrentUser = true
            ),
            Announcement(
                anuncioId = "an_3",
                titulo = "Reunión de padres de familia - Cierre de trimestre",
                mensaje = "Estimados padres, los invitamos a la charla virtual sobre nutrición deportiva infantil este miércoles a las 19:30 vía Meet.",
                destinatarios = "Padres",
                prioridad = "Informativa",
                fechaPublicacion = "2026-09-22 10:15",
                autorNombre = "Gabriela Marín (Admin)",
                lecturasCount = 26,
                leidoPorCurrentUser = true
            )
        )
    )
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    // Community (US-028)
    private val _communityPosts = MutableStateFlow(
        listOf(
            CommunityPost(
                postId = "cp_1",
                autorUid = "coach_01",
                autorNombre = "Carlos Gareca",
                autorRol = UserRole.DT,
                esMenor = false,
                texto = "Orgulloso de la entrega táctica y el compañerismo demostrado hoy por los muchachos en la Videna. ¡A seguir construyendo juntos el camino formativo!",
                visibilidad = "Comunidad abierta",
                estado = "Publicado",
                fechaPublicacion = "Hace 2 horas",
                likesCount = 19,
                aplausosCount = 12,
                fuerzaCount = 8,
                myReaction = "likes",
                reportesCount = 0,
                comentarios = listOf(
                    PostComment("cm_1", "Rosa Flores", "¡Felicitaciones a todo el comando técnico y al plantel!", "Hace 1 hora"),
                    PostComment("cm_2", "Alonso Vidal", "Excelente planteamiento profe.", "Hace 45 min")
                )
            ),
            CommunityPost(
                postId = "cp_2",
                autorUid = "player_01",
                autorNombre = "Mateo Flores",
                autorRol = UserRole.JUG,
                esMenor = true,
                texto = "Lindo partido y feliz por sumar minutos y goles con mis compañeros. Vamos por más SportPro ⚽🔥",
                visibilidad = "Solo mi academia",
                estado = "Publicado",
                fechaPublicacion = "Hace 4 horas",
                likesCount = 24,
                aplausosCount = 15,
                fuerzaCount = 11,
                myReaction = null,
                reportesCount = 0
            )
        )
    )
    val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

    // Scouting & Trials (US-029)
    private val _scoutingNotices = MutableStateFlow(
        listOf(
            ScoutingNotice(
                noticeId = "sc_1",
                titulo = "Pruebas masivas categoría 2009-2010 (Sub-15)",
                clubNombre = "SportPro Academy Lima",
                categoria = "Sub-15",
                posiciones = listOf("Arquero", "Defensa central", "Volante mixto"),
                fechaPrueba = "2026-10-10",
                hora = "08:30 AM",
                distrito = "Surco",
                ubicacion = "Cancha Sintética Chacarilla (Referencia: Espalda Wong)",
                requisitos = "Traer DNI original, indumentaria deportiva blanca y botella de hidratación.",
                contactoInstitucional = "pruebas@sportprolima.pe / 01-4458920",
                reportesCount = 0
            ),
            ScoutingNotice(
                noticeId = "sc_2",
                titulo = "Captación de talentos Sub-17 para torneo metropolitano",
                clubNombre = "Club Deportivo Los Leones",
                categoria = "Sub-17",
                posiciones = listOf("Delantero", "Extremo", "Lateral izquierdo"),
                fechaPrueba = "2026-10-15",
                hora = "09:00 AM",
                distrito = "San Miguel",
                ubicacion = "Complejo Costanera Norte",
                requisitos = "Experiencia previa en torneos federados o distritales.",
                contactoInstitucional = "contacto@losleonesfc.org",
                reportesCount = 0
            )
        )
    )
    val scoutingNotices: StateFlow<List<ScoutingNotice>> = _scoutingNotices.asStateFlow()

    // Moderation Queue (US-030)
    private val _moderationReports = MutableStateFlow(
        listOf(
            ModerationReport(
                reportId = "rep_1",
                targetId = "cp_suspicious",
                tipoContenido = "Publicación",
                motivo = "Datos personales expuestos",
                detalle = "Usuario publicó foto de un carnet con DNI visible de un menor de edad en la descripción.",
                contenidoResumen = "Publicación de prueba con documento adjunto",
                autorNombre = "Usuario Externo",
                involucraMenor = true,
                estado = "Pendiente",
                reportesAcumulados = 3
            ),
            ModerationReport(
                reportId = "rep_2",
                targetId = "sc_fake",
                tipoContenido = "Aviso",
                motivo = "Convocatoria engañosa",
                detalle = "El aviso solicita depósito de dinero para derecho a prueba.",
                contenidoResumen = "Pruebas Sub-20 en canchas privadas con cobro previo",
                autorNombre = "Club No Verificado",
                involucraMenor = false,
                estado = "Pendiente",
                reportesAcumulados = 4
            )
        )
    )
    val moderationReports: StateFlow<List<ModerationReport>> = _moderationReports.asStateFlow()

    private val _blockedUsers = MutableStateFlow<List<String>>(emptyList())
    val blockedUsers: StateFlow<List<String>> = _blockedUsers.asStateFlow()

    // ==========================================
    // ACTION METHODS
    // ==========================================

    fun switchUserRole(role: UserRole) {
        val user = demoUsers.find { it.rol == role } ?: demoUsers[0]
        _currentUser.value = user
    }

    fun setOfflineMode(offline: Boolean) {
        _isOffline.value = offline
    }

    fun login(correo: String, clave: String): Result<UserProfile> {
        val user = demoUsers.find { it.correo.equals(correo.trim(), ignoreCase = true) }
        return if (user != null && clave == "SportPro2026*") {
            _loginAttempts.value = 0
            _currentUser.value = user
            Result.success(user)
        } else {
            val newAttempts = _loginAttempts.value + 1
            _loginAttempts.value = newAttempts
            if (newAttempts >= 3) {
                _lockoutSeconds.value = 30
            }
            Result.failure(Exception("Correo o contraseña incorrectos"))
        }
    }

    fun registerUser(user: UserProfile): Result<Unit> {
        _currentUser.value = user
        return Result.success(Unit)
    }

    fun logout() {
        _currentUser.value = null
    }

    // US-004 Club
    fun updateClub(updated: Club) {
        _club.value = updated
    }

    fun generateInvitationCode(role: UserRole): String {
        val randomChars = (1..5).map { ('A'..'Z').random() }.joinToString("")
        val newCode = "SP-${role.code}$randomChars".take(8)
        val list = _invitationCodes.value.toMutableList()
        list.add(0, InvitationCode(newCode, role, "Vigente", "2026-10-07"))
        _invitationCodes.value = list
        return newCode
    }

    fun revokeInvitationCode(code: String) {
        _invitationCodes.value = _invitationCodes.value.map {
            if (it.code == code) it.copy(estado = "Expirado") else it
        }
    }

    // US-005 Teams
    fun addTeam(team: Team) {
        val list = _teams.value.toMutableList()
        list.add(team)
        _teams.value = list
    }

    fun deactivateTeam(teamId: String) {
        _teams.value = _teams.value.map {
            if (it.teamId == teamId) it.copy(activo = false) else it
        }
    }

    // US-006 Player
    fun updatePlayer(player: Player) {
        _players.value = _players.value.map {
            if (it.jugadorId == player.jugadorId) player else it
        }
    }

    // US-007 Parent Link
    fun requestParentLink(jugadorId: String, jugadorNombre: String) {
        val current = _currentUser.value ?: return
        val list = _parentLinkRequests.value.toMutableList()
        list.add(
            ParentLinkRequest(
                requestId = "req_${System.currentTimeMillis()}",
                padreUid = current.uid,
                padreNombre = "${current.nombres} ${current.apellidos}",
                jugadorId = jugadorId,
                jugadorNombre = jugadorNombre,
                estado = "Pendiente"
            )
        )
        _parentLinkRequests.value = list
    }

    fun resolveParentLink(requestId: String, approve: Boolean, reason: String? = null) {
        _parentLinkRequests.value = _parentLinkRequests.value.map {
            if (it.requestId == requestId) {
                it.copy(estado = if (approve) "Aprobada" else "Rechazada", motivoRechazo = reason)
            } else it
        }
    }

    // US-008 Monthly Fees
    fun registerMonthlyFeePayment(fee: MonthlyFee) {
        val list = _monthlyFees.value.toMutableList()
        list.removeAll { it.feeId == fee.feeId }
        list.add(0, fee)
        _monthlyFees.value = list
    }

    // US-009 Drills
    fun addDrill(drill: Drill) {
        val list = _drills.value.toMutableList()
        list.add(0, drill)
        _drills.value = list
    }

    // US-010 Training Session
    fun addTrainingSession(session: TrainingSession) {
        val list = _trainingSessions.value.toMutableList()
        list.add(0, session)
        _trainingSessions.value = list
    }

    fun cancelTrainingSession(sessionId: String, motivo: String) {
        _trainingSessions.value = _trainingSessions.value.map {
            if (it.sessionId == sessionId) it.copy(estado = "Cancelada", motivoCancelacion = motivo) else it
        }
    }

    // US-011 Attendance
    fun updateAttendanceRecord(sessionId: String, record: AttendanceRecord) {
        val map = _attendanceRecords.value.toMutableMap()
        val currentList = map[sessionId]?.toMutableList() ?: mutableListOf()
        val idx = currentList.indexOfFirst { it.recordId == record.recordId }
        if (idx >= 0) {
            currentList[idx] = record
        } else {
            currentList.add(record)
        }
        map[sessionId] = currentList
        _attendanceRecords.value = map
    }

    fun markAllPresent(sessionId: String) {
        val map = _attendanceRecords.value.toMutableMap()
        val currentList = map[sessionId]?.map {
            it.copy(estado = AttendanceStatus.PRESENTE, minutosTarde = 0, motivoJustificacion = "")
        } ?: emptyList()
        map[sessionId] = currentList
        _attendanceRecords.value = map
    }

    fun closeAttendanceSession(sessionId: String) {
        _trainingSessions.value = _trainingSessions.value.map {
            if (it.sessionId == sessionId) it.copy(estado = "Realizada") else it
        }
    }

    // US-013 Event Catalog
    fun addEventDefinition(eventDef: EventDefinition) {
        val list = _eventDefinitions.value.toMutableList()
        list.add(eventDef)
        _eventDefinitions.value = list
    }

    fun toggleEventDefinition(typeId: String) {
        _eventDefinitions.value = _eventDefinitions.value.map {
            if (it.typeId == typeId) it.copy(activo = !it.activo) else it
        }
    }

    // US-014 Match Program
    fun addMatch(match: Match) {
        val list = _matches.value.toMutableList()
        list.add(0, match)
        _matches.value = list
    }

    // US-015 Convocatoria
    fun respondConvocatoria(matchId: String, jugadorId: String, confirmed: Boolean, motivo: String?) {
        val map = _convocatorias.value.toMutableMap()
        val list = map[matchId]?.map {
            if (it.jugadorId == jugadorId) {
                it.copy(
                    estado = if (confirmed) "Confirmado" else "No disponible",
                    motivoRechazo = motivo,
                    respondidoPor = _currentUser.value?.nombres ?: "Usuario"
                )
            } else it
        } ?: emptyList()
        map[matchId] = list
        _convocatorias.value = map
    }

    // US-016 Lineup
    fun setFormation(formation: String) {
        _lineupFormation.value = formation
    }

    fun confirmLineup(matchId: String) {
        _matches.value = _matches.value.map {
            if (it.matchId == matchId) it.copy(alineacionConfirmada = true) else it
        }
    }

    fun setCaptain(jugadorId: String) {
        _lineupPlayers.value = _lineupPlayers.value.map {
            it.copy(esCapitan = (it.jugadorId == jugadorId))
        }
    }

    // US-017 Live Match Events
    fun addMatchEvent(matchId: String, event: MatchEvent) {
        val map = _matchEvents.value.toMutableMap()
        val list = map[matchId]?.toMutableList() ?: mutableListOf()
        list.add(event)
        map[matchId] = list
        _matchEvents.value = map

        // Recalculate score
        recalculateScore(matchId)
    }

    fun annulMatchEvent(matchId: String, eventId: String, motivo: String) {
        val map = _matchEvents.value.toMutableMap()
        val list = map[matchId]?.map {
            if (it.eventId == eventId) it.copy(estado = "Anulado", motivoCorreccion = motivo) else it
        } ?: emptyList()
        map[matchId] = list
        _matchEvents.value = map
        recalculateScore(matchId)
    }

    fun editMatchEvent(matchId: String, eventId: String, newMinute: Int, newPlayerName: String, motivo: String) {
        val map = _matchEvents.value.toMutableMap()
        val list = map[matchId]?.map {
            if (it.eventId == eventId) {
                it.copy(
                    minuto = newMinute,
                    jugadorNombre = newPlayerName,
                    motivoCorreccion = motivo,
                    version = it.version + 1,
                    esIncompleto = false
                )
            } else it
        } ?: emptyList()
        map[matchId] = list
        _matchEvents.value = map
        recalculateScore(matchId)
    }

    private fun recalculateScore(matchId: String) {
        val validEvents = _matchEvents.value[matchId]?.filter { it.estado == "Vigente" } ?: return
        var localScore = 0
        var rivalScore = 0
        validEvents.forEach {
            if (it.tipo.equals("Gol", ignoreCase = true)) {
                if (it.equipo.equals("Local", ignoreCase = true)) localScore++ else rivalScore++
            }
        }
        _matches.value = _matches.value.map {
            if (it.matchId == matchId) it.copy(marcadorLocal = localScore, marcadorRival = rivalScore) else it
        }
    }

    // US-022 Match Closure
    fun closeMatch(matchId: String) {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        _matches.value = _matches.value.map {
            if (it.matchId == matchId) it.copy(
                estado = MatchStatus.FINALIZADO,
                actaCerrada = true,
                fechaCierre = now
            ) else it
        }
    }

    fun reopenMatch(matchId: String) {
        _matches.value = _matches.value.map {
            if (it.matchId == matchId) it.copy(
                estado = MatchStatus.EN_CURSO,
                actaCerrada = false,
                fechaCierre = null
            ) else it
        }
    }

    // US-023, US-024 AI Narrative
    fun saveAISummary(summary: AISummary) {
        val map = _aiSummaries.value.toMutableMap()
        map[summary.matchId] = summary
        _aiSummaries.value = map
    }

    fun approveAISummary(matchId: String, coachNotes: String) {
        val summary = _aiSummaries.value[matchId] ?: return
        val approved = summary.copy(
            interpretacionEntrenador = coachNotes,
            estado = "Aprobado",
            aprobadoPorUid = _currentUser.value?.uid ?: "coach_01",
            fechaAprobacion = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )
        saveAISummary(approved)
    }

    // US-025 Evaluation
    fun submitSummaryEvaluation(evaluation: SummaryEvaluation) {
        val list = _summaryEvaluations.value.toMutableList()
        list.add(0, evaluation)
        _summaryEvaluations.value = list
    }

    // US-027 Announcements
    fun addAnnouncement(announcement: Announcement) {
        val list = _announcements.value.toMutableList()
        list.add(0, announcement)
        _announcements.value = list
    }

    fun markAnnouncementRead(anuncioId: String) {
        _announcements.value = _announcements.value.map {
            if (it.anuncioId == anuncioId) it.copy(leidoPorCurrentUser = true) else it
        }
    }

    // US-028 Community
    fun addCommunityPost(post: CommunityPost) {
        val list = _communityPosts.value.toMutableList()
        list.add(0, post)
        _communityPosts.value = list
    }

    fun toggleReaction(postId: String, reaction: String) {
        _communityPosts.value = _communityPosts.value.map { post ->
            if (post.postId == postId) {
                val isSame = post.myReaction == reaction
                val newReaction = if (isSame) null else reaction
                val newLikes = post.likesCount + when {
                    reaction == "likes" && !isSame -> 1
                    post.myReaction == "likes" && isSame -> -1
                    else -> 0
                }
                val newAplausos = post.aplausosCount + when {
                    reaction == "aplausos" && !isSame -> 1
                    post.myReaction == "aplausos" && isSame -> -1
                    else -> 0
                }
                val newFuerza = post.fuerzaCount + when {
                    reaction == "fuerza" && !isSame -> 1
                    post.myReaction == "fuerza" && isSame -> -1
                    else -> 0
                }
                post.copy(
                    myReaction = newReaction,
                    likesCount = maxOf(0, newLikes),
                    aplausosCount = maxOf(0, newAplausos),
                    fuerzaCount = maxOf(0, newFuerza)
                )
            } else post
        }
    }

    fun addComment(postId: String, text: String) {
        val current = _currentUser.value ?: return
        val newComment = PostComment("cm_${System.currentTimeMillis()}", "${current.nombres} ${current.apellidos}", text, "Ahora")
        _communityPosts.value = _communityPosts.value.map {
            if (it.postId == postId) it.copy(comentarios = it.comentarios + newComment) else it
        }
    }

    // US-029 Scouting
    fun addScoutingNotice(notice: ScoutingNotice) {
        val list = _scoutingNotices.value.toMutableList()
        list.add(0, notice)
        _scoutingNotices.value = list
    }

    // US-030 Moderation & Reports
    fun reportContent(targetId: String, type: String, reason: String, detail: String, autorNombre: String, isMinor: Boolean = false) {
        val list = _moderationReports.value.toMutableList()
        val existing = list.indexOfFirst { it.targetId == targetId }
        if (existing >= 0) {
            val curr = list[existing]
            list[existing] = curr.copy(reportesAcumulados = curr.reportesAcumulados + 1)
        } else {
            list.add(
                0,
                ModerationReport(
                    reportId = "rep_${System.currentTimeMillis()}",
                    targetId = targetId,
                    tipoContenido = type,
                    motivo = reason,
                    detalle = detail,
                    contenidoResumen = "Contenido reportado de $autorNombre",
                    autorNombre = autorNombre,
                    involucraMenor = isMinor,
                    reportesAcumulados = 1
                )
            )
        }
        _moderationReports.value = list

        // Auto-hide rule RN-10: 3 or more reports
        val targetReports = list.find { it.targetId == targetId }?.reportesAcumulados ?: 0
        if (targetReports >= 3) {
            _communityPosts.value = _communityPosts.value.map {
                if (it.postId == targetId) it.copy(estado = "En revision") else it
            }
        }
    }

    fun resolveModerationReport(reportId: String, action: String, reason: String) {
        _moderationReports.value = _moderationReports.value.map {
            if (it.reportId == reportId) it.copy(estado = "Resuelto") else it
        }
    }

    fun blockUser(userUid: String) {
        val list = _blockedUsers.value.toMutableList()
        if (!list.contains(userUid)) list.add(userUid)
        _blockedUsers.value = list
    }

    fun unblockUser(userUid: String) {
        _blockedUsers.value = _blockedUsers.value.filter { it != userUid }
    }
}
