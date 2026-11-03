package ru.offerfactory.alfasobes.presentation

data class RegistrationUiState(
    val login: String = "",
    val phone: String = "",
    val email: String = "",
    val loginErrors: List<String> = emptyList(),
    val phoneErrors: List<String> = emptyList(),
    val emailErrors: List<String> = emptyList(),
    val isLoading: Boolean = false,
)
