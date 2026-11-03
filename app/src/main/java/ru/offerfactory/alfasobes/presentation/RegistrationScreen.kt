package ru.offerfactory.alfasobes.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.offerfactory.alfasobes.ui.theme.AlfasobesTheme

@Composable
fun RegistrationScreen() {
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
                onClick = {},
                modifier = Modifier.navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Продолжить", style = MaterialTheme.typography.titleMedium)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            RegistrationField("Логин")
            RegistrationField("Номер телефона", KeyboardType.Phone)
            RegistrationField("Мейл", KeyboardType.Email)
        }
    }
}

@Composable
private fun RegistrationField(
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun RegistrationPreview() {
    AlfasobesTheme { RegistrationScreen() }
}
