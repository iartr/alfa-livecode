package ru.offerfactory.alfasobes.presentation

sealed interface RegistrationAction {
    data class LoginChanged(val value: String) : RegistrationAction
    data class PhoneChanged(val value: String) : RegistrationAction
    data class EmailChanged(val value: String) : RegistrationAction
    data object Submit : RegistrationAction
}
