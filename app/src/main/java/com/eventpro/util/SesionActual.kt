package com.eventpro.util

import com.eventpro.data.MockDataRepository
import com.eventpro.model.Asistente

/**
 * Objeto Singleton que gestiona la sesión del usuario actual en la aplicación.
 * Actualmente simula un usuario con ID = 1.
 */
object SesionActual {
    var asistenteIdActual: Int = 1

    /**
     * Obtiene los datos completos del asistente logueado actualmente.
     */
    fun obtenerAsistenteActual(): Asistente? {
        return MockDataRepository.asistentes.find { it.id == asistenteIdActual }
    }
}
