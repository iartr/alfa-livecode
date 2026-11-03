package ru.offerfactory.alfasobes.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.offerfactory.alfasobes.ui.theme.AlfasobesTheme

@Composable
fun RegistrationScreen(
    state: RegistrationUiState,
    onAction: (RegistrationAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                    onAction(RegistrationAction.Submit)
                },
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color(0xFFB8BBC0),
                    disabledContentColor = Color.White,
                ),
                modifier = Modifier.navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Продолжить", style = MaterialTheme.typography.titleMedium)
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(start = 12.dp).size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            RegistrationField(
                placeholder = "Логин",
                value = state.login,
                onValueChange = { onAction(RegistrationAction.LoginChanged(it)) },
                errors = state.loginErrors,
                readOnly = state.isLoading,
            )
            RegistrationField(
                placeholder = "Номер телефона",
                value = state.phone,
                onValueChange = { onAction(RegistrationAction.PhoneChanged(it)) },
                errors = state.phoneErrors,
                readOnly = state.isLoading,
                keyboardType = KeyboardType.Phone,
            )
            RegistrationField(
                placeholder = "Мейл",
                value = state.email,
                onValueChange = { onAction(RegistrationAction.EmailChanged(it)) },
                errors = state.emailErrors,
                readOnly = state.isLoading,
                keyboardType = KeyboardType.Email,
            )
        }
    }
}

@Composable
private fun RegistrationField(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    errors: List<String>,
    readOnly: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            singleLine = true,
            readOnly = readOnly,
            isError = errors.isNotEmpty(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        )
        if (errors.isNotEmpty()) {
            Text(
                text = errors.joinToString(", "),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RegistrationPreview() {
    AlfasobesTheme {
        RegistrationScreen(RegistrationUiState(), onAction = {})
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoadingPreview() {
    AlfasobesTheme {
        RegistrationScreen(
            RegistrationUiState(
                login = "user123", phone = "8999123456", email = "user@mai",
                isLoading = true,
            ),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ErrorsPreview() {
    AlfasobesTheme {
        RegistrationScreen(
            RegistrationUiState(
                login = "user123", phone = "8999123456", email = "user@mai",
                loginErrors = listOf("Введен существующий логин"),
                phoneErrors = listOf("Несуществующий номер", "длина должна быть 11 символов"),
                emailErrors = listOf("Неверный формат"),
            ),
            onAction = {},
        )
    }
}
