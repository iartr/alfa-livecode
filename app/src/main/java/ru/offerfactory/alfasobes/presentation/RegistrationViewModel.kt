package ru.offerfactory.alfasobes.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrationViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegistrationUiState())
    val state = _state.asStateFlow()

    fun onAction(action: RegistrationAction) {
        when (action) {
            is RegistrationAction.LoginChanged ->
                _state.update { it.copy(login = action.value) }
            is RegistrationAction.PhoneChanged ->
                _state.update { it.copy(phone = action.value) }
            is RegistrationAction.EmailChanged ->
                _state.update { it.copy(email = action.value) }
            RegistrationAction.Submit -> Unit // Подключим отправку на шаге 06.
        }
    }
}
