package com.example.pawconnect.UserManagement.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawconnect.UserManagement.data.AuthRepository
import com.example.pawconnect.UserManagement.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: UserProfile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: (UserProfile) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter both email and password.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = repository.login(email.trim(), password)
            result.fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(isLoading = false, currentUser = profile, errorMessage = null)
                    }
                    onSuccess(profile)
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Login failed.")
                    }
                }
            )
        }
    }

    fun register(
        email: String,
        password: String,
        fullName: String,
        role: String,
        phoneNumber: String?,
        organizationName: String?,
        onSuccess: (UserProfile) -> Unit
    ) {
        if (fullName.isBlank() || email.isBlank() || password.length < 6) {
            _uiState.update {
                it.copy(errorMessage = "Full name, valid email, and password (min 6 chars) are required.")
            }
            return
        }
        if (role == "business" && organizationName.isNullOrBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Organization / Business name is required for partner accounts.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = repository.register(
                email = email.trim(),
                password = password,
                fullName = fullName.trim(),
                role = role,
                phoneNumber = phoneNumber?.trim()?.ifBlank { null },
                organizationName = organizationName?.trim()?.ifBlank { null }
            )
            result.fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(isLoading = false, currentUser = profile, errorMessage = null)
                    }
                    onSuccess(profile)
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Registration failed.")
                    }
                }
            )
        }
    }

    fun updateProfile(
        fullName: String,
        phoneNumber: String?,
        address: String?,
        organizationName: String?
    ) {
        val userId = _uiState.value.currentUser?.id ?: return
        if (fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Full name cannot be empty.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = repository.updateProfile(
                userId = userId,
                fullName = fullName.trim(),
                phoneNumber = phoneNumber?.trim()?.ifBlank { null },
                address = address?.trim()?.ifBlank { null },
                organizationName = organizationName?.trim()?.ifBlank { null }
            )
            result.fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentUser = updated,
                            successMessage = "Profile updated successfully!"
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Failed to update profile.")
                    }
                }
            )
        }
    }

    fun deactivateAccount(onDeactivated: () -> Unit) {
        val userId = _uiState.value.currentUser?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.deactivateAccount(userId)
            result.fold(
                onSuccess = {
                    _uiState.update { AuthUiState() }
                    onDeactivated()
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "Failed to deactivate account.")
                    }
                }
            )
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            _uiState.update { AuthUiState() }
            onLoggedOut()
        }
    }
}