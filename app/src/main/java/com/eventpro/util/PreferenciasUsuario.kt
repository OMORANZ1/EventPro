package com.eventpro.util

import android.content.Context
import android.content.SharedPreferences
import com.eventpro.model.EstadoAsistente

/**
 * Clase encargada de persistir las preferencias del usuario mediante SharedPreferences.
 */
class PreferenciasUsuario(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("EventProPrefs", Context.MODE_PRIVATE)

    /**
     * Guarda el estado de confirmación del asistente actual.
     */
    fun guardarEstadoConfirmacion(asistenteId: Int, estado: EstadoAsistente) {
        prefs.edit().putString("estado_confirmacion_$asistenteId", estado.name).apply()
    }

    /**
     * Obtiene el estado de confirmación guardado para el asistente actual.
     * Si no existe, devuelve PENDIENTE por defecto.
     */
    fun obtenerEstadoConfirmacion(asistenteId: Int): EstadoAsistente {
        val estadoStr = prefs.getString("estado_confirmacion_$asistenteId", EstadoAsistente.PENDIENTE.name)
        return try {
            EstadoAsistente.valueOf(estadoStr ?: EstadoAsistente.PENDIENTE.name)
        } catch (e: Exception) {
            EstadoAsistente.PENDIENTE
        }
    }
}
