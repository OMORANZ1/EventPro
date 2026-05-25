package com.eventpro.data

import com.eventpro.model.*

/**
 * Repositorio Singleton que contiene datos de prueba para la aplicación.
 * Los datos persisten en memoria mientras la aplicación esté abierta.
 */
object MockDataRepository {

    val tareas = mutableListOf(
        Tarea(1, "Revisar sonido en salón principal", "Técnica", false),
        Tarea(2, "Confirmar catering para almuerzo", "Logística", true),
        Tarea(3, "Colocar señalética en entrada", "Ambientación", false),
        Tarea(4, "Entregar acreditaciones a prensa", "RRPP", false),
        Tarea(5, "Probar conexión Wi-Fi para asistentes", "Técnica", true),
        Tarea(6, "Revisar stock de agua en camerinos", "Logística", false)
    )

    val incidencias = mutableListOf(
        Incidencia(1, "Fallo de proyector", "El proyector del salón B parpadea", Prioridad.ALTA, false),
        Incidencia(2, "Falta silla en mesa 4", "El asistente reporta una silla menos", Prioridad.BAJA, false),
        Incidencia(3, "Aire acondicionado ruidoso", "Salón A tiene ruido molesto", Prioridad.MEDIA, true)
    )

    val actividades = mutableListOf(
        ActividadCronograma(1, "Registro y Bienvenida", "08:00", "09:00", EstadoActividad.COMPLETADA),
        ActividadCronograma(2, "Keynote: Futuro de la IA", "09:00", "10:30", EstadoActividad.EN_CURSO),
        ActividadCronograma(3, "Coffee Break", "10:30", "11:00", EstadoActividad.PROXIMA),
        ActividadCronograma(4, "Panel de Expertos", "11:00", "12:30", EstadoActividad.PROXIMA),
        ActividadCronograma(5, "Almuerzo Networking", "12:30", "14:00", EstadoActividad.PROXIMA)
    )

    val asistentes = mutableListOf(
        Asistente(1, "Juan Pérez", "juan.perez@email.com", EstadoAsistente.CONFIRMADO),
        Asistente(2, "María García", "maria.g@email.com", EstadoAsistente.PENDIENTE),
        Asistente(3, "Carlos López", "c.lopez@email.com", EstadoAsistente.CONFIRMADO),
        Asistente(4, "Ana Martínez", "ana.mtz@email.com", EstadoAsistente.CANCELADO),
        Asistente(5, "Roberto Díaz", "roberto.d@email.com", EstadoAsistente.CONFIRMADO),
        Asistente(6, "Laura Torres", "l.torres@email.com", EstadoAsistente.PENDIENTE),
        Asistente(7, "Diego Ruiz", "d.ruiz@email.com", EstadoAsistente.CONFIRMADO),
        Asistente(8, "Elena Sanz", "e.sanz@email.com", EstadoAsistente.CONFIRMADO),
        Asistente(9, "Pablo Vega", "p.vega@email.com", EstadoAsistente.PENDIENTE),
        Asistente(10, "Sofía Luna", "s.luna@email.com", EstadoAsistente.CONFIRMADO)
    )

    val ingresos = mutableListOf<RegistroIngreso>()

    val eventoActual = Evento(
        nombre = "Conferencia Tech 2026",
        fecha = "15 de Agosto, 2026",
        hora = "18:00 hrs",
        lugar = "Centro de Convenciones Lima",
        dressCode = "Business Casual",
        indicaciones = "Llegar 30 minutos antes. Estacionamiento disponible."
    )

    /**
     * Registra el ingreso de un asistente por su ID.
     */
    fun registrarIngreso(asistenteId: Int): ResultadoEscaneo {
        val asistente = asistentes.find { it.id == asistenteId } ?: return ResultadoEscaneo.NoValido
        
        // Solo pueden ingresar si están CONFIRMADOS (regla de negocio opcional, pero lógica)
        // Pero el requerimiento no especifica filtrar por estado del asistente, solo por ID.
        
        if (ingresos.any { it.asistenteId == asistenteId }) {
            return ResultadoEscaneo.Duplicado(asistente)
        }

        val nuevoIngreso = RegistroIngreso(
            id = ingresos.size + 1,
            asistenteId = asistenteId,
            fechaHora = System.currentTimeMillis()
        )
        ingresos.add(nuevoIngreso)
        return ResultadoEscaneo.Exito(asistente)
    }

    fun obtenerTotalIngresados(): Int = ingresos.size
    
    fun obtenerTotalConfirmados(): Int = asistentes.count { it.estado == EstadoAsistente.CONFIRMADO }
}
