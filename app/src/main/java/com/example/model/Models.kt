package com.example.model

enum class UserRole(val label: String, val code: String) {
    DT("Entrenador", "DT"),
    JUG("Jugador", "JUG"),
    PAD("Padre de familia", "PAD"),
    ADM("Administrador", "ADM")
}

data class UserProfile(
    val uid: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val telefono: String,
    val fechaNacimiento: String,
    val rol: UserRole,
    val esMenor: Boolean = false,
    val correoApoderado: String? = null,
    val clubId: String? = "club_01",
    val clubNombre: String? = "SportPro Academy Lima",
    val estado: String = "Activo",
    val suspendidoHasta: String? = null
)

data class Club(
    val clubId: String,
    val nombre: String,
    val ciudad: String,
    val distrito: String,
    val direccion: String,
    val telefono: String,
    val correo: String,
    val anioFundacion: Int,
    val adminUid: String
)

data class InvitationCode(
    val code: String,
    val rol: UserRole,
    val estado: String, // Vigente, Usado, Expirado
    val expiraEn: String
)

data class Team(
    val teamId: String,
    val clubId: String,
    val nombre: String,
    val categoria: String, // Sub-8, Sub-10, Sub-12, Sub-15, Sub-17, Sub-20, Primera, Otra
    val anioMin: Int,
    val anioMax: Int,
    val entrenadorUid: String,
    val entrenadorNombre: String,
    val colorUniformeHex: Long,
    val activo: Boolean = true,
    val jugadoresCount: Int = 18
)

data class Player(
    val jugadorId: String,
    val teamId: String,
    val nombres: String,
    val apellidos: String,
    val dorsal: Int,
    val posicionPrincipal: String,
    val posicionSecundaria: String = "",
    val pieHabil: String = "Derecho",
    val estatura: Float = 1.74f,
    val peso: Float = 66.0f,
    val telefono: String = "987654321",
    val dni: String = "74581230",
    val fechaNacimiento: String = "2009-04-15",
    val contactoEmergenciaNombre: String = "Rosa Flores",
    val contactoEmergenciaParentesco: String = "Madre",
    val contactoEmergenciaTelefono: String = "987112233",
    val esMenor: Boolean = true,
    val apoderadosUids: List<String> = listOf("pad_01"),
    val excepcionCategoria: Boolean = false,
    val partidosJugados: Int = 12,
    val minutosJugados: Int = 890,
    val goles: Int = 7,
    val asistencias: Int = 4,
    val tarjetasAmarillas: Int = 2,
    val tarjetasRojas: Int = 0
)

data class ParentLinkRequest(
    val requestId: String,
    val padreUid: String,
    val padreNombre: String,
    val jugadorId: String,
    val jugadorNombre: String,
    val estado: String, // Pendiente, Aprobada, Rechazada
    val motivoRechazo: String? = null
)

data class MonthlyFee(
    val feeId: String,
    val jugadorId: String,
    val jugadorNombre: String,
    val teamId: String = "team_u15",
    val mes: String,
    val anio: Int,
    val estado: String, // Pagado, Pendiente, Vencido, Exonerado
    val montoReferencial: Double,
    val fechaRegistro: String,
    val medioDeclarado: String, // Efectivo, Transferencia, Otro
    val observacion: String = "",
    val corregido: Boolean = false
)

data class Drill(
    val drillId: String,
    val nombre: String,
    val objetivo: String,
    val categoriaTecnica: String, // Calentamiento, Técnica individual, Pase y control, Definición, Táctica ofensiva, Táctica defensiva, Físico, Arqueros, Vuelta a la calma
    val duracionMin: Int,
    val minJugadores: Int,
    val materiales: String,
    val descripcion: String,
    val activo: Boolean = true
)

data class TrainingSession(
    val sessionId: String,
    val teamId: String,
    val teamNombre: String,
    val fecha: String,
    val horaInicio: String,
    val duracionTotalMin: Int,
    val lugar: String,
    val objetivoGeneral: String,
    val estado: String, // Programada, Realizada, Cancelada
    val motivoCancelacion: String? = null,
    val drills: List<SessionDrill> = emptyList()
)

data class SessionDrill(
    val drillId: String,
    val nombre: String,
    val duracionMin: Int
)

enum class AttendanceStatus(val label: String) {
    PRESENTE("Presente"),
    TARDE("Tarde"),
    FALTA_JUSTIFICADA("Falta justificada"),
    FALTA_INJUSTIFICADA("Falta injustificada"),
    SIN_MARCAR("Sin marcar")
}

data class AttendanceRecord(
    val recordId: String,
    val sessionId: String,
    val jugadorId: String,
    val jugadorNombre: String,
    val dorsal: Int,
    val estado: AttendanceStatus,
    val minutosTarde: Int = 0,
    val motivoJustificacion: String = "",
    val sincronizado: Boolean = true
)

data class EventDefinition(
    val typeId: String,
    val nombre: String,
    val icono: String,
    val colorHex: Long,
    val ambito: String, // Equipo, Jugador
    val camposObligatorios: String,
    val sumaMarcador: Boolean,
    val activo: Boolean = true,
    val esPersonalizado: Boolean = false
)

