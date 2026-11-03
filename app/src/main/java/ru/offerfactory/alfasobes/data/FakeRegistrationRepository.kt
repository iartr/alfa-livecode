package ru.offerfactory.alfasobes.data

import kotlinx.coroutines.delay
import ru.offerfactory.alfasobes.domain.RegistrationErrors
import ru.offerfactory.alfasobes.domain.RegistrationRepository
import ru.offerfactory.alfasobes.domain.RegistrationRequest

class FakeRegistrationRepository : RegistrationRepository {
    override suspend fun register(request: RegistrationRequest): RegistrationErrors {
        delay(3_000)
        return RegistrationErrors(
            login = listOf("Введен существующий логин"),
            phone = listOf("Несуществующий номер", "длина должна быть 11 символов"),
            email = listOf("Неверный формат"),
        )
    }
}
