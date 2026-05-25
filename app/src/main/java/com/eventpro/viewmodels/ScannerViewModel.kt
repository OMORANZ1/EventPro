package com.eventpro.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eventpro.data.MockDataRepository
import com.eventpro.model.ResultadoEscaneo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {

    private val _scanResult = MutableStateFlow<ResultadoEscaneo?>(null)
    val scanResult: StateFlow<ResultadoEscaneo?> = _scanResult.asStateFlow()

    private val _isScanning = MutableStateFlow(true)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun onQrScanned(qrValue: String) {
        if (!_isScanning.value) return
        processId(qrValue)
    }

    fun onManualInput(idText: String) {
        val id = idText.toIntOrNull()
        if (id != null) {
            processId(id.toString())
        }
    }

    private fun processId(idString: String) {
        val id = idString.toIntOrNull()
        if (id == null) {
            _scanResult.value = ResultadoEscaneo.NoValido
        } else {
            _scanResult.value = MockDataRepository.registrarIngreso(id)
        }
        
        _isScanning.value = false
        
        // Regresar al escáner después de 2 segundos
        viewModelScope.launch {
            delay(2000)
            resetScanner()
        }
    }

    fun resetScanner() {
        _scanResult.value = null
        _isScanning.value = true
    }
}
