package com.eventpro.model

/**
 * Representa una tarea dentro del checklist del evento.
 */
data class Tarea(
    val id: Int,
    val titulo: String,
    val area: String,
    val completada: Boolean = false
)

/**
 * Prioridad de una incidencia.
 */
enum class Prioridad {
    ALTA, MEDIA, BAJA
}

/**
 * Representa una incidencia reportada en el evento.
 */
data class Incidencia(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val prioridad: Prioridad,
    val resuelta: Boolean = false
)

/**
 * Estado de una actividad en el cronograma.
 */
enum class EstadoActividad {
    EN_CURSO, PROXIMA, COMPLETADA, RETRASADA
}

/**
 * Representa una actividad o charla dentro del cronograma.
 */
data class ActividadCronograma(
    val id: Int,
    val nombre: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: EstadoActividad
)

/**
 * Estado de confirmación de un asistente.
 */
enum class EstadoAsistente {
    CONFIRMADO, PENDIENTE, CANCELADO
}

/**
 * Representa a una persona registrada para el evento.
 */
data class Asistente(
    val id: Int,
    val nombre: String,
    val email: String,
    val estado: EstadoAsistente
)

/**
 * Registro de un ingreso al evento.
 */
data class RegistroIngreso(
    val id: Int,
    val asistenteId: Int,
    val fechaHora: Long
)

/**
 * Representa la información general del evento.
 */
data class Evento(
    val nombre: String,
    val fecha: String,
    val hora: String,
    val lugar: String,
    val dressCode: String,
    val indicaciones: String
)

/**
 * Representa el resultado del escaneo de un código QR.
 */
sealed class ResultadoEscaneo {
    data class Exito(val asistente: Asistente) : ResultadoEscaneo()
    data class Duplicado(val asistente: Asistente) : ResultadoEscaneo()
    object NoValido : ResultadoEscaneo()
}
