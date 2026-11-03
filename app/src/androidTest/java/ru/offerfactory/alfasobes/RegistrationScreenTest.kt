package ru.offerfactory.alfasobes

import android.graphics.Bitmap
import android.view.WindowInsets
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.offerfactory.alfasobes.presentation.RegistrationViewModel

@RunWith(AndroidJUnit4::class)
class RegistrationScreenTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun registrationShowsAllStatesAndAllowsRetry() {
        val viewModel = compose.runOnIdle {
            ViewModelProvider(compose.activity)[RegistrationViewModel::class.java]
        }
        compose.onNodeWithText("Регистрация").assertIsDisplayed()
        button().assertIsEnabled()
        capture("initial")
        fillForm()

        // The last input and the fixed bottom button must remain reachable with the IME open.
        fields()[2].performScrollTo().performClick()
        waitForKeyboard(visible = true)
        button().assertIsDisplayed()
        capture("keyboard")

        button().performClick()
        button().assertIsNotEnabled()
        compose.onAllNodes(hasSetTextAction()).assertCountEquals(0)
        capture("loading")
        waitForErrors()
        assertErrors()
        compose.onNodeWithText("user123").assertIsDisplayed()
        compose.onNodeWithText("8999123456").assertIsDisplayed()
        compose.onNodeWithText("user@mai").assertIsDisplayed()
        button().assertIsEnabled()
        capture("errors")

        fields()[0].performTextReplacement("new-user")
        fields()[0].performClick()
        waitForKeyboard(visible = true)
        compose.onNodeWithText("Введен существующий логин").assertIsDisplayed()
        // The IME is moving the button here; invoke its action without stale tap coordinates.
        // The first submit above exercises an actual touch with the keyboard open.
        button().performSemanticsAction(SemanticsActions.OnClick) { click ->
            assertTrue(click())
            // Check the immediate reset before keyboard synchronization can consume the 3 seconds.
            val loading = viewModel.state.value
            assertTrue(loading.isLoading)
            assertTrue(loading.loginErrors.isEmpty())
            assertTrue(loading.phoneErrors.isEmpty())
            assertTrue(loading.emailErrors.isEmpty())
        }
        waitForKeyboard(visible = false)
        waitForErrors()
        assertErrors()
        compose.onNodeWithText("new-user").assertIsDisplayed()
        button().assertIsEnabled()
    }

    @Test
    fun emptyFormCanBeSubmitted() {
        button().assertIsEnabled().performClick()
        button().assertIsNotEnabled()
        waitForErrors()
        assertErrors()
        button().assertIsEnabled()
    }

    @Test
    fun recreatingActivityPreservesTheViewModelAndPendingRequest() {
        fillForm()
        val before = compose.runOnIdle {
            ViewModelProvider(compose.activity)[RegistrationViewModel::class.java]
        }
        button().performClick()
        compose.runOnIdle { assertTrue(before.state.value.isLoading) }
        compose.activityRule.scenario.recreate()
        val after = compose.runOnIdle {
            ViewModelProvider(compose.activity)[RegistrationViewModel::class.java]
        }
        assertSame(before, after)
        waitForErrors()
        assertErrors()
        compose.onNodeWithText("user123").assertIsDisplayed()
        compose.onNodeWithText("8999123456").assertIsDisplayed()
        compose.onNodeWithText("user@mai").assertIsDisplayed()
        button().assertIsEnabled()
    }

    private fun fields() = compose.onAllNodes(hasSetTextAction())
    private fun button() = compose.onNodeWithText("Продолжить")

    private fun fillForm() {
        fields()[0].performTextReplacement("user123")
        fields()[1].performTextReplacement("8999123456")
        fields()[2].performTextReplacement("user@mai")
    }

    private fun waitForErrors() {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText("Неверный формат").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForKeyboard(visible: Boolean) {
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.activity.window.decorView.rootWindowInsets
                ?.isVisible(WindowInsets.Type.ime()) == visible
        }
    }

    private fun assertErrors() {
        compose.onNodeWithText("Введен существующий логин").assertIsDisplayed()
        compose.onNodeWithText("Несуществующий номер, длина должна быть 11 символов").assertIsDisplayed()
        compose.onNodeWithText("Неверный формат").assertIsDisplayed()
    }

    private fun capture(state: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val prefix = InstrumentationRegistry.getArguments().getString("evidencePrefix", "screen")
        // App-owned media survives the runner's artifact collection without scoped-data UID issues.
        @Suppress("DEPRECATION")
        val directory = File(instrumentation.targetContext.externalMediaDirs.first(), "evidence")
        check(directory.exists() || directory.mkdirs())
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(directory, "${prefix}-${state}.png").outputStream().use {
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
        bitmap.recycle()
    }
}
