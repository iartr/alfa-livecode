package ru.offerfactory.alfasobes.domain

data class RegistrationRequest(
    val login: String,
    val phone: String,
    val email: String,
)

data class RegistrationErrors(
    val login: List<String>,
    val phone: List<String>,
    val email: List<String>,
)

interface RegistrationRepository {
    suspend fun register(request: RegistrationRequest): RegistrationErrors
}