enum class MatchStatus(val label: String) {
    PROGRAMADO("Programado"),
    EN_CURSO("En curso"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado")
}

data class Match(
    val matchId: String,
    val teamId: String,
    val teamNombre: String,
    val rivalNombre: String,
    val fecha: String,
    val hora: String,
    val lugar: String,
    val tipo: String, // Amistoso, Liga, Torneo
    val duracionTiempoMin: Int = 45,
    val maxConvocados: Int = 18,
    val estado: MatchStatus = MatchStatus.PROGRAMADO,
    val marcadorLocal: Int = 0,
    val marcadorRival: Int = 0,
    val minutoActual: Int = 0,
    val alineacionConfirmada: Boolean = false,
    val versionCatalogo: String = "v1.2",
    val actaCerrada: Boolean = false,
    val fechaCierre: String? = null
)

data class ConvocatoriaItem(
    val convocatoriaId: String,
    val matchId: String,
    val jugadorId: String,
    val jugadorNombre: String,
    val dorsal: Int,
    val estado: String, // Confirmado, No disponible, Sin responder
    val respondidoPor: String? = null,
    val motivoRechazo: String? = null,
    val fechaLimite: String = "2026-09-28 12:00"
)

data class LineupPlayer(
    val jugadorId: String,
    val nombre: String,
    val dorsal: Int,
    val posicion: String,
    val xRatio: Float,
    val yRatio: Float,
    val titular: Boolean,
    val esCapitan: Boolean = false,
    val confirmadaAsistencia: Boolean = true
)

data class MatchEvent(
    val eventId: String,
    val matchId: String,
    val tipo: String, // Gol, Falta, Tarjeta amarilla, Tarjeta roja, Cambio, Penal, etc.
    val minuto: Int,
    val equipo: String, // Local, Rival
    val jugadorId: String?,
    val jugadorNombre: String,
    val jugadorSecundarioId: String? = null,
    val jugadorSecundarioNombre: String? = null,
    val observacion: String = "",
    val estado: String = "Vigente", // Vigente, Reemplazado, Anulado
    val motivoCorreccion: String? = null,
    val operadorUid: String = "coach_01",
    val version: Int = 1,
    val esIncompleto: Boolean = false,
    val timestampMillis: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = true
)

data class AISummary(
    val summaryId: String,
    val matchId: String,
    val version: Int = 1,
    val hechosRegistrados: String,
    val desarrolloPartido: String,
    val interpretacionEntrenador: String = "",
    val modelo: String = "gemini-3.5-flash",
    val estado: String = "Borrador", // Borrador, Aprobado
    val datosNoVerificados: List<String> = emptyList(),
    val aprobadoPorUid: String? = null,
    val fechaAprobacion: String? = null
)

data class SummaryEvaluation(
    val evalId: String,
    val matchId: String,
    val utilidad: Int, // 1 a 5
    val omisiones: Int,
    val erroresFactuales: Int,
    val datosInventados: Int,
    val comentario: String,
    val correcciones: String,
    val evaluadoPorUid: String
)

data class Announcement(
    val anuncioId: String,
    val titulo: String,
    val mensaje: String,
    val destinatarios: String, // Todo el club, Sub-15, Entrenadores, Jugadores, Padres
    val prioridad: String, // Informativa, Urgente
    val fechaPublicacion: String,
    val autorNombre: String,
    val lecturasCount: Int,
    val leidoPorCurrentUser: Boolean = false
)

data class CommunityPost(
    val postId: String,
    val autorUid: String,
    val autorNombre: String,
    val autorRol: UserRole,
    val esMenor: Boolean,
    val texto: String,
    val visibilidad: String, // Solo mi academia, Comunidad abierta
    val estado: String, // Publicado, En revision, Oculto
    val fechaPublicacion: String,
    val likesCount: Int = 0,
    val aplausosCount: Int = 0,
    val fuerzaCount: Int = 0,
    val myReaction: String? = null,
    val reportesCount: Int = 0,
    val comentarios: List<PostComment> = emptyList()
)

data class PostComment(
    val commentId: String,
    val autorNombre: String,
    val texto: String,
    val fecha: String
)

data class ScoutingNotice(
    val noticeId: String,
    val titulo: String,
    val clubNombre: String,
    val categoria: String,
    val posiciones: List<String>,
    val fechaPrueba: String,
    val hora: String,
    val distrito: String,
    val ubicacion: String,
    val requisitos: String,
    val contactoInstitucional: String,
    val reportesCount: Int = 0
)

data class ModerationReport(
    val reportId: String,
    val targetId: String,
    val tipoContenido: String, // Publicación, Aviso, Comentario
    val motivo: String, // Contenido ofensivo, Acoso, Datos personales expuestos, Convocatoria engañosa, Suplantación, Otro
    val detalle: String,
    val contenidoResumen: String,
    val autorNombre: String,
    val involucraMenor: Boolean = false,
    val estado: String = "Pendiente", // Pendiente, Resuelto
    val reportesAcumulados: Int = 1
)
