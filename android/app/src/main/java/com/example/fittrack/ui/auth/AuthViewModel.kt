package com.example.fittrack.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.domain.usecase.auth.LoginUseCase
import com.example.fittrack.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            loginUseCase(username, password)
                .onSuccess { _authState.value = AuthUiState.Success }
                .onFailure { _authState.value = AuthUiState.Error(it.message ?: "Login failed") }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            registerUseCase(username, email, password)
                .onSuccess { _authState.value = AuthUiState.Success }
                .onFailure { _authState.value = AuthUiState.Error(it.message ?: "Registration failed") }
        }
    }

    fun resetState() {
        _authState.value = AuthUiState.Idle
    }
}
