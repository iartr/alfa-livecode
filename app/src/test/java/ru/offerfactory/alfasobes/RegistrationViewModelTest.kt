package ru.offerfactory.alfasobes

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.offerfactory.alfasobes.domain.RegistrationErrors
import ru.offerfactory.alfasobes.domain.RegistrationRepository
import ru.offerfactory.alfasobes.domain.RegistrationRequest
import ru.offerfactory.alfasobes.presentation.RegistrationAction
import ru.offerfactory.alfasobes.presentation.RegistrationViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {
    @get:Rule val mainDispatcher = MainDispatcherRule()

    private val errors = RegistrationErrors(
        login = listOf("login-error"),
        phone = listOf("phone-error-1", "phone-error-2"),
        email = listOf("email-error"),
    )
    private val repository = RecordingRepository(errors)
    private val viewModel = RegistrationViewModel(repository)

    @Test
    fun changingEachFieldLeavesTheOtherFieldsUntouched() {
        viewModel.onAction(RegistrationAction.LoginChanged("alice"))
        assertEquals("alice", viewModel.state.value.login)
        assertEquals("", viewModel.state.value.phone)
        assertEquals("", viewModel.state.value.email)

        viewModel.onAction(RegistrationAction.PhoneChanged("89991234567"))
        assertEquals("alice", viewModel.state.value.login)
        assertEquals("89991234567", viewModel.state.value.phone)
        assertEquals("", viewModel.state.value.email)

        viewModel.onAction(RegistrationAction.EmailChanged("alice@example.com"))
        assertEquals("alice", viewModel.state.value.login)
        assertEquals("89991234567", viewModel.state.value.phone)
        assertEquals("alice@example.com", viewModel.state.value.email)
        assertTrue(repository.requests.isEmpty())
    }

    @Test
    fun submitCapturesValuesAndIgnoresAllEventsWhileLoading() = runTest {
        fillForm()
        viewModel.onAction(RegistrationAction.Submit)
        assertTrue(viewModel.state.value.isLoading)

        viewModel.onAction(RegistrationAction.Submit)
        viewModel.onAction(RegistrationAction.LoginChanged("changed"))
        viewModel.onAction(RegistrationAction.PhoneChanged("changed"))
        viewModel.onAction(RegistrationAction.EmailChanged("changed"))
        runCurrent()

        assertEquals(listOf(RegistrationRequest("alice", "89991234567", "a@example.com")), repository.requests)
        assertEquals("alice", viewModel.state.value.login)
        assertEquals("89991234567", viewModel.state.value.phone)
        assertEquals("a@example.com", viewModel.state.value.email)
        advanceTimeBy(3_000)
        runCurrent()
    }

    @Test
    fun emptyFormCanBeSubmitted() = runTest {
        viewModel.onAction(RegistrationAction.Submit)
        runCurrent()
        assertEquals(listOf(RegistrationRequest("", "", "")), repository.requests)
        advanceTimeBy(3_000)
        runCurrent()
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(errors.login, viewModel.state.value.loginErrors)
    }

    @Test
    fun errorsAppearAfterThreeSecondsUnderTheirOwnFields() = runTest {
        fillForm()
        viewModel.onAction(RegistrationAction.Submit)
        runCurrent()
        advanceTimeBy(2_999)
        runCurrent()
        assertTrue(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.loginErrors.isEmpty())
        assertTrue(viewModel.state.value.phoneErrors.isEmpty())
        assertTrue(viewModel.state.value.emailErrors.isEmpty())

        advanceTimeBy(1)
        runCurrent()
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(errors.login, state.loginErrors)
        assertEquals(errors.phone, state.phoneErrors)
        assertEquals(errors.email, state.emailErrors)
        assertEquals("alice", state.login)
        assertEquals("89991234567", state.phone)
        assertEquals("a@example.com", state.email)
    }

    @Test
    fun retryClearsEveryErrorImmediatelyAndSendsAnotherRequest() = runTest {
        fillForm()
        viewModel.onAction(RegistrationAction.Submit)
        runCurrent()
        advanceTimeBy(3_000)
        runCurrent()

        viewModel.onAction(RegistrationAction.Submit)
        val state = viewModel.state.value
        assertTrue(state.isLoading)
        assertEquals("alice", state.login)
        assertTrue(state.loginErrors.isEmpty())
        assertTrue(state.phoneErrors.isEmpty())
        assertTrue(state.emailErrors.isEmpty())
        runCurrent()
        assertEquals(2, repository.requests.size)
        advanceTimeBy(3_000)
        runCurrent()
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(errors.phone, viewModel.state.value.phoneErrors)
    }

    @Test
    fun editingAfterResponseKeepsErrorsUntilTheNextSubmit() = runTest {
        viewModel.onAction(RegistrationAction.Submit)
        runCurrent()
        advanceTimeBy(3_000)
        runCurrent()
        viewModel.onAction(RegistrationAction.EmailChanged("fixed@example.com"))
        assertEquals("fixed@example.com", viewModel.state.value.email)
        assertEquals(errors.login, viewModel.state.value.loginErrors)
        assertEquals(errors.phone, viewModel.state.value.phoneErrors)
        assertEquals(errors.email, viewModel.state.value.emailErrors)
    }

    private fun fillForm() {
        viewModel.onAction(RegistrationAction.LoginChanged("alice"))
        viewModel.onAction(RegistrationAction.PhoneChanged("89991234567"))
        viewModel.onAction(RegistrationAction.EmailChanged("a@example.com"))
    }

    private class RecordingRepository(
        private val response: RegistrationErrors,
    ) : RegistrationRepository {
        val requests = mutableListOf<RegistrationRequest>()

        override suspend fun register(request: RegistrationRequest): RegistrationErrors {
            requests += request
            delay(3_000)
            return response
        }
    }
}
