package ru.offerfactory.alfasobes

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import ru.offerfactory.alfasobes.data.FakeRegistrationRepository
import ru.offerfactory.alfasobes.domain.RegistrationErrors
import ru.offerfactory.alfasobes.domain.RegistrationRequest

@OptIn(ExperimentalCoroutinesApi::class)
class FakeRegistrationRepositoryTest {
    @Test
    fun fakeWaitsThreeSecondsAndAlwaysReturnsTheSpecifiedErrors() = runTest {
        val repository = FakeRegistrationRepository()
        for (request in listOf(
            RegistrationRequest("", "", ""),
            RegistrationRequest("new-user", "89991234567", "valid@example.com"),
        )) {
            val result = async { repository.register(request) }
            runCurrent()
            advanceTimeBy(2_999)
            runCurrent()
            assertFalse(result.isCompleted)
            advanceTimeBy(1)
            runCurrent()
            assertEquals(
                RegistrationErrors(
                    login = listOf("Введен существующий логин"),
                    phone = listOf("Несуществующий номер", "длина должна быть 11 символов"),
                    email = listOf("Неверный формат"),
                ),
                result.await(),
            )
        }
    }
}
