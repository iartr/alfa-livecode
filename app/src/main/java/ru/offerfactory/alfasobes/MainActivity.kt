package ru.offerfactory.alfasobes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.offerfactory.alfasobes.presentation.RegistrationScreen
import ru.offerfactory.alfasobes.presentation.RegistrationUiState
import ru.offerfactory.alfasobes.ui.theme.AlfasobesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlfasobesTheme {
                RegistrationScreen(RegistrationUiState(), onAction = {})
            }
        }
    }
}
