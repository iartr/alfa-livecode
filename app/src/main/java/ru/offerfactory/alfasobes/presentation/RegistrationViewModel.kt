package ru.offerfactory.alfasobes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.offerfactory.alfasobes.domain.RegistrationRepository
import ru.offerfactory.alfasobes.domain.RegistrationRequest

class RegistrationViewModel(
    private val repository: RegistrationRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(RegistrationUiState())
    val state = _state.asStateFlow()

    fun onAction(action: RegistrationAction) {
        if (_state.value.isLoading) return
        when (action) {
            is RegistrationAction.LoginChanged ->
                _state.update { it.copy(login = action.value) }
            is RegistrationAction.PhoneChanged ->
                _state.update { it.copy(phone = action.value) }
            is RegistrationAction.EmailChanged ->
                _state.update { it.copy(email = action.value) }
            RegistrationAction.Submit -> submit()
        }
    }

    private fun submit() {
        val current = _state.value
        val request = RegistrationRequest(current.login, current.phone, current.email)
        _state.update {
            it.copy(
                isLoading = true,
                loginErrors = emptyList(),
                phoneErrors = emptyList(),
                emailErrors = emptyList(),
            )
        }
        viewModelScope.launch {
            val errors = repository.register(request)
            _state.update {
                it.copy(
                    isLoading = false,
                    loginErrors = errors.login,
                    phoneErrors = errors.phone,
                    emailErrors = errors.email,
                )
            }
        }
    }
}
