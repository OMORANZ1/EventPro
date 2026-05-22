package com.eventpro.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiEvent {
    data object Idle : LoginUiEvent()
    data class Error(val message: String) : LoginUiEvent()
    data object Success : LoginUiEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginUiEvent = MutableStateFlow<LoginUiEvent>(LoginUiEvent.Idle)
    val loginUiEvent: StateFlow<LoginUiEvent> = _loginUiEvent.asStateFlow()

    private val _forgotPasswordMessage = MutableStateFlow<String?>(null)
    val forgotPasswordMessage: StateFlow<String?> = _forgotPasswordMessage.asStateFlow()

    fun onEmailChange(value: String) {
        _email.value = value
        clearErrorIfPresent()
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        clearErrorIfPresent()
    }

    fun onLoginClick() {
        val emailValue = _email.value.trim()
        val passwordValue = _password.value

        when {
            emailValue.isEmpty() -> {
                _loginUiEvent.value = LoginUiEvent.Error("Ingresa tu correo electrónico.")
            }
            passwordValue.isEmpty() -> {
                _loginUiEvent.value = LoginUiEvent.Error("Ingresa tu contraseña.")
            }
            else -> performLogin()
        }
    }

    fun onForgotPasswordClick() {
        val emailValue = _email.value.trim()
        Log.d(TAG, "HU-02: Solicitud de recuperación de contraseña para: $emailValue")
        _forgotPasswordMessage.value = if (emailValue.isNotEmpty()) {
            "Se enviará un correo de recuperación a $emailValue."
        } else {
            "Se enviará un correo de recuperación a tu bandeja de entrada."
        }
    }

    fun onLoginUiEventConsumed() {
        _loginUiEvent.value = LoginUiEvent.Idle
    }

    fun onForgotPasswordMessageConsumed() {
        _forgotPasswordMessage.value = null
    }

    private fun performLogin() {
        viewModelScope.launch {
            _isLoading.value = true
            _loginUiEvent.value = LoginUiEvent.Idle
            delay(1500)
            _isLoading.value = false
            _loginUiEvent.value = LoginUiEvent.Success
        }
    }

    private fun clearErrorIfPresent() {
        if (_loginUiEvent.value is LoginUiEvent.Error) {
            _loginUiEvent.value = LoginUiEvent.Idle
        }
    }
}

private const val TAG = "LoginViewModel"
