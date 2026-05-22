package com.eventpro.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {

    private val _scannedQrValue = MutableStateFlow<String?>(null)
    val scannedQrValue: StateFlow<String?> = _scannedQrValue.asStateFlow()

    private val _isScanning = MutableStateFlow(true)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun onQrScanned(qrValue: String) {
        if (!_isScanning.value) return
        _isScanning.value = false
        _scannedQrValue.value = qrValue
    }

    fun resetScanner() {
        _scannedQrValue.value = null
        _isScanning.value = true
    }
}
